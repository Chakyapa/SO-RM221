import java.awt.*;
import java.util.TimerTask;

class ColorAnimationTask extends TimerTask {
    final private ProductivityTimerApplication productivityTimerApplication;

    public ColorAnimationTask(ProductivityTimerApplication productivityTimerApplication) {
        this.productivityTimerApplication = productivityTimerApplication;
    }

    @Override
    public void run() {
        productivityTimerApplication.colorAnimation();
    }
}