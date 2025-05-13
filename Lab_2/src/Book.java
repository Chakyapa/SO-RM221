public class Book {

    private static int instanceCount = 0;   // счетчик созданных объектов
    private final String name;              // "содержание книги"

    public Book() throws Exception {
        if (instanceCount >= Config.BOOKS_NUM) {    // в случае если достигли
            throw new Exception("Cannot create more than " + Config.BOOKS_NUM + " instances of Book.");
        }
        instanceCount++;
        this.name = "Book " + instanceCount;
    }

    public String getName() {
        return name;
    }

}