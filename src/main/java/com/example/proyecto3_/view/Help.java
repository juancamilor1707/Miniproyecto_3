package com.example.proyecto3_.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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

