package com.example.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DiningPhilosophers extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(com.example.lab3.DiningPhilosophers.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Table");
        stage.setScene(scene);
        stage.show();

        int maxEatingTime = 5;
        Philosopher[] philosophers = new Philosopher[18];
        Object[] forks = new Object[philosophers.length];

        HelloController controller = fxmlLoader.getController();

        for (int i = 0; i < philosophers.length; i++) {
            forks[i] = new Object();
        }

        for(int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            if( i == philosophers.length - 1) {
                // Последний философ берет правую вилку первой
                philosophers[i] = new Philosopher(rightFork, leftFork, maxEatingTime, controller);
            }else {
                philosophers[i] = new Philosopher(leftFork,rightFork, maxEatingTime, controller);
            }
            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
