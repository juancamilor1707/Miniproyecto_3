package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Help;
import com.example.proyecto3_.view.Menu;
import com.example.proyecto3_.view.Options;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class MenuController {

    @FXML
    void playButton(MouseEvent event) throws IOException {
        Options.getInstance();
        Menu.deleteInstance();
    }
    @FXML
    void helpButton(MouseEvent event) throws IOException {
        Help.getInstance();
        Menu.deleteInstance();
    }
    @FXML
    void exitButton(MouseEvent event) {
        Platform.exit();
    }
}
