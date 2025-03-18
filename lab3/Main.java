import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fork {
    private final Lock lock = new ReentrantLock();

    public boolean pickUp() {
        return lock.tryLock();
    }

    public void putDown() {
        lock.unlock();
    }
}

class Philosopher extends Thread {
    private final int id;
    private final Fork leftFork;
    private final Fork rightFork;
    private final int totalCycles;
    private int remainingCycles;

    public Philosopher(int id, Fork leftFork, Fork rightFork, int totalCycles) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.totalCycles = totalCycles;
        this.remainingCycles = totalCycles;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking ");
        Thread.sleep((int) (Math.random() * 100));
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating. Remaining cycles: " + remainingCycles);
        Thread.sleep((int) (Math.random() * 100));
    }

    @Override
    public void run() {
        try {
            while (remainingCycles > 0) {
                think();
                if (leftFork.pickUp()) {
                    if (rightFork.pickUp()) {
                        eat();
                        remainingCycles--;
                        rightFork.putDown();
                    }
                    leftFork.putDown();
                }
            }
            System.out.println("Philosopher " + id + " has finished all cycles.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        int N = 5;
        int cycles = 10;
        Fork[] forks = new Fork[N];
        Philosopher[] philosophers = new Philosopher[N];

        for (int i = 0; i < N; i++) {
            forks[i] = new Fork();
        }

        for (int i = 0; i < N; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % N];

            if (i == N - 1) {
                philosophers[i] = new Philosopher(i, rightFork, leftFork, cycles);
            } else {
                philosophers[i] = new Philosopher(i, leftFork, rightFork, cycles);
            }
            philosophers[i].start();
        }

        for (Philosopher philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All philosophers have finished their cycles.");
    }
}