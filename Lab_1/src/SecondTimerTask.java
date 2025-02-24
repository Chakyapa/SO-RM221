import java.util.TimerTask;

class SecondTimerTask extends TimerTask {
    final private ProductivityTimerApplication productivityTimerApplication;

    public SecondTimerTask(ProductivityTimerApplication productivityTimerApplication) {
        this.productivityTimerApplication = productivityTimerApplication;
    }

    @Override
    public void run() {
        productivityTimerApplication.decrementSeconds();
    }
}