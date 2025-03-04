import java.util.concurrent.CountDownLatch;

public class Writer extends Thread {
    private static int instanceCount = 0;
    private final String title;
    private final CountDownLatch startLatch;
    private final WriterReaderGUI gui;

    public Writer(CountDownLatch startLatch, WriterReaderGUI gui) throws Exception {
        if (instanceCount >= Config.WRITERS_NUM) {
            throw new Exception("Cannot create more than " + Config.WRITERS_NUM + " instances of Writer.");
        }
        instanceCount++;
        this.startLatch = startLatch;
        this.title = "Writer " + instanceCount;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            startLatch.await();
            while (true) {
                try {
                    Library.writeLock.lock();
                    try {
                        Book book = new Book();
                        Library.addBook(book);
                        gui.addBookToLibrary(book.getName());
                        gui.addBookToWriter(getTitle(), book.getName());
                        System.out.println(title + " wrote " + book.getName());
                        gui.logMessage(title + " wrote " + book.getName());
                    }
                    catch (Exception e) {
                        break;
                    }
                } finally {
                    Library.writeLock.unlock();
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        //System.out.println(name + " finished writing.");

    }

    public String getTitle()
    {
        return title;
    }
}