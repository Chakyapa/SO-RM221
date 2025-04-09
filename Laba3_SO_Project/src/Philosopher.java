import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.*;

public class Philosopher implements Runnable {
    private final int id;
    private final Lock leftFork;
    private final Lock rightFork;
    private final AtomicInteger meals;
    private final PhilosophersGUI gui;

    public Philosopher(int id, Lock leftFork, Lock rightFork, AtomicInteger meals, PhilosophersGUI gui) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.meals = meals;
        this.gui = gui;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + (id + 1) + " is thinking.");
        gui.updateState(id, "Thinking", Color.YELLOW);
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + (id + 1) + " is eating. (" + (meals.get() + 1) + ")");
        gui.updateState(id, "Eating", Color.GREEN);
        Thread.sleep(1000);
    }

    @Override
    public void run() {
        try {
            while (meals.get() < 5) {
                think();

                if (leftFork.tryLock()) {
                    try {
                        if (rightFork.tryLock()) {
                            try {
                                eat();
                                meals.incrementAndGet();
                            } finally {
                                rightFork.unlock();
                            }
                        }
                    } finally {
                        leftFork.unlock();
                    }
                }
            }
            gui.updateState(id, "Full", Color.BLUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
