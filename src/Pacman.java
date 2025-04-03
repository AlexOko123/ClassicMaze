// this is the class that will lay out the instructions for how pacman will move around

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class Pacman implements KeyListener {
    private int name;
    private Vector position;
    private Map<Integer, Vector> directions;
    private int direction;
    private double speed;
    private int radius;
    private int[] color;

    // keep track of currently pressed keys
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Pacman() {
        this.name = Constants.PACMAN;
        this.position = new Vector(200, 400);

        this.directions = new HashMap<>();
        this.directions.put(Constants.STOP, new Vector());
        this.directions.put(Constants.UP, new Vector(0, -1));
        this.directions.put(Constants.DOWN, new Vector(0, 1));
        this.directions.put(Constants.LEFT, new Vector(-1, 0));
        this.directions.put(Constants.RIGHT, new Vector(1, 0));

        this.direction = Constants.STOP;
        this.speed = 100 * Constants.TILE_WIDTH/16;
        this.radius = 10;
        this.color = Constants.YELLOW;
    }

    // this method updates an object's position over time based on its current direction
    // and speed, and then determines a new direction dynamically
    public void update(double dt) {
        Vector movement = this.directions.get(this.direction);
        movement = new Vector(movement.getX() * this.speed * dt, movement.getY() * this.speed * dt);
        this.position = this.position.add(movement);

        int newDirection = getValidKey();
        this.direction = newDirection;
    }

    public int getValidKey() {
        if (upPressed) {
            return Constants.UP;
        }
        if (downPressed) {
            return Constants.DOWN;
        }
        if (leftPressed) {
            return Constants.LEFT;
        }
        if (rightPressed) {
            return Constants.RIGHT;
        }
        return Constants.STOP;
    }
    // method visually represents pacman on the screen as a colored circle at its current position
    public void render(Graphics g) {
        int[] p = this.position.asInt();
        g.setColor(new Color(color[0], color[1], color[2]));
        g.fillOval(p[0] - radius, p[1] - radius, radius * 2, radius * 2);
    }

    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
       // it wouldn't let me run the code, without having this function, but we don't need so
        // i overrid it
    }
}