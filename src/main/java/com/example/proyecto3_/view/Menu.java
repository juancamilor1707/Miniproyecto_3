package com.example.proyecto3_.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;
/**
 * Represents the main menu window of the application.
 * <p>
 * This class implements the Singleton pattern using a private static inner {@code Holder} class.
 * It loads the FXML view for the menu and configures the JavaFX stage properties.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Loads and displays the main menu view of the game.</li>
 *     <li>Uses {@code StageStyle.UNDECORATED} for a clean custom appearance.</li>
 *     <li>Displays the application icon and title.</li>
 *     <li>Ensures only one menu window instance exists at any given time.</li>
 * </ul>
 *
 * @author
 * @version 1.0 Andres Muñoz,Juan Camilo,Juan Manuel
 */
public class Menu extends Stage {
    /**
     * Private constructor that initializes and displays the Menu window.
     * <p>
     * Loads the {@code Menu-view.fxml} file, sets the scene, title, icon, and style,
     * and displays the menu interface.
     * </p>
     *
     * @throws IOException if the FXML file cannot be found or loaded.
     */
    private Menu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto3_/Menu-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Menu");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image(Win.class.getResourceAsStream("/com/example/proyecto3_/Img/Icon.png")));
        show();
    }
    /**
     * Inner static class that holds the singleton instance of {@link Menu}.
     * <p>
     * This ensures lazy initialization and thread safety for the singleton pattern.
     * </p>
     */
    private static class Holder {
        private static Menu INSTANCE = null;
    }
    /**
     * Returns the single instance of the {@link Menu} window.
     * <p>
     * If no instance exists, it creates a new one; otherwise, it returns the existing instance.
     * </p>
     *
     * @return the singleton {@link Menu} instance.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static Menu getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE != null ?
                Holder.INSTANCE : new Menu();
        return Holder.INSTANCE;
    }
    /**
     * Closes and removes the current instance of the {@link Menu} window.
     * <p>
     * This method allows reopening the Menu later by recreating the instance.
     * </p>
     */
    public static void deleteInstance() {
        Holder.INSTANCE.close();
        Holder.INSTANCE = null;
    }
}
