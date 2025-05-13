import java.util.TimerTask;

class HourTimerTask extends TimerTask {
    final private ProductivityTimerApplication productivityTimerApplication;

    public HourTimerTask(ProductivityTimerApplication productivityTimerApplication) {
        this.productivityTimerApplication = productivityTimerApplication;
    }

    @Override
    public void run() {
        productivityTimerApplication.decrementHours();
    }
}