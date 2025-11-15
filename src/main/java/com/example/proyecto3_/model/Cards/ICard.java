package com.example.proyecto3_.model.Cards;

/**
 * Represents the base contract for all card types.
 */
public interface ICard {

    /**
     * Gets the rank of the card.
     * @return the rank (2-10, J, Q, K, A)
     */
    String getRank();

    /**
     * Gets the suit of the card.
     * @return the suit (Hearts, Diamonds, Clubs, Spades)
     */
    String getSuit();

    /**
     * Gets the card value based on the current table sum.
     * @param currentSum the current sum on the table
     * @return the card value
     */
    int getValue(int currentSum);

    /**
     * Sets whether the card is face up or face down.
     * @param faceUp true if face up, false if face down
     */
    void setFaceUp(boolean faceUp);

    /**
     * Checks if the card is face up.
     * @return true if the card is face up
     */
    boolean isFaceUp();

    /**
     * Returns a string representation of the card.
     * @return rank and suit as a string
     */
    String toString();
}

