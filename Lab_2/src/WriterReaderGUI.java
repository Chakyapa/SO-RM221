import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WriterReaderGUI extends JFrame {
    private DefaultListModel<String> bookListModel = new DefaultListModel<>();
    private DefaultListModel<String> readersListModel = new DefaultListModel<>();
    private DefaultListModel<String> writersListModel = new DefaultListModel<>();
    private JTextArea logArea = new JTextArea();

    private Map<String, String> readersMap = new HashMap<>();
    private Map<String, String> writersMap = new HashMap<>();

    public WriterReaderGUI() {
        setTitle("Writer/Reader Simulation");
        setSize(1200, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 4));


        JList<String> bookList = new JList<>(bookListModel);
        JScrollPane bookScrollPane = new JScrollPane(bookList);
        bookScrollPane.setBorder(BorderFactory.createTitledBorder("Library"));


        JList<String> readersList = new JList<>(readersListModel);
        JScrollPane readersScrollPane = new JScrollPane(readersList);
        readersScrollPane.setBorder(BorderFactory.createTitledBorder("Readers"));


        JList<String> writersList = new JList<>(writersListModel);
        JScrollPane writersScrollPane = new JScrollPane(writersList);
        writersScrollPane.setBorder(BorderFactory.createTitledBorder("Writers"));


        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Log"));


        add(bookScrollPane);
        add(readersScrollPane);
        add(writersScrollPane);
        add(logScrollPane);

        setVisible(true);
    }

    public void addBookToLibrary(String bookName) {
        SwingUtilities.invokeLater(() -> bookListModel.addElement(bookName));
    }

    public void addWriter(String writerName) {
        SwingUtilities.invokeLater(() -> {
            if (!writersMap.containsKey(writerName)) {
                writersMap.put(writerName, writerName + ": ");
                refreshWritersList();
            }
        });
    }

    public void addReader(String readerName) {
        SwingUtilities.invokeLater(() -> {
            if (!readersMap.containsKey(readerName)) {
                readersMap.put(readerName, readerName + ": ");
                refreshReadersList();
            }
        });
    }

    public void addBookToReader(String readerName, String bookName) {
        SwingUtilities.invokeLater(() -> {
            if (readersMap.containsKey(readerName)) {
                readersMap.put(readerName, readersMap.get(readerName) + bookName + ", ");
                refreshReadersList();
            }
        });
    }

    public void addBookToWriter(String writerName, String bookName) {
        SwingUtilities.invokeLater(() -> {
            if (writersMap.containsKey(writerName)) {
                writersMap.put(writerName, writersMap.get(writerName) + bookName + ", ");
                refreshWritersList();
            }
        });
    }

    private void refreshReadersList() {
        readersListModel.clear();
        for (String entry : readersMap.values()) {
            readersListModel.addElement(entry);
        }
    }

    private void refreshWritersList() {
        writersListModel.clear();
        for (String entry : writersMap.values()) {
            writersListModel.addElement(entry);
        }
    }

    public void logMessage(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

}