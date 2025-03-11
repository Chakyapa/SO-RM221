import java.util.ArrayList;

class Reader extends Thread {
    ArrayList<String> readBooks = new ArrayList<>();
    ArrayList<String> library = Library.library;
    String name;
    public Reader(String name) {
        this.name = name;
    }
    @Override
    public void run() {
        while(readBooks.size() < Library.books){
            try {
                if (Library.rwl.isWriteLocked()){
                    System.out.println(name + ": writer is in library");
                }
                Library.readLock.lock();
                int random = (int)(Math.random()*library.size());
                if(random < library.size()) {
                    String randomBook = library.get(random);
                    if(readBooks.size() < Library.books){
                        if(!readBooks.contains(randomBook)){
                            sleep(300);
                            readBooks.add(randomBook);
                            System.out.println(name + " read book " + randomBook);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Library.readLock.unlock();
            }
        }
        System.out.println(name + " finished reading \n" + readBooks);
    }
}