import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Writer extends Thread {

}

class Library {
    static int books;
    Writer[] writers;
    Reader[] readers;
    static ArrayList<String> library = new ArrayList<>();
    static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    static final Lock writeLock = rwl.writeLock();
    static final Lock readLock = rwl.readLock();
    public Library (int writers, int readers, int books) {
        this.writers = new Writer[writers];
        this.readers = new Reader[readers];
        Library.books = books;
        /*for (int i = 0; i < writers; i++) {
            this.writers[i] = new Writer("Writer " + (i+1));
        }*/
        for (int i = 0; i < readers; i++) {
            this.readers[i] = new Reader("Reader " + (i+1));
        }
    }
    public void start() {
        /*for (Writer writer : this.writers) {
            writer.start();
        }*/
        for (Reader reader : this.readers) {
            reader.start();
        }
    }
}