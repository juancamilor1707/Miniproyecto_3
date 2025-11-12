package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Game.GameModel;
import com.example.proyecto3_.model.Game.GameConfig;
import com.example.proyecto3_.view.Game;
import com.example.proyecto3_.view.Win;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

/**
 * Controller ONLY for UI/FXML interaction
 * All game logic is delegated to GameConfig
 */
public class GameController {

    @FXML private Label tableSumLabel;
    @FXML private Label topCardLabel;
    @FXML private Label currentTurnLabel;
    @FXML private Label statsLabel;
    @FXML private Button deckButton;
    @FXML private Button tableCardButton;
    @FXML private HBox humanHandBox;
    @FXML private HBox bot1Area;
    @FXML private HBox bot2Area;
    @FXML private HBox bot3Area;

    private boolean isMachineTurnRunning = false;

    /**
     * Initializes the game controller
     */
    @FXML
    public void initialize() {
        int numBots = GameConfig.getInstance().getNumBots();
        configureUIForBots(numBots);

        GameConfig.getInstance().initializeGame();

        updateUI();
        processTurn();
    }

    /**
     * Handles drawing a card from the deck
     */
    @FXML
    private void onDrawFromDeck() {
        if (GameConfig.getInstance().drawCard()) {
            updateUI();
            GameConfig.getInstance().completeTurn();
            processTurn();
        }
    }

    /**
     * Handles card click by human player
     */
    private void onCardClicked(Card card) {
        if (GameConfig.getInstance().playCard(card)) {
            updateUI();
        }
    }

    /**
     * Processes the current turn (human or machine)
     */
    private void processTurn() {
        try {
            if (GameConfig.getInstance().isGameOver()) {
                showWinner();
                return;
            }

            Player currentPlayer = GameConfig.getInstance().getGame().getCurrentPlayer();

            if (!GameConfig.getInstance().canCurrentPlayerPlay()) {
                handlePlayerElimination();
                return;
            }

            if (currentPlayer.isMachine()) {
                executeMachineTurn();
            }

            updateUI();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a machine player's turn in a separate thread
     */
    private void executeMachineTurn() {
        if (isMachineTurnRunning) return;

        isMachineTurnRunning = true;

        new Thread(() -> {
            try {
                // Wait before playing (simulate thinking)
                Thread.sleep(2000 + (int)(Math.random() * 2000));

                Platform.runLater(() -> {
                    // Play the card first
                    Player machine = GameConfig.getInstance().getGame().getCurrentPlayer();

                    if (!machine.canPlay(GameConfig.getInstance().getGame().getTableSum())) {
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                        return;
                    }

                    Card selectedCard = machine.selectCard(GameConfig.getInstance().getGame().getTableSum());
                    if (selectedCard == null) {
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                        return;
                    }

                    try {
                        GameConfig.getInstance().getGame().playCard(selectedCard);
                        updateUI();

                        // NOW wait before drawing the card
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000 + (int)(Math.random() * 1000));

                                Platform.runLater(() -> {
                                    try {
                                        GameConfig.getInstance().getGame().drawCard();
                                        updateUI();

                                        // Wait a bit more before completing turn
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(1000);

                                                Platform.runLater(() -> {
                                                    GameConfig.getInstance().completeTurn();
                                                    isMachineTurnRunning = false;
                                                    processTurn();
                                                });

                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                isMachineTurnRunning = false;
                                            }
                                        }).start();

                                    } catch (Exception e) {
                                        System.err.println("Bot no pudo robar: " + e.getMessage());
                                        GameConfig.getInstance().completeTurn();
                                        isMachineTurnRunning = false;
                                        processTurn();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                isMachineTurnRunning = false;
                            }
                        }).start();

                    } catch (Exception e) {
                        System.err.println("Bot movimiento inválido: " + e.getMessage());
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                isMachineTurnRunning = false;
            }
        }).start();
    }

    /**
     * Handles player elimination
     */
    private void handlePlayerElimination() {
        String eliminatedPlayer = GameConfig.getInstance().eliminateCurrentPlayer();
        System.out.println(eliminatedPlayer + " ha sido eliminado.");

        GameConfig.getInstance().completeTurn();
        processTurn();
    }

    /**
     * Shows the winner screen
     */
    private void showWinner() throws IOException {
        Player winner = GameConfig.getInstance().getWinner();

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

    // ==================== UI UPDATE METHODS ====================

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
     * Updates the entire UI with current game state
     */
    private void updateUI() {
        GameModel game = GameConfig.getInstance().getGame();

        updateLabels(game);
        updateTableArea(game);
        updateHumanHand(game);
        updateBotHands(game);
    }

    /**
     * Updates all text labels
     */
    private void updateLabels(GameModel game) {
        if (tableSumLabel != null) {
            tableSumLabel.setText("Total de puntos: " + game.getTableSum());
        }

        if (topCardLabel != null && game.getTopCard() != null) {
            topCardLabel.setText("Ultima carta jugada: " + game.getTopCard().toString());
        }

        if (currentTurnLabel != null) {
            String turnText = "Turno actual: " + game.getCurrentPlayer().getName();

            if (!game.getCurrentPlayer().isMachine()) {
                if (!GameConfig.getInstance().hasHumanPlayedCard()) {
                    turnText += " - Juega una carta";
                } else if (!GameConfig.getInstance().hasHumanDrawnCard()) {
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
    }

    /**
     * Updates the table area with card images
     */
    private void updateTableArea(GameModel game) {
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
     * Updates the human player's hand display
     */
    private void updateHumanHand(GameModel game) {
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
     * Updates the bots' hand displays
     */
    private void updateBotHands(GameModel game) {
        List<Player> players = game.getPlayers();

        if (bot1Area != null && players.size() > 1) {
            updateBotArea(bot1Area, players.get(1));
        }

        if (bot2Area != null && players.size() > 2) {
            updateBotArea(bot2Area, players.get(2));
        }

        if (bot3Area != null && players.size() > 3) {
            updateBotArea(bot3Area, players.get(3));
        }
    }

    /**
     * Updates a single bot area
     */
    private void updateBotArea(HBox botArea, Player bot) {
        botArea.getChildren().clear();
        if (!bot.isEliminated()) {
            for (int i = 0; i < bot.getHand().size(); i++) {
                ImageView cardBack = createCardBackImageView(100, 120);
                botArea.getChildren().add(cardBack);
            }
        }
    }

    // ==================== IMAGE CREATION METHODS ====================

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
     * Creates an ImageView for card back
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