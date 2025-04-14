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


    // ghost manager (maze navigation)
    public GhostAI(MazeGroup nodes) {
        this.ghosts = new ArrayList<>();
        this.modeTimer = 0;
        this.frightenedTimer = 0;
        this.isChaseMode = false;
        this.patternIndex = 0;

        // create ghosts if we have enough nodes
        List<Maze> nodeList = nodes.getNodeList();
        if (nodeList.size() >= 4) {
            // find appropriate nodes for ghost starting positions and scatter targets
            // picking somewhat arbitrary nodes from the list

            Maze blinkyStart = nodeList.get(nodeList.size() / 4);
            Maze pinkyStart = nodeList.get(nodeList.size() / 3);
            Maze inkyStart = nodeList.get(nodeList.size() / 2);
            Maze clydeStart = nodeList.get(3 * nodeList.size() / 4);

            // scatter targets (typically corners of the maze)
            Maze blinkyScatter = nodeList.get(0);
            Maze pinkyScatter = nodeList.get(nodeList.size() - 1);
            Maze inkyScatter = nodeList.get(nodeList.size() / 2 - 1);
            Maze clydeScatter = nodeList.get(nodeList.size() / 2 + 1);

            // create the four ghosts with unique colors
            addGhost(0, blinkyStart, blinkyScatter, Color.RED);       // blinky (red)
            addGhost(1, pinkyStart, pinkyScatter, Color.PINK);        // pinky (pink)
            addGhost(2, inkyStart, inkyScatter, new Color(0, 255, 255)); // inky (cyan)
            addGhost(3, clydeStart, clydeScatter, Color.ORANGE);      // clyde (orange)
        }
    }

    // add a new ghost to the new manager with a specfic type, start position, scatter node, and color
    private void addGhost(int type, Maze startNode, Maze scatterNode, Color color) {
        ghosts.add(new Ghost(type, startNode, scatterNode, color));
    }

    // update all ghosts and manage mode timing
    public void update(double dt, Pacman pacman) {
        // update mode timers
        updateTimers(dt);

        // update each ghost
        for (Ghost ghost : ghosts) {
            ghost.update(dt, pacman);

            // check for collision with Pacman
            if (ghost.isCollidingWith(pacman.getPosition())) {
                handlePacmanCollision(ghost, pacman);
            }
        }
    }

    // update the timers dor mode switching and frightned mode
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
                switchChaseSctterMode();
            }
        }
    }


    // switch between chase and scatter modes
    private void switchChaseSctterMode() {
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

    // activate frightned mode for all ghosts
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

    /**
     * Handle collision between pacman and a ghost
     *
     * @param ghost The ghost that collided
     * @param pacman The pacman instance
     * @return true if pacman died, false if ghost was eaten
     */
    // handle collision between pacamn and ghost, true if pacman died, false if ghots was eaten
    private boolean handlePacmanCollision(Ghost ghost, Pacman pacman) {
        if (ghost.getBehavior() == Constants.FRIGHTENED) {
            // ghost is eaten
            ghost.collideWithPacman();
            return false;
        } else if (ghost.getBehavior() != Constants.EATEN) {
            // pacman is caught = game should handle this event
            return true;
        }
        return false;
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


    // reset all ghots to their starting positions
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
        for (Ghost ghost : ghosts) {
            ghost.render(g);
        }
    }


    // get the list of ghosts
    public List<Ghost> getGhosts() {
        return ghosts;
    }
}