package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;
import com.example.proyecto3_.model.Game.GameModel;
import com.example.proyecto3_.model.Game.GameConfig;
import com.example.proyecto3_.view.Game;
import com.example.proyecto3_.view.Win;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the game view with manual turn completion
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

    private boolean humanHasPlayedCard = false;
    private boolean humanHasDrawnCard = false;

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
                                        game.drawCard();
                                        updateUI();

                                        game.nextTurn();
                                        isMachineTurnRunning = false;
                                        processTurn();

                                    } catch (DeckEmptyException e) {
                                        System.err.println("Bot no pudo robar: " + e.getMessage());
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
                        System.err.println("Bot movimiento inválido: " + e.getMessage());
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
            System.err.println("Error inicializando juego: " + e.getMessage());
        }
    }

    @FXML
    private void onDrawFromDeck() {
        try {
            Player current = game.getCurrentPlayer();

            if (current.isMachine() || !humanHasPlayedCard || humanHasDrawnCard) {
                return;
            }

            game.drawCard();
            humanHasDrawnCard = true;
            updateUI();

            humanHasPlayedCard = false;
            humanHasDrawnCard = false;

            game.nextTurn();
            processTurn();

        } catch (DeckEmptyException e) {
            System.err.println("Error al robar carta: " + e.getMessage());

            humanHasPlayedCard = false;
            humanHasDrawnCard = false;
            game.nextTurn();
            processTurn();
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
            tableSumLabel.setText("Total de puntos: " + game.getTableSum());
        }

        if (topCardLabel != null && game.getTopCard() != null) {
            topCardLabel.setText("Ultima carta jugada: " + game.getTopCard().toString());
        }

        if (currentTurnLabel != null) {
            String turnText = "Turno actual: " + game.getCurrentPlayer().getName();

            if (!game.getCurrentPlayer().isMachine()) {
                if (!humanHasPlayedCard) {
                    turnText += " - Juega una carta";
                } else if (!humanHasDrawnCard) {
                    turnText += " - Roba una carta del mazo";
                }
            }

            currentTurnLabel.setText(turnText);
        }

        if (statsLabel != null) {
            GameModel.GameStats stats = game.getStats();
            statsLabel.setText(
                    "Turnos: " + stats.getTotalTurns() +
                            " | Cartas: " + stats.getCardsPlayed() +
                            " | Eliminados: " + stats.getPlayersEliminated()
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
            ImageView cardImage = createCardImageView(topCard, 100, 120);
            tableCardButton.setGraphic(cardImage);
            tableCardButton.setText("");
            tableCardButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } else if (tableCardButton != null) {
            tableCardButton.setText("Empty Table");
            tableCardButton.setGraphic(null);
        }

        if (deckButton != null) {
            int deckSize = game.getDeckSize();
            ImageView deckImage = createCardBackImageView(100, 120);
            deckButton.setGraphic(deckImage);
            deckButton.setText("(" + deckSize + ")");
            deckButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
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

            if (currentPlayer.isMachine() || humanHasPlayedCard) {
                return;
            }

            game.playCard(card);
            humanHasPlayedCard = true;

            updateUI();

        } catch (InvalidMoveException e) {
            System.err.println("Movimiento inválido: " + e.getMessage());
        }
    }

    /**
     * Processes the current turn
     */
    private void processTurn() {
        try {
            if (game.isGameOver()) {
                showWinner();
                return;
            }

            Player currentPlayer = game.getCurrentPlayer();

            if (!currentPlayer.canPlay(game.getTableSum())) {
                System.out.println(currentPlayer.getName() + " no puede jugar ninguna carta. Eliminando...");
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
            } else {
                humanHasPlayedCard = false;
                humanHasDrawnCard = false;
            }

            updateUI();

        } catch (InvalidGameStateException e) {
            System.err.println("Error de estado del juego: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles player elimination
     */
    private void handlePlayerElimination() {
        try {
            String playerName = game.getCurrentPlayer().getName();
            game.eliminateCurrentPlayer();
            System.out.println(playerName + " ha sido eliminado.");

            if (!game.getCurrentPlayer().isMachine()) {
                humanHasPlayedCard = false;
                humanHasDrawnCard = false;
            }

            game.nextTurn();
            processTurn();

        } catch (NoValidCardException e) {
            System.out.println("Jugador eliminado: " + e.getMessage());

            humanHasPlayedCard = false;
            humanHasDrawnCard = false;

            game.nextTurn();
            processTurn();
        }
    }

    /**
     * Shows the winner
     */
    private void showWinner() throws IOException {
        Player winner = game.getWinner();

        if (winner != null) {
            Win winScene = Win.getInstance();

            if (winScene != null && winScene.getController() != null) {
                winScene.getController().setWinnerName(winner.getName());
            } else {
                System.err.println("Error: Win o su controlador es null");
            }

            Game.deleteInstance();
        }
    }

    /**
     * Updates the bots' hand displays with card back images
     */
    private void updateBotHands() {
        List<Player> players = game.getPlayers();

        if (bot1Area != null && players.size() > 1) {
            bot1Area.getChildren().clear();
            Player bot1 = players.get(1);
            if (!bot1.isEliminated()) {
                for (int i = 0; i < bot1.getHand().size(); i++) {
                    ImageView cardBack = createCardBackImageView(100, 120);
                    bot1Area.getChildren().add(cardBack);
                }
            }
        }

        if (bot2Area != null && players.size() > 2) {
            bot2Area.getChildren().clear();
            Player bot2 = players.get(2);
            if (!bot2.isEliminated()) {
                for (int i = 0; i < bot2.getHand().size(); i++) {
                    ImageView cardBack = createCardBackImageView(100, 120);
                    bot2Area.getChildren().add(cardBack);
                }
            }
        }

        if (bot3Area != null && players.size() > 3) {
            bot3Area.getChildren().clear();
            Player bot3 = players.get(3);
            if (!bot3.isEliminated()) {
                for (int i = 0; i < bot3.getHand().size(); i++) {
                    ImageView cardBack = createCardBackImageView(100, 120);
                    bot3Area.getChildren().add(cardBack);
                }
            }
        }
    }

    /**
     * Creates a clickable card button with image
     */
    private Button createCardButton(Card card) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        ImageView imageView = createCardImageView(card, 100, 120);
        button.setGraphic(imageView);

        button.setOnMouseEntered(e -> {
            imageView.setFitWidth(110);
            imageView.setFitHeight(130);
        });

        button.setOnMouseExited(e -> {
            imageView.setFitWidth(100);
            imageView.setFitHeight(120);
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
            ImageView fallback = new ImageView();
            fallback.setFitWidth(width);
            fallback.setFitHeight(height);
            return fallback;
        }
    }

    /**
     * Converts card to image filename
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