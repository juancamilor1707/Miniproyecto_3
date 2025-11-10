package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Game.GameConfig;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

public class GameController {

    @FXML
    private Label tableSumLabel;

    @FXML
    private VBox bot1Area;

    @FXML
    private VBox bot2Area;

    @FXML
    private VBox bot3Area;
    // private Game game;

    /**
     * Initializes the game controller
     * This method is automatically called by JavaFX after loading the FXML
     */
    @FXML
    public void initialize() {
        int numBots = GameConfig.getInstance().getNumBots();
        System.out.println("Game initialized with " + numBots + " bots");
        configureUIForBots(numBots);

        // Aquí crearías tu lógica de juego
        // game = new Game(numBots);
        // game.start();
        updateUI();
    }

    /**
     * Configures the UI visibility based on number of bots
     * @param numBots number of machine players in the game
     */
    private void configureUIForBots(int numBots) {
        if (bot1Area != null) {
            bot1Area.setVisible(true);
            bot1Area.setManaged(true);
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
            tableSumLabel.setText("Sum: 0");
        }
    }
}