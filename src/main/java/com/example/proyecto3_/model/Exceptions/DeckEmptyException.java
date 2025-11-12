package com.example.proyecto3_.model.Exceptions;

/**
 * Unchecked exception thrown when trying to draw from an empty deck
 */
public class DeckEmptyException extends RuntimeException {

    /**
     * Creates a new DeckEmptyException
     */
    public DeckEmptyException() {
        super("Cannot draw card: deck is empty");
    }

    /**
     * Creates a new DeckEmptyException with message
     * @param message the error message
     */
    public DeckEmptyException(String message) {
        super(message);
    }
}