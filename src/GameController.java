

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameController extends JPanel {
    // creating a backgroundColor and setting to the BLACK declared in Constants.java
    private JFrame frame;
    private Color backgroundColor = Color.BLACK;

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

        frame.setVisible(true);
    }

    public void setBackground() {
        this.backgroundColor = Color.BLACK;
    }

    public void startGame() {
        setBackground();
        // not finished
    }

    public void update() {
        repaint(); // triggers rendering
        // not finished
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
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
