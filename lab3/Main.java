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
    private final int cycles;

    public Philosopher(int id, Fork leftFork, Fork rightFork, int cycles) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.cycles = cycles;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep((int) (Math.random() * 100));
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating.");
        Thread.sleep((int) (Math.random() * 100));
    }

}

public class main {
    public static void main(String[] args) {
        int N = 5;
        int cycles = 10;
        Fork[] forks = new Fork[N];
        Philosopher[] philosophers = new Philosopher[N];
    }
}
