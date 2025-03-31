package com.example.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Library {
    static final int MAX_BOOKS = 6;
    static List<String> books = new ArrayList<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    static final Lock writeLock = lock.writeLock();
    static final Lock readLock = lock.readLock();

    public static List<String> getBooks(int count) {
        int available = Math.min(count, books.size());
        return new ArrayList<>(books.subList(0, available));
    }

    public static int getBooksCount() {
        return books.size(); // Предполагаем, что books — это список, хранящий книги
    }
}
