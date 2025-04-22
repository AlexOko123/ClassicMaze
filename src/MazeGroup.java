import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class MazeGroup {
    private List<Maze> nodeList;
    private char[][] mazeData;
    private List<Pellet> pellets;

    public MazeGroup(char[][] maze) {
        this.nodeList = new ArrayList<>();
        this.pellets= new ArrayList<>();
        this.mazeData = maze;
        buildNodeNetwork(maze);
        buildPellets(maze);
    }


    private void buildPellets(char[][] maze) {
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                if (maze[row][col] == Constants.PATH) { // '.' tile
                    int x = col * Constants.TILE_WIDTH + Constants.TILE_WIDTH/2;
                    int y = row * Constants.TILE_HEIGHT + Constants.TILE_HEIGHT/2;
                    pellets.add(new Pellet(new Vector(x, y)));
                }
            }
        }
    }
    public List<Pellet> getPellets() {
        return pellets;
    }

    public void renderPellets(Graphics g) {
        for (Pellet pellet : pellets) {
            pellet.render(g);
        }
    }

    private void buildNodeNetwork(char[][] maze) {
            // Create all nodes
            Maze[][] nodeGrid = new Maze[maze.length][maze[0].length];
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[row].length; col++) {
                    if (maze[row][col] == '+') {
                        nodeGrid[row][col] = new Maze(
                                col * Constants.TILE_WIDTH,
                                row * Constants.TILE_HEIGHT
                        );
                        nodeList.add(nodeGrid[row][col]);
                    }
                }
            }

            // Connect Nodes w/wall checking
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[row].length; col++) {
                    if (nodeGrid[row][col] != null) {
                        connectWithWallCheck(nodeGrid, maze, row, col);
                    }
                }
            }
        }

        private void connectWithWallCheck(Maze[][] nodeGrid, char[][] maze, int row, int col) {
            // Check RIGHT connection
            for (int c = col + 1; c < maze[row].length; c++) {
                if (maze[row][c] == 'X') break; // Wall blocks connection
                if (nodeGrid[row][c] != null) {
                    nodeGrid[row][col].setNeighbor(Constants.RIGHT, nodeGrid[row][c]);
                    nodeGrid[row][c].setNeighbor(Constants.LEFT, nodeGrid[row][col]);
                    break;
                }
            }

            // Check LEFT connection
            for (int c = col - 1; c >= 0; c--) {
                if (maze[row][c] == 'X') break;
                if (nodeGrid[row][c] != null) {
                    nodeGrid[row][col].setNeighbor(Constants.LEFT, nodeGrid[row][c]);
                    nodeGrid[row][c].setNeighbor(Constants.RIGHT, nodeGrid[row][col]);
                    break;
                }
            }

            // Check DOWN connection
            for (int r = row + 1; r < maze.length; r++) {
                if (maze[r][col] == 'X') break;
                if (nodeGrid[r][col] != null) {
                    nodeGrid[row][col].setNeighbor(Constants.DOWN, nodeGrid[r][col]);
                    nodeGrid[r][col].setNeighbor(Constants.UP, nodeGrid[row][col]);
                    break;
                }
            }

            // Check UP connection
            for (int r = row - 1; r >= 0; r--) {
                if (maze[r][col] == 'X') break;
                if (nodeGrid[r][col] != null) {
                    nodeGrid[row][col].setNeighbor(Constants.UP, nodeGrid[r][col]);
                    nodeGrid[r][col].setNeighbor(Constants.DOWN, nodeGrid[row][col]);
                    break;
                }
            }
        }

    public List<Maze> getNodeList() {
        return nodeList;
    }

    public void render(Graphics g) {
        for (Maze node : nodeList) {
            node.render(g);
        }
    }

    private void renderMaze(Graphics g) {
        if (mazeData == null) return;

        g.setColor(Color.BLUE);

        for (int row = 0; row < mazeData.length; row++) {
            for (int col = 0; col < mazeData[row].length; col++) {
                if (mazeData[row][col] == Constants.WALL) {
                    g.fillRect(
                            col * Constants.TILE_WIDTH,
                            row * Constants.TILE_HEIGHT,
                            Constants.TILE_WIDTH,
                            Constants.TILE_HEIGHT
                    );
                }
            }
        }
    }

    public Maze getStartNode() {
        // return a good starting node for Pacman
        if (!nodeList.isEmpty()) {
            for (Maze node : nodeList) {
                Vector pos = node.getPosition();
                int row = (int)(pos.getY() / Constants.TILE_HEIGHT);
                int col = (int)(pos.getX() / Constants.TILE_WIDTH);

                // look for a node in the bottom half of the maze
                if (row > mazeData.length / 2) {
                    return node;
                }
            }
            return nodeList.get(0); // fallback to first node
        }
        return null;
    }

    public Maze[] getGhostStartNodes() {
        Maze[] ghostNodes = new Maze[4];

        if (nodeList.isEmpty()) {
            System.err.println("ERROR: No maze nodes available for ghosts");
            return ghostNodes;
        }

        // find nodes in the middle area for ghost house
        List<Maze> centerNodes = new ArrayList<>();
        double centerX = 0;
        double centerY = 0;

        // calculate the center of the maze
        for (Maze node : nodeList) {
            Vector pos = node.getPosition();
            centerX += pos.getX();
            centerY += pos.getY();
        }
        centerX /= nodeList.size();
        centerY /= nodeList.size();

        // sort nodes by distance from center
        List<Maze> sortedNodes = new ArrayList<>(nodeList);
        final double finalCenterX = centerX;
        final double finalCenterY = centerY;

        sortedNodes.sort((n1, n2) -> {
            Vector p1 = n1.getPosition();
            Vector p2 = n2.getPosition();
            double d1 = Math.pow(p1.getX() - finalCenterX, 2) + Math.pow(p1.getY() - finalCenterY, 2);
            double d2 = Math.pow(p2.getX() - finalCenterX, 2) + Math.pow(p2.getY() - finalCenterY, 2);
            return Double.compare(d1, d2);
        });

        // select nodes closest to the center
        int centerSize = Math.min(sortedNodes.size(), 8);
        for (int i = 0; i < centerSize; i++) {
            centerNodes.add(sortedNodes.get(i));
        }

        // use center nodes if available, OW space them out
        if (centerNodes.size() >= 4) {
            System.out.println("Using center nodes for ghosts");
            for (int i = 0; i < 4; i++) {
                ghostNodes[i] = centerNodes.get(i);
            }
        } else {
            // fallback to evenly spaced nodes from the node list
            System.out.println("Using evenly spaced nodes for ghosts");
            int spacing = Math.max(1, nodeList.size() / 4);
            for (int i = 0; i < 4; i++) {
                int index = (i * spacing) % nodeList.size();
                ghostNodes[i] = nodeList.get(index);
            }
        }

        // debug output
        System.out.println("Ghost start nodes:");
        for (int i = 0; i < ghostNodes.length; i++) {
            if (ghostNodes[i] != null) {
                Vector pos = ghostNodes[i].getPosition();
                System.out.println("Ghost " + i + ": (" + pos.getX() + ", " + pos.getY() + ")");
            } else {
                System.out.println("Ghost " + i + ": null");
            }
        }

        return ghostNodes;
    }

    public Maze[] getScatterNodes() {
        Maze[] scatterNodes = new Maze[4];

        if (nodeList.size() < 4) {
            return scatterNodes;
        }

        // top-left corner
        Maze topLeft = null;
        double minSum = Double.MAX_VALUE;

        // otp-right corner
        Maze topRight = null;
        double minDiff1 = Double.MAX_VALUE;

        // bottom-left corner
        Maze bottomLeft = null;
        double minDiff2 = Double.MAX_VALUE;

        // bottom-right corner
        Maze bottomRight = null;
        double maxSum = Double.MIN_VALUE;

        for (Maze node : nodeList) {
            Vector pos = node.getPosition();
            double sum = pos.getX() + pos.getY();
            double diff1 = pos.getY() - pos.getX();
            double diff2 = pos.getX() - pos.getY();

            if (sum < minSum) {
                minSum = sum;
                topLeft = node;
            }

            if (diff1 < minDiff1) {
                minDiff1 = diff1;
                topRight = node;
            }

            if (diff2 < minDiff2) {
                minDiff2 = diff2;
                bottomLeft = node;
            }

            if (sum > maxSum) {
                maxSum = sum;
                bottomRight = node;
            }
        }

        scatterNodes[0] = topRight;     // blinky goes to top right
        scatterNodes[1] = topLeft;      // pinky goes to top left
        scatterNodes[2] = bottomRight;  // inky goes to bottom right
        scatterNodes[3] = bottomLeft;   // clyde goes to bottom left

        return scatterNodes;
    }
}