import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Library {
    static List<String> books = new ArrayList<>();
    static final Semaphore mutex = new Semaphore(1); // Семафор для писателей
}

class Writer extends Thread {
    String name;
    int booksToWrite;
    JTextArea outputArea;

    public Writer(String name, int booksToWrite, JTextArea outputArea) {
        this.name = name;
        this.booksToWrite = booksToWrite;
        this.outputArea = outputArea;
    }

    @Override
    public void run() {
        for (int i = 0; i < booksToWrite; i++) {
            try {
                Library.mutex.acquire(); // Захват семафора для записи
                String book = "Book " + (Library.books.size() + 1);
                Library.books.add(book);
                SwingUtilities.invokeLater(() -> outputArea.append(name + " написал: " + book + "\n"));
                Thread.sleep(500); // Имитация времени написания книги
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Library.mutex.release(); // Освобождение семафора
            }
        }
    }
}

class Reader extends Thread {
    String name;
    int maxBooksToRead;
    List<String> booksRead = new ArrayList<>();
    JTextArea outputArea;

    public Reader(String name, int maxBooksToRead, JTextArea outputArea) {
        this.name = name;
        this.maxBooksToRead = maxBooksToRead;
        this.outputArea = outputArea;
    }

    @Override
    public void run() {
        int localRead = 0;
        while (localRead < maxBooksToRead) {
            if (Library.books.size() > localRead) {
                String book = Library.books.get(localRead);
                booksRead.add(book);
                SwingUtilities.invokeLater(() -> outputArea.append(name + " читает: " + book + "\n"));
                localRead++;
                try {
                    Thread.sleep(300); // Имитация времени чтения
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(200); // Ждём, пока появится новая книга
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        SwingUtilities.invokeLater(() -> outputArea.append(name + " прочитал: " + booksRead + "\n"));
    }
}



public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Library Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new GridLayout(4, 2));
        JLabel writerLabel = new JLabel("Писателей:");
        JTextField writerField = new JTextField("10", 3);
        controlPanel.add(writerLabel);
        controlPanel.add(writerField);

        JLabel readerLabel = new JLabel("Читателей:");
        JTextField readerField = new JTextField("12", 3);
        controlPanel.add(readerLabel);
        controlPanel.add(readerField);

        JLabel bookLabel = new JLabel("Книг на писателя:");
        JTextField bookField = new JTextField("3", 3);
        controlPanel.add(bookLabel);
        controlPanel.add(bookField);

        JButton startButton = new JButton("Запуск");
        controlPanel.add(startButton);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            outputArea.setText("");
            int numWriters = Integer.parseInt(writerField.getText());
            int numReaders = Integer.parseInt(readerField.getText());
            int booksPerWriter = Integer.parseInt(bookField.getText());
            int booksToReadPerReader = 30;

            outputArea.append("Запуск симуляции...\n");

            for (int i = 1; i <= numWriters; i++) {
                new Writer("Writer " + i, booksPerWriter, outputArea).start();
            }

            for (int i = 1; i <= numReaders; i++) {
                new Reader("Reader " + i, booksToReadPerReader, outputArea).start();
            }
        });

        frame.setVisible(true);
    }
}