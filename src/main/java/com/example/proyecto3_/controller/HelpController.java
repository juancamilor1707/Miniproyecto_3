package com.example.proyecto3_.controller;

import com.example.proyecto3_.view.Help;
import com.example.proyecto3_.view.Menu;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class HelpController {

    @FXML
    void backButton(MouseEvent event) throws IOException {
        Menu.getInstance();
        Help.deleteInstance();
    }
}
