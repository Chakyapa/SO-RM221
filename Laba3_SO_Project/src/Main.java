import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        int n = 7;
        PhilosophersGUI gui = new PhilosophersGUI(n);
        Lock[] forks = new ReentrantLock[n];
        AtomicInteger[] meals = new AtomicInteger[n];
        Thread[] philosophers = new Thread[n];

        for (int i = 0; i < n; i++) {
            forks[i] = new ReentrantLock();
            meals[i] = new AtomicInteger(0);
        }

        for (int i = 0; i < n; i++) {
            Lock leftFork = forks[i];
            Lock rightFork = forks[(i + 1) % n];
            Philosopher philosopher = new Philosopher(i, leftFork, rightFork, meals[i], gui);
            philosophers[i] = new Thread(philosopher);
            philosophers[i].start();
        }

        for (int i = 0; i < n; i++) {
            try {
                philosophers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All philosophers have eaten 5 times.");
    }
}
