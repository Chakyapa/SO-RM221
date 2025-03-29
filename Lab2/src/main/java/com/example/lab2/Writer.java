package com.example.lab2;

import javafx.application.Platform;

public class Writer extends Thread {
    private final String name;
    private final HelloController controller;

    public Writer(String name, HelloController controller) {
        this.name = name;
        this.controller = controller;
    }

    public void run() {
        try {
           for (int i = 1; i <= Library.MAX_BOOKS; i++) {
               Library.writeLock.lock();
               String book = name + " - Книга " + i;
               Library.books.add(book);

               String message = name + " написал: " + book;
               System.out.println(message);

               Platform.runLater(() -> controller.appendText(message));

               Library.writeLock.unlock();

               Thread.sleep(500);
           }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
