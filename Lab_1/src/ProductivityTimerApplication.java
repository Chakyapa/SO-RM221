import java.util.Timer;



public class ProductivityTimerApplication {

    // сколько раз менять мигать цветом при срабатывании таймера
    static final int COLOR_ANIMATION_PERIOD = 500;
    static final int COLOR_ANIMATION_NUM = 5;

    // переменная количество таймеров
    private static int apps_num = 0;

    // переменные отсчета времени
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    // таймера
    private Timer secondTimer = null;               // для анимации секундной стрелки
    private Timer minuteTimer = null;               // для анимации минутной стрелки
    private Timer hourTimer = null;                 // для анимации часовой стрелки
    private Timer countdownTimer = null;            // для отсчета времени таймера
    private Timer colorAnimationTimer = null;       // для анимации срабатывания таймера (мигание цветом)

    private int colorAnimationCount = 0;            // счетчик анимации миганий цветом

    private CustomColor animationColor = CustomColor.BLACK;     // переменная текущего цвета


    private final ProductivityTimerGUI gui;         // объект графического интерфейса

    public ProductivityTimerApplication() {
        gui = new ProductivityTimerGUI(this, "Productivity Timer " + apps_num);        // создать объект графического интерфейса
        apps_num++;
    }

    // метод, который запускает таймер
    public void startTimer(int hours, int minutes, int seconds) {

        // остановка таймеров (на случай если был нажат Старт когда таймер уже запущен
        stopTimer();

        if (colorAnimationTimer != null) {
            colorAnimationTimer.cancel();
        }

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.animationColor = CustomColor.BLACK;

        gui.repaint();

        // создаем таймеры
        secondTimer = new Timer();
        minuteTimer = new Timer();
        hourTimer = new Timer();
        countdownTimer = new Timer();

        long totalMilliseconds = (hours * 3600L + minutes * 60L + seconds) * 1000L;
        countdownTimer.schedule(new CountdownTimerTask(this), totalMilliseconds);

        secondTimer.scheduleAtFixedRate(new SecondTimerTask(this), 1000, 1000);
        minuteTimer.scheduleAtFixedRate(new MinuteTimerTask(this), (seconds + 1) * 1000L, 60000);
        hourTimer.scheduleAtFixedRate(new HourTimerTask(this), (seconds + 1 + minutes * 60L) * 1000L, 3600000);

    }

    // метод, который останавливает работу таймера
    public void stopTimer() {
        if (secondTimer != null) secondTimer.cancel();
        if (minuteTimer != null) minuteTimer.cancel();
        if (hourTimer != null) hourTimer.cancel();
        if (countdownTimer != null) countdownTimer.cancel();
    }

    public void quit() {
        stopTimer();
        if (colorAnimationTimer != null) {
            colorAnimationTimer.cancel();
        }
        Main.closeApp(this);
    }

    public void newTimer() {
        Main.newApp();
    }

    public void decrementSeconds() {
        if (seconds + minutes + hours > 0)
        {
            if (seconds == 0) {
                seconds = 59;
            } else {
                seconds--;
            }
            gui.repaint();
        }

    }

    public void decrementMinutes() {
        if (minutes + hours > 0) {
            if (minutes == 0) {
                minutes = 59;
            } else {
                minutes--;
            }

            gui.repaint();
        }
    }

    public void decrementHours() {
        if (hours > 0) {
            hours--;
            gui.repaint();
        }

    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public CustomColor getAnimationColor() {
        return animationColor;
    }



    void countdownFinished() {
        stopTimer();
        hours = 0;
        minutes = 0;
        seconds = 0;
        gui.repaint();
        startColorAnimation();
        gui.playEndSound();

    }

    void colorAnimation()
    {
        colorAnimationCount--;
        if (colorAnimationCount % 2 == 1) {
            animationColor = CustomColor.RED;
            gui.updateDigitalClock();
        }
        else
        {
            animationColor = CustomColor.BLACK;
            gui.updateDigitalClock();
        }

        if (colorAnimationCount == 0) {
            colorAnimationTimer.cancel();
        }
    }

    private void startColorAnimation() {
        colorAnimationCount = COLOR_ANIMATION_NUM * 2;
        colorAnimationTimer = new Timer();
        colorAnimationTimer.schedule(new ColorAnimationTask(this), 0, COLOR_ANIMATION_PERIOD);
    }

}









