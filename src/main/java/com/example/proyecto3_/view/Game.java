package com.example.proyecto3_.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * View class for the game window using Holder pattern
 */
public class Game extends Stage {

    /**
     * Private constructor to initialize the game view
     * @throws IOException if FXML file cannot be loaded
     */
    private Game() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/proyecto3_/Game-view.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Juego");
        setResizable(false);
        show();
    }

    /**
     * Holder class for lazy singleton initialization
     */
    private static class Holder {
        private static Game INSTANCE = null;
    }

    /**
     * Gets the singleton instance of the game view
     * @return the Game instance
     * @throws IOException if FXML file cannot be loaded
     */
    public static Game getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE == null ?
                new Game() : Holder.INSTANCE;
        return Holder.INSTANCE;
    }

    /**
     * Deletes the current instance and closes the window
     */
    public static void deleteInstance() {
            Holder.INSTANCE.close();
            Holder.INSTANCE = null;
    }
}