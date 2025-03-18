import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class TimerAppGUI extends JFrame implements ActionListener {
    private JButton startButton;
    private JLabel label, timeLabel;
    private JTextField timeField;
    private Timer timer1, timer2, timer3;
    private int count1 = 0, count2 = 0, count3 = 0;
    private long timer3StartTime;
    private long timer1StartTime;

    public TimerAppGUI() {
        // se seteaza fereastra aplicatiei
        setTitle("Timer_App");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // se initilizeaza componentele
        label = new JLabel("Timerele inca nu au pornit.");
        timeLabel = new JLabel("Introduceți ora pentru Timer 2 (HH:mm): ");
        timeField = new JTextField(5);
        timeField.setText(getCurrentTime());
        startButton = new JButton("Start Timere");
        startButton.addActionListener(this);

        // Add la componente in frame
        add(timeLabel);
        add(timeField);
        add(label);
        add(startButton);

        // Aranjarea componentelor
        JPanel timePanel = new JPanel(new FlowLayout());
        timePanel.add(timeLabel);
        timePanel.add(timeField);

        JPanel labelPanel = new JPanel(new FlowLayout());
        labelPanel.add(label);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startButton);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(timePanel);
        add(labelPanel);
        add(buttonPanel);
    }

    // Obține ora curentă în format HH:mm
    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return formatter.format(date);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            // Timer 1, executare la fiecare 2 secunde
            timer1 = new Timer();
            timer1StartTime = System.currentTimeMillis();
            timer1.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long elapsedTime1 = System.currentTimeMillis() - timer1StartTime;

                    if (elapsedTime1 <= 20000) { // Execute only if we're still under 20 seconds
                        count1++;
                        System.out.println("Timer 1 trigerit dupa timp fix: " + count1 +
                                " (timp trecut: " + elapsedTime1/1000 + " sec)");
                    } else {
                        System.out.println("Timer 1 stopat dupa 20 sec");
                        this.cancel(); // Stop task-ul timerului
                        timer1.cancel(); // Stop complet
                    }
                }
            }, 0, 2000);

            // Timer 2, executare la ora specificată
            try {
                String timeStr = timeField.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date scheduledTime = sdf.parse(timeStr);


                Calendar scheduledCal = Calendar.getInstance();
                Calendar currentCal = Calendar.getInstance();

                scheduledCal.setTime(scheduledTime);


                scheduledCal.set(Calendar.YEAR, currentCal.get(Calendar.YEAR));
                scheduledCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH));
                scheduledCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH));
                scheduledCal.set(Calendar.SECOND, 0);

                
                if (scheduledCal.before(currentCal)) {
                    scheduledCal.add(Calendar.DAY_OF_MONTH, 1);
                }

                long delay = scheduledCal.getTimeInMillis() - System.currentTimeMillis();

                System.out.println("Timer 2 programat pentru: " + sdf.format(scheduledCal.getTime()) +
                        " (Întârziere: " + delay/1000 + " secunde)");

                timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        count2++;
                        System.out.println("Timer 2 trigerit la ora programată: " + count2);
                    }
                }, delay);

            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Format de oră invalid! Folosiți formatul HH:mm",
                        "Eroare",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("Eroare la parsarea orei: " + ex.getMessage());
                return;
            }

            // Timer 3, executare fiecare 3 sec, deadline 10 sec
            timer3 = new Timer();
            timer3StartTime = System.currentTimeMillis();
            timer3.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    long elapsedTime = System.currentTimeMillis() - timer3StartTime;

                    if (elapsedTime <= 10000) { // Execute only if we're still under 10 seconds
                        count3++;
                        System.out.println("Timer 3 trigerit dupa timp fix: " + count3 +
                                " (timp trecut: " + elapsedTime/1000 + " sec)");
                    } else {
                        System.out.println("Timer 3 stopat dupa 10 sec");
                        this.cancel(); // Stop task-ul timerului
                        timer3.cancel(); // Stop complet
                    }
                }
            }, 0, 3000);

            label.setText("Timerele ruleaza...");
        }
    }

    public static void main(String[] args) {
        TimerAppGUI app = new TimerAppGUI();
        app.setVisible(true);
    }
}