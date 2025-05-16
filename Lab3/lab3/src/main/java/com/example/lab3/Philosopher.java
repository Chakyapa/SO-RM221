package com.example.lab3;

import javafx.application.Platform;

import java.util.Random;

public class Philosopher implements Runnable {
    private Object leftFork;
    private Object rightFork;
    private int maxEatingTime;
    private int eatingCount;
    private final HelloController controller;
    private final String[] thoughts; // Массив мыслей
    private final Random random;

    public Philosopher(Object leftFork, Object rightFork, int maxEatingTime, HelloController controller) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.maxEatingTime = maxEatingTime;
        this.controller = controller;
        this.thoughts = new String[] {"about life", "about death", "about universe", "about life meaning"};
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            while (eatingCount < maxEatingTime) {
                doAction(": Thinking " + thoughts[random.nextInt(thoughts.length)]);
                synchronized (leftFork) {
                    doAction(": Picked up left fork");
                    synchronized (rightFork) {
                        doAction(": Picked up right fork - eating");
                        eatingCount++;
                        Platform.runLater(() -> controller.appendText("Цикл: " + eatingCount));
                        doAction(": Put down right fork");
                    }
                    doAction(": Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void doAction(String action) throws InterruptedException {
        String message = Thread.currentThread().getName() + " " + action;
        Platform.runLater(() -> controller.appendText(message));
        Thread.sleep(((int) (Math.random() * 100)));
    }
}
