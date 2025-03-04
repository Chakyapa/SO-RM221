import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Lab1 extends JPanel {
    JFrame frame;

    public Lab1(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("Lab 1");
            final Lab1 ime = new Lab1(frame);
            final JButton button = new JButton("Click Me");
            frame.add(ime);
            frame.pack();
            frame.setVisible(true);
            frame.add(button);
            button.addActionListener(e -> ini());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent arg0) {
                    ime.close();
                }
            });

        });
    }

    public void close() {
        System.exit(1);
    }

    private static void ini() {
        var timer1 = new Timer();
        var timer2 = new Timer();
        var timer3 = new Timer();

        timer1.schedule(new IntervalTimer(), 0, 3000);
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        timer2.schedule(new SpecificTimer(), calendar.getTime());
        timer3.schedule(new PeriodicTimer(), 2000, 5000);
    }

    static class IntervalTimer extends TimerTask {
        @Override
        public void run() {
            System.out.println("IntervalTask: Se execută la fiecare 3 secunde. [" + new Date() + "]");
        }
    }
    

    static class SpecificTimer extends TimerTask {
        @Override
        public void run() {
            System.out.println("IntervalTask: Se execută o singură dată la ora setată. [" + new Date() + "]");
        }
    }

    static class PeriodicTimer extends TimerTask {
        @Override
        public void run() {
            System.out.println("PeriodicTask: Se execută la fiecare 5 secunde. [" + new Date() + "]");
        }
    }
}
