import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.Date;

class Message extends TimerTask {
    private JLabel label;

    public Message(JLabel label) {
        this.label = label;
    }
    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> label.setText("Lunch time!"));
    }
}

public class PomodoroTimer {
    private JLabel timerLabel;
    private JLabel pomodoroLabel;
    private JLabel lunchLabel;
    private JButton startButton, stopButton, resetButton;
    private JTextField workTimeField, breakTimeField;

    private Timer timer;
    private TimerTask task;
    private int secPassed = 0;

    private long remainingTime;
    private int pomodoroTime = 25 * 60;
    private int shortBreakTime = 5 * 60;
    private boolean workMode = true;
    private boolean isRunning = false;
    private long endTime;

    public PomodoroTimer() {
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new FlowLayout());

        timerLabel = new JLabel("Прошло 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(timerLabel);

        pomodoroLabel = new JLabel("Работа: 25:00");
        pomodoroLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(pomodoroLabel);

        frame.add(new JLabel("Время работы (мин):"));
        workTimeField = new JTextField("25", 5);
        frame.add(workTimeField);

        frame.add(new JLabel("Время отдыха (мин):"));
        breakTimeField = new JTextField("5", 5);
        frame.add(breakTimeField);

        startButton = new JButton("Старт/Продолжить");
        stopButton = new JButton("Стоп");
        resetButton = new JButton("Сброс");

        frame.add(startButton);
        frame.add(stopButton);
        frame.add(resetButton);

        lunchLabel = new JLabel("");
        lunchLabel.setFont(new Font("Arial", Font.BOLD, 18));
        lunchLabel.setForeground(Color.RED);
        frame.add(lunchLabel);

        startButton.addActionListener(e -> startPomodoro());
        stopButton.addActionListener(e -> stopPomodoro());
        resetButton.addActionListener(e -> resetPomodoro());

        remainingTime = pomodoroTime;

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (timer != null) {
                    timer.cancel();
                    System.out.println("The Purge value:" + timer.purge());
                }
            }
        });

        frame.setVisible(true);
        startTimer();
        scheduleLunchNotification();
    }

    private void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                secPassed++;
                updateLabel();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void updateLabel() {
        int minPas = secPassed / 60;
        int secPas = secPassed % 60;
        timerLabel.setText("Прошло " + String.format("%02d:%02d", minPas, secPas));

        int min = (int) (remainingTime / 60);
        int sec = (int) (remainingTime % 60);
        pomodoroLabel.setText((workMode ? "Работа: " : "Отдых: ") + String.format("%02d:%02d", min, sec));
    }

    private void startPomodoro() {
        if (isRunning) return;
        isRunning = true;

        try {
            pomodoroTime = Integer.parseInt(workTimeField.getText()) * 60;
            shortBreakTime = Integer.parseInt(breakTimeField.getText()) * 60;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Введите корректное число минут!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        remainingTime = workMode ? pomodoroTime : shortBreakTime;
        endTime = System.currentTimeMillis() + (remainingTime * 1000);

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                remainingTime = (endTime - System.currentTimeMillis()) / 1000;
                if (remainingTime > 0) {
                    updateLabel();
                } else {
                    timer.cancel();
                    System.out.println("The Purge value:" + timer.purge());
                    isRunning = false;
                    workMode = false;
                    startShortBreak();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void stopPomodoro() {
        if (timer != null) {
            timer.cancel();
            System.out.println("The Purge value:" + timer.purge());
            isRunning = false;
        }
    }

    private void resetPomodoro() {
        stopPomodoro();
        workMode = true;
        isRunning = false;
        remainingTime = pomodoroTime;
        updateLabel();
    }

    private void startShortBreak() {
        workMode = false;
        remainingTime = shortBreakTime;
        endTime = System.currentTimeMillis() + (remainingTime * 1000);

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                remainingTime = (endTime - System.currentTimeMillis()) / 1000;
                if (remainingTime > 0) {
                    updateLabel();
                } else {
                    timer.cancel();
                    System.out.println("The Purge value:" + timer.purge());
                    isRunning = false;
                    resetPomodoro();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private Timer timer1;
    private TimerTask lunchTask;

    private void scheduleLunchNotification() {
        if (lunchTask != null) {
            lunchTask.cancel();
        }
        if (timer1 != null) {
            System.out.println("The Purge value:" + timer1.purge());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 40);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();

        Timer timer1 = new Timer();
        timer1.schedule(new Message(lunchLabel), date);
    }
}