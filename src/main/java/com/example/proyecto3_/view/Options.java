package com.example.proyecto3_.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
/**
 * Represents the Options window of the application.
 * <p>
 * This class uses the Singleton pattern with a private static inner {@code Holder} class
 * to ensure that only one instance of the options window exists at any time.
 * It loads and displays the {@code Options-view.fxml} interface.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Loads and shows the Options view of the game.</li>
 *     <li>Uses {@code StageStyle.UNDECORATED} for a modern and clean appearance.</li>
 *     <li>Displays the game icon and sets the window title.</li>
 *     <li>Guarantees a single active instance through the Singleton pattern.</li>
 * </ul>
 *
 * @author Andres Muñoz,Juan Camilo,Juan Manuel
 * @version 1.0
 */
public class Options extends Stage {
    /**
     * Private constructor that initializes and displays the Options window.
     * <p>
     * Loads the FXML file, sets up the stage properties such as title, style, and icon,
     * and shows the window.
     * </p>
     *
     * @throws IOException if the FXML file cannot be found or loaded.
     */
    private Options() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto3_/Options-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Opciones");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image(Win.class.getResourceAsStream("/com/example/proyecto3_/Img/Icon.png")));
        show();
    }
    /**
     * Inner static class that holds the singleton instance of {@link Options}.
     * <p>
     * This ensures lazy initialization and thread safety.
     * </p>
     */
    private static class Holder {
        private static Options INSTANCE = null;
    }
    /**
     * Returns the single instance of the {@link Options} window.
     * <p>
     * If the instance does not exist, it creates a new one; otherwise, it returns the existing instance.
     * </p>
     *
     * @return the singleton {@link Options} instance.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static Options getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE != null ?
                Holder.INSTANCE : new Options();
        return Holder.INSTANCE;
    }
    /**
     * Closes and removes the current instance of the {@link Options} window.
     * <p>
     * This allows the Options window to be reopened later when needed.
     * </p>
     */
    public static void deleteInstance() {
        Holder.INSTANCE.close();
        Holder.INSTANCE = null;
    }
}
