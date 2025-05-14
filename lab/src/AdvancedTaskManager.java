import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Timer;

public class AdvancedTaskManager extends JFrame {
    private static final String LOG_FILE = "task_manager_log.txt";
    private static final String STARTUP_REG_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String APP_NAME = "AdvancedTaskManager";
    private final DefaultListModel<String> processListModel = new DefaultListModel<>();
    private final JList<String> processList = new JList<>(processListModel);
    private final JTextField keywordField = new JTextField(20);
    private final JSpinner timeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1440, 1));
    private final Map<String, Long> processStartTimes = new HashMap<>();

    public AdvancedTaskManager() {
        setTitle("Advanced Windows Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        addToStartup();
        startMonitoringTask();
        refreshProcessList();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdvancedTaskManager app = new AdvancedTaskManager();
            app.setVisible(true);
        });
    }

    private void initUI() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de configurare
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        configPanel.add(new JLabel("Cuvinte cheie:"));
        configPanel.add(keywordField);
        configPanel.add(new JLabel("Timeout (min):"));
        configPanel.add(timeoutSpinner);

        JButton addKeywordButton = new JButton("Adaugă Monitorizare");
        addKeywordButton.addActionListener(e -> addKeywordMonitoring());
        configPanel.add(addKeywordButton);

        // Lista de procese
        processList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processList.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(processList);

        // Panel pentru butoane
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton refreshButton = new JButton("Reîmprospătează");
        JButton endTaskButton = new JButton("Oprește Procesul");
        JButton detailsButton = new JButton("Detalii Proces");

        refreshButton.addActionListener(e -> refreshProcessList());
        endTaskButton.addActionListener(e -> endSelectedProcess());
        detailsButton.addActionListener(e -> showProcessDetails());

        buttonPanel.add(refreshButton);
        buttonPanel.add(endTaskButton);
        buttonPanel.add(detailsButton);

        // Asamblarea interfeței
        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addKeywordMonitoring() {
        String keywords = keywordField.getText().trim();
        if (!keywords.isEmpty()) {
            logEvent("Monitorizare adăugată pentru: " + keywords);
            JOptionPane.showMessageDialog(this,
                    "Procesele care conțin aceste cuvinte vor fi monitorizate și oprite după timeout.",
                    "Monitorizare activată", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshProcessList() {
        processListModel.clear();
        try {
            Process process = Runtime.getRuntime().exec("tasklist /fo csv /nh");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\",\"");
                    String name = parts[0].replace("\"", "");
                    String pid = parts[1].replace("\"", "");
                    String memory = parts[4].replace("\"", "");

                    String displayText = String.format("%-40s %8s %12s",
                            name.length() > 40 ? name.substring(0, 37) + "..." : name,
                            pid, memory);

                    processListModel.addElement(displayText);

                    // Verifică procese pentru monitorizare
                    checkProcessForMonitoring(name, pid);
                }
            }
        } catch (IOException ex) {
            logEvent("EROARE la refresh: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Eroare la obținerea listei de procese: " + ex.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkProcessForMonitoring(String processName, String pid) {
        String keywords = keywordField.getText().trim().toLowerCase();
        if (keywords.isEmpty()) return;

        String[] keywordArray = keywords.split(",");
        boolean matches = Arrays.stream(keywordArray)
                .anyMatch(keyword -> processName.toLowerCase().contains(keyword.trim()));

        if (matches) {
            String key = pid + ":" + processName;
            if (!processStartTimes.containsKey(key)) {
                processStartTimes.put(key, System.currentTimeMillis());
                logEvent("Proces detectat pentru monitorizare: " + processName + " (PID: " + pid + ")");
            } else {
                long startTime = processStartTimes.get(key);
                long elapsedMinutes = (System.currentTimeMillis() - startTime) / (60 * 1000);
                int timeout = (Integer) timeoutSpinner.getValue();

                if (elapsedMinutes >= timeout) {
                    endProcess(pid);
                    logEvent("Proces oprit automat: " + processName + " (PID: " + pid +
                            ") după " + elapsedMinutes + " minute");
                    processStartTimes.remove(key);
                }
            }
        }
    }

    private void endSelectedProcess() {
        int selectedIndex = processList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedProcess = processListModel.get(selectedIndex);
            String pid = selectedProcess.split("\\s+")[1];
            endProcess(pid);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Selectați un proces pentru oprire!", "Eroare", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void endProcess(String pid) {
        try {
            Runtime.getRuntime().exec("taskkill /pid " + pid + " /f");
            refreshProcessList();
        } catch (IOException ex) {
            logEvent("EROARE la oprirea procesului PID " + pid + ": " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Eroare la oprirea procesului: " + ex.getMessage(),
                    "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProcessDetails() {
        int selectedIndex = processList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedProcess = processListModel.get(selectedIndex);
            String pid = selectedProcess.split("\\s+")[1];

            try {
                Process process = Runtime.getRuntime().exec("wmic process where processid=" + pid + " get commandline");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder details = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        details.append(line).append("\n");
                    }
                }

                JTextArea textArea = new JTextArea(details.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 300));

                JOptionPane.showMessageDialog(this, scrollPane,
                        "Detalii proces PID: " + pid, JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                logEvent("EROARE la obținerea detaliilor PID " + pid + ": " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Eroare la obținerea detaliilor procesului: " + ex.getMessage(),
                        "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logEvent(String message) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logEntry = "[" + timestamp + "] " + message + "\n";
            Files.write(Paths.get(LOG_FILE), logEntry.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("Eroare la scrierea în jurnal: " + ex.getMessage());
        }
    }

    private void addToStartup() {
        try {
            String path = new File(AdvancedTaskManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getPath();

            Process process = Runtime.getRuntime().exec("reg add HKCU\\" + STARTUP_REG_KEY +
                    " /v " + APP_NAME + " /t REG_SZ /d \"" + path + "\" /f");
            process.waitFor();

            logEvent("Aplicație adăugată în startup: " + path);
        } catch (Exception ex) {
            logEvent("EROARE la adăugarea în startup: " + ex.getMessage());
        }
    }

    private void startMonitoringTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshProcessList();
            }
        }, 0, 60 * 1000); // Actualizează la fiecare minut
    }
}