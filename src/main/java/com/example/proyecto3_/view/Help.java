package com.example.proyecto3_.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
/**
 * Represents the Help window of the application.
 * <p>
 * This class implements the Singleton pattern using a private static inner {@code Holder} class.
 * It loads the "Rules-view.fxml" file and displays the help or rules interface of the game.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Loads the rules or help section of the game.</li>
 *     <li>Uses a custom undecorated window style.</li>
 *     <li>Displays the application icon and title.</li>
 *     <li>Ensures only one instance of the Help window exists at a time.</li>
 * </ul>
 *
 * @author Andres Muñoz,Juan Camilo,Juan Manuel
 * @version 1.0
 */
public class Help extends Stage {
    /**
     * Private constructor that initializes and displays the Help window.
     * <p>
     * Loads the FXML file for the rules view, sets the title, icon, and custom style,
     * and shows the window to the user.
     * </p>
     *
     * @throws IOException if the FXML file cannot be found or loaded.
     */
    private Help() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto3_/Rules-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Help");
        setResizable(false);
        initStyle(StageStyle.UNDECORATED);
        getIcons().add(new Image(Win.class.getResourceAsStream("/com/example/proyecto3_/Img/Icon.png")));
        show();
    }
    /**
     * Inner static holder class that contains the singleton instance of {@link Help}.
     * <p>
     * This ensures thread-safe lazy initialization of the instance.
     * </p>
     */
    private static class Holder {
        private static Help INSTANCE = null;
    }
    /**
     * Creates or returns the existing instance of the Help window.
     * <p>
     * If no instance exists, a new one is created and displayed.
     * </p>
     *
     * @throws IOException if the FXML file cannot be loaded.
     */
    public static void getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE != null ?
                Holder.INSTANCE : new Help();
    }
    /**
     * Closes and removes the current instance of the Help window.
     * <p>
     * This method allows reopening the Help window later if needed.
     * </p>
     */
    public static void deleteInstance() {
        Holder.INSTANCE.close();
        Holder.INSTANCE = null;
    }

}

