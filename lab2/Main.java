import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Library {
    static int books;
    Writer[] writers;
    Reader[] readers;
    static ArrayList<String> library = new ArrayList<>();
    static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    static final Lock writeLock = rwl.writeLock();
    static final Lock readLock = rwl.readLock();
    static List<String> bookList = Arrays.asList(
            "Book 1", "Book 2", "Book 3", "Book 4", "Book 5", "Book 6", "Book 7", "Book 8", "Book 9", "Book 10",
            "Book 11", "Book 12", "Book 13", "Book 14", "Book 15", "Book 16", "Book 17", "Book 18", "Book 19",
            "Book 20", "Book 21", "Book 22", "Book 23", "Book 24", "Book 25"
    );

    public Library(int writers, int readers, int books) {
        this.writers = new Writer[writers];
        this.readers = new Reader[readers];
        Library.books = books;

        for (int i = 0; i < writers; i++) {
            this.writers[i] = new Writer("Writer " + (i + 1));
        }
        for (int i = 0; i < readers; i++) {
            this.readers[i] = new Reader("Reader " + (i + 1));
        }
    }

    public void start() {
        for (Writer writer : this.writers) {
            writer.start();
        }
        for (Reader reader : this.readers) {
            reader.start();
        }
    }
}

class Writer extends Thread {
    String name;
    public final Lock writeLock = Library.writeLock;
    ArrayList<String> library = Library.library;
    ArrayList<String> writtenBooks = new ArrayList<>();
    static int count = 0;

    public Writer(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (library.size() < Library.books) {
            try {
                writeLock.lock();
                if (library.size() < Library.books) {
                    String book = Library.bookList.get(count);
                    if (!library.contains(book)) {
                        Thread.sleep(500);
                        library.add(book);
                        writtenBooks.add(book);
                        System.out.println(name + " wrote " + book);
                        count++;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        }
        System.out.println(name + " finished writing: " + writtenBooks);
    }
}

class Reader extends Thread {
    public final Lock readLock = Library.readLock;
    ArrayList<String> readBooks = new ArrayList<>();
    ArrayList<String> library = Library.library;
    String name;

    public Reader(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (readBooks.size() < Library.books) {
            try {
                readLock.lock();
                if (!library.isEmpty()) {
                    int random = (int) (Math.random() * library.size());
                    String book = library.get(random);
                    if (!readBooks.contains(book)) {
                        Thread.sleep(300);
                        readBooks.add(book);
                        System.out.println(name + " read " + book);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        }
        System.out.println(name + " finished reading: " + readBooks);
    }
}

public class Main {
    public static void main(String[] args) {
        final int writers = 23;
        final int readers = 44;
        final int books = 25;

        Library library = new Library(writers, readers, books);
        library.start();
    }
}