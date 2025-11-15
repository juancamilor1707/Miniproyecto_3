package com.example.proyecto3_.model.Player;

import com.example.proyecto3_.model.Cards.Card;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract class that provides the basic implementation for players.
 * Implements IPlayer and defines shared attributes and logic.
 */
public abstract class AbstractPlayer implements IPlayer {

    protected String name;
    protected List<Card> hand;
    protected boolean isEliminated;
    protected boolean isMachine;

    /**
     * Creates a new abstract player.
     * @param name the player's name
     * @param isMachine true if machine player, false if human
     */
    public AbstractPlayer(String name, boolean isMachine) {
        this.name = name;
        this.isMachine = isMachine;
        this.hand = new ArrayList<>();
        this.isEliminated = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isMachine() {
        return isMachine;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void addCard(Card card) {
        hand.add(card);
        if (!isMachine) {
            card.setFaceUp(true); // Human cards are face up
        }
    }

    @Override
    public boolean removeCard(Card card) {
        return hand.remove(card);
    }

    @Override
    public boolean isEliminated() {
        return isEliminated;
    }

    @Override
    public void eliminate() {
        this.isEliminated = true;
    }

    @Override
    public List<Card> clearHand() {
        List<Card> cards = new ArrayList<>(hand);
        hand.clear();
        return cards;
    }

    /**
     * Inner class that allows iterating through the player's hand.
     */
    protected class HandIterator implements Iterator<Card> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < hand.size();
        }

        @Override
        public Card next() {
            return hand.get(index++);
        }
    }

    /**
     * Checks if player can play any card.
     * Must be implemented by subclasses.
     * @param currentSum the current sum on the table
     * @return true if can play, false otherwise
     */
    @Override
    public abstract boolean canPlay(int currentSum);

    /**
     * Selects a valid card to play (for machine).
     * Must be implemented by subclasses.
     * @param currentSum the current sum on the table
     * @return a valid card, or null if none available
     */
    @Override
    public abstract Card selectCard(int currentSum);
}

