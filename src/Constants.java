// This class is a place where all of our constant values relating to the maze layout will be stored
// I figured it might be easier to have them all in one place, in the event we need to modify something.

public class Constants {
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    public static final int NROWS = 36;
    public static final int NCOLS = 28;
    public static final int SCREEN_WIDTH = NCOLS * TILE_WIDTH;
    public static final int SCREEN_HEIGHT = NROWS * TILE_HEIGHT;

    public static final int[] YELLOW = {255, 255, 0};

    public static final int STOP = 0;
    public static final int UP = 1;
    public static final int DOWN = -1;
    public static final int LEFT = 2;
    public static final int RIGHT = -2;

    public static final int PACMAN = 0;
}