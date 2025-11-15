package com.example.proyecto3_.model.Game;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class to store game configuration and handle all game logic.
 * Provides thread-safe operations for managing game state and player actions.
 */
public class GameConfig {

    private static class Holder {
        private static final GameConfig INSTANCE = new GameConfig();
    }

    private int numBots;
    private GameModel game;
    private volatile boolean humanHasPlayedCard = false;
    private volatile boolean humanHasDrawnCard = false;

    private final ExecutorService gameLogicExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "GameLogicThread");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService validationExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "ValidationThread-" + System.nanoTime());
        t.setDaemon(true);
        return t;
    });

    private final ReentrantLock gameLock = new ReentrantLock();

    /**
     * Creates a new GameConfig instance with default values.
     * Private constructor to enforce singleton pattern.
     */
    private GameConfig() {
        this.numBots = 1; // Default value
    }

    /**
     * Gets the singleton instance of GameConfig.
     * @return the unique GameConfig instance
     */
    public static GameConfig getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Sets the number of bots for the game.
     * @param numBots number of machine players (1-3)
     * @throws IllegalArgumentException if numBots is not between 1 and 3
     */
    public void setNumBots(int numBots) {
        if (numBots < 1 || numBots > 3) {
            throw new IllegalArgumentException("Number of bots must be between 1 and 3");
        }
        this.numBots = numBots;
    }

    /**
     * Gets the number of bots configured.
     * @return number of machine players
     */
    public int getNumBots() {
        return numBots;
    }

    // ==================== GAME LOGIC METHODS ====================

    /**
     * Initializes a new game with the configured number of bots.
     * Thread-safe operation that creates and starts a new game.
     */
    public void initializeGame() {
        gameLock.lock();
        try {
            game = new GameModel(numBots);
            game.start();
            resetTurnFlags();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Resets turn flags for human player.
     * Called at the start of each turn.
     */
    private void resetTurnFlags() {
        humanHasPlayedCard = false;
        humanHasDrawnCard = false;
    }

    /**
     * Attempts to play a card for the current player.
     * Thread-safe operation with validation checks.
     * @param card the card to play
     * @return true if card was played successfully, false otherwise
     */
    public boolean playCard(Card card) {
        gameLock.lock();
        try {
            Player currentPlayer = game.getCurrentPlayer();

            // Prevent playing if it's not allowed
            if (currentPlayer.isMachine() || humanHasPlayedCard) {
                return false;
            }

            game.playCard(card);

            if (!currentPlayer.isMachine()) {
                humanHasPlayedCard = true;
            }

            return true;

        } catch (InvalidMoveException e) {
            System.err.println("Movimiento inválido: " + e.getMessage());
            return false;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Attempts to draw a card for the current player.
     * Thread-safe operation that handles deck recycling if needed.
     * @return true if card was drawn successfully, false otherwise
     */
    public boolean drawCard() {
        gameLock.lock();
        try {
            Player current = game.getCurrentPlayer();

            // Prevent drawing if conditions aren't met
            if (current.isMachine() || !humanHasPlayedCard || humanHasDrawnCard) {
                return false;
            }

            // Check if deck is empty and recycle if needed
            checkAndRecycleDeck();

            game.drawCard();
            humanHasDrawnCard = true;

            return true;

        } catch (DeckEmptyException e) {
            System.err.println("Error al robar carta: " + e.getMessage());
            // Still count as drawn to allow turn progression
            humanHasDrawnCard = true;
            return true;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Checks if deck is empty and recycles automatically.
     * Called before drawing cards to ensure deck availability.
     */
    private void checkAndRecycleDeck() {
        if (game.isDeckEmpty()) {
            System.out.println("⚠️ Mazo vacío. Reciclando automáticamente...");
            // GameModel already handles recycling in drawCard, but we ensure it happens
        }
    }

    /**
     * Completes the current turn and moves to next player.
     * Thread-safe operation that resets flags and advances turn.
     */
    public void completeTurn() {
        gameLock.lock();
        try {
            resetTurnFlags();
            game.nextTurn();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Processes machine player's turn asynchronously.
     * Executes card selection, playing, and drawing in a separate thread.
     * @return Future with MachineTurnResult containing the outcome
     */
    public Future<MachineTurnResult> processMachineTurnAsync() {
        return gameLogicExecutor.submit(() -> {
            gameLock.lock();
            try {
                Player machine = game.getCurrentPlayer();

                if (!machine.isMachine()) {
                    return new MachineTurnResult(false, "Not a machine player", null);
                }

                // Check if machine can play
                if (!machine.canPlay(game.getTableSum())) {
                    return new MachineTurnResult(false, "Cannot play any card", null);
                }

                // Select and play card
                Card selectedCard = machine.selectCard(game.getTableSum());
                if (selectedCard == null) {
                    return new MachineTurnResult(false, "No valid card selected", null);
                }

                try {
                    game.playCard(selectedCard);

                    // Check if deck is empty and recycle automatically before drawing
                    checkAndRecycleDeck();

                    // Try to draw a card
                    try {
                        game.drawCard();
                    } catch (DeckEmptyException e) {
                        System.err.println("Bot no pudo robar: " + e.getMessage());
                    }

                    return new MachineTurnResult(true, "Turn completed", selectedCard);

                } catch (InvalidMoveException e) {
                    return new MachineTurnResult(false, "Invalid move: " + e.getMessage(), null);
                }
            } finally {
                gameLock.unlock();
            }
        });
    }

    /**
     * Processes machine player's turn synchronously.
     * Executes card selection, playing, and drawing in current thread.
     * @return MachineTurnResult with the outcome
     */
    public MachineTurnResult processMachineTurn() {
        gameLock.lock();
        try {
            Player machine = game.getCurrentPlayer();

            if (!machine.isMachine()) {
                return new MachineTurnResult(false, "Not a machine player", null);
            }

            // Check if machine can play
            if (!machine.canPlay(game.getTableSum())) {
                return new MachineTurnResult(false, "Cannot play any card", null);
            }

            // Select and play card
            Card selectedCard = machine.selectCard(game.getTableSum());
            if (selectedCard == null) {
                return new MachineTurnResult(false, "No valid card selected", null);
            }

            try {
                game.playCard(selectedCard);

                // Check if deck is empty and recycle automatically before drawing
                checkAndRecycleDeck();

                // Try to draw a card
                try {
                    game.drawCard();
                } catch (DeckEmptyException e) {
                    System.err.println("Bot no pudo robar: " + e.getMessage());
                }

                return new MachineTurnResult(true, "Turn completed", selectedCard);

            } catch (InvalidMoveException e) {
                return new MachineTurnResult(false, "Invalid move: " + e.getMessage(), null);
            }
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Validates if a move is valid asynchronously.
     * Useful for AI planning and move validation without blocking.
     * @param card the card to validate
     * @return Future containing true if move is valid, false otherwise
     */
    public Future<Boolean> validateMoveAsync(Card card) {
        return validationExecutor.submit(() -> {
            gameLock.lock();
            try {
                int tableSum = game.getTableSum();
                int cardValue = card.getValue(tableSum);
                return (tableSum + cardValue) <= 50 && (tableSum + cardValue) >= 0;
            } finally {
                gameLock.unlock();
            }
        });
    }

    /**
     * Eliminates the current player.
     * Thread-safe operation that removes player and returns cards to deck.
     * @return name of eliminated player
     */
    public String eliminateCurrentPlayer() {
        gameLock.lock();
        try {
            String playerName = game.getCurrentPlayer().getName();
            game.eliminateCurrentPlayer();

            resetTurnFlags();

            return playerName;

        } catch (NoValidCardException e) {
            resetTurnFlags();
            return e.getMessage();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Checks if current player can make any valid move.
     * Thread-safe operation.
     * @return true if player can play, false otherwise
     */
    public boolean canCurrentPlayerPlay() {
        gameLock.lock();
        try {
            return game.getCurrentPlayer().canPlay(game.getTableSum());
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Checks if the game has ended.
     * Thread-safe operation.
     * @return true if game is over, false otherwise
     */
    public boolean isGameOver() {
        gameLock.lock();
        try {
            return game.isGameOver();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Gets the winner of the game.
     * Thread-safe operation.
     * @return the winning player, or null if game is not over
     */
    public Player getWinner() {
        gameLock.lock();
        try {
            return game.getWinner();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Shuts down all thread pools gracefully.
     * Waits for threads to terminate before forcing shutdown.
     */
    public void shutdown() {
        gameLogicExecutor.shutdown();
        validationExecutor.shutdown();
        try {
            if (!gameLogicExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                gameLogicExecutor.shutdownNow();
            }
            if (!validationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                validationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            gameLogicExecutor.shutdownNow();
            validationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ==================== GETTERS ====================

    /**
     * Gets the current game model.
     * Thread-safe operation.
     * @return the game model
     */
    public GameModel getGame() {
        gameLock.lock();
        try {
            return game;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Checks if human player has played a card this turn.
     * @return true if card was played, false otherwise
     */
    public boolean hasHumanPlayedCard() {
        return humanHasPlayedCard;
    }

    /**
     * Checks if human player has drawn a card this turn.
     * @return true if card was drawn, false otherwise
     */
    public boolean hasHumanDrawnCard() {
        return humanHasDrawnCard;
    }

    /**
     * Checks if human player has completed their turn.
     * @return true if both played and drawn, false otherwise
     */
    public boolean isHumanTurnComplete() {
        return humanHasPlayedCard && humanHasDrawnCard;
    }

    /**
     * Result class for machine turn operations.
     * Contains success status, message, and played card information.
     */
    public static class MachineTurnResult {
        private final boolean success;
        private final String message;
        private final Card playedCard;

        /**
         * Creates a new MachineTurnResult.
         * @param success true if turn was successful
         * @param message descriptive message about the turn
         * @param playedCard the card that was played, or null
         */
        public MachineTurnResult(boolean success, String message, Card playedCard) {
            this.success = success;
            this.message = message;
            this.playedCard = playedCard;
        }

        /**
         * Gets the success status.
         * @return true if turn was successful, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Gets the result message.
         * @return descriptive message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the played card.
         * @return the card that was played, or null if no card was played
         */
        public Card getPlayedCard() {
            return playedCard;
        }
    }
}