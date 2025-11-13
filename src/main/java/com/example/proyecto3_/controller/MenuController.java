package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Help;
import com.example.proyecto3_.view.Menu;
import com.example.proyecto3_.view.Options;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * Controller for the main menu view.
 * Handles navigation to different game sections and application exit.
 */
public class MenuController {

    /**
     * Handles the play button click event.
     * Opens the options view to select number of bots.
     * @param event the mouse click event
     * @throws IOException if the options view cannot be loaded
     */
    @FXML
    void playButton(MouseEvent event) throws IOException {
        Options.getInstance();
        Menu.deleteInstance();
    }

    /**
     * Handles the help button click event.
     * Opens the help view with game instructions.
     * @param event the mouse click event
     * @throws IOException if the help view cannot be loaded
     */
    @FXML
    void helpButton(MouseEvent event) throws IOException {
        Help.getInstance();
        Menu.deleteInstance();
    }

    /**
     * Handles the exit button click event.
     * Closes the application completely.
     * @param event the mouse click event
     */
    @FXML
    void exitButton(MouseEvent event) {
        Platform.exit();
    }
}