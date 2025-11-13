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
 * @author
 * @version 1.0
 */
public class Help extends Stage {

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

    private static class Holder {
        private static Help INSTANCE = null;
    }

    public static void getInstance() throws IOException {
        Holder.INSTANCE = Holder.INSTANCE != null ?
                Holder.INSTANCE : new Help();
    }

    public static void deleteInstance() {
        Holder.INSTANCE.close();
        Holder.INSTANCE = null;
    }

}

