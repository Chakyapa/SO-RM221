public class Main {
    public static void main(String[] args) {
        final int writers = 2;
        final int readers = 3;
        final int books = 6;
        Library library = new Library(writers, readers, books);
        library.start();
    }
}