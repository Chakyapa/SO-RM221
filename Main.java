package lab1;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main extends JFrame {
    private JLabel label1, label2, label3, labelExit, labelCurrentTime;
    private JButton startBtn, stopBtn, setTimer3Btn;
    private JTextArea logArea;
    private JTextField timer3Input;
    private Timer timer1, timer2, timer3, currentTimeTimer;
    private boolean isRunning = false;

    private int timer1Counter = 0;
    private int timer2Counter = 0;
    private int timer3Counter = 0;

    public Main() {
        setTitle("Aplicație Interactivă cu Taimere");
        setSize(400, 400);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.BLACK);  
        UIManager.put("Label.foreground", Color.RED);  
        UIManager.put("TextArea.foreground", Color.RED);  
        UIManager.put("TextArea.background", Color.BLACK);  
        UIManager.put("Button.foreground", Color.RED);  
        UIManager.put("Button.background", Color.BLACK);  

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  
        label1 = new JLabel("Timer 1: Inactiv", SwingConstants.CENTER);
        label2 = new JLabel("Timer 2: Așteptând...", SwingConstants.CENTER);
        label3 = new JLabel("Timer 3: Inactiv", SwingConstants.CENTER);
        labelExit = new JLabel("", SwingConstants.CENTER);
        labelCurrentTime = new JLabel("Ora curentă: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), SwingConstants.CENTER);

        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(labelExit);
        panel.add(labelCurrentTime);

        JPanel timer3Panel = new JPanel();
        timer3Input = new JTextField(8);
        setTimer3Btn = new JButton("Set Timer 3");
        timer3Panel.add(new JLabel("Setează Timer 3 (HH:mm:ss):"));
        timer3Panel.add(timer3Input);
        timer3Panel.add(setTimer3Btn);
        panel.add(timer3Panel);

        add(panel, BorderLayout.CENTER);

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        startBtn = new JButton("Start Timere");
        stopBtn = new JButton("Stop Timere");
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        add(buttonPanel, BorderLayout.NORTH);

        startBtn.addActionListener(e -> startTimers());
        stopBtn.addActionListener(e -> stopTimers());
        setTimer3Btn.addActionListener(e -> setTimer3());

        setVisible(true);

        currentTimeTimer = new Timer();
        currentTimeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> labelCurrentTime.setText("Ora curentă: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            }
        }, 0, 1000);
    }

    private void startTimers() {
        if (isRunning) return;
        isRunning = true;

        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);

        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    label1.setText("Timer 1: " + timer1Counter + " secunde");
                    timer1Counter++;
                    if (timer1Counter == 10) {
                        logArea.append("Timer 1: Finalizat după 10 secunde\n");
                        label1.setText("Timer 1: Executat!");
                        startTimer2();
                        cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    private void startTimer2() {
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    label2.setText("Timer 2: " + timer2Counter + " secunde");
                    timer2Counter++;
                    if (timer2Counter == 5) {
                        logArea.append("Timer 2: Pornit după 5 secunde de la start al lui Timer1\n");
                        label2.setText("Timer 2: Executat!");
                        cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    private void setTimer3() {
        if (timer3 != null) timer3.cancel();
        String timeText = timer3Input.getText();
        try {
            LocalTime targetTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm:ss"));
            long delay = java.time.Duration.between(LocalTime.now(), targetTime).toMillis();

            if (delay < 0) {
                JOptionPane.showMessageDialog(this, "Ora introdusă este în trecut! Folosiți o oră viitoare.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }

            timer3 = new Timer();
            timer3.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        label3.setText("Timer 3: " + timer3Counter + " secunde");
                        timer3Counter++;

                        if (timer3Counter == 5) {
                            logArea.append("Timer 3: Pornit la ora " + timeText + " și executat 5 secunde\n");
                            logArea.append("Timer 3: Executat!\n");
                            label3.setText("Timer 3: Executat!");
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    SwingUtilities.invokeLater(() -> {
                                        logArea.append("Toate timerele au fost executate cu succes!\n");
                                        labelExit.setText("Toate timerele au fost executate!");
                                    });
                                }
                            }, 6000);
                            cancel();
                        }
                    });
                }
            }, delay, 1000);

            logArea.append("Timer 3 setat pentru ora " + timeText + "\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format invalid! Folosiți HH:mm:ss", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopTimers() {
        if (timer1 != null) {
            timer1.cancel();
            timer1.purge();
        }
        if (timer2 != null) {
            timer2.cancel();
            timer2.purge();
        }
        if (timer3 != null) {
            timer3.cancel();
            timer3.purge();
        }

        logArea.append("Timere oprite!\n");
        isRunning = false;

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
