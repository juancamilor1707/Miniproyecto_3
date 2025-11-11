package com.example.proyecto3_.model.Cards;

public class Card {
    private String rank;
    private String suit;
    private boolean faceUp;

    /**
     * Creates a new card
     * @param rank the rank (2-10, J, Q, K, A)
     * @param suit the suit (Hearts, Diamonds, Clubs, Spades)
     */
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = false;
    }

    /**
     * Gets the value of the card based on current table sum
     * @param currentSum the current sum on the table
     * @return the card's value
     */
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
     * Sets whether the card is face up
     * @param faceUp true if face up, false if face down
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Checks if card is face up
     * @return true if face up
     */
    public boolean isFaceUp() {
        return faceUp;
    }

    // ========================================
    // ESTOS SON LOS QUE NECESITAS AGREGAR:
    // ========================================

    /**
     * Gets the rank of the card
     * @return the rank (2-10, J, Q, K, A)
     */
    public String getRank() {
        return rank;
    }

    /**
     * Gets the suit of the card
     * @return the suit (Hearts, Diamonds, Clubs, Spades)
     */
    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}