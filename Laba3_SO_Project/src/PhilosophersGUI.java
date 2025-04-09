import javax.swing.*;
import java.awt.*;

public class PhilosophersGUI extends JFrame {
    private final JLabel[] philosopherLabels;

    public PhilosophersGUI(int n) {
        setTitle("Dining Philosophers");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        philosopherLabels = new JLabel[n];
        int centerX = 300, centerY = 300, radius = 200;

        for (int i = 0; i < n; i++) {
            philosopherLabels[i] = new JLabel("P" + (i + 1), SwingConstants.CENTER);
            philosopherLabels[i].setOpaque(true);
            philosopherLabels[i].setBackground(Color.GRAY);
            philosopherLabels[i].setBounds(
                    (int) (centerX + radius * Math.cos(2 * Math.PI * i / n) - 30),
                    (int) (centerY + radius * Math.sin(2 * Math.PI * i / n) - 15),
                    60, 30
            );
            add(philosopherLabels[i]);
        }
        setVisible(true);
    }

    public void updateState(int id, String state, Color color) {
        SwingUtilities.invokeLater(() -> {
            philosopherLabels[id].setText("P" + (id + 1) + " " + state);
            philosopherLabels[id].setBackground(color);
        });
    }
}
