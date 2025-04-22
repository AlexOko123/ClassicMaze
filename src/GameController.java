
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;


public class GameController extends JPanel {
    // creating a backgroundColor and setting to the BLACK declared in Constants.java
    private JFrame frame;
    private Color backgroundColor = Color.BLACK;
    private Pacman pacman;
    private Instant lastTime;
    private BufferedImage background;
    private Graphics screen;
    private MazeGroup nodes;
    private GameState gameState;
    private UIRender uiRender;
    private GhostAI ghostAI;
    private boolean isGameRunning;
    private boolean deathDelay = false;
    private double deathTimer = 0;
    private final double DEATH_DELAY = 2.0; // 2 seconds delay

    public GameController() {
        // initialize the game window
        frame = new JFrame("Pacman Game");
        // pulled sizes from Constants.java
        frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(false);
        frame.setVisible(true);

        // handle window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // initialzie the background image and graphics content
        background = new BufferedImage(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        screen = background.getGraphics();

        this.gameState = new GameState();
        this.uiRender = new UIRender(gameState);
        this.isGameRunning = true;

        // key listener for global game contorls
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

    }

    public void setBackground() {
        this.backgroundColor = Color.BLACK;
    }

    public void startGame() {
        setBackground();

        try {
            // read in maze file to set up maze
            char[][] maze = FileReader.readMazeFile("pacMaze1.txt");
            System.out.println("Successfully loaded maze with dimensions: " +
                    maze.length + " rows x " + maze[0].length + " cols");

            this.nodes = new MazeGroup(maze);
        } catch (IOException e) {
            System.err.println("Error loading maze file: " + e.getMessage());
            System.out.println("Using emergency fallback maze");

            // minimal fallback maze
            char[][] fallbackMaze = {
                    {'X','+','X'},
                    {'+','.','+'},
                    {'X','+','X'}
            };
            this.nodes = new MazeGroup(fallbackMaze);
        }

        // initialize ghost AI with the maze nodes
        this.ghostAI = new GhostAI(this.nodes);

        // initialize pacman with the first node in the nodeList
        if (!this.nodes.getNodeList().isEmpty()) {
            this.pacman = new Pacman(this.nodes.getNodeList().get(0));
        } else {
            //  if the nodes list is empty
            System.err.println("Node list is empty");
            this.pacman = new Pacman(new Maze(200, 400));
        }
        lastTime = Instant.now();

        // registering the Pacman instance as a KeyListener
        if (frame != null) {
            frame.addKeyListener(pacman);
            frame.setFocusable(true);
            frame.requestFocus();
        }
    }

    public void update() {
        Instant currentTime = Instant.now();
        double dt = Duration.between(lastTime, currentTime).toMillis() / 1000.0;
        lastTime = currentTime;

        // limit to around 30 FPS
        if(dt < 1.0/30.0) {
            try {
                Thread.sleep((long)((1.0/3.0 -dt) * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // update UI animations
        uiRender.update(dt);

        // only update gameplay components if game is in playing state
        if (gameState.getCurrentState() == Constants.PLAYING) {
            // update pacman with the time
            this.pacman.update(dt);

            for (Pellet pellet : nodes.getPellets()) {
                if (!pellet.isEaten()) {
                    double distance = pellet.getPosition().subtract(pacman.getPosition()).magnitude();
                    if (distance < 10) { // Pacman close enough
                        pellet.eat();
                        gameState.addScore(Constants.DOT_SCORE); // +10 points
                        System.out.println("Pellet eaten! Score: " + gameState.getScore());
                    }
                }
            }

            // update ghosts
            if (ghostAI != null) {
                // debug: print ghost count and positions
                System.out.println("Updating " + ghostAI.getGhosts().size() + " ghosts");
                ghostAI.update(dt, pacman);
            } else {
                System.out.println("WARNING: ghostAI is null!");
            }

            this.checkEvents(dt);
        } else if (deathDelay && gameState.getCurrentState() == Constants.DEATH_ANIMATION) {
            // during death animation, count down the delay
            deathTimer -= dt;
            if (deathTimer <= 0) {
                deathDelay = false;
                // we don't transition automatically - wait for user input
            }
        }

        // render the game
        this.render();

        repaint();
    }

    public void checkEvents(double dt) {
        // if we're in death delay, count down the timer THIS MAY NOT WORK MIGHT NEED TO DEBUG
        if (deathDelay) {
            deathTimer -= dt;
            if (deathTimer <= 0) {
                deathDelay = false;

                // reset positions but continue game
                if (pacman != null) {
                    pacman = new Pacman(this.nodes.getNodeList().get(0));
                    frame.addKeyListener(pacman);
                }
                ghostAI.resetGhosts();
            }
            return; // skip the rest of the checks during delay
        }

        // check if Pacman was caught by a ghost
        if (ghostAI != null && ghostAI.checkPacmanCaught(pacman.getPosition())) {
            // handle Pacman death
            boolean gameStillGoing = gameState.pacmanDeath();
            System.out.println("Pacman caught! Game continues: " + gameStillGoing);

            if (gameStillGoing) {
                // Set up delay before allowing reset
                deathDelay = true;
                deathTimer = DEATH_DELAY;
            }
        }

        // NEED METHOD for power pellet collection (example - will need to be implemented with a dot system)
        // if (checkPowerPelletCollected()) {
        //     ghostManager.frightenGhosts();
        // }
    }

    public void render() {
        // clear the background
        screen.setColor(backgroundColor);
        screen.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // only render game elements when not on start screen
        if (gameState.getCurrentState() != Constants.START) {
            // render nodes
            this.nodes.render(screen);

            this.nodes.renderPellets(screen);

            // render pacman only if not in death animation or game over
            if (gameState.getCurrentState() != Constants.GAME_OVER) {
                this.pacman.render(screen);
            }

            // render ghosts unless game over
            if (ghostAI != null && gameState.getCurrentState() != Constants.GAME_OVER) {
                // debug: print ghost count when rendering
                System.out.println("Rendering " + ghostAI.getGhosts().size() + " ghosts");
                ghostAI.render(screen);
            }

            // render death animation if in that state
            if (gameState.getCurrentState() == Constants.DEATH_ANIMATION) {
                //renderDeathAnimation(screen);
            }
        }

        // always render UI elements (score, lives, game state messages)
        uiRender.render(screen);
    }

   /* private void renderDeathAnimation(Graphics g) {
        // calculate animation progress based on death timer
        double animationProgress = 1.0 - (deathTimer / DEATH_DELAY);

        // get pacman position
        //int x = Pacman.get.getX();
        //int y = Pacman.getPosition().getY();
        //int size = Constants.
        // draw death animation (example: shrinking circle)
        g.setColor(Color.YELLOW);
        int animSize = (int)(size * (1.0 - animationProgress));
        if (animSize > 0) {
            g.fillArc(
                    x - animSize/2,
                    y - animSize/2,
                    animSize,
                    animSize,
                    0,
                    360 - (int)(animationProgress * 360)
            );
        }
    } */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw the background image to the panel
        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }

    }

    public static void main(String[] args) {
        // create a GameController object + start the game
        SwingUtilities.invokeLater(() -> {
            GameController game = new GameController();
            game.startGame();

            // game loop
            Timer gameTimer = new Timer(16, e -> game.update());
            gameTimer.start();
        });
    }

    public void resetGame() {
        // print debug info
        System.out.println("Fully resetting game...");

        // reset game state if needed
        if (gameState.getCurrentState() != Constants.PLAYING) {
            gameState.startGame();
        }

        // create a new maze and nodes
        try {
            char[][] maze = FileReader.readMazeFile("pacMaze1.txt");
            this.nodes = new MazeGroup(maze);
        } catch (IOException e) {
            System.err.println("Error loading maze file: " + e.getMessage());
            char[][] fallbackMaze = {
                    {'X','+','X'},
                    {'+','.','+'},
                    {'X','+','X'}
            };
            this.nodes = new MazeGroup(fallbackMaze);
        }

        // initialize ghost AI with the maze nodes
        this.ghostAI = new GhostAI(this.nodes);

        // reset Pacman - ensure old listeners are removed
        if (pacman != null) {
            frame.removeKeyListener(pacman);
        }

        if (!this.nodes.getNodeList().isEmpty()) {
            this.pacman = new Pacman(this.nodes.getNodeList().get(0));
            frame.addKeyListener(pacman);
            frame.requestFocus();
        }

        // reset time tracking
        lastTime = Instant.now();

        System.out.println("Game reset complete!");
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        switch (gameState.getCurrentState()) {
            case Constants.START:
                if (key == KeyEvent.VK_SPACE) {
                    gameState.startGame();
                    resetGame();
                }
                break;

            case Constants.PLAYING:
                if (key == KeyEvent.VK_P) {
                    gameState.togglePause();
                } else if (key == KeyEvent.VK_ESCAPE) {
                    // close the application completely
                    System.exit(0);
                }
                break;

            case Constants.PAUSED:
                if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_P) {
                    gameState.togglePause();
                } else if (key == KeyEvent.VK_ESCAPE) {
                    // close the application completely
                    System.exit(0);
                }
                break;

            case Constants.GAME_OVER:
                if (key == KeyEvent.VK_SPACE) {
                    // complete restart - create new game state and entities
                    //gameState = new GameState();
                    gameState.startGame();
                    resetGame(); // this method needs to be fixed too
                    //frame.removeKeyListener(pacman); // remove old listener
                    //frame.addKeyListener(pacman);    // add new listener
                    System.out.println("Game restarted from game over!");
                } else if (key == KeyEvent.VK_ESCAPE) {
                    // close the application completely
                    System.exit(0);
                }
                break;

            case Constants.DEATH_ANIMATION:
                if (key == KeyEvent.VK_SPACE) {
                    gameState.continueAfterDeath();
                    resetPositions();  // only reset positions, not the whole game
                } else if (key == KeyEvent.VK_ESCAPE) {
                    // close the application completely
                    System.exit(0);
                }
                break;
        }
    }

    //  new method to reset only positions (for death)
    private void resetPositions() {
        // reset pacman position
        if (!this.nodes.getNodeList().isEmpty()) {
            this.pacman = new Pacman(this.nodes.getNodeList().get(0));
            frame.addKeyListener(pacman);
            frame.requestFocus();
        }

        // reset ghost positions
        ghostAI.resetAfterDeath();
    }
}