// this class manages multiple ghosts, their AI behavior modes, and timing for mode switches between chase and scatter

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class GhostAI {
    // list of all ghosts in the game
    private List<Ghost> ghosts;

    // scatter/chase mode timing
    private double modeTimer;
    private double frightenedTimer;
    private boolean isChaseMode;

    // duration of frightened mode in seconds
    private final double FRIGHTENED_DURATION = 8.0;

    // timing patterns (in seconds) for switching between scatter and chase
    // based on the original Pac-Man game timings
    private final double[] scatterTimes = {7.0, 7.0, 5.0, 5.0};
    private final double[] chaseTimes = {20.0, 20.0, 20.0, 999.0}; // last one is "permanent"
    private int patternIndex;

    // debug flag
    private boolean debug = false;

    // ghost manager (maze navigation)
    public GhostAI(MazeGroup nodes) {
        this.ghosts = new ArrayList<>();
        this.modeTimer = scatterTimes[0]; // Start with scatter mode
        this.frightenedTimer = 0;
        this.isChaseMode = false;
        this.patternIndex = 0;

        initializeGhosts(nodes);

        // Debug output to check if ghosts were created
        if (ghosts.isEmpty()) {
            System.err.println("WARNING: No ghosts were initialized!");
        } else {
            System.out.println("Successfully initialized " + ghosts.size() + " ghosts");
            for (Ghost ghost : ghosts) {
                System.out.println("Ghost " + ghost.getGhostType() + " position: " + ghost.getPosition());
            }
        }
    }

    private void initializeGhosts(MazeGroup nodes) {
        // Get ghost home positions
        Maze[] startNodes = nodes.getGhostStartNodes();
        Maze[] scatterNodes = nodes.getScatterNodes();

        // Add fallback logic in case the nodes aren't properly initialized
        if (startNodes == null || startNodes.length == 0 || startNodes[0] == null) {
            System.out.println("Warning: Using fallback nodes for ghosts");
            List<Maze> allNodes = nodes.getNodeList();
            if (allNodes.size() >= 4) {
                // Create fallback nodes using available maze nodes
                startNodes = new Maze[4];
                scatterNodes = new Maze[4];

                // Space them out across the maze
                int step = allNodes.size() / 4;
                for (int i = 0; i < 4; i++) {
                    startNodes[i] = allNodes.get(i * step);
                    scatterNodes[i] = allNodes.get(allNodes.size() - 1 - (i * step));
                }
            } else {
                System.err.println("ERROR: Not enough nodes available for ghosts!");
                return;
            }
        }

        // blinky (Red) - direct chaser
        addGhost(0, startNodes[0], scatterNodes[0], Color.RED);

        // pinky (Pink) - ambusher
        if (startNodes.length > 1 && startNodes[1] != null) {
            addGhost(1, startNodes[1], scatterNodes[1], Color.PINK);
        } else if (startNodes[0] != null) {
            // Fallback to using the same node with slight offset
            addGhost(1, startNodes[0], scatterNodes[0], Color.PINK);
        }

        // inky (Cyan) - unpredictable
        if (startNodes.length > 2 && startNodes[2] != null) {
            addGhost(2, startNodes[2], scatterNodes[2], new Color(0, 255, 255));
        } else if (startNodes[0] != null) {
            addGhost(2, startNodes[0], scatterNodes[0], new Color(0, 255, 255));
        }

        // clyde (Orange) - random
        if (startNodes.length > 3 && startNodes[3] != null) {
            addGhost(3, startNodes[3], scatterNodes[3], Color.ORANGE);
        } else if (startNodes[0] != null) {
            addGhost(3, startNodes[0], scatterNodes[0], Color.ORANGE);
        }
    }

    // add a new ghost to the manager with a specific type, start position, scatter node, and color
    private void addGhost(int type, Maze startNode, Maze scatterNode, Color color) {
        Ghost ghost = new Ghost(type, startNode, scatterNode, color);

        // Add small offset to prevent ghosts from stacking if they share the same node
        Vector pos = ghost.getPosition();
        pos = pos.add(new Vector(type * 5, type * 5));

        ghosts.add(ghost);
    }

    // update all ghosts and manage mode timing
    public void update(double dt, Pacman pacman) {
        // update mode timers
        updateTimers(dt);

        // update each ghost
        for (Ghost ghost : ghosts) {
            ghost.update(dt, pacman);
        }
    }

    // enable or disable debug mode
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    // update the timers for mode switching and frightened mode
    private void updateTimers(double dt) {
        // update frightened timer if active
        if (frightenedTimer > 0) {
            frightenedTimer -= dt;
            if (frightenedTimer <= 0) {
                // end of frightened mode
                restorePreviousMode();
            }
        } else {
            // update chase/scatter mode timer
            modeTimer -= dt;
            if (modeTimer <= 0) {
                // time to switch modes
                switchChaseScatterMode();
            }
        }
    }

    // switch between chase and scatter modes
    private void switchChaseScatterMode() {
        isChaseMode = !isChaseMode;

        // set timer based on current mode
        if (isChaseMode) {
            modeTimer = chaseTimes[patternIndex];
        } else {
            modeTimer = scatterTimes[patternIndex];
            // pattern index after completing a scatter phase
            patternIndex = Math.min(patternIndex + 1, scatterTimes.length - 1);
        }

        // update all ghosts with new mode
        for (Ghost ghost : ghosts) {
            ghost.switchMode(isChaseMode ? Constants.CHASE : Constants.SCATTER);
        }
    }

    // activate frightened mode for all ghosts
    public void frightenGhosts() {
        // reset frightened timer
        frightenedTimer = FRIGHTENED_DURATION;

        // set all ghosts to frightened mode
        for (Ghost ghost : ghosts) {
            ghost.frighten();
        }
    }

    // restore previous chase/scatter mode after frightened ends
    private void restorePreviousMode() {
        // update all ghosts with previous mode
        for (Ghost ghost : ghosts) {
            if (ghost.getBehavior() == Constants.FRIGHTENED) {
                ghost.switchMode(isChaseMode ? Constants.CHASE : Constants.SCATTER);
            }
        }
    }

    // check if any ghost caught pacman, true if caught, OW false
    public boolean checkPacmanCaught(Vector pacmanPos) {
        for (Ghost ghost : ghosts) {
            if (ghost.isCollidingWith(pacmanPos) &&
                    ghost.getBehavior() != Constants.FRIGHTENED &&
                    ghost.getBehavior() != Constants.EATEN) {
                return true;
            }
        }
        return false;
    }

    // handle collision between pacman and a ghost
    public boolean handleGhostCollision(Vector pacmanPos, GameState gameState) {
        for (Ghost ghost : ghosts) {
            if (ghost.isCollidingWith(pacmanPos)) {
                if (ghost.getBehavior() == Constants.FRIGHTENED) {
                    // Ghost is eaten
                    ghost.collideWithPacman();
                    gameState.addScore(Constants.GHOST_SCORE);
                    return false;
                } else if (ghost.getBehavior() != Constants.EATEN) {
                    // Pacman is caught
                    return true;
                }
            }
        }
        return false;
    }

    // reset all ghosts to their starting positions
    public void resetGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.respawn();
        }

        // reset mode timers
        modeTimer = scatterTimes[0];
        frightenedTimer = 0;
        isChaseMode = false;
        patternIndex = 0;
    }

    // render all ghosts on the screen
    public void render(Graphics g) {
        // Debug outline for ghost positions
        if (debug) {
            g.setColor(Color.GREEN);
            for (Ghost ghost : ghosts) {
                Vector pos = ghost.getPosition();
                int[] p = pos.asInt();
                g.drawRect(p[0] - 15, p[1] - 15, 30, 30);
            }
        }

        // Render each ghost
        for (Ghost ghost : ghosts) {
            ghost.render(g);
        }
    }

    // get the list of ghosts
    public List<Ghost> getGhosts() {
        return ghosts;
    }
}