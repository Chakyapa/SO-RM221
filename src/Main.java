import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;

class Library {
    static List<String> books = new ArrayList<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); // true - приоритет читателей
    static final Lock writeLock = lock.writeLock();
    static final Lock readLock = lock.readLock();
}

class Writer extends Thread {
    String name;
    int booksToWrite;

    public Writer(String name, int booksToWrite) {
        this.name = name;
        this.booksToWrite = booksToWrite;
    }

    @Override
    public void run() {
        for (int i = 0; i < booksToWrite; i++) {
            try {
                Library.writeLock.lock(); // Писатель блокирует writeLock чтобы записывать книгу
                String book = "Book " + (Library.books.size() + 1);
                Library.books.add(book);
                System.out.println(name + " написал " + book);
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

    public Reader(String name, int maxBooksToRead) {
        this.name = name;
        this.maxBooksToRead = maxBooksToRead;
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
                    System.out.println(name + " читает: " + book);
                    booksReadCountLocal++;
                }
            } finally {
                Library.readLock.unlock(); // прочитали
            }
        }
        //если читатель закончил читать указанный лимит книг выводим результат
        System.out.println(name + " прочитал: " + booksRead);
    }
}

public class Main {
    public static void main(String[] args) {
        int numWriters = 10; // 10 писателей
        int numReaders = 12; // 12 читателей
        int booksPerWriter = 3; // писатель 3 книги
        int booksToReadPerReader = 30; // должен прочитать каждый читатель

        // Запуск писателей
        for (int i = 1; i <= numWriters; i++) {
            new Writer("Writer " + i, booksPerWriter).start();
        }

        // Запуск читателей
        for (int i = 1; i <= numReaders; i++) {
            new Reader("Reader " + i, booksToReadPerReader).start();
        }
    }
}
