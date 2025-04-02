package com.example.lab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Library");
        stage.setScene(scene);
        stage.show();


        HelloController controller = fxmlLoader.getController();


        new Writer("Writer1", controller).start();
        new Writer("Writer2", controller).start();
        new Writer("Writer3", controller).start();
        new Writer("Writer4", controller).start();


        new Reader("Reader1", controller,6).start();
        new Reader("Reader2", controller,6).start();
        new Reader("Reader3", controller,6).start();
        new Reader("Reader4", controller,6).start();
        new Reader("Reader5", controller,6).start();
        new Reader("Reader6", controller,6).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
