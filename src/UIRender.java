
// this class handles rendering of UI elements like score, lives, and game state transitions
// start screen, pause screen, game over screen

import java.awt.*;

public class UIRender {
    // reference to game state
    private GameState gameState;

    // fonts for different UI elements
    private Font titleFont;
    private Font normalFont;
    private Font smallFont;
    private Font arcadeFont;

    // colors for UI elements
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color MENU_BG_COLOR = new Color(0, 0, 0, 200);
    private final Color HIGHLIGHT_COLOR = Color.YELLOW;
    private final Color GHOST_INFO_COLOR = new Color(0, 200, 255);

    // screen dimensions from Constants
    private final int SCREEN_WIDTH = Constants.SCREEN_WIDTH;
    private final int SCREEN_HEIGHT = Constants.SCREEN_HEIGHT;

    // animation properties for certain UI elements
    private double animationTime = 0;
    private boolean blinkOn = true;

    // pacman animation properties
    private double pacmanAnimTime = 0;
    private int pacmanMouthAngle = 45;
    private boolean pacmanMouthClosing = false;

    public UIRender(GameState gameState) {
        this.gameState = gameState;

        // initialize fonts
        try {
            //  load an arcade-style font
            Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT,
                    new java.io.File("fonts/arcadeclassic.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(arcadeFont);

            this.titleFont = arcadeFont.deriveFont(Font.BOLD, 36);
            this.normalFont = arcadeFont.deriveFont(Font.BOLD, 18);
            this.smallFont = arcadeFont.deriveFont(Font.PLAIN, 14);
            this.arcadeFont = arcadeFont.deriveFont(Font.BOLD, 20);
        } catch (Exception e) {
            // fallback to a similar-looking system font
            this.titleFont = new Font("Courier New", Font.BOLD, 36);
            this.normalFont = new Font("Courier New", Font.BOLD, 18);
            this.smallFont = new Font("Courier New", Font.PLAIN, 14);
            this.arcadeFont = new Font("Courier New", Font.BOLD, 20);
        }
    }

    // render all UI elements based on current game state
    public void render(Graphics g) {
        // always render score and lives during gameplay
        if (gameState.getCurrentState() != Constants.START) {
            renderGameInfo(g);
        }

        // render specific screens based on game state
        switch (gameState.getCurrentState()) {
            case Constants.START:
                renderStartScreen(g);
                break;

            case Constants.PAUSED:
                renderPauseScreen(g);
                break;

            case Constants.GAME_OVER:
                renderGameOverScreen(g);
                break;

            case Constants.DEATH_ANIMATION:
                renderDeathScreen(g);
                break;
        }
    }

    // update animation timers for UI elements
    public void update(double dt) {
        // update animation timer
        animationTime += dt;

        // toggle blink state every 0.5 seconds
        if (animationTime >= 0.5) {
            animationTime = 0;
            blinkOn = !blinkOn;
        }

        // update Pacman animation
        pacmanAnimTime += dt;
        if (pacmanAnimTime >= 0.001) {
            pacmanAnimTime = 0;

            // animate mouth opening and closing
            if (pacmanMouthClosing) {
                pacmanMouthAngle += 5;
                if (pacmanMouthAngle >= 45) {
                    pacmanMouthAngle = 45;
                    pacmanMouthClosing = false;
                }
            } else {
                pacmanMouthAngle -= 5;
                if (pacmanMouthAngle <= 0) {
                    pacmanMouthAngle = 0;
                    pacmanMouthClosing = true;
                }
            }
        }
    }

    // render game information (score, lives, levels)
    private void renderGameInfo(Graphics g) {
        // draw the top info bar
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, 70);

        g.setColor(TEXT_COLOR);
        g.setFont(arcadeFont);

        // score
        g.drawString("SCORE " + gameState.getScore(), 10, 25);

        // lives
        g.drawString("LIVES " + gameState.getLives(), 10, 50);

        // level
        String levelText = "LEVEL " + gameState.getLevel();
        FontMetrics fm = g.getFontMetrics();
        int levelTextWidth = fm.stringWidth(levelText);
        g.drawString(levelText, SCREEN_WIDTH - levelTextWidth - 10, 25);

        // draw Pacman icons for lives
        int pacmanSize = 16;
        for (int i = 0; i < gameState.getLives(); i++) {
            g.setColor(Color.YELLOW);
            g.fillArc(200 + i * (pacmanSize + 10), 40, pacmanSize, pacmanSize, 30, 300);
        }
    }


    // render the start screen
    private void renderStartScreen(Graphics g) {
        // Draw classic arcade-style background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw Pacman logo at the top
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "PACMAN";
        FontMetrics titleMetrics = g.getFontMetrics();
        g.drawString(title, (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2, 100);

        // draw animated Pacman
        drawAnimatedPacman(g, SCREEN_WIDTH / 2 - 50, 150, 40);

        // draw ghost parade
        drawGhostParade(g, SCREEN_WIDTH / 2 + 10, 150);

        // draw game controls section
        g.setColor(Color.WHITE);
        g.setFont(arcadeFont);
        int controlsY = 250;

        g.drawString("GAME CONTROLS", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 30;

        g.setFont(normalFont);
        g.drawString("UP ARROW or W  Move Up", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 25;

        g.drawString("DOWN ARROW or S   Move Down", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 25;

        g.drawString("LEFT ARROW or A  Move Left", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 25;

        g.drawString("RIGHT ARROW or D  Move Right", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 25;

        g.drawString("P  Pause Game", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 25;

        g.drawString("ESC  Quit to Menu", SCREEN_WIDTH / 2 - 80, controlsY);
        controlsY += 40;

        // draw ghosts with their descriptions
        drawGhostInfo(g, controlsY);

        // draw blinking start prompt
        if (blinkOn) {
            g.setColor(Color.YELLOW);
            g.setFont(arcadeFont);
            String startPrompt = "PRESS SPACE TO START";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(startPrompt, (SCREEN_WIDTH - fm.stringWidth(startPrompt)) / 2, SCREEN_HEIGHT - 80);
        }

        // draw copyright
        g.setColor(Color.WHITE);
        g.setFont(smallFont);
        String copyright = "© CLASSIC ARCADE REMAKE";
        FontMetrics copyrightFm = g.getFontMetrics();
        g.drawString(copyright, (SCREEN_WIDTH - copyrightFm.stringWidth(copyright)) / 2, SCREEN_HEIGHT - 20);
    }

    // draw animated pacman for start screen
    private void drawAnimatedPacman(Graphics g, int x, int y, int size) {
        g.setColor(Color.YELLOW);
        // right-facing with animated mouth
        g.fillArc(x, y, size, size, pacmanMouthAngle, 360 - 2 * pacmanMouthAngle);
    }

    // draw ghost parade for start screen
    private void drawGhostParade(Graphics g, int x, int y) {
        int ghostSize = 20;
        Color[] ghostColors = {Color.RED, Color.PINK, new Color(0, 255, 255), Color.ORANGE};

        for (int i = 0; i < ghostColors.length; i++) {
            drawSimpleGhost(g, x + i * 30, y + (i % 2) * 5, ghostSize, ghostColors[i]);
        }
    }

    // draw ghost with name and info
    private void drawGhostInfo(Graphics g, int startY) {
        int x = SCREEN_WIDTH / 2 - 80;
        int y = startY;
        int size = 20;
       // g.setFont(normalFont);

        // blinky (Red)
        g.setColor(Color.RED);
        drawSimpleGhost(g, x - 30, y - 15, size, Color.RED);
        g.drawString("BLINKY  Chases directly", x, y);
        y += 30;

        // pinky (Pink)
        g.setColor(Color.PINK);
        drawSimpleGhost(g, x - 30, y - 15, size, Color.PINK);
        g.drawString("PINKY  Ambushes ahead", x, y);
        y += 30;

        // inky (Cyan)
        g.setColor(new Color(0, 255, 255));
        drawSimpleGhost(g, x - 30, y - 15, size, new Color(0, 255, 255));
        g.drawString("INKY  Unpredictable", x, y);
        y += 30;

        // clyde (Orange)
        g.setColor(Color.ORANGE);
        drawSimpleGhost(g, x - 30, y - 15, size, Color.ORANGE);
        g.drawString("CLYDE  Moves randomly", x, y);
    }

    // draw a simple ghost
    private void drawSimpleGhost(Graphics g, int x, int y, int size, Color ghostColor) {
        // ghost body with rounded top
        g.setColor(ghostColor);

        // top arc (half circle)
        g.fillArc(x, y, size, size, 0, 180);

        // main body rectangle
        g.fillRect(x, y + size/2, size, size/2);

        // bottom waves (3 small arcs)
        int waveCount = 3;
        int waveWidth = size / waveCount;
        for (int i = 0; i < waveCount; i++) {
            g.fillArc(x + i * waveWidth, y + size, waveWidth, waveWidth/2, 180, 180);
        }

        // eyes
        g.setColor(Color.WHITE);
        int eyeSize = size / 3;
        g.fillOval(x + size/4 - eyeSize/2, y + size/3, eyeSize, eyeSize);
        g.fillOval(x + 3*size/4 - eyeSize/2, y + size/3, eyeSize, eyeSize);

        // pupils (looking right by default)
        g.setColor(Color.BLACK);
        int pupilSize = eyeSize / 2;
        g.fillOval(x + size/4 + pupilSize/2 - eyeSize/2, y + size/3 + eyeSize/4, pupilSize, pupilSize);
        g.fillOval(x + 3*size/4 + pupilSize/2 - eyeSize/2, y + size/3 + eyeSize/4, pupilSize, pupilSize);
    }

    // render pause screen
    private void renderPauseScreen(Graphics g) {
        // draw overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw pause title
        g.setFont(titleFont);
        g.setColor(HIGHLIGHT_COLOR);
        String title = "GAME PAUSED";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, (SCREEN_WIDTH - titleWidth) / 2, SCREEN_HEIGHT / 3);

        // draw menu options
        g.setFont(arcadeFont);
        g.setColor(TEXT_COLOR);
        String[] menuOptions = {
                "PRESS SPACE OR P TO RESUME",
                "PRESS ESC TO QUIT"
        };

        int y = SCREEN_HEIGHT / 2;
        for (String option : menuOptions) {
            FontMetrics fm = g.getFontMetrics();
            int optionWidth = fm.stringWidth(option);
            g.drawString(option, (SCREEN_WIDTH - optionWidth) / 2, y);
            y += 40;
        }

        // draw current score
        g.setFont(normalFont);
        g.setColor(HIGHLIGHT_COLOR);
        String scoreText = "CURRENT SCORE " + gameState.getScore();
        FontMetrics fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        g.drawString(scoreText, (SCREEN_WIDTH - scoreWidth) / 2, SCREEN_HEIGHT * 3 / 4);
    }

    // render death animation screen
    private void renderDeathScreen(Graphics g) {
        // still show the game elements behind

        // draw a semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (blinkOn) {
            g.setColor(Color.YELLOW);
            g.setFont(arcadeFont);
            String prompt = "PACMAN CAUGHT! PRESS SPACE TO CONTINUE";
            FontMetrics fm = g.getFontMetrics();
            int promptWidth = fm.stringWidth(prompt);
            g.drawString(prompt, (SCREEN_WIDTH - promptWidth) / 2, SCREEN_HEIGHT / 2);
        }
    }

    // render the game over screen
    private void renderGameOverScreen(Graphics g) {
        // draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw game over message
        g.setFont(titleFont);
        g.setColor(Color.RED);
        String gameOver = "GAME OVER";
        FontMetrics fmGameOver = g.getFontMetrics();
        int gameOverWidth = fmGameOver.stringWidth(gameOver);
        g.drawString(gameOver, (SCREEN_WIDTH - gameOverWidth) / 2, SCREEN_HEIGHT / 3);

        // draw final score
        g.setFont(arcadeFont);
        g.setColor(HIGHLIGHT_COLOR);
        String finalScore = "FINAL SCORE: " + gameState.getScore();
        FontMetrics fmScore = g.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(finalScore);
        g.drawString(finalScore, (SCREEN_WIDTH - scoreWidth) / 2, SCREEN_HEIGHT / 2);

        // draw level reached
        String levelText = "LEVELS COMPLETED " + (gameState.getLevel() - 1);
        int levelWidth = fmScore.stringWidth(levelText);
        g.drawString(levelText, (SCREEN_WIDTH - levelWidth) / 2, SCREEN_HEIGHT / 2 + 30);

        // draw restart prompt - blinking
        if (blinkOn) {
            g.setColor(Color.WHITE);
            String restartPrompt = "PRESS SPACE TO PLAY AGAIN";
            FontMetrics fmPrompt = g.getFontMetrics();
            int promptWidth = fmPrompt.stringWidth(restartPrompt);
            g.drawString(restartPrompt, (SCREEN_WIDTH - promptWidth) / 2, SCREEN_HEIGHT * 3 / 4);
        }

        // draw "ghost graveyard" decoration
        drawGhostGraveyard(g);
    }

    // decorative ghost graveyard on game over screen
    private void drawGhostGraveyard(Graphics g) {
        // draw some  RIP signs
        drawGhostWithTombstone(g, SCREEN_WIDTH / 4 - 30, SCREEN_HEIGHT * 2 / 3, Color.RED);
        drawGhostWithTombstone(g, SCREEN_WIDTH / 2 - 30, SCREEN_HEIGHT * 2 / 3 + 20, Color.PINK);
        drawGhostWithTombstone(g, SCREEN_WIDTH * 3 / 4 - 30, SCREEN_HEIGHT * 2 / 3, new Color(0, 255, 255));
    }

    // tombstone
    private void drawGhostWithTombstone(Graphics g, int x, int y, Color ghostColor) {
        // draw tombstone
        g.setColor(Color.GRAY);
        g.fillRect(x, y, 25, 30);
        g.fillArc(x, y - 10, 25, 20, 0, 180);

        // draw RIP text
        g.setColor(Color.BLACK);
        g.drawString("RIP", x + 5, y + 20);

        // draw ghost peeking from behind
        g.setColor(new Color(ghostColor.getRed(), ghostColor.getGreen(), ghostColor.getBlue(), 150));
        g.fillOval(x + 20, y + 10, 20, 20);
        g.fillRect(x + 20, y + 20, 20, 15);
    }

    // draw button with text
    private
    Rectangle drawButton(Graphics g, String text, int x, int y, int width, int height, boolean isActive) {
        // draw button background
        g.setColor(isActive ? HIGHLIGHT_COLOR : TEXT_COLOR);
        g.fillRoundRect(x, y, width, height, 10, 10);

        // draw button border
        g.setColor(isActive ? TEXT_COLOR : HIGHLIGHT_COLOR);
        g.drawRoundRect(x, y, width, height, 10, 10);

        // draw button text
        g.setFont(normalFont);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, x + (width - textWidth) / 2, y + height / 2 + textHeight / 4);

        return new Rectangle(x, y, width, height);
    }
}
