import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        boolean startCount = false;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("лабораторная");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            Balls ballsPanel = new Balls();
            frame.add(ballsPanel);






            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            if (ballsPanel.getSelectedBall() == 0) {
                                ballsPanel.incrementScore(5);

                            }ballsPanel.selectBall(4);
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (ballsPanel.getSelectedBall() == 1) {
                                ballsPanel.incrementScore(5);

                            }ballsPanel.selectBall(4);
                            break;
                        case KeyEvent.VK_UP:
                            if (ballsPanel.getSelectedBall() == 2) {
                                ballsPanel.incrementScore(5);

                            }ballsPanel.selectBall(4);
                            break;
                        case KeyEvent.VK_DOWN:
                            if (ballsPanel.getSelectedBall() == 3) {
                                ballsPanel.incrementScore(5);

                            }
                            ballsPanel.selectBall(4);


                            break;
                        case KeyEvent.VK_SPACE:
                            ballsPanel.resetScore();
                            break;
                    }
                }
            });

            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    ballsPanel.selectBall(new Random().nextInt(4));
                }
            }, 0, 1000);

            frame.setFocusable(true);
            frame.setVisible(true);
        });
    }
}

class Balls extends JPanel {
    private int selectedBall = -1;
    private int score = 0;
    private boolean gameOver = false;
    public Balls(){
        startTimer();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBalls(g);
        drawScore(g);
    }

    private void drawBalls(Graphics g) {
        int cordX = getWidth() / 2;
        int cordY = getHeight() / 2;
        int diam = 50;
        int razn = 60;


        int[][] positions = {
                {cordX - razn, cordY},    // Лево
                {cordX + razn, cordY},    // Право
                {cordX, cordY - razn},    // Верх
                {cordX, cordY + razn}     // Низ
        };

        for (int i = 0; i < positions.length; i++) {
            if (i == selectedBall) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            if(!gameOver) {
                g.fillOval(positions[i][0] - diam / 2, positions[i][1] - diam / 2, diam, diam);
            } else{
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Время вышло" , 20, getHeight()/2);
            }

        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Счёт: " + score, getWidth() - 80, 20);
    }

    public void selectBall(int index) {

            selectedBall = index;
            repaint();

    }

    public int getSelectedBall() {
        return selectedBall;
    }

    public void incrementScore(int amount) {
        score += amount;
        repaint();
    }

    public void resetScore() {
        score = 0;
        repaint();
    }
    private void startTimer() {
        LocalTime startTime = LocalTime.now(); // Запоминаем время старта
        System.out.println("Таймер запущен в: " + startTime); // Вывод в консоль

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LocalTime endTime = LocalTime.now(); // Фиксируем время окончания
                System.out.println("Игра окончена в: " + endTime); // Выводим время окончания
                gameOver=true;
                repaint();
            }
        }, 5 * 60 * 1000); // 5 минут в миллисекундах
    }
}