// This class is a place where all of our constant values relating to the maze layout will be stored
// I figured it might be easier to have them all in one place, in the event we need to modify something.

public class Constants {
    // tile dimensions
    public static final int TILE_WIDTH = 24;
    public static final int TILE_HEIGHT = 24;
    //public static final int NROWS = 36;
    //public static final int NCOLS = 28;

    // screen dimensions
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 700;

    // colors
    public static final int[] YELLOW = {255, 255, 0};
    public static final int[] WHITE = {255, 255, 255}; // color of the nodes
    public static final int[] RED = {255, 0, 0};    // color of the paths between nodes

    // movements
    public static final int STOP = 0;
    public static final int UP = 1;
    public static final int DOWN = -1;
    public static final int LEFT = 2;
    public static final int RIGHT = -2;

    // character types
    public static final int PACMAN = 0;
    private static final int GHOST = 1;

    // game state constants
    public static final int START = 0;
    public static final int PLAYING = 1;
    public static final int PAUSED = 2;
    public static final int GAME_OVER = 3;
    public static final int DEATH_ANIMATION = 4;

    // ghost behavior
    public static final int CHASE = 0;    // chase Pacman directly
    public static final int SCATTER = 1;  // return to home corner
    public static final int FRIGHTENED = 2; // run away from Pacman
    public static final int EATEN = 3;    // return to ghost house when eaten

    // node types from maze files
    public static final char WALL = 'X';
    public static final char NODE = '+';
    public static final char PATH = '.';
    public static final char EMPTY = ' ';
    public static final char GHOST_HOUSE = '=';

    // game score values (you can change these alex)
    public static final int DOT_SCORE = 10;
    public static final int POWER_PELLET_SCORE = 50;
    public static final int GHOST_SCORE = 200;





}