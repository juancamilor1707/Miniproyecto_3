package com.example.proyecto3_.model.Cards;

/**
 * Abstract class that provides the basic implementation for a playing card.
 * Implements the ICard interface and defines common attributes.
 */
public abstract class AbstractCard implements ICard {

    protected String rank;
    protected String suit;
    protected boolean faceUp;

    /**
     * Creates a new abstract card.
     * @param rank the rank (2-10, J, Q, K, A)
     * @param suit the suit (Hearts, Diamonds, Clubs, Spades)
     */
    public AbstractCard(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = false;
    }

    @Override
    public String getRank() {
        return rank;
    }

    @Override
    public String getSuit() {
        return suit;
    }

    @Override
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    @Override
    public boolean isFaceUp() {
        return faceUp;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    /**
     * Gets the card value based on the current table sum.
     * Must be implemented by subclasses.
     * @param currentSum the current sum on the table
     * @return the card value
     */
    @Override
    public abstract int getValue(int currentSum);
}

