package com.example.proyecto3_.model.Cards;

/**
 * Represents a playing card in the game
 */
public class Card {
    private String rank; // "2", "3", ..., "10", "J", "Q", "K", "A"
    private String suit; // "Hearts", "Diamonds", "Clubs", "Spades"
    private boolean faceUp;

    /**
     * Creates a new card
     * @param rank the rank of the card (2-10, J, Q, K, A)
     * @param suit the suit of the card
     */
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = false;
    }

    /**
     * Gets the rank of the card
     * @return the card rank
     */
    public String getRank() {
        return rank;
    }

    /**
     * Gets the suit of the card
     * @return the card suit
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Checks if card is face up
     * @return true if face up, false otherwise
     */
    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Sets the card face up or down
     * @param faceUp true for face up, false for face down
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Calculates the value of the card for the game
     * @param currentSum the current sum on the table
     * @return the value to add/subtract
     */
    public int getValue(int currentSum) {
        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);

            case "9":
                return 0; // Neither adds nor subtracts

            case "J": case "Q": case "K":
                return -10; // Subtracts 10

            case "A":
                // A is 1 or 10, choose the best option
                if (currentSum + 10 <= 50) {
                    return 10;
                } else {
                    return 1;
                }

            default:
                return 0;
        }
    }

    /**
     * Gets the string representation of the card
     * @return card as string (e.g., "A♠")
     */
    @Override
    public String toString() {
        String suitSymbol = "";
        switch (suit) {
            case "Hearts": suitSymbol = "♥"; break;
            case "Diamonds": suitSymbol = "♦"; break;
            case "Clubs": suitSymbol = "♣"; break;
            case "Spades": suitSymbol = "♠"; break;
        }
        return rank + suitSymbol;
    }
}