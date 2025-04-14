import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MazeGroup {
    private List<Maze> nodeList;

    public MazeGroup(char[][] maze) {
        this.nodeList = new ArrayList<>();
        buildNodeNetwork(maze);
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
}