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
import java.util.concurrent.*;

/**
 * Controller for UI and FXML interaction in the game.
 * Delegates all game logic to GameConfig and manages thread execution for machine players.
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
    @FXML private ImageView bot1Imageview;
    @FXML private ImageView bot2Imageview;
    @FXML private ImageView bot3Imageview;

    private final ExecutorService machineTurnExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "MachineTurnThread");
        t.setDaemon(true);
        return t;
    });

    private final ScheduledExecutorService uiUpdateScheduler = Executors.newScheduledThreadPool(1, r -> {
        Thread t = new Thread(r, "UIUpdateThread");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean isMachineTurnRunning = false;

    /**
     * Initializes the game controller.
     * Configures UI elements based on number of bots and starts the game.
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
     * Handles drawing a card from the deck.
     * Called when the player clicks the deck button.
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
     * Handles card click by human player.
     * Attempts to play the selected card and updates the UI.
     * @param card the card clicked by the player
     */
    private void onCardClicked(Card card) {
        if (GameConfig.getInstance().playCard(card)) {
            updateUI();
        }
    }

    /**
     * Processes the current turn for human or machine players.
     * Checks game over condition and delegates to appropriate turn handler.
     */
    private void processTurn() {
        try {
            if (GameConfig.getInstance().isGameOver()) {
                showWinner();
                shutdown();
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
     * Executes a machine player's turn using a single thread with delays.
     * Simulates thinking time and plays a card automatically.
     */
    private void executeMachineTurn() {
        if (isMachineTurnRunning) return;

        isMachineTurnRunning = true;

        machineTurnExecutor.submit(() -> {
            try {
                Thread.sleep(2000 + (long)(Math.random() * 2000));

                Player machine = GameConfig.getInstance().getGame().getCurrentPlayer();

                if (!machine.canPlay(GameConfig.getInstance().getGame().getTableSum())) {
                    Platform.runLater(() -> {
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                    });
                    return;
                }

                Card selectedCard = machine.selectCard(GameConfig.getInstance().getGame().getTableSum());
                if (selectedCard == null) {
                    Platform.runLater(() -> {
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                    });
                    return;
                }

                // STEP 1: Play the card
                try {
                    GameConfig.getInstance().getGame().playCard(selectedCard);
                    Platform.runLater(this::updateUI);
                } catch (Exception e) {
                    System.err.println("Bot movimiento inválido: " + e.getMessage());
                    Platform.runLater(() -> {
                        isMachineTurnRunning = false;
                        handlePlayerElimination();
                    });
                    return;
                }

                Thread.sleep(2000 + (long)(Math.random() * 1000));

                // STEP 2: Draw a card
                try {
                    GameConfig.getInstance().getGame().drawCard();
                    Platform.runLater(this::updateUI);
                } catch (Exception e) {
                    System.err.println("Bot no pudo robar: " + e.getMessage());
                }

                Thread.sleep(1000);

                // STEP 3: Complete turn
                Platform.runLater(() -> {
                    GameConfig.getInstance().completeTurn();
                    isMachineTurnRunning = false;
                    processTurn();
                });

            } catch (InterruptedException e) {
                System.err.println("Machine turn interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                Platform.runLater(() -> isMachineTurnRunning = false);
            }
        });
    }

    /**
     * Handles player elimination when they cannot play.
     * Removes the player and continues to the next turn.
     */
    private void handlePlayerElimination() {
        String eliminatedPlayer = GameConfig.getInstance().eliminateCurrentPlayer();
        System.out.println(eliminatedPlayer + " ha sido eliminado.");

        GameConfig.getInstance().completeTurn();
        processTurn();
    }

    /**
     * Shows the winner screen when the game ends.
     * Transitions to the Win view with the winner's name.
     * @throws IOException if the Win scene cannot be loaded
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

    /**
     * Shuts down all thread pools gracefully.
     * Waits for threads to terminate before forcing shutdown.
     */
    private void shutdown() {
        machineTurnExecutor.shutdown();
        uiUpdateScheduler.shutdown();
        try {
            if (!machineTurnExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                machineTurnExecutor.shutdownNow();
            }
            if (!uiUpdateScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                uiUpdateScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            machineTurnExecutor.shutdownNow();
            uiUpdateScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ==================== UI UPDATE METHODS ====================

    /**
     * Configures UI visibility based on number of bots.
     * Shows or hides bot areas and loads their images.
     * @param numBots number of machine players (1-3)
     */
    private void configureUIForBots(int numBots) {
        if (bot1Area != null) {
            boolean activo = numBots >= 1;
            bot1Area.setVisible(activo);
            bot1Area.setManaged(activo);
            if (activo) {
                Image bot1Image = new Image(getClass().getResource("/com/example/proyecto3_/Img/Bot1.png").toExternalForm());
                bot1Imageview.setImage(bot1Image);
            } else {
                bot1Imageview.setImage(null);
            }
        }

        if (bot2Area != null) {
            boolean activo = numBots >= 2;
            bot2Area.setVisible(activo);
            bot2Area.setManaged(activo);
            if (activo) {
                Image bot2Image = new Image(getClass().getResource("/com/example/proyecto3_/Img/Bot2.png").toExternalForm());
                bot2Imageview.setImage(bot2Image);
            } else {
                bot2Imageview.setImage(null);
            }
        }

        if (bot3Area != null) {
            boolean activo = numBots >= 3;
            bot3Area.setVisible(activo);
            bot3Area.setManaged(activo);
            if (activo) {
                Image bot3Image = new Image(getClass().getResource("/com/example/proyecto3_/Img/Bot3.png").toExternalForm());
                bot3Imageview.setImage(bot3Image);
            } else {
                bot3Imageview.setImage(null);
            }
        }
    }


    /**
     * Updates the entire UI with current game state.
     * Called whenever the game state changes.
     */
    private void updateUI() {
        Platform.runLater(() -> {
            GameModel game = GameConfig.getInstance().getGame();

            updateLabels(game);
            updateTableArea(game);
            updateHumanHand(game);
            updateBotHands(game);
        });
    }

    /**
     * Updates all text labels with current game information.
     * Includes table sum, top card, current turn, and statistics.
     * @param game the current game model
     */
    private void updateLabels(GameModel game) {
        if (tableSumLabel != null) {
            tableSumLabel.setText("Total de puntos: " + game.getTableSum());
        }

        if (topCardLabel != null && game.getTopCard() != null) {
            Card topCard = game.getTopCard();
            String cardText = translateCardToSpanish(topCard);
            topCardLabel.setText("Ultima carta jugada: " + cardText);
        }

        if (currentTurnLabel != null) {
            String turnText = " " + game.getCurrentPlayer().getName();

            if (!game.getCurrentPlayer().isMachine()) {
                if (!GameConfig.getInstance().hasHumanPlayedCard()) {
                    turnText += " - Juega una carta";
                } else if (!GameConfig.getInstance().hasHumanDrawnCard()) {
                    turnText += " - Roba una carta";
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
     * Translates a card to Spanish format.
     * @param card the card to translate
     * @return Spanish representation of the card
     */
    private String translateCardToSpanish(Card card) {
        String rank = translateRank(card.getRank());
        String suit = translateSuitToSpanish(card.getSuit());
        return rank + " de " + suit;
    }

    /**
     * Translates card rank to Spanish.
     * @param rank the card rank (J, Q, K, A, or number)
     * @return Spanish translation of the rank
     */
    private String translateRank(String rank) {
        switch (rank) {
            case "J":
                return "Jota";
            case "Q":
                return "Reina";
            case "K":
                return "Rey";
            case "A":
                return "As";
            default:
                return rank;
        }
    }

    /**
     * Translates suit names to Spanish (full names).
     * @param suit the suit name in English
     * @return Spanish translation of the suit
     */
    private String translateSuitToSpanish(String suit) {
        switch (suit.toLowerCase()) {
            case "hearts":
                return "Corazones";
            case "diamonds":
                return "Diamantes";
            case "clubs":
                return "Trébol";
            case "spades":
                return "Picas";
            default:
                return suit;
        }
    }

    /**
     * Updates the table area with card images.
     * Shows the top card and deck with remaining cards count.
     * @param game the current game model
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
     * Updates the human player's hand display.
     * Creates clickable buttons for each card in the player's hand.
     * @param game the current game model
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
     * Updates the bots' hand displays.
     * Shows card backs for each bot's hand.
     * @param game the current game model
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
     * Updates a single bot area with card backs.
     * @param botArea the HBox container for the bot's cards
     * @param bot the bot player
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
     * Creates a clickable card button with image and hover effects.
     * @param card the card to display
     * @return button with card image
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
     * Creates an ImageView for a card.
     * @param card the card to display
     * @param width the desired width
     * @param height the desired height
     * @return ImageView with the card image
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
     * Creates an ImageView for card back.
     * @param width the desired width
     * @param height the desired height
     * @return ImageView with the card back image
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
     * Converts card to image filename.
     * @param card the card to convert
     * @return filename for the card image
     */
    private String getCardImageName(Card card) {
        String rank = card.getRank();
        String suit = translateSuit(card.getSuit());
        return rank + "_" + suit;
    }

    /**
     * Translates English suit names to Spanish for image filenames.
     * @param suit the suit name in English
     * @return Spanish suit name for filename
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