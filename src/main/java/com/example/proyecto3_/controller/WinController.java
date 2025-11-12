package com.example.proyecto3_.controller;

import com.example.proyecto3_.model.Game.GameModel;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.view.Menu;
import com.example.proyecto3_.view.Win;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import java.io.IOException;

public class WinController {

    @FXML
    void backButton(MouseEvent event) throws IOException {
        Menu.getInstance();
        Win.deleteInstance();
    }
    
    @FXML
    private Label winnerLabel;

    public void setWinnerName(String name) {
        winnerLabel.setText("¡El ganador es: " + name + "!");
    }

}
