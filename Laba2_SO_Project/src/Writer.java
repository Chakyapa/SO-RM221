import java.util.ArrayList;
import java.util.Arrays;


class Writer extends Thread {
    String name;
    ArrayList<String> bookList = new
            ArrayList<>(Arrays.asList("Преступление и наказание", "Братья Карамазовы", "Идиот",
            "Униженные и оскорбленные", "Бесы", "Игрок"));
    ArrayList<String> library = Library.library;
    ArrayList<String> writtenBooks = new ArrayList<>();
    static int count = 0;
    public Writer (String name) {
        this.name = name;
    }
    @Override
    public void run() {
        while(library.size() < Library.books){
            try {
                Library.writeLock.lock();
                if (library.size() < Library.books){
                    String randomBook = bookList.get(count);
                    if (!library.contains(randomBook)){
                        sleep(1000);
                        library.add(randomBook);
                        writtenBooks.add(randomBook);
                        System.out.println(name + " wrote " + randomBook);
                        count++;
                        sleep(100);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                Library.writeLock.unlock();
            }
            if (library.size() == Library.books){
                System.out.println(name + " book list: \n" + writtenBooks);
            }
        }
    }
}