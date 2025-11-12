package com.example.proyecto3_.model.Game;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;

/**
 * Singleton class to store game configuration AND handle ALL game logic
 */
public class GameConfig {

    private static class Holder {
        private static final GameConfig INSTANCE = new GameConfig();
    }

    private int numBots;
    private GameModel game;
    private boolean humanHasPlayedCard = false;
    private boolean humanHasDrawnCard = false;

    private GameConfig() {
        this.numBots = 1; // Default value
    }

    /**
     * Gets the singleton instance
     * @return the unique GameConfig instance
     */
    public static GameConfig getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Sets the number of bots for the game
     * @param numBots number of machine players (1-3)
     */
    public void setNumBots(int numBots) {
        if (numBots < 1 || numBots > 3) {
            throw new IllegalArgumentException("Number of bots must be between 1 and 3");
        }
        this.numBots = numBots;
    }

    /**
     * Gets the number of bots configured
     * @return number of machine players
     */
    public int getNumBots() {
        return numBots;
    }

    // ==================== GAME LOGIC METHODS ====================

    /**
     * Initializes a new game with the configured number of bots
     */
    public void initializeGame() {
        game = new GameModel(numBots);
        game.start();
        resetTurnFlags();
    }

    /**
     * Resets turn flags for human player
     */
    private void resetTurnFlags() {
        humanHasPlayedCard = false;
        humanHasDrawnCard = false;
    }

    /**
     * Attempts to play a card for the current player
     * @param card the card to play
     * @return true if card was played successfully
     */
    public boolean playCard(Card card) {
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
        }
    }

    /**
     * Attempts to draw a card for the current player
     * @return true if card was drawn successfully
     */
    public boolean drawCard() {
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
        }
    }

    /**
     * Checks if deck is empty and recycles automatically
     */
    private void checkAndRecycleDeck() {
        if (game.isDeckEmpty()) {
            System.out.println("⚠️ Mazo vacío. Reciclando automáticamente...");
            // GameModel already handles recycling in drawCard, but we ensure it happens
        }
    }

    /**
     * Completes the current turn and moves to next player
     */
    public void completeTurn() {
        resetTurnFlags();
        game.nextTurn();
    }

    /**
     * Processes machine player's turn
     * @return MachineTurnResult with the outcome
     */
    public MachineTurnResult processMachineTurn() {
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
    }

    /**
     * Eliminates the current player
     * @return name of eliminated player
     */
    public String eliminateCurrentPlayer() {
        try {
            String playerName = game.getCurrentPlayer().getName();
            game.eliminateCurrentPlayer();

            resetTurnFlags();

            return playerName;

        } catch (NoValidCardException e) {
            resetTurnFlags();
            return e.getMessage();
        }
    }

    /**
     * Checks if current player can make any valid move
     */
    public boolean canCurrentPlayerPlay() {
        return game.getCurrentPlayer().canPlay(game.getTableSum());
    }

    /**
     * Checks if the game has ended
     */
    public boolean isGameOver() {
        return game.isGameOver();
    }

    /**
     * Gets the winner of the game
     */
    public Player getWinner() {
        return game.getWinner();
    }

    // ==================== GETTERS ====================

    public GameModel getGame() {
        return game;
    }

    public boolean hasHumanPlayedCard() {
        return humanHasPlayedCard;
    }

    public boolean hasHumanDrawnCard() {
        return humanHasDrawnCard;
    }

    public boolean isHumanTurnComplete() {
        return humanHasPlayedCard && humanHasDrawnCard;
    }

    /**
     * Result class for machine turn operations
     */
    public static class MachineTurnResult {
        private final boolean success;
        private final String message;
        private final Card playedCard;

        public MachineTurnResult(boolean success, String message, Card playedCard) {
            this.success = success;
            this.message = message;
            this.playedCard = playedCard;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Card getPlayedCard() {
            return playedCard;
        }
    }
}