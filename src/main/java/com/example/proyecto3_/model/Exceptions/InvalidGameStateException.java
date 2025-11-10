package com.example.proyecto3_.model.Exceptions;

/**
 * Unchecked exception thrown when the game is in an invalid state
 */
public class InvalidGameStateException extends RuntimeException {

    /**
     * Creates a new InvalidGameStateException
     * @param message the error message
     */
    public InvalidGameStateException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidGameStateException with cause
     * @param message the error message
     * @param cause the cause of the exception
     */
    public InvalidGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}