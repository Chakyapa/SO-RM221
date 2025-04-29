import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class Main {

    static class Philosopher extends Thread {
        int id;
        Semaphore leftFork;
        Semaphore rightFork;
        Semaphore table;
        int eatCount;
        PhilosopherPanel panel;

        public Philosopher(int id, Semaphore leftFork, Semaphore rightFork, Semaphore room, int eatCount, PhilosopherPanel panel) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.table = room;
            this.eatCount = eatCount;
            this.panel = panel;

        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < eatCount; i++) {
                    table.acquire();

                    leftFork.acquire();
                    panel.updateForkStatus("Взял левую вилку", Color.ORANGE, true);

                    rightFork.acquire();
                    panel.updateForkStatus("Взял правую вилку", Color.PINK, false);

                    eat();

                    leftFork.release();
                    rightFork.release();
                    panel.updateForkStatus("Положил обе вилки", Color.GRAY, true);
                    panel.updateForkStatus("Положил обе вилки", Color.GRAY, false);

                    table.release();

                    think();
                }
                panel.updateStatus("Закончил ужинать", Color.BLACK);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void think() throws InterruptedException {
            panel.updateStatus("Размышляет...", Color.BLUE);
            Thread.sleep((int) (Math.random() * 1000));
        }

        private void eat() throws InterruptedException {
            panel.updateStatus("Ест...", Color.GREEN);
            Thread.sleep((int) (Math.random() * 1000));
        }
    }

    static class PhilosopherPanel extends JPanel {
        private final JLabel statusLabel;
        private final JLabel leftForkLabel;
        private final JLabel rightForkLabel;

        public PhilosopherPanel(int id) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder("Философ " + id));

            statusLabel = new JLabel("Ждёт начала...", SwingConstants.CENTER);
            leftForkLabel = new JLabel("Левая вилка", SwingConstants.CENTER);
            rightForkLabel = new JLabel("Правая вилка", SwingConstants.CENTER);

            JPanel forkPanel = new JPanel(new GridLayout(1, 2));
            forkPanel.add(leftForkLabel);
            forkPanel.add(rightForkLabel);

            add(statusLabel, BorderLayout.CENTER);
            add(forkPanel, BorderLayout.SOUTH);
        }

        public void updateStatus(String status, Color color) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(status);
                statusLabel.setForeground(color);
            });
        }

        public void updateForkStatus(String status, Color color, boolean isLeft) {
            SwingUtilities.invokeLater(() -> {
                if (isLeft) {
                    leftForkLabel.setText(status);
                    leftForkLabel.setForeground(color);
                } else {
                    rightForkLabel.setText(status);
                    rightForkLabel.setForeground(color);
                }
            });
        }
    }

    public static void main(String[] args) {
        final int N = 19; // Количество философов
        final int eatCount = 15;

        // Создаём "справедливые" семафоры для вилок
        Semaphore[] forks = new Semaphore[N];
        for (int i = 0; i < N; i++) {
            forks[i] = new Semaphore(1, true); // fair = true
        }

        // Справедливый семафор для ограничения количества философов за столом
        Semaphore room = new Semaphore(N - 1, true); // fair = true

        JFrame frame = new JFrame("Проблема философов");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(N, 1));

        for (int i = 0; i < N; i++) {
            PhilosopherPanel panel = new PhilosopherPanel(i);
            contentPanel.add(panel);
            new Philosopher(i, forks[i], forks[(i + 1) % N], room, eatCount, panel).start();
        }


        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        frame.getContentPane().add(scrollPane);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
