import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Library {
    static private final List<Book> books = new ArrayList<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    static final Lock writeLock = lock.writeLock();
    static final Lock readLock = lock.readLock();
    static final Condition newBookAvailable = lock.writeLock().newCondition(); // Условие для ожидания книги


    static void addBook(Book book) {
        books.add(book);
        newBookAvailable.signalAll();;
    }

    static Book getBook(int i) {
        if (i < books.size()) {
            return books.get(i);
        }
        return null;
    }

    static boolean isNewBook(int i)
    {
        return i < books.size();
    }
}
