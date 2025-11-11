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
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Controller for the game view with exception handling and card images
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
    private HBox bot2Area;

    @FXML
    private HBox bot3Area;

    private GameModel game;
    private boolean isMachineTurnRunning = false;


    /**
     * Inner class to handle machine player turns
     */
    private class MachineTurnHandler implements Runnable {
        private final Player machine;

        public MachineTurnHandler(Player machine) {
            this.machine = machine;
        }

        @Override
        public void run() {
            try {
                // PASO 1: Delay para "pensar" qué carta jugar (2-4 segundos)
                Thread.sleep(2000 + (int)(Math.random() * 2000));

                Platform.runLater(() -> {
                    try {
                        if (!machine.canPlay(game.getTableSum())) {
                            isMachineTurnRunning = false;
                            handlePlayerElimination();
                            return;
                        }

                        Card selectedCard = machine.selectCard(game.getTableSum());

                        if (selectedCard == null) {
                            isMachineTurnRunning = false;
                            handlePlayerElimination();
                            return;
                        }

                        game.playCard(selectedCard);
                        updateUI();

                        new Thread(() -> {
                            try {
                                Thread.sleep(1000 + (int)(Math.random() * 1000));

                                Platform.runLater(() -> {
                                    try {
                                        if (!game.isDeckEmpty()) {
                                            game.drawCard();
                                            updateUI();
                                        }


                                        game.nextTurn();
                                        isMachineTurnRunning = false;
                                        processTurn();

                                    } catch (DeckEmptyException e) {
                                        showAlert("Mazo vacío", "No hay más cartas disponibles.");
                                        game.nextTurn();
                                        isMachineTurnRunning = false;
                                        processTurn();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                isMachineTurnRunning = false;
                            }
                        }).start();

                    } catch (InvalidMoveException e) {
                        showAlert("Error", "Movimiento inválido: " + e.getMessage());
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                isMachineTurnRunning = false;
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

    /**
     * Updates the table area with card images
     */
    private void updateTableArea() {

        if (game.getTopCard() != null && tableCardButton != null) {
            Card topCard = game.getTopCard();
            ImageView cardImage = createCardImageView(topCard, 60, 80);
            tableCardButton.setGraphic(cardImage);
            tableCardButton.setText("");
            tableCardButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else if (tableCardButton != null) {
            tableCardButton.setText("Mesa vacía");
            tableCardButton.setGraphic(null);
        }


        if (deckButton != null) {
            int deckSize = game.getDeckSize();
            ImageView deckImage = createCardBackImageView(60, 80);
            deckButton.setGraphic(deckImage);
            deckButton.setText("(" + deckSize + ")");
            deckButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            deckButton.setDisable(deckSize == 0);
        }
    }


    /**
     * Updates the human player's hand display with card images
     */
    private void updateHumanHand() {
        if (humanHandBox == null) return;

        humanHandBox.getChildren().clear();

        Player human = game.getPlayers().get(0);
        for (Card card : human.getHand()) {
            Button cardButton = createCardButton(card);
            cardButton.setOnAction(e -> onCardClicked(card));
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

            // Try to play the card
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
                if (!isMachineTurnRunning) {
                    isMachineTurnRunning = true;
                    new Thread(() -> {
                        new MachineTurnHandler(currentPlayer).run();
                    }).start();
                }
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

    /**
     * Shows an alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Updates the bots' hand displays with card back images
     */
    private void updateBotHands() {
        List<Player> players = game.getPlayers();

        if (bot1Area != null && players.size() > 1) {
            bot1Area.getChildren().clear();
            Player bot1 = players.get(1);
            for (int i = 0; i < bot1.getHand().size(); i++) {
                ImageView cardBack = createCardBackImageView(60, 80);
                bot1Area.getChildren().add(cardBack);
            }
        }

        if (bot2Area != null && players.size() > 2) {
            bot2Area.getChildren().clear();
            Player bot2 = players.get(2);
            for (int i = 0; i < bot2.getHand().size(); i++) {
                ImageView cardBack = createCardBackImageView(60, 80);
                bot2Area.getChildren().add(cardBack);
            }
        }

        if (bot3Area != null && players.size() > 3) {
            bot3Area.getChildren().clear();
            Player bot3 = players.get(3);
            for (int i = 0; i < bot3.getHand().size(); i++) {
                ImageView cardBack = createCardBackImageView(60, 80);
                bot3Area.getChildren().add(cardBack);
            }
        }
    }

    /**
     * Creates a clickable card button with image
     */
    private Button createCardButton(Card card) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        ImageView imageView = createCardImageView(card, 60, 80);
        button.setGraphic(imageView);

        //hover effects
        button.setOnMouseEntered(e -> {
            imageView.setFitWidth(70);
            imageView.setFitHeight(90);
        });

        button.setOnMouseExited(e -> {
            imageView.setFitWidth(60);
            imageView.setFitHeight(80);
        });

        return button;
    }

    /**
     * Creates an ImageView for a card
     */
    private ImageView createCardImageView(Card card, double width, double height) {
        String imageName = getCardImageName(card);
        String imagePath = "/com/example/proyecto3_/Img/" + imageName + ".png";

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            // Fallback: crear ImageView vacío
            ImageView fallback = new ImageView();
            fallback.setFitWidth(width);
            fallback.setFitHeight(height);
            return fallback;
        }
    }

    /**
     * Creates an ImageView for card back (Reverso.png)
     */
    private ImageView createCardBackImageView(double width, double height) {
        String imagePath = "/com/example/proyecto3_/Img/Reverso.png";

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Error loading card back image: " + imagePath);
            e.printStackTrace();
            ImageView fallback = new ImageView();
            fallback.setFitWidth(width);
            fallback.setFitHeight(height);
            return fallback;
        }
    }

    /**
     * Converts card to image filename
     * Example: Card("7", "Diamonds") -> "7_Diamantes"
     */
    private String getCardImageName(Card card) {
        String rank = card.getRank();
        String suit = translateSuit(card.getSuit());
        return rank + "_" + suit;
    }

    /**
     * Translates English suit names to Spanish for image filenames
     */
    private String translateSuit(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
                return "Corazones";
            case "diamonds":
                return "Diamantes";
            case "clubs":
                return "Trebol";
            case "spades":
                return "Picas";
            default:
                return suit;
        }
    }
}