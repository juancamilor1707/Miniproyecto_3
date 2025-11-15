package com.example.proyecto3_.model.Player;

import com.example.proyecto3_.model.Cards.Card;
import java.util.List;

/**
 * Represents the base contract for all player types.
 */
public interface IPlayer {

    /**
     * Gets the player's name.
     * @return player name
     */
    String getName();

    /**
     * Checks if player is a machine.
     * @return true if machine, false if human
     */
    boolean isMachine();

    /**
     * Gets the player's hand.
     * @return list of cards in hand
     */
    List<Card> getHand();

    /**
     * Adds a card to the player's hand.
     * @param card the card to add
     */
    void addCard(Card card);

    /**
     * Removes a card from the player's hand.
     * @param card the card to remove
     * @return true if removed, false otherwise
     */
    boolean removeCard(Card card);

    /**
     * Checks if player is eliminated.
     * @return true if eliminated, false otherwise
     */
    boolean isEliminated();

    /**
     * Sets the player as eliminated.
     */
    void eliminate();

    /**
     * Checks if player can play any card.
     * @param currentSum the current sum on the table
     * @return true if can play, false otherwise
     */
    boolean canPlay(int currentSum);

    /**
     * Selects a valid card to play (for machine).
     * @param currentSum the current sum on the table
     * @return a valid card, or null if none available
     */
    Card selectCard(int currentSum);

    /**
     * Gets all cards from hand and clears it.
     * @return list of all cards that were in hand
     */
    List<Card> clearHand();
}
