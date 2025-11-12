package com.example.proyecto3_.model.Exceptions;

/**
 * Checked exception thrown when a player has no valid cards to play
 */
public class NoValidCardException extends Exception {

    private String playerName;

    /**
     * Creates a new NoValidCardException
     * @param playerName the name of the player
     */
    public NoValidCardException(String playerName) {
        super("Player " + playerName + " has no valid cards to play");
        this.playerName = playerName;
    }

    /**
     * Gets the player name
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }
}