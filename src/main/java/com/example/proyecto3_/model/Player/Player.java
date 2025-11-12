package com.example.proyecto3_.model.Player;

import com.example.proyecto3_.model.Cards.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game
 */
public class Player {
    private String name;
    private List<Card> hand;
    private boolean isEliminated;
    private boolean isMachine;

    /**
     * Creates a new player
     * @param name the player's name
     * @param isMachine true if machine player, false if human
     */
    public Player(String name, boolean isMachine) {
        this.name = name;
        this.isMachine = isMachine;
        this.hand = new ArrayList<>();
        this.isEliminated = false;
    }

    /**
     * Gets the player's name
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if player is a machine
     * @return true if machine, false if human
     */
    public boolean isMachine() {
        return isMachine;
    }

    /**
     * Gets the player's hand
     * @return list of cards in hand
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Adds a card to the player's hand
     * @param card the card to add
     */
    public void addCard(Card card) {
        hand.add(card);
        if (!isMachine) {
            card.setFaceUp(true); // Human cards are face up
        }
    }

    /**
     * Removes a card from the player's hand
     * @param card the card to remove
     * @return true if removed, false otherwise
     */
    public boolean removeCard(Card card) {
        return hand.remove(card);
    }

    /**
     * Checks if player is eliminated
     * @return true if eliminated, false otherwise
     */
    public boolean isEliminated() {
        return isEliminated;
    }

    /**
     * Sets the player as eliminated
     */
    public void eliminate() {
        this.isEliminated = true;
    }

    /**
     * Checks if player can play any card
     * @param currentSum the current sum on the table
     * @return true if can play, false otherwise
     */
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
     * Selects a valid card to play (for machine)
     * @param currentSum the current sum on the table
     * @return a valid card, or null if none available
     */
    public Card selectCard(int currentSum) {
        if (!isMachine) {
            return null; // Human selects manually
        }

        // Machine logic: select first valid card
        for (Card card : hand) {
            int newSum = currentSum + card.getValue(currentSum);
            if (newSum <= 50) {
                return card;
            }
        }
        return null;
    }

    /**
     * Gets all cards from hand and clears it
     * @return list of all cards that were in hand
     */
    public List<Card> clearHand() {
        List<Card> cards = new ArrayList<>(hand);
        hand.clear();
        return cards;
    }
}