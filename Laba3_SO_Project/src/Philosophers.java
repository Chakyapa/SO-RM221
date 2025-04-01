import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.*;
class Philosopher implements Runnable {
    private final int id;
    private final Lock leftFork;
    private final Lock rightFork;
    private final AtomicInteger meals;  // Счетчик приемов пищи
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
        System.out.println("Philosopher " + (id + 1) + " is eating. ("+(meals.get()+1) +")");
        gui.updateState(id, "Eating", Color.GREEN);
        Thread.sleep(1000);

    }

    @Override
    public void run() {
        try {
            // Философ ест дважды
            while (meals.get() < 5) {
                think();

                if (leftFork.tryLock()) {
                    try {
                        if (rightFork.tryLock()) {
                            try {
                                eat();
                                meals.incrementAndGet();  // Увеличиваем счетчик приемов пищи
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

public class Philosophers {
    public static void main(String[] args) {
        int n = 7;
        PhilosophersGUI gui = new PhilosophersGUI(n);
        ExecutorService executorService = Executors.newFixedThreadPool(n);
        Lock[] forks = new ReentrantLock[n];
        AtomicInteger[] meals = new AtomicInteger[n]; // Счетчик приемов пищи для каждого философа

        for (int i = 0; i < n; i++) {
            forks[i] = new ReentrantLock();
            meals[i] = new AtomicInteger(0); // Изначально никто не ел
        }

        // Запускаем философов
        for (int i = 0; i < n; i++) {
            Lock leftFork = forks[i];
            Lock rightFork = forks[(i + 1) % n];
            executorService.execute(new Philosopher(i, leftFork, rightFork, meals[i], gui));
        }

        executorService.shutdown();

        // Ждем, пока все философы не поедят дважды
        boolean allAteTwice = false;
        while (!allAteTwice) {
            allAteTwice = true;
            for (AtomicInteger meal : meals) {
                if (meal.get() < 5) {
                    allAteTwice = false;
                    break;
                }
            }
        }

        System.out.println("All philosophers have eaten twice.");
    }
}