package com.example.proyecto3_.view;

import com.example.proyecto3_.controller.WinController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
/**
 * Represents the Win window of the application.
 * <p>
 * This class follows the Singleton pattern to ensure that only one instance
 * of the Win window exists at a time. It loads the {@code Win-view.fxml} file
 * and initializes its associated {@link WinController}.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Displays the Win screen when the player wins the game.</li>
 *     <li>Loads and connects the FXML file with its controller.</li>
 *     <li>Uses {@code StageStyle.UNDECORATED} for a clean window design.</li>
 *     <li>Ensures only one instance of the window is created (Singleton pattern).</li>
 * </ul>
 *
 * @author Andres Muñoz,Juan Camilo,Juan Manuel
 * @version 1.0
 */
public class Win extends Stage {
    /** Singleton instance of the {@link Win} class. */
    private static Win instance;
    /** Controller associated with the Win view. */
    private WinController controller;
    /**
     * Private constructor that initializes and displays the Win window.
     * <p>
     * Loads the FXML file, retrieves its controller, sets up the scene and
     * window properties, and displays the window to the user.
     * </p>
     *
     * @throws IOException if the FXML file cannot be found or loaded.
     */
    private Win() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto3_/Win-view.fxml"));
        Parent root = loader.load();

        controller = loader.getController();

        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Win");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image(Win.class.getResourceAsStream("/com/example/proyecto3_/Img/Icon.png")));
        show();
    }
    /**
     * Returns the single instance of the {@link Win} window.
     * <p>
     * If the instance does not exist, it creates a new one; otherwise, it returns the existing instance.
     * </p>
     *
     * @return the singleton {@link Win} instance.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static Win getInstance() throws IOException {
        if (instance == null) {
            instance = new Win();
        }
        return instance;
    }
    /**
     * Closes and removes the current instance of the {@link Win} window.
     * <p>
     * This method ensures that the window can be reopened later by recreating the instance.
     * </p>
     */
    public static void deleteInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
    /**
     * Returns the controller associated with the Win window.
     *
     * @return the {@link WinController} instance managing this window.
     */
    public WinController getController() {
        return controller;
    }
}
