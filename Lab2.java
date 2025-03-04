import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class Lab2 {
    public static void main(String[]
                                    args){
        long start = System.nanoTime();
        int x = 13;
        int y = 24;
        int z = 15;
        Library library = new Library(z);
        ExecutorService service =Executors.newVirtualThreadPerTaskExecutor();


        IntStream.range(0, x)
                .forEach(i -> service.submit(()-> new Writer(library, "Writer " + (i + 1)).run()));


        IntStream.range(0, y)
                .forEach(i ->service.submit(() -> new Reader(library, "Reader " + (i +
                        1)).run()));


        try {
            Thread.sleep(25);
            service.shutdown();
        } catch (InterruptedException e){
            e.printStackTrace();

            Thread.currentThread().interrupt();
        }


        long end = System.nanoTime();
        double realTime = (end - start -25 * 1_000_000) / 1_000_000_000.0;
        System.out.println(realTime);
    }


    static class Library {
        private final StampedLock lock =new StampedLock();
        private final int books;


        public Library(int books) {
            this.books = books;
        }

        public void read(String name) {
            long stamp = lock.readLock();
            try {
                IntStream.range(0, books)
                        .forEach(i ->
                                System.out.println(name + " citeste cartea " + i));
            } finally {
                lock.unlockRead(stamp);
            }
        }

        public void write(String name) {
            long stamp =
                    lock.writeLock();
            try {
                IntStream.range(0, books)
                        .forEach(i ->System.out.println(name + " scrie cartea " + i));
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }


    record Reader(Library library, String bookName) implements Runnable {
        @Override
        public void run() {
            library.read(bookName);
        }
    }


    static class Writer implements Runnable {
        private final Library library;
        private final String bookName;


        Writer(Library library, String bookName) {
            this.library = library;
            this.bookName = bookName;
        }


        @Override
        public void run() {
            library.write(bookName);
        }
    }
}