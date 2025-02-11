package org.example.solab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.solab1.remote.MeteoAPI;

import java.util.*;

public class Controller {
    @FXML
    private Label temperature;
    @FXML
    private Label humidity;
    @FXML
    private Label precipitation;
    @FXML
    private Label water;

    @FXML
    protected void onHelloButtonClick() {
        startMeteoUpdates();
        startWaterSchedule();
    }

    private void startMeteoUpdates() {
        Timer meteoTimer = new Timer();
        meteoTimer.scheduleAtFixedRate(new Meteo(this), 0, 2000);
    }

    private void startWaterSchedule() {
        Timer waterTimer = new Timer();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 17);
        calendar.set(Calendar.SECOND, 0);

        Date date = calendar.getTime();

        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
        }

        waterTimer.schedule(new WaterMessage("It's time to drink water!!!",this), date);
    }

    public void updateTemperature(double temperature, double humidity, double precipitation) {
        Platform.runLater(() -> {
            this.temperature.setText("Temperatura: " + temperature + "°C");
            this.humidity.setText("Humidade: " + humidity + "%");
            this.precipitation.setText("Precipitation: " + precipitation + "mm");

        });
        System.out.println("Temperatura: " + temperature);
    }

    public void setWaterSchedule(String waterTime) {
        Platform.runLater(() -> water.setText(waterTime));
    }
}

class Meteo extends TimerTask {
    private final MeteoAPI meteoAPI = new MeteoAPI();
    private final Controller controller;

    public Meteo(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            HashMap<String, Double> meteoInfo = meteoAPI.getMeteoInfo();

            controller.updateTemperature(meteoInfo.get("temperature"), meteoInfo.get("humidity"), meteoInfo.get("precipitation"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class WaterMessage extends TimerTask {
    private final String message;
    private final Controller controller;

    public WaterMessage(String message, Controller controller) {
        this.message = message;
        this.controller = controller;
    }

    @Override
    public void run() {

        controller.setWaterSchedule(message);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                controller.setWaterSchedule("");
            }
        }, 60 * 1000);
    }
}
