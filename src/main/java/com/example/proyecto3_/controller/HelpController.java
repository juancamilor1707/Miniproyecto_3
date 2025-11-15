package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Help;
import com.example.proyecto3_.view.Menu;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

/**
 * Controller for the Help view.
 * Handles navigation back to the main menu.
 */
public class HelpController {

    /**
     * Handles the back button click event.
     * Returns to the main menu and closes the help view.
     * @param event the mouse click event
     * @throws IOException if the menu view cannot be loaded
     */
    @FXML
    void backButton(MouseEvent event) throws IOException {
        Menu.getInstance();
        Help.deleteInstance();
    }
}