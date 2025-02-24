import java.util.TimerTask;

class MinuteTimerTask extends TimerTask {
    final private ProductivityTimerApplication productivityTimerApplication;

    public MinuteTimerTask(ProductivityTimerApplication productivityTimerApplication) {
        this.productivityTimerApplication = productivityTimerApplication;
    }

    @Override
    public void run() {
        productivityTimerApplication.decrementMinutes();
    }
}