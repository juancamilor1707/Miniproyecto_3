package com.example.proyecto3_;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.proyecto3_.view.Menu;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) throws IOException {
        Menu.getInstance();
    }
}
