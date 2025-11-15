package com.example.proyecto3_.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Represents the main game window of the application.
 * <p>
 * This class follows the Singleton pattern using a private static inner {@code Holder} class
 * to ensure that only one instance of the game window exists during runtime.
 * It loads the corresponding FXML view and initializes the JavaFX stage configuration.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Loads the game interface from the FXML file.</li>
 *     <li>Uses {@code StageStyle.UNDECORATED} for a custom window design.</li>
 *     <li>Displays the game icon and title.</li>
 *     <li>Ensures only one active instance of the window is running at a time.</li>
 * </ul>
 *
 * @author Andres Felipe,Juan Camilo Ramos,Juan Manuel
 * @version 1.0
 */
public class Game extends Stage {

    /**
     * Private constructor that initializes the game window.
     * <p>
     * Loads the FXML file, sets up the scene, title, icon, and window style.
     * </p>
     *
     * @throws IOException if the FXML file cannot be loaded or is missing.
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
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image(Win.class.getResourceAsStream("/com/example/proyecto3_/Img/Icon.png")));
        show();
    }

    /**
     * Inner static class that holds the singleton instance of {@link Game}.
     * <p>
     * This pattern ensures thread-safe lazy initialization of the singleton instance.
     * </p>
     */
    private static class Holder {
        private static Game INSTANCE = null;
    }

    /**
     * Returns the single instance of the {@link Game} window.
     * <p>
     * If no instance exists, it creates a new one; otherwise, it returns the existing instance.
     * </p>
     *
     * @return the singleton {@link Game} instance.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static Game getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE == null ?
                new Game() : Holder.INSTANCE;
        return Holder.INSTANCE;
    }

    /**
     * Closes and removes the current instance of the {@link Game} window.
     * <p>
     * This method should be called when the game is finished or restarted,
     * ensuring proper resource management and allowing the instance to be recreated later.
     * </p>
     */
    public static void deleteInstance() {
            Holder.INSTANCE.close();
            Holder.INSTANCE = null;
    }
}