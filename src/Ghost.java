// this class implements the ghost characters with different AI movement patterns

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;

public class Ghost {
    // ghost identification and their properties
    private int ghostType;       // which ghost is which(Blinky, Pinky, Clyde. etc.)
    private Vector position;     // current position on screen
    private Vector startPosition; // starting position to return to
    private int behavior;        // current behavior mode
    private int direction;
    private double speed;
    private int radius;
    private Color color;

    // node-based movement properties
    private Maze currentNode;    // current node the ghost is on
    private Maze targetNode;     // target node the ghost is moving toward
    private Maze scatterTarget;  // corner node to move to when in scatter mode

    // target in chase mode (usually Pacman or a position near Pacman)
    private Vector chaseTarget;
    private Random random;


    // creating new ghost with a specific behaviors
    public Ghost(int ghostType, Maze startNode, Maze scatterNode, Color ghostColor) {
        this.ghostType = ghostType;
        this.behavior = Constants.SCATTER; // ghost will always start in scatter mode, which is random

        // initialize position from the starting node
        this.currentNode = startNode;
        this.targetNode = startNode;
        this.position = startNode.getPosition().copy();
        this.startPosition = this.position.copy();
        this.scatterTarget = scatterNode;

        // set movement properties
        this.direction = Constants.STOP;
        this.speed = 80 * Constants.TILE_WIDTH/16; // making them slower than Pacman
        this.radius = 8;
        this.color = ghostColor;

        // initialize random number generator
        this.random = new Random();

        // initialize chase target
        this.chaseTarget = new Vector(0, 0);
    }


    // updates the ghost's position and behavior, dt represents the time since last update
    public void update(double dt, Pacman pacman) {
        // update chase target based on Pacman's position
        updateChaseTarget(pacman);

        // handle movement based on current behavior
        switch (behavior) {
            case Constants.CHASE:
                moveTowards(dt, pacman.getPosition());
                break;

            case Constants.SCATTER:
                moveTowards(dt, scatterTarget.getPosition());
                break;

            case Constants.FRIGHTENED:
                moveRandomly(dt);
                break;

            case Constants.EATEN:
                moveTowards(dt, startPosition);
                // check if we've reached the start position to respawn
                if (position.subtract(startPosition).magnitudeSquared() < 4) {
                    respawn();
                }
                break;
        }
    }

    // upadte the chase target based on the ghost type (differnt targtes for each ghost)
    private void updateChaseTarget(Pacman pacman) {
        Vector pacmanPos = pacman.getPosition();
        Vector pacmanDir = pacman.getDirection();

        // different targeting based on ghost type
        switch (ghostType) {
            case 0: // Blinky (red) - targets Pacman directly
                chaseTarget = pacmanPos;
                break;

            case 1: // Pinky (pink) - targets 4 tiles ahead of Pacman
                chaseTarget = pacmanPos.add(pacmanDir.multiply(4 * Constants.TILE_WIDTH));
                break;

            case 2: // Inky (cyan) - complex targeting involving blinky's position
                // simple for now - targets 2 tiles ahead of Pacman
                chaseTarget = pacmanPos.add(pacmanDir.multiply(2 * Constants.TILE_WIDTH));
                break;

            case 3: // Clyde (orange) - targets Pacman unless too close, then scatters
                double distance = pacmanPos.subtract(position).magnitude();
                if (distance > 8 * Constants.TILE_WIDTH) {
                    chaseTarget = pacmanPos;
                } else {
                    chaseTarget = scatterTarget.getPosition();
                }
                break;
        }
    }


    // move torwards a specifc target position using the node-based movements
    private void moveTowards(double dt, Vector target) {
        // calculate movement vector based on current direction
        Vector movement = getDirectionVector(direction);
        movement = movement.multiply(speed * dt);

        // update position
        position = position.add(movement);

        // check if we've reached or overshot the target node
        if (hasReachedOrOvershotNode()) {
            // reached a node take a new directon
            currentNode = targetNode;
            position = currentNode.getPosition().copy();

            // choose next direction based on target
            direction = chooseNextDirection(target);

            // set new target node based on chosen direction
            targetNode = getNextNode(direction);
        }
    }


    // move in a random direction in frighten mode (pacman power up)
    private void moveRandomly(double dt) {
        Vector movement = getDirectionVector(direction);
        movement = movement.multiply(speed * 0.5 * dt); // move slower when frightened

        // update position
        position = position.add(movement);

        // check if we've reached or overshot the target node
        if (hasReachedOrOvershotNode()) {
            currentNode = targetNode;
            position = currentNode.getPosition().copy();

            // choose next direction randomly
            direction = chooseRandomDirection();

            // set new target node based on chosen direction
            targetNode = getNextNode(direction);
        }
    }

    // return true if reached or overshot the target node, OW false
    private boolean hasReachedOrOvershotNode() {
        if (targetNode != null) {
            Vector nodeToTarget = targetNode.getPosition().subtract(currentNode.getPosition());
            Vector nodeToSelf = position.subtract(currentNode.getPosition());

            double node2Target = nodeToTarget.magnitudeSquared();
            double node2Self = nodeToSelf.magnitudeSquared();

            return node2Self >= node2Target;
        }
        return false;
    }

    // choose the next direction based on target position, AI path-finding algorithm
    private int chooseNextDirection(Vector target) {
        // get all available directions, or nodes that are connected to current node
        List<Integer> availableDirections = getAvailableDirections();

        // remove opposite direction to prevent 180-degree turns
        int oppositeDir = -direction;
        availableDirections.removeIf(dir -> dir == oppositeDir);

        // if no valid directions, reverse
        if (availableDirections.isEmpty()) {
            availableDirections.add(oppositeDir);
        }

        // find direction that gets closest to target
        int bestDirection = Constants.STOP;
        double bestDistance = Double.MAX_VALUE;

        for (int dir : availableDirections) {
            Maze nextNode = getNextNode(dir);
            if (nextNode != null) {
                double distance = nextNode.getPosition().subtract(target).magnitudeSquared();
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestDirection = dir;
                }
            }
        }

        return bestDirection == Constants.STOP ? chooseRandomDirection() : bestDirection;
    }


    // choose a random direction to move
    private int chooseRandomDirection() {
        List<Integer> availableDirections = getAvailableDirections();

        // remove opposite direction
        int oppositeDir = -direction;
        availableDirections.removeIf(dir -> dir == oppositeDir);

        // revsersing
        if (availableDirections.isEmpty()) {
            availableDirections.add(oppositeDir);
        }

        // random direction if available
        return availableDirections.get(random.nextInt(availableDirections.size()));
    }

    // creating a list of all available directions from the current node
    private List<Integer> getAvailableDirections() {
        java.util.ArrayList<Integer> directions = new java.util.ArrayList<>();

        // check each direction for connected nodes
        if (currentNode.getNeighbor(Constants.UP) != null) {
            directions.add(Constants.UP);
        }
        if (currentNode.getNeighbor(Constants.DOWN) != null) {
            directions.add(Constants.DOWN);
        }
        if (currentNode.getNeighbor(Constants.LEFT) != null) {
            directions.add(Constants.LEFT);
        }
        if (currentNode.getNeighbor(Constants.RIGHT) != null) {
            directions.add(Constants.RIGHT);
        }

        return directions;
    }


    private Maze getNextNode(int direction) {
        return currentNode.getNeighbor(direction);
    }


    private Vector getDirectionVector(int direction) {
        switch (direction) {
            case Constants.UP:
                return new Vector(0, -1);
            case Constants.DOWN:
                return new Vector(0, 1);
            case Constants.LEFT:
                return new Vector(-1, 0);
            case Constants.RIGHT:
                return new Vector(1, 0);
            default:
                return new Vector(0, 0);
        }
    }

    // frightened mode
    public void frighten() {
        if (behavior != Constants.EATEN) {
            behavior = Constants.FRIGHTENED;
            // reverse direction when frightened
            direction *= -1;
            if (targetNode != null) {
                Maze temp = currentNode;
                currentNode = targetNode;
                targetNode = temp;
            }
        }
    }


    // collision handling, return true if pacamn dies, false if ghost is eaten
    public boolean collideWithPacman() {
        if (behavior == Constants.FRIGHTENED) {
            // ghost is eaten by pacman
            behavior = Constants.EATEN;
            return false;
        } else {
            // pacman is caught by ghost
            return true;
        }
    }

    // respawn ghost if eaten
    public void respawn() {
        position = startPosition.copy();
        currentNode = targetNode; // reset node tracking
        behavior = Constants.SCATTER;      // start in scatter mode again
        direction = Constants.STOP;
    }

    // switch between scatter and chase mode depending on the situation
    public void switchMode(int mode) {
        if (behavior != Constants.FRIGHTENED && behavior != Constants.EATEN) {
            behavior = mode;
            // reverse direction when switching modes
            direction *= -1;
            if (targetNode != null) {
                Maze temp = currentNode;
                currentNode = targetNode;
                targetNode = temp;
            }
        }
    }

    // check if this ghost is colliding with pacman, true if so, OW false
    public boolean isCollidingWith(Vector pacmanPos) {
        double distanceSquared = position.subtract(pacmanPos).magnitudeSquared();
        double collisionRadius = (radius + 10) * (radius + 10); // 10 is pacman's radius
        return distanceSquared < collisionRadius;
    }

    // render ghost on the screen

    public void render(Graphics g) {
        // draw the ghost with appropriate color based on the game mode
        Color renderColor;

        switch (behavior) {
            case Constants.FRIGHTENED:
                renderColor = Color.BLUE;
                break;
            case Constants.EATEN:
                renderColor = Color.WHITE;
                break;
            default:
                renderColor = color;
                break;
        }

        // drawing the ghost body
        int[] p = this.position.asInt();
        g.setColor(renderColor);
        g.fillOval(p[0] - radius, p[1] - radius, radius * 2, radius * 2);

        // draw eyes
        g.setColor(Color.WHITE);
        int eyeSize = radius / 2;
        g.fillOval(p[0] - radius/2, p[1] - radius/3, eyeSize, eyeSize);
        g.fillOval(p[0] + radius/2 - eyeSize, p[1] - radius/3, eyeSize, eyeSize);

        // draw pupils based on direction
        g.setColor(Color.BLACK);
        int pupilSize = eyeSize / 2;

        int[] pupilX = {0, 0, -1, 1}; // UP, DOWN, LEFT, RIGHT
        int[] pupilY = {-1, 1, 0, 0}; // UP, DOWN, LEFT, RIGHT

        int dirIndex = 0;
        switch (direction) {
            case Constants.UP:
                dirIndex = 0;
                break;
            case Constants.DOWN:
                dirIndex = 1;
                break;
            case Constants.LEFT:
                dirIndex = 2;
                break;
            case Constants.RIGHT:
                dirIndex = 3;
                break;
        }

        g.fillOval(p[0] - radius/2 + pupilX[dirIndex] * 2,
                p[1] - radius/3 + pupilY[dirIndex] * 2,
                pupilSize, pupilSize);
        g.fillOval(p[0] + radius/2 - eyeSize + pupilX[dirIndex] * 2,
                p[1] - radius/3 + pupilY[dirIndex] * 2,
                pupilSize, pupilSize);
    }
    // getters and setters

    public Vector getPosition() {
        return position;
    }

    public int getBehavior() {
        return behavior;
    }

    public int getGhostType() {
        return ghostType;
    }


}