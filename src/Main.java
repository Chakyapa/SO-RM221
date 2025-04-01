import javax.swing.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

class Library {
    static List<String> books = new ArrayList<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); // true - приоритет читателей
    static final Lock writeLock = lock.writeLock();
    static final Lock readLock = lock.readLock();
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
                Library.writeLock.lock(); // Писатель блокирует writeLock чтобы записывать книгу
                String book = "Book " + (Library.books.size() + 1);
                Library.books.add(book);
                outputArea.append(name + " написал " + book + "\n"); // Выводим результат в JTextArea
            } finally {
                Library.writeLock.unlock(); // Освобождаем блокировку после записи книги
            }
        }
    }
}

class Reader extends Thread {
    String name;
    List<String> booksRead = new ArrayList<>();
    int maxBooksToRead; // Число книг которые должен прочитать читатель
    JTextArea outputArea;

    public Reader(String name, int maxBooksToRead, JTextArea outputArea) {
        this.name = name;
        this.maxBooksToRead = maxBooksToRead;
        this.outputArea = outputArea;
    }

    @Override
    public void run() {
        int booksReadCountLocal = 0;
        while (booksReadCountLocal < maxBooksToRead) { // теперь читатель будет читать указанное количество книг
            try {
                Library.readLock.lock(); // Читатель блокирует readLock для чтения

                while (Library.books.size() <= booksReadCountLocal) {
                    Library.readLock.unlock(); //книг нет, дать другим шанс
                    Library.readLock.lock(); //когда они закончат снова захватить
                }

                if (booksReadCountLocal < maxBooksToRead && Library.books.size() > booksReadCountLocal) {
                    String book = Library.books.get(booksReadCountLocal);
                    booksRead.add(book);
                    outputArea.append(name + " читает: " + book + "\n"); // Выводим результат в JTextArea
                    booksReadCountLocal++;
                }
            } finally {
                Library.readLock.unlock(); // прочитали
            }
        }
        //если читатель закончил читать указанный лимит книг выводим результат
        outputArea.append(name + " прочитал: " + booksRead + "\n"); // Выводим итог в JTextArea
    }
}

public class Main {
    public static void main(String[] args) {
        // Создание интерфейса
        JFrame frame = new JFrame("Library Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Панель для управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 2));

        JLabel writerLabel = new JLabel("Писателей:");
        JTextField writerField = new JTextField("5", 3);
        controlPanel.add(writerLabel);
        controlPanel.add(writerField);

        JLabel readerLabel = new JLabel("Читателей:");
        JTextField readerField = new JTextField("5", 3);
        controlPanel.add(readerLabel);
        controlPanel.add(readerField);

        JLabel readerLabel1 = new JLabel("Книги:");
        JTextField readerField1 = new JTextField("5", 3);
        controlPanel.add(readerLabel1);
        controlPanel.add(readerField1);

        JButton startButton = new JButton("Запуск");
        controlPanel.add(startButton);

        // Панель для отображения вывода
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Обработчик нажатия кнопки
        startButton.addActionListener(e -> {
            try {
                int numWriters = Integer.parseInt(writerField.getText());
                int numReaders = Integer.parseInt(readerField.getText());
                int booksPerWriter = Integer.parseInt(readerField1.getText());
                int booksToReadPerReader = Integer.parseInt(readerField1.getText());

                outputArea.append("Запуск симуляции...\n");

                // Запуск писателей
                for (int i = 1; i <= numWriters; i++) {
                    new Writer("Writer " + i, booksPerWriter, outputArea).start();
                }

                // Запуск читателей
                for (int i = 1; i <= numReaders; i++) {
                    new Reader("Reader " + i, booksToReadPerReader, outputArea).start();
                }
            } catch (NumberFormatException ex) {
                outputArea.append("Ошибка ввода данных!\n");
            }
        });

        frame.setVisible(true);
    }
}
