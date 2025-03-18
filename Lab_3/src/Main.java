import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        int n = 15;
        int eatingRounds = 3;

        ReentrantLock[] forks = new ReentrantLock[n];
        Philosopher[] philosophers = new Philosopher[n];

        for (int i = 0; i < n; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < n; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % n];

            if (i == n - 1) {
                philosophers[i] = new Philosopher(i + 1, rightFork, leftFork, eatingRounds);
            } else {
                philosophers[i] = new Philosopher(i + 1, leftFork, rightFork, eatingRounds);
            }

            philosophers[i].start();
        }

        for (Philosopher philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finished eating rounds.");

    }
}