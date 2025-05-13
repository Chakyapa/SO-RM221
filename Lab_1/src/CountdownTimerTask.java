import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

class CountdownTimerTask extends TimerTask {
    final private ProductivityTimerApplication productivityTimerApplication;

    public CountdownTimerTask(ProductivityTimerApplication productivityTimerApplication) {
        this.productivityTimerApplication = productivityTimerApplication;
    }

    @Override
    public void run() {
        productivityTimerApplication.countdownFinished();
    }
}