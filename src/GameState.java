// this class will manage the current state of the game (START, PLAYING, PAUSED, GAME_OVER) and implement the transitions from each state

public class GameState {

    // current state of the game
    private int currentState;

    // game stats
    private int score;
    private int lives;
    private int level;

    // initialize the game to start
    public GameState() {
        this.currentState = Constants.START;
        resetStats();
    }

    // reset stats at the start of a new game
    public void resetStats() {
        this.score = 0;
        this.lives = 3;
        this.level = 1;
    }

    // start a new game
    public void startGame() {
        resetStats();
        this.currentState = Constants.PLAYING;
    }

    // pause + unpause the game
    public void togglePause() {
        if (currentState == Constants.PLAYING) {
            currentState = Constants.PAUSED;
        } else if (currentState == Constants.PAUSED) {
            currentState = Constants.PLAYING;
        }
    }

    // game over state
    public void gameOver() {
        this.currentState = Constants.GAME_OVER;
    }

    // set the death animation state
    public void setDeathAnimationState() {
        this.currentState = Constants.DEATH_ANIMATION;
    }

    // continue game after death (when space is pressed)
    public void continueAfterDeath() {
        if (currentState == Constants.DEATH_ANIMATION) {
            currentState = Constants.PLAYING;
        }
    }

    // handle pacman death, minus a life or trigger game over
    // return true if game continues, false if game over
    public boolean pacmanDeath() {
        lives--;
        if (lives <= 0) {
            gameOver();
            return false;
        }
        // Set death animation state
        setDeathAnimationState();
        return true;
    }


    // add points to the score
    public void addScore(int points) {
        this.score += points;

        // extra life if you get over 10,000 points
        if (score % 10000 < points) {
            lives++;
        }
    }


    // complete the current level and go to the next one
    public void completeLevel() {
        this.level++;
    }

    // getters for game state

    public int getCurrentState() {
        return currentState;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getLevel() {
        return level;
    }

    // return true if game is in play, OW false
    public boolean isPlaying() {
        return currentState == Constants.PLAYING;
    }

    // return true if game is paused, OW false
    public boolean isPaused() {
        return currentState == Constants.PAUSED;
    }

    // return true if in death animation, OW false
    public boolean inDeathAnimation() {
        return currentState == Constants.DEATH_ANIMATION;
    }

    public void restartGame() {
        resetStats();
        currentState = Constants.START; // i might try PLAYING if this doesn't work
    }

}