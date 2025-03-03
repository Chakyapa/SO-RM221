public class Lab2 {
    class Writerimplements Runnable {

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
