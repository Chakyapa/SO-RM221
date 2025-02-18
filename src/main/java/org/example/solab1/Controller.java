package org.example.solab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.solab1.remote.MeteoAPI;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private Label temperature;
    @FXML
    private Label humidity;
    @FXML
    private Label precipitation;
    @FXML
    private Label water;
    @FXML
    private TextField waterInput;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;

    private Timer waterTimer;
    private Timer meteoTimer;
    private Timer notificationTimer;

    private int mlTotal = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startMeteoUpdates();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 7));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        water.setText(mlTotal + " ml");
    }

    public void onAddWater() {
        try {
            int mlDrink = Integer.parseInt(waterInput.getText());
            int ml = Integer.parseInt(water.getText().split(" ")[0]);

            mlTotal = ml + mlDrink;

            water.setText(mlTotal + " ml");
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid water value");
            alert.showAndWait();
        }
    }

    public void onSetReminder() {
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        startWaterSchedule(hour, minute);
    }

    protected void startMeteoUpdates() {
        meteoTimer = new Timer();
        meteoTimer.scheduleAtFixedRate(new Meteo(this), 0, 2000);
    }

    private void startWaterSchedule(int hour, int minute) {
        waterTimer = new Timer();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Date date = calendar.getTime();

        if (date.before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
        }

        waterTimer.schedule(new WaterMessage("It's time to drink water!!!"), date);
    }

    public void updateTemperature(double temperature, double humidity, double precipitation) {
        Platform.runLater(() -> {
            this.temperature.setText(temperature + "°C");
            this.humidity.setText(humidity + "%");
            this.precipitation.setText(precipitation + "mm");

        });
    }

    public void stopTasks() {
        stopMeteoUpdates();
        stopWaterSchedule();
    }

    private void stopMeteoUpdates() {
        if (meteoTimer != null) {
            meteoTimer.cancel();
            meteoTimer.purge();
            meteoTimer = null;
        }
    }

    private void stopWaterSchedule() {
        if (waterTimer != null) {
            waterTimer.cancel();
            waterTimer.purge();
            waterTimer = null;
        }
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
            System.out.println("Meteo API Error");
        }
    }
}

class WaterMessage extends TimerTask {
    private final String message;

    public WaterMessage(String message) {
        this.message = message;
    }

    public void showNotification(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Water Message");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(alert::close);
                }
            }, 30 * 1000);
        });
    }

    @Override
    public void run() {
        showNotification(message);
    }
}
