package com.example.proyecto3_.model.Cards;

/**
 * Represents a standard playing card in the game.
 */
public class Card extends AbstractCard {

    /**
     * Creates a new card.
     * @param rank the rank (2-10, J, Q, K, A)
     * @param suit the suit (Hearts, Diamonds, Clubs, Spades)
     */
    public Card(String rank, String suit) {
        super(rank, suit);
    }

    /**
     * Gets the rank of the card.
     * @return the rank (2-10, J, Q, K, A)
     */
    @Override
    public String getRank() {
        return super.getRank();
    }

    /**
     * Gets the suit of the card.
     * @return the suit (Hearts, Diamonds, Clubs, Spades)
     */
    @Override
    public String getSuit() {
        return super.getSuit();
    }

    /**
     * Sets whether the card is face up.
     * @param faceUp true if face up, false if face down
     */
    @Override
    public void setFaceUp(boolean faceUp) {
        super.setFaceUp(faceUp);
    }

    /**
     * Checks if the card is face up.
     * @return true if face up
     */
    @Override
    public boolean isFaceUp() {
        return super.isFaceUp();
    }

    /**
     * Gets the value of the card based on current table sum.
     * @param currentSum the current sum on the table
     * @return the card's value
     */
    @Override
    public int getValue(int currentSum) {
        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);
            case "9":
                return 0;
            case "J": case "Q": case "K":
                return -10;
            case "A":
                // Choose 1 or 10 based on what keeps sum under 50
                return (currentSum + 10 <= 50) ? 10 : 1;
            default:
                return 0;
        }
    }

    /**
     * Returns a string representation of the card.
     * @return rank and suit as a string
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
