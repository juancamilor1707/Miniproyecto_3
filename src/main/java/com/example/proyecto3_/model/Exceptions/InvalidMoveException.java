package com.example.proyecto3_.model.Exceptions;

/**
 * Checked exception thrown when a player tries to make an invalid move
 */
public class InvalidMoveException extends Exception {

    /**
     * Creates a new InvalidMoveException
     * @param message the error message
     */
    public InvalidMoveException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidMoveException with cause
     * @param message the error message
     * @param cause the cause of the exception
     */
    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}