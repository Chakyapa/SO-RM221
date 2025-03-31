package com.example.lab2;

import com.example.lab2.HelloController;
import javafx.application.Platform;

import java.util.List;

public class Reader extends Thread {
    private final String name;
    private final HelloController controller;
    private final int booksToRead;

    public Reader(String name, HelloController controller, int booksToRead) {
        this.name = name;
        this.controller = controller;
        this.booksToRead = booksToRead;
    }

    public void run() {
        try {
            while (true) {
                Library.readLock.lock();

                if (Library.getBooksCount() >= booksToRead) { // Ждём, пока появится 6 книг
                    List<String> booksRead = Library.getBooks(booksToRead);
                    String message = name + " читает " + booksRead;
                    System.out.println(message);

                    Platform.runLater(() -> controller.appendText(message));

                    Library.readLock.unlock();
                    break; // После успешного чтения завершаем поток
                }

                Library.readLock.unlock();
                Thread.sleep(100); // Даем шанс писателям добавить книги
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
