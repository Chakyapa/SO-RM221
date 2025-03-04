import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws Exception {
        int numWriters = Config.WRITERS_NUM;
        int numReaders = Config.READERS_NUM;

        WriterReaderGUI gui = new WriterReaderGUI();

        CountDownLatch startLatch = new CountDownLatch(1);      // защелка для одновременного старта

        Writer[] writers = new Writer[numWriters];
        Reader[] readers = new Reader[numReaders];

        // создание потоков Writer
        for (int i = 0; i < numWriters; i++) {
            writers[i] = new Writer(startLatch, gui);
            gui.addWriter(writers[i].getTitle());
        }

        // создание потоков Reader
        for (int i = 0; i < numReaders; i++) {
            readers[i] = new Reader(startLatch, gui);
            gui.addReader(readers[i].getTitle());
        }

        // запуск всех потоков
        for (Writer writer : writers) {
            writer.start();
        }

        for (Reader reader : readers) {
            reader.start();
        }

        // дать одновременный запуск всем потокам
        startLatch.countDown();

        // не завершать программу пока все потоки не выполнять свои задачи
        for (Thread writer : writers) {
            writer.join();
        }

        for (Thread reader : readers) {
            reader.join();
        }

        System.out.println("Program finished.");
    }
}

