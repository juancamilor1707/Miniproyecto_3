package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Game.GameConfig;
import com.example.proyecto3_.view.Game;
import com.example.proyecto3_.view.Menu;
import com.example.proyecto3_.view.Options;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class OptionsController {

    /**
     * Handles the start game button clicks
     * Uses userData from FXML to determine number of bots
     */
    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            Button clickedButton = (Button) event.getSource();
            int numBots = Integer.parseInt((String) clickedButton.getUserData());

            GameConfig.getInstance().setNumBots(numBots);

            System.out.println("Starting game with " + numBots + " bots");

            Options.deleteInstance();
            Game.getInstance();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @FXML
    private void goBackToMenu(ActionEvent event) {
        try {
            Options.deleteInstance();
            Menu.getInstance();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}