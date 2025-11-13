package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Menu;
import com.example.proyecto3_.view.Win;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import java.io.IOException;

/**
 * Controller for the win screen view.
 * Displays the winner's name and handles navigation back to menu.
 */
public class WinController {

    @FXML
    private Label winnerLabel;

    /**
     * Handles the back button click event.
     * Returns to the main menu and closes the win screen.
     * @param event the mouse click event
     * @throws IOException if the menu view cannot be loaded
     */
    @FXML
    void backButton(MouseEvent event) throws IOException {
        Menu.getInstance();
        Win.deleteInstance();
    }

    /**
     * Sets the winner's name to be displayed on the screen.
     * Updates the winner label with the provided name.
     * @param name the name of the winning player
     */
    public void setWinnerName(String name) {
        winnerLabel.setText("¡El ganador es: " + name + "!");
    }

}