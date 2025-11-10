package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;
import com.example.proyecto3_.model.Game.GameModel;
import com.example.proyecto3_.model.Game.GameConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.util.List;

/**
 * Controller for the game view with exception handling
 */
public class GameController {

    @FXML
    private Label tableSumLabel;

    @FXML
    private Label topCardLabel;

    @FXML
    private Label currentTurnLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private Button deckButton;

    @FXML
    private Button tableCardButton;

    @FXML
    private HBox humanHandBox;

    @FXML
    private HBox bot1Area;

    @FXML
    private VBox bot2Area;

    @FXML
    private VBox bot3Area;

    private GameModel game;

    /**
     * Inner class to handle machine player turns
     */
    private class MachineTurnHandler implements Runnable {
        private Player machine;
        private Card selectedCard;

        public MachineTurnHandler(Player machine) {
            this.machine = machine;
        }

        @Override
        public void run() {
            try {
                // Simulate thinking time (2-4 seconds)
                Thread.sleep(2000 + (int)(Math.random() * 2000));

                Platform.runLater(() -> {
                    try {
                        selectedCard = machine.selectCard(game.getTableSum());
                        if (selectedCard != null) {
                            game.playCard(selectedCard);
                            updateUI();

                            // Wait before drawing (1-2 seconds)
                            new Thread(new DrawCardHandler()).start();
                        }
                    } catch (InvalidMoveException e) {
                        showAlert("Error", "Machine player made an invalid move: " + e.getMessage());
                        handlePlayerElimination();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inner class to handle drawing cards
     */
    private class DrawCardHandler implements Runnable {
        @Override
        public void run() {
            new Thread(() -> {
                try {
                    Thread.sleep(1000 + (int)(Math.random() * 1000));

                    Platform.runLater(() -> {
                        try {
                            game.drawCard();
                            game.nextTurn();
                            updateUI();
                            processTurn();
                        } catch (DeckEmptyException e) {
                            showAlert("Error", "Deck is empty: " + e.getMessage());
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Initializes the game controller
     */
    @FXML
    public void initialize() {
        try {
            int numBots = GameConfig.getInstance().getNumBots();

            configureUIForBots(numBots);

            game = new GameModel(numBots);
            game.start();

            updateUI();
            processTurn();

        } catch (InvalidGameStateException e) {
            showAlert("Game Error", "Failed to initialize game: " + e.getMessage());
        }
    }

    @FXML
    private void onDrawFromDeck() {
        try {
            Player current = game.getCurrentPlayer();
            if (current.isMachine()) {
                showAlert("Turno inválido", "Espera tu turno.");
                return;
            }

            if (current.getHand().size() >= 4) {
                showAlert("No puedes robar aún", "Primero debes jugar una carta.");
                return;
            }

            game.drawCard();
            updateUI();

            game.nextTurn();
            processTurn();

        } catch (DeckEmptyException e) {
            showAlert("Mazo vacío", "No hay más cartas disponibles.");
        }
    }

    /**
     * Configures UI visibility based on number of bots
     */
    private void configureUIForBots(int numBots) {
        if (bot1Area != null) {
            bot1Area.setVisible(numBots >= 1);
            bot1Area.setManaged(numBots >= 1);
        }

        if (bot2Area != null) {
            bot2Area.setVisible(numBots >= 2);
            bot2Area.setManaged(numBots >= 2);
        }

        if (bot3Area != null) {
            bot3Area.setVisible(numBots >= 3);
            bot3Area.setManaged(numBots >= 3);
        }
    }

    /**
     * Updates the UI with current game state
     */
    private void updateUI() {
        if (tableSumLabel != null) {
            tableSumLabel.setText("Sum: " + game.getTableSum());
        }

        if (topCardLabel != null && game.getTopCard() != null) {
            topCardLabel.setText("Card: " + game.getTopCard().toString());
        }

        if (currentTurnLabel != null) {
            currentTurnLabel.setText("Turn: " + game.getCurrentPlayer().getName());
        }

        if (statsLabel != null) {
            GameModel.GameStats stats = game.getStats();
            statsLabel.setText(
                    "Turns: " + stats.getTotalTurns() +
                            " | Cards: " + stats.getCardsPlayed() +
                            " | Eliminated: " + stats.getPlayersEliminated()
            );
        }

        updateHumanHand();
        updateBotHands();
        updateTableArea();
    }

    private void updateTableArea() {
        if (game.getTopCard() != null && tableCardButton != null) {
            tableCardButton.setText(game.getTopCard().toString());
        } else if (tableCardButton != null) {
            tableCardButton.setText("Mesa vacía");
        }

        if (deckButton != null) {
            int deckSize = game.getDeckSize();
            deckButton.setText("🂠 (" + deckSize + ")");
            deckButton.setDisable(deckSize == 0);
        }
    }

    /**
     * Updates the human player's hand display
     */
    private void updateHumanHand() {
        if (humanHandBox == null) return;

        humanHandBox.getChildren().clear();

        Player human = game.getPlayers().get(0);
        for (Card card : human.getHand()) {
            Button cardButton = new Button(card.toString());
            cardButton.setOnAction(e -> onCardClicked(card));
            cardButton.setStyle("-fx-min-width: 40; -fx-min-height: 60; -fx-font-size: 10;");
            humanHandBox.getChildren().add(cardButton);
        }
    }

    /**
     * Handles card click by human player
     */
    private void onCardClicked(Card card) {
        try {
            Player currentPlayer = game.getCurrentPlayer();

            if (currentPlayer.isMachine()) {
                showAlert("Not your turn!", "Wait for your turn to play.");
                return;
            }

            game.playCard(card);
            game.nextTurn();

            updateUI();
            processTurn();

        } catch (InvalidMoveException e) {
            showAlert("Invalid Move", e.getMessage());
        } catch (DeckEmptyException e) {
            showAlert("Deck Empty", e.getMessage());
        }
    }

    /**
     * Processes the current turn
     */
    private void processTurn() {
        try {
            Player currentPlayer = game.getCurrentPlayer();

            if (game.isGameOver()) {
                showWinner();
                return;
            }

            if (!currentPlayer.canPlay(game.getTableSum())) {
                handlePlayerElimination();
                return;
            }

            if (currentPlayer.isMachine()) {
                new Thread(new MachineTurnHandler(currentPlayer)).start();
            }

            updateUI();

        } catch (InvalidGameStateException e) {
            showAlert("Game Error", e.getMessage());
        }
    }

    /**
     * Handles player elimination
     */
    private void handlePlayerElimination() {
        try {
            game.eliminateCurrentPlayer();
        } catch (NoValidCardException e) {
            showAlert("Player Eliminated", e.getMessage());
            game.nextTurn();
            processTurn();
        }
    }

    /**
     * Shows the winner
     */
    private void showWinner() {
        Player winner = game.getWinner();
        if (winner != null) {
            GameModel.GameStats stats = game.getStats();
            showAlert(
                    "Game Over!",
                    winner.getName() + " wins!\n\n" +
                            "Total Turns: " + stats.getTotalTurns() + "\n" +
                            "Cards Played: " + stats.getCardsPlayed()
            );
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the bots' hand displays
     */
    private void updateBotHands() {
        List<Player> players = game.getPlayers();

        if (bot1Area != null && players.size() > 1) {
            bot1Area.getChildren().clear();
            Player bot1 = players.get(1);
            for (int i = 0; i < bot1.getHand().size(); i++) {
                Button hiddenCard = new Button("?");
                hiddenCard.setStyle("-fx-min-width: 40; -fx-min-height: 60; -fx-font-size: 16;");
                bot1Area.getChildren().add(hiddenCard);
            }
        }

        if (bot2Area != null && players.size() > 2) {
            bot2Area.getChildren().clear();
            Player bot2 = players.get(2);
            for (int i = 0; i < bot2.getHand().size(); i++) {
                Button hiddenCard = new Button("?");
                hiddenCard.setStyle("-fx-min-width: 40; -fx-min-height: 60; -fx-font-size: 16;");
                bot2Area.getChildren().add(hiddenCard);
            }
        }

        if (bot3Area != null && players.size() > 3) {
            bot3Area.getChildren().clear();
            Player bot3 = players.get(3);
            for (int i = 0; i < bot3.getHand().size(); i++) {
                Button hiddenCard = new Button("?");
                hiddenCard.setStyle("-fx-min-width: 40; -fx-min-height: 60; -fx-font-size: 16;");
                bot3Area.getChildren().add(hiddenCard);
            }
        }
    }
}
