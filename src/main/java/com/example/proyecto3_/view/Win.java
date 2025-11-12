package com.example.proyecto3_.view;

import com.example.proyecto3_.controller.WinController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Win extends Stage {

    private static Win instance;
    private WinController controller;

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

    public static Win getInstance() throws IOException {
        if (instance == null) {
            instance = new Win();
        }
        return instance;
    }

    public static void deleteInstance() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public WinController getController() {
        return controller;
    }
}
