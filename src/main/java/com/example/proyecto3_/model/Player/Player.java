package com.example.proyecto3_.model.Player;

import com.example.proyecto3_.model.Cards.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game.
 */
public class Player extends AbstractPlayer {

    /**
     * Creates a new player.
     * @param name the player's name
     * @param isMachine true if machine player, false if human
     */
    public Player(String name, boolean isMachine) {
        super(name, isMachine);
    }

    /**
     * Gets the player's name.
     * @return player name
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Checks if player is a machine.
     * @return true if machine, false if human
     */
    @Override
    public boolean isMachine() {
        return super.isMachine();
    }

    /**
     * Gets the player's hand.
     * @return list of cards in hand
     */
    @Override
    public List<Card> getHand() {
        return super.getHand();
    }

    /**
     * Adds a card to the player's hand.
     * @param card the card to add
     */
    @Override
    public void addCard(Card card) {
        super.addCard(card);
    }

    /**
     * Removes a card from the player's hand.
     * @param card the card to remove
     * @return true if removed, false otherwise
     */
    @Override
    public boolean removeCard(Card card) {
        return super.removeCard(card);
    }

    /**
     * Checks if player is eliminated.
     * @return true if eliminated, false otherwise
     */
    @Override
    public boolean isEliminated() {
        return super.isEliminated();
    }

    /**
     * Sets the player as eliminated.
     */
    @Override
    public void eliminate() {
        super.eliminate();
    }

    /**
     * Checks if player can play any card.
     * @param currentSum the current sum on the table
     * @return true if can play, false otherwise
     */
    @Override
    public boolean canPlay(int currentSum) {
        for (Card card : hand) {
            int newSum = currentSum + card.getValue(currentSum);
            if (newSum <= 50) {
                return true;
            }
        }
        return false;
    }

    /**
     * Selects a valid card to play (for machine).
     * @param currentSum the current sum on the table
     * @return a valid card, or null if none available
     */
    @Override
    public Card selectCard(int currentSum) {
        if (!isMachine) {
            return null; // Human selects manually
        }

        for (Card card : hand) {
            int newSum = currentSum + card.getValue(currentSum);
            if (newSum <= 50) {
                return card;
            }
        }
        return null;
    }

    /**
     * Gets all cards from hand and clears it.
     * @return list of all cards that were in hand
     */
    @Override
    public List<Card> clearHand() {
        return super.clearHand();
    }
}
