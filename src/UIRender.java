// this class handles rendering of UI elements like score, lives, and game state transitions
// start screen, pause screen, game over screen

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Rectangle;

public class UIRender {
    // reference to game state
    private GameState gameState;

    // fonts for different UI elements
    private Font titleFont;
    private Font normalFont;
    private Font smallFont;

    // colors for UI elements
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color MENU_BG_COLOR = new Color(0, 0, 0, 200); 
    private final Color HIGHLIGHT_COLOR = Color.YELLOW;
    private final Color GHOST_INFO_COLOR = new Color(0, 200, 255);

    // screen dimensions from Constants
    private final int SCREEN_WIDTH = Constants.SCREEN_WIDTH;
    private final int SCREEN_HEIGHT = Constants.SCREEN_HEIGHT;

    // animation properties for certain UI elements
    private double animationTime = 0;
    private boolean blinkOn = true;
    
    public UIRender(GameState gameState) {
        this.gameState = gameState;

        // initialize fonts
        this.titleFont = new Font("Arial", Font.BOLD, 36);
        this.normalFont = new Font("Arial", Font.BOLD, 18);
        this.smallFont = new Font("Arial", Font.PLAIN, 14);
    }
    
    // render all UI elements based on current game state
    public void render(Graphics g) {
        // Always render score and lives during gameplay
        if (gameState.getCurrentState() != Constants.START) {
            renderGameInfo(g);
        }

        // render specific screens based on game state
        switch (gameState.getCurrentState()) {
            case Constants.START:
                renderStartScreen(g);
                break;

            case Constants.PAUSED:
                renderPauseScreen(g);
                break;

            case Constants.GAME_OVER:
                renderGameOverScreen(g);
                break;
        }
    }
    
    // update animation timers for UI elements
    public void update(double dt) {
        // update animation timer
        animationTime += dt;

        // toggle blink state every 0.5 seconds
        if (animationTime >= 0.5) {
            animationTime = 0;
            blinkOn = !blinkOn;
        }
    }
    
    // render game information (score, lives, levels)
    private void renderGameInfo(Graphics g) {
        // draw the top info bar 
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, 70);

        g.setColor(TEXT_COLOR);
        g.setFont(normalFont);

        // score
        g.drawString("SCORE: " + gameState.getScore(), 10, 25);

        // lives
        g.drawString("LIVES: " + gameState.getLives(), 10, 50);

        // level 
        String levelText = "LEVEL: " + gameState.getLevel();
        FontMetrics fm = g.getFontMetrics();
        int levelTextWidth = fm.stringWidth(levelText);
        g.drawString(levelText, SCREEN_WIDTH - levelTextWidth - 10, 25);

        // draw Pacman icons for lives
        int pacmanSize = 12;
        for (int i = 0; i < gameState.getLives(); i++) {
            g.setColor(Color.YELLOW);
            g.fillOval(200 + i * (pacmanSize + 5), 40, pacmanSize, pacmanSize);
        }
    }


    // render the start screen
    private void renderStartScreen(Graphics g) {
        // draw background
        g.setColor(MENU_BG_COLOR);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw title
        g.setFont(titleFont);
        g.setColor(HIGHLIGHT_COLOR);
        String title = "PAC-MAN";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, (SCREEN_WIDTH - titleWidth) / 2, SCREEN_HEIGHT / 4);

        // draw ghost descriptions
        g.setFont(smallFont);
        g.setColor(GHOST_INFO_COLOR);
        String[] ghostInfo = {
                "Red Ghost (Blinky) - Chases you directly",
                "Pink Ghost (Pinky) - Tries to ambush you",
                "Cyan Ghost (Inky) - Unpredictable movement",
                "Orange Ghost (Clyde) - Sometimes shy, sometimes not"
        };

        int y = SCREEN_HEIGHT / 2 - 50;
        for (String info : ghostInfo) {
            g.drawString(info, SCREEN_WIDTH / 4, y);
            y += 25;
        }

        // draw instructions
        g.setFont(normalFont);
        g.setColor(TEXT_COLOR);
        String[] instructions = {
                "Use Arrow Keys to move",
                "Eat all dots to advance levels",
                "Avoid ghosts unless they're blue"
        };

        y = SCREEN_HEIGHT / 2 + 50;
        for (String instruction : instructions) {
            g.drawString(instruction, SCREEN_WIDTH / 4, y);
            y += 30;
        }

        // draw start prompt - blinking
        if (blinkOn) {
            g.setFont(normalFont);
            g.setColor(HIGHLIGHT_COLOR);
            String startPrompt = "PRESS SPACE TO START";
            FontMetrics fm = g.getFontMetrics();
            int promptWidth = fm.stringWidth(startPrompt);
            g.drawString(startPrompt, (SCREEN_WIDTH - promptWidth) / 2, SCREEN_HEIGHT * 3 / 4 + 30);
        }

        // draw pacman logo
        g.setColor(Color.YELLOW);
        g.fillArc((SCREEN_WIDTH - 80) / 2, SCREEN_HEIGHT * 3 / 4 - 50, 80, 80, 30, 300);
    }

    // render pause screen
    private void renderPauseScreen(Graphics g) {
        // draw overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw pause title
        g.setFont(titleFont);
        g.setColor(HIGHLIGHT_COLOR);
        String title = "GAME PAUSED";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g.drawString(title, (SCREEN_WIDTH - titleWidth) / 2, SCREEN_HEIGHT / 3);

        // draw menu options
        g.setFont(normalFont);
        g.setColor(TEXT_COLOR);
        String[] menuOptions = {
                "PRESS SPACE TO RESUME",
                "PRESS ESC TO QUIT"
        };

        int y = SCREEN_HEIGHT / 2;
        for (String option : menuOptions) {
            FontMetrics fm = g.getFontMetrics();
            int optionWidth = fm.stringWidth(option);
            g.drawString(option, (SCREEN_WIDTH - optionWidth) / 2, y);
            y += 40;
        }

        // draw current score
        g.setFont(normalFont);
        g.setColor(HIGHLIGHT_COLOR);
        String scoreText = "CURRENT SCORE: " + gameState.getScore();
        FontMetrics fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        g.drawString(scoreText, (SCREEN_WIDTH - scoreWidth) / 2, SCREEN_HEIGHT * 3 / 4);
    }

    // render the game over screen
    private void renderGameOverScreen(Graphics g) {
        // draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // draw game over message
        g.setFont(titleFont);
        g.setColor(Color.RED);
        String gameOver = "GAME OVER";
        FontMetrics fmGameOver = g.getFontMetrics();
        int gameOverWidth = fmGameOver.stringWidth(gameOver);
        g.drawString(gameOver, (SCREEN_WIDTH - gameOverWidth) / 2, SCREEN_HEIGHT / 3);

        // draw final score
        g.setFont(normalFont);
        g.setColor(HIGHLIGHT_COLOR);
        String finalScore = "FINAL SCORE: " + gameState.getScore();
        FontMetrics fmScore = g.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(finalScore);
        g.drawString(finalScore, (SCREEN_WIDTH - scoreWidth) / 2, SCREEN_HEIGHT / 2);

        // draw level reached
        String levelText = "LEVELS COMPLETED: " + (gameState.getLevel() - 1);
        int levelWidth = fmScore.stringWidth(levelText);
        g.drawString(levelText, (SCREEN_WIDTH - levelWidth) / 2, SCREEN_HEIGHT / 2 + 30);

        // draw restart prompt - blinking
        if (blinkOn) {
            g.setColor(Color.WHITE);
            String restartPrompt = "PRESS SPACE TO PLAY AGAIN";
            FontMetrics fmPrompt = g.getFontMetrics();
            int promptWidth = fmPrompt.stringWidth(restartPrompt);
            g.drawString(restartPrompt, (SCREEN_WIDTH - promptWidth) / 2, SCREEN_HEIGHT * 3 / 4);
        }

        // draw "ghost graveyard" decoration
        drawGhostGraveyard(g);
    }

    // decorative ghost graveyard on game over screen
    private void drawGhostGraveyard(Graphics g) {
        // draw some  RIP signs
        drawGhostWithTombstone(g, SCREEN_WIDTH / 4 - 30, SCREEN_HEIGHT * 2 / 3, Color.RED);
        drawGhostWithTombstone(g, SCREEN_WIDTH / 2 - 30, SCREEN_HEIGHT * 2 / 3 + 20, Color.PINK);
        drawGhostWithTombstone(g, SCREEN_WIDTH * 3 / 4 - 30, SCREEN_HEIGHT * 2 / 3, new Color(0, 255, 255));
    }

    // tombstone
    private void drawGhostWithTombstone(Graphics g, int x, int y, Color ghostColor) {
        // draw tombstone
        g.setColor(Color.GRAY);
        g.fillRect(x, y, 25, 30);
        g.fillArc(x, y - 10, 25, 20, 0, 180);

        // draw RIP text
        g.setColor(Color.BLACK);
        g.drawString("RIP", x + 5, y + 20);

        // draw ghost peeking from behind
        g.setColor(new Color(ghostColor.getRed(), ghostColor.getGreen(), ghostColor.getBlue(), 150));
        g.fillOval(x + 20, y + 10, 20, 20);
        g.fillRect(x + 20, y + 20, 20, 15);
    }

    // draw buttone with text
    private
    Rectangle drawButton(Graphics g, String text, int x, int y, int width, int height, boolean isActive) {
        // draw button background
        g.setColor(isActive ? HIGHLIGHT_COLOR : TEXT_COLOR);
        g.fillRoundRect(x, y, width, height, 10, 10);

        // draw button border
        g.setColor(isActive ? TEXT_COLOR : HIGHLIGHT_COLOR);
        g.drawRoundRect(x, y, width, height, 10, 10);

        // draw button text
        g.setFont(normalFont);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, x + (width - textWidth) / 2, y + height / 2 + textHeight / 4);

        return new Rectangle(x, y, width, height);
    }
}