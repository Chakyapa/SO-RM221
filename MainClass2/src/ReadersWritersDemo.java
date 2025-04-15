import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.ArrayList;

public class ReadersWritersDemo {

    // Базовый класс (общий ресурс) – библиотека книг
    static class Library {
        // Список книг
        private List<String> books = new ArrayList<>();
        // Read-write блокировка с параметром fairness (true) для избежания голодания
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

        // Метод записи книги – используется писателями
        public void writeBook(String book) {
            lock.writeLock().lock();  // писатель получает эксклюзивный доступ
            try {
                books.add(book);
                System.out.println(Thread.currentThread().getName() + " написал книгу: " + book);
            } finally {
                lock.writeLock().unlock();
            }
        }

        // Метод чтения – используется читателями
        public void readBooks() {
            lock.readLock().lock();   // читатели могут работать параллельно
            try {
                System.out.println(Thread.currentThread().getName() + " читает книги: " + books);
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    // Класс писателя (вариант 11, группа 221)
    // Каждый писатель пишет Z книг; здесь Z = 4
    static class Writer extends Thread {
        private Library library;
        private int booksToWrite;

        public Writer(String name, Library library, int booksToWrite) {
            super(name);
            this.library = library;
            this.booksToWrite = booksToWrite;
        }

        @Override
        public void run() {
            for (int i = 1; i <= booksToWrite; i++) {
                // Формируем название книги с именем писателя и номером книги
                String book = getName() + "_Book_" + i;
                library.writeBook(book);
                try {
                    Thread.sleep(50); // имитация времени записи
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Класс читателя (вариант 1, группа 221)
    // Каждый читатель выполняет Z операций чтения, где Z = 4
    static class Reader extends Thread {
        private Library library;
        private int readsToPerform;

        public Reader(String name, Library library, int readsToPerform) {
            super(name);
            this.library = library;
            this.readsToPerform = readsToPerform;
        }

        @Override
        public void run() {
            for (int i = 1; i <= readsToPerform; i++) {
                library.readBooks();
                try {
                    Thread.sleep(100); // имитация времени чтения
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Расчеты по заданным формулам:
        // Для писателей: вариант 11, группа 221
        // X = 11 + (последняя цифра группы 221) = 11 + 1 = 12 писателей
        int variantWriter = 11;
        int group = 221;
        int lastDigit = group % 10;
        int X = variantWriter + lastDigit; // 12 писателей

        // Для читателей: вариант 1, группа 221
        // Y = 1 * 2 = 2 читателя
        int variantReader = 1;
        int Y = variantReader * 2; // 2 читателя

        // Для книг: вариант 1, группа 221
        // Z = 1 + 3 = 4 книги (каждый писатель пишет 4 книги, а читатель выполняет 4 чтения)
        int variantBooks = 1;
        int Z = variantBooks + 3; // 4 книги

        System.out.println("Запуск программы:");
        System.out.println("Писателей (X): " + X);
        System.out.println("Читателей (Y): " + Y);
        System.out.println("Книг на каждого писателя (Z): " + Z);
        System.out.println("-------------------------------------");

        Library library = new Library(); // общий ресурс

        // Запускаем потоки писателей
        for (int i = 1; i <= X; i++) {
            Writer writer = new Writer("Writer_" + i, library, Z);
            writer.start();
        }

        // Небольшая задержка, чтобы писатели начали писать книги
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Запускаем потоки читателей
        for (int i = 1; i <= Y; i++) {
            Reader reader = new Reader("Reader_" + i, library, Z);
            reader.start();
        }
    }
}










