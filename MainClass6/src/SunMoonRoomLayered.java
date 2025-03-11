import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SunMoonRoomLayered {
    // Флаг завершения анимации, по умолчанию false
    public static boolean animationsFinished = false;
    // Статические ссылки на панели анимации, чтобы можно было их скрыть
    public static WindowBackgroundPanel windowBackgroundPanel;
    public static WindowFramePanel windowFramePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sun & Moon in Room");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(500, 500));

            // Фон комнаты (нижний слой)
            RoomPanel roomPanel = new RoomPanel();
            roomPanel.setBounds(0, 0, 500, 500);
            layeredPane.add(roomPanel, Integer.valueOf(0));

            // Панель с анимацией (солнце/луна) – средний слой
            windowBackgroundPanel = new WindowBackgroundPanel();
            windowBackgroundPanel.setBounds(140, 38, 200, 264);
            layeredPane.add(windowBackgroundPanel, Integer.valueOf(1));

            // Панель с рамками окна (ставни и обрамление) – верхний слой
            windowFramePanel = new WindowFramePanel();
            windowFramePanel.setBounds(140, 38, 200, 264);
            layeredPane.add(windowFramePanel, Integer.valueOf(2));

            frame.add(layeredPane);
            frame.setVisible(true);
        });
    }
}

// ---------------------------------------------------
// Фон комнаты и статичные элементы (с «часами» на столе)
// ---------------------------------------------------
class RoomPanel extends JPanel {
    // Храним текущее время как строку
    private String currentTime = "";
    // Формат отображения времени (часы:минуты:секунды)
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public RoomPanel() {
        // Таймер, который раз в секунду обновляет время
        Timer clockTimer = new Timer(1000, e -> {
            currentTime = LocalTime.now().format(timeFormatter);
            repaint();
        });
        clockTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Стены комнаты
        g.setColor(Color.decode("#f2b84b"));
        g.fillRect(0, 0, 500, 40);    // верхняя стена
        g.fillRect(0, 300, 500, 450);  // нижняя стена
        g.fillRect(0, 0, 140, 500);    // левая стена
        g.fillRect(340, 0, 160, 500);  // правая стена

        // Отрисовка стола и "экрана" часов
        g.setColor(Color.decode("#000000"));
        g.drawRect(25, 303, 101, 42);

        g.setColor(Color.decode("#ad5e18"));
        g.fillRect(0, 344, 200, 120);

        g.setColor(Color.decode("#bf6f28"));
        g.fillRect(5, 354, 190, 110);

        g.setColor(Color.decode("#a7b0b0"));
        g.fillRect(26, 304, 100, 40);

        g.setColor(Color.decode("#3a3d3d"));
        g.fillRect(30, 308, 92, 32);

        // Отрисовка времени на часах только если анимация не завершена
        if (!SunMoonRoomLayered.animationsFinished) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(currentTime);
            int textHeight = fm.getAscent();

            int clockX = 30;
            int clockY = 308;
            int clockWidth = 92;
            int clockHeight = 32;

            int x = clockX + (clockWidth - textWidth) / 2;
            int y = clockY + (clockHeight + textHeight) / 2 - 2; // коррекция по вертикали

            g.drawString(currentTime, x, y);
        }
    }
}


// Панель с анимацией, использующая два таймера: один для солнца, другой для луны
class WindowBackgroundPanel extends JPanel {
    private static final int SUN_DIAMETER = 80;
    private static final int MOON_DIAMETER = 80;
    private double angle = Math.PI;
    private double angleStep = 0.02;
    private boolean isSun = true;

    private Timer sunTimer;
    private Timer moonTimer;

    public WindowBackgroundPanel() {
        setOpaque(true);

        // Таймер для солнца
        sunTimer = new Timer(16, e -> {
            angle += angleStep;
            if (angle >= 2 * Math.PI) {
                sunTimer.stop();
                angle = Math.PI; // сброс угла для цикла луны
                isSun = false;   // переключаемся на луну
                moonTimer.start();
            }
            repaint();
        });

        // Таймер для луны
        moonTimer = new Timer(16, e -> {
            angle += angleStep;
            if (angle >= 2 * Math.PI) {
                // Завершаем анимацию: останавливаем таймер луны
                moonTimer.stop();
                angle = Math.PI; // сброс угла (опционально)
                // Устанавливаем флаг завершения анимации
                SunMoonRoomLayered.animationsFinished = true;
                // Скрываем панели с анимацией и рамкой
                this.setVisible(false);
                SunMoonRoomLayered.windowFramePanel.setVisible(false);
            }
            repaint();
        });

        // Начинаем с анимации солнца
        sunTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Фон окна: светлый (день) или тёмный (ночь)
        g.setColor(isSun ? Color.decode("#34cceb") : Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawSunMoon(g);
    }

    private void drawSunMoon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int radius = Math.min(w, h) / 2 - (SUN_DIAMETER / 2);
        int centerX = w / 2;
        int centerY = h / 2;

        double x = centerX + radius * Math.cos(angle);
        double y = centerY + radius * Math.sin(angle);

        int drawX = (int) Math.round(x - SUN_DIAMETER / 2.0);
        int drawY = (int) Math.round(y - SUN_DIAMETER / 2.0);

        if (isSun) {
            g2.setColor(Color.YELLOW);
            g2.fillOval(drawX, drawY, SUN_DIAMETER, SUN_DIAMETER);
        } else {
            drawMoon(g2, drawX, drawY);
        }
        g2.dispose();
    }

    private void drawMoon(Graphics2D g2, int x, int y) {
        // Эффект полумесяца с помощью вычитания эллипса
        Ellipse2D moonFull = new Ellipse2D.Double(x, y, MOON_DIAMETER, MOON_DIAMETER);
        Ellipse2D cutOut = new Ellipse2D.Double(x + MOON_DIAMETER / 2.5, y, MOON_DIAMETER, MOON_DIAMETER);
        Area moonShape = new Area(moonFull);
        moonShape.subtract(new Area(cutOut));
        g2.setColor(Color.WHITE);
        g2.fill(moonShape);
    }
}


// Панель, отрисовывающая рамки и ставни окна (верхний слой)
class WindowFramePanel extends JPanel {
    public WindowFramePanel() {
        setOpaque(false); // прозрачный фон, чтобы анимация оставалась видимой
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.decode("#fcfcfc"));
        // Верхняя рамка
        g.fillRect(0, 0, getWidth(), 10);
        // Нижняя рамка
        g.fillRect(0, getHeight() - 10, getWidth(), 10);
        // Левая рамка
        g.fillRect(0, 0, 10, getHeight());
        // Правая рамка
        g.fillRect(getWidth() - 10, 0, 10, getHeight());
        // Вертикальный разделитель (ставень посередине)
        g.fillRect(getWidth() / 2 - 5, 0, 10, getHeight());
    }
}


