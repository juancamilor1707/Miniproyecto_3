package com.example.proyecto3_.model.Game;

/**
 * Singleton class to store game configuration across views
 */
public class GameConfig {

    private static class Holder {
        private static final GameConfig INSTANCE = new GameConfig();
    }

    private int numBots;

    private GameConfig() {
        this.numBots = 1; // Default value
    }

    /**
     * Gets the singleton instance
     * @return the unique GameConfig instance
     */
    public static GameConfig getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Sets the number of bots for the game
     * @param numBots number of machine players (1-3)
     */
    public void setNumBots(int numBots) {
        if (numBots < 1 || numBots > 3) {
            throw new IllegalArgumentException("Number of bots must be between 1 and 3");
        }
        this.numBots = numBots;
    }

    /**
     * Gets the number of bots configured
     * @return number of machine players
     */
    public int getNumBots() {
        return numBots;
    }
}