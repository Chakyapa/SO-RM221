import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ProductivityTimerGUI extends JPanel{

    // путь к звуковой дорожке, которая звучит при окончании таймера
    static final String END_SOUND_PATH = "C:/Users/innak/Desktop/tum/3curs/2sem/SO/SO-RM221/sound.wav";

    // размеры окна приложения
    static final int WINDOW_WIDTH = 800;
    static final int WINDOW_HEIGHT = 600;

    // радиус циферблата
    static final int DIAL_RADIUS = 150;
    // длины стрелок
    static final int HOURS_ARROW_LENGTH = 60;
    static final int MINUTES_ARROW_LENGTH = 80;
    static final int SECONDS_ARROW_LENGTH = 100;

    // цвета стрелок
    static final Color HOURS_ARROW_COLOR = Color.GREEN;
    static final Color MINUTES_ARROW_COLOR = Color.BLUE;
    static final Color SECONDS_ARROW_COLOR = Color.RED;



    // поля для ввода времени таймера
    private final JTextField hourInput;
    private final JTextField minuteInput;
    private final JTextField secondInput;

    // кнопки управлением таймером
    private JButton startButton;
    private JButton stopButton;
    private JButton newButton;



    JLabel digitalClockLabel;

    private ProductivityTimerApplication app;

    public ProductivityTimerGUI(ProductivityTimerApplication app, String title) {

        this.app = app;

        setLayout(new BorderLayout());

        // панель инструментов управления: ввод данных и кнопки управления
        JPanel inputPanel = new JPanel();
        hourInput = new JTextField("0", 3);
        minuteInput = new JTextField("0", 3);
        secondInput = new JTextField("0", 3);
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        newButton = new JButton("New Timer");

        inputPanel.add(new JLabel("Hours:"));
        inputPanel.add(hourInput);
        inputPanel.add(new JLabel("Minutes:"));
        inputPanel.add(minuteInput);
        inputPanel.add(new JLabel("Seconds:"));
        inputPanel.add(secondInput);
        inputPanel.add(startButton);
        inputPanel.add(stopButton);
        inputPanel.add(newButton);

        add(inputPanel, BorderLayout.NORTH);

        // Панель цифрового отображения времени
        digitalClockLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        digitalClockLabel.setFont(new Font("Serif", Font.BOLD, 32));
        add(digitalClockLabel, BorderLayout.CENTER);

        // иницализация действий по нажатию кнопок
        startButton.addActionListener(e -> startButtonClick());
        stopButton.addActionListener(e -> stopButtonClick());
        newButton.addActionListener(e -> newButtonClick());

        // создание окна таймера
        JFrame frame = new JFrame(title);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);

        // Добавление обработчика события закрытия окна
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.quit();
                // Дополнительные действия по освобождению ресурсов, если необходимо
            }
        });

    }

    private void newButtonClick() {
        app.newTimer();
    }

    private void startButtonClick() {
        int hours, minutes, seconds;

        try {
            hours = Integer.parseInt(hourInput.getText());
            minutes = Integer.parseInt(minuteInput.getText());
            seconds = Integer.parseInt(secondInput.getText());
            if (minutes < 0 || minutes > 59) {
                throw new NumberFormatException();
            }

            if (seconds < 0 || seconds > 59) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Введите корректное время!");
            return;
        }

        app.startTimer(hours, minutes, seconds);
    }

    private void stopButtonClick() {
        app.stopTimer();
    }


    public void updateDigitalClock() {
        Color swing_color = convertColor(app.getAnimationColor());
        digitalClockLabel.setForeground(swing_color);
        int hours = app.getHours();
        int minutes = app.getMinutes();
        int seconds = app.getSeconds();
        digitalClockLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private Color convertColor(CustomColor color) {
        return switch (color) {
            case BLACK -> Color.BLACK;
            case RED -> Color.RED;
            default -> Color.WHITE;
        };
    }

    public void playEndSound() {
        try {
            File soundFile = new File(END_SOUND_PATH);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.addLineListener(event -> {             //для уверенности что ресурсы будут освобождены
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateDigitalClock();

        // определить координаты центра
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // отрисовка циферблата
        g.setColor(Color.BLACK);
        g.drawOval(centerX - DIAL_RADIUS, centerY - DIAL_RADIUS, 2 * DIAL_RADIUS, 2 * DIAL_RADIUS);

        // числа циферблата
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            // вычисление координат числа
            int x = centerX + (int) (DIAL_RADIUS * 0.85 * Math.cos(angle));
            int y = centerY + (int) (DIAL_RADIUS * 0.85 * Math.sin(angle));
            g.drawString(Integer.toString(i), x - 5, y + 5);
        }

        // стрелки циферблата
        int hours = app.getHours();
        int minutes = app.getMinutes();
        int seconds = app.getSeconds();
        drawHand(g, centerX, centerY, (hours % 12) * 30 + minutes / 2, HOURS_ARROW_LENGTH, HOURS_ARROW_COLOR);
        drawHand(g, centerX, centerY, minutes * 6, MINUTES_ARROW_LENGTH, MINUTES_ARROW_COLOR);
        drawHand(g, centerX, centerY, seconds * 6, SECONDS_ARROW_LENGTH, SECONDS_ARROW_COLOR);
    }

    // метод отрисовки стрелок
    private void drawHand(Graphics g, int x, int y, int angle, int length, Color color) {
        double radians = Math.toRadians(angle - 90);
        int endX = x + (int) (length * Math.cos(radians));
        int endY = y + (int) (length * Math.sin(radians));
        g.setColor(color);
        g.drawLine(x, y, endX, endY);
    }
}
