// when we create a Node we pass in the row and col values and then compute the x and y
// position we want to place the Node on the screen

// we also set up the neighbors as a dictionary, this way it's easy to know which node
// is in which direction to this Node

import java.awt.Graphics;
import java.awt.Color;

public class Maze {
    private Vector position;
    private Maze[] neighbors;

    public Maze(int x, int y) {
        this.position = new Vector(x, y);
        this.neighbors = new Maze[4]; // UP, DOWN, LEFT, RIGHT
    }

    public Vector getPosition() {
        return position;
    }

    public Maze[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbor(int direction, Maze node) {
        this.neighbors[getDirectionIndex(direction)] = node;
    }

    public Maze getNeighbor(int direction) {
        return this.neighbors[getDirectionIndex(direction)];
    }

    private int getDirectionIndex(int direction) {
        switch (direction) {
            case Constants.UP: return 0;
            case Constants.DOWN: return 1;
            case Constants.LEFT: return 2;
            case Constants.RIGHT: return 3;
            default: return -1;
        }
    }

    public void render(Graphics g) {
        // draw lines to all connected neighbors
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null) {
                int[] start = this.position.asInt();
                int[] end = neighbors[i].position.asInt();
                g.setColor(Color.WHITE);
                g.drawLine(start[0], start[1], end[0], end[1]);
            }
        }
        // draw the node itself as a circle
        int[] pos = this.position.asInt();
        g.setColor(Color.RED);
        g.fillOval(pos[0] - 6, pos[1] - 6, 12, 12);
    }
}