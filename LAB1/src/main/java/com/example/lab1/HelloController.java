package com.example.lab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import java.util.Timer;
import java.util.TimerTask;

public class HelloController {

    
    @FXML
    private Label label;
    @FXML// Метка из FXML
    private Circle ball1;
    @FXML
    private ImageView gifView;

    private Timer timer;
    private Timer timer1;
    private Timer timer2;
    private int counter = 30;
    private int counter1 = 0;
    private Paint color = Color.web("#f4f4f4");

    public void initialize() {
        Image gifImage = new Image(getClass().getResource("/com/example/lab1/1500538730192556632.gif").toExternalForm());
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> label.setText("Timer: " + (--counter)));
            }
        }, 0, 1000);
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> ball1.setCenterX((counter1++) + 1));
            }
        }, 0, 50);
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (counter == 0) {
                    timer1.cancel();
                    ball1.setFill(color);
                    javafx.application.Platform.runLater(() ->  gifView.setImage(gifImage));

                }
                if(counter == -5){
                    timer.cancel();
                    Platform.runLater(() -> gifView.setImage(null));
                    timer2.cancel();

                }
            }
        }, 0, 1000);

    }
}
