

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
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

    public GameController() {
        // initialize the game window
        frame = new JFrame("Pacman Game");
        // pulled sizes from Constants.java
        frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(false);

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

        frame.setVisible(true);
    }

    public void setBackground() {
        this.backgroundColor = Color.BLACK;
    }

    public void startGame() {
        setBackground();
        this.pacman = new Pacman();
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

        // update pacman with the elapsed time
        this.pacman.update(dt);

        // check for game events
        this.checkEvents();

        // render the game
        this.render();

        repaint();
    }

    public void checkEvents(){
        // this will handle game or window events

    }

    public void render() {
        // clear the background
        screen.setColor(backgroundColor);
        screen.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // render pacman
        this.pacman.render(screen);
    }

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
        GameController game = new GameController();
        game.startGame();

        // game loop
        while (true) {
            game.update();
            try {
                Thread.sleep(16); // approximately 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
