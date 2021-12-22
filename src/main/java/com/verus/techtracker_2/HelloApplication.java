package com.verus.techtracker_2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HelloApplication extends Application {


    private final MainViewController mvc = new MainViewController();

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root, 1920, 1000);
        stage.setScene(scene);
        stage.setTitle("TechTracker Reborn");

        stage.getIcons().add(new Image("file:icon.png"));
        stage.show();


    }

    public static void main(String[] args) {
        launch();

    }
}