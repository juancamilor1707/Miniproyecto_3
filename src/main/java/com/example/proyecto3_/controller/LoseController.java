package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Lose;
import com.example.proyecto3_.view.Menu;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoseController {
    @FXML
    void backButton(MouseEvent event) throws IOException {
        Menu.getInstance();
        Lose.deleteInstance();
    }
}
