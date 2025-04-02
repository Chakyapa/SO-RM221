package com.example.lab3;

import javafx.application.Platform;

public class Philosopher implements Runnable {
    private Object leftFork;
    private Object rightFork;
    private int maxEatingTime;
    private int eatingCount;
    private final HelloController controller;

    public Philosopher(Object leftFork, Object rightFork, int maxEatingTime, HelloController controller) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.maxEatingTime = maxEatingTime;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (eatingCount < maxEatingTime) {
                doAction(": Thinking");
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
