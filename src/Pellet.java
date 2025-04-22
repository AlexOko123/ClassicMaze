import java.awt.Color;
import java.awt.Graphics;

public class Pellet {
    private Vector position;
    private boolean eaten;
    private int radius;

    public Pellet(Vector position) {
        this.position = position;
        this.eaten = false;
        this.radius = 4; // small size for dot
    }

    public Vector getPosition() {
        return position;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void eat() {
        eaten = true;
    }

    public void render(Graphics g) {
        if (!eaten) {
            int[] pos = position.asInt();
            g.setColor(Color.WHITE);
            g.fillOval(pos[0] - radius, pos[1] - radius, radius * 2, radius * 2);
        }
    }
}
