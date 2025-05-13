import java.util.concurrent.CountDownLatch;

public class Reader extends Thread {

    private static final int BOOKS_TO_READ = Config.BOOKS_NUM;

    private static int instanceCount = 0;

    private final String title;
    private final CountDownLatch startLatch;
    private final WriterReaderGUI gui;

    private int bookCount = 0;

    public Reader(CountDownLatch startLatch, WriterReaderGUI gui) throws Exception {
        if (instanceCount >= Config.READERS_NUM) {
            throw new Exception("Cannot create more than " + Config.READERS_NUM + " instances of Reader.");
        }
        instanceCount++;
        this.startLatch = startLatch;
        this.title = "Reader " + instanceCount;
        this.gui = gui;
    }

    @Override
    public void run() {

        try {
            startLatch.await();
            while (bookCount < BOOKS_TO_READ) {

                Library.readLock.lock();
                while (bookCount < BOOKS_TO_READ) {
                    Book book = Library.getBook(bookCount);
                    if (book != null) {
                        gui.addBookToReader(getTitle(), book.getName());
                        System.out.println(title + " read " + book.getName());
                        gui.logMessage(title + " read " + book.getName());
                        bookCount++;
                    }
                    else {
                        System.out.println(getName() + ": No book found");
                        gui.logMessage(getName() + ": No book found");
                        Library.readLock.unlock();
                        break;
                    }
                }

            }
            Library.readLock.unlock();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // System.out.println(name + " finished reading.");

    }

    public String getTitle()
    {
        return title;
    }
}