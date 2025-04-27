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
        // draw lines to connected neighbors with blue color
        g.setColor(Color.BLUE);
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null) {
                int[] start = this.position.asInt();
                int[] end = neighbors[i].position.asInt();
                // use thicker lines for the maze walls
                g.drawLine(start[0], start[1], end[0], end[1]);
                // draw a second line to make it thicker
                switch (i) {
                    case 0: // UP
                    case 1: // DOWN
                        g.drawLine(start[0]-1, start[1], end[0]-1, end[1]);
                        g.drawLine(start[0]+1, start[1], end[0]+1, end[1]);
                        break;
                    case 2: // LEFT
                    case 3: // RIGHT
                        g.drawLine(start[0], start[1]-1, end[0], end[1]-1);
                        g.drawLine(start[0], start[1]+1, end[0], end[1]+1);
                        break;
                }
            }
        }

        // we can keep the node visualization for debugging but making it smaller
        // or comment this out in the final version
        int[] pos = this.position.asInt();
        g.setColor(Color.RED);
        g.fillOval(pos[0] - 3, pos[1] - 3, 6, 6);
    }
}