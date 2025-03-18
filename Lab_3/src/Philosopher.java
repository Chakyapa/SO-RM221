import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher extends Thread{
    private final int id;
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;
    private final int eatingRounds;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork, int eatingRounds) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.eatingRounds = eatingRounds;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " thinks.");
        Thread.sleep(200);
    }

    private void eat() throws InterruptedException {
        System.out.println(LocalTime.now().format(formatter) + " Philosopher " + id + " start eating.");
        Thread.sleep(200);
        System.out.println(LocalTime.now().format(formatter) + " Philosopher " + id + " finish eating.");
    }

    private void takeOneFork()
    {
        System.out.println(LocalTime.now().format(formatter) + " Philosopher " + id + " takes one fork.");
    }

    private void takeTwoForks()
    {
        System.out.println(LocalTime.now().format(formatter) + " Philosopher " + id + " takes two forks.");
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < eatingRounds; i++) {

                think();

                leftFork.lock();
                takeOneFork();
                rightFork.lock();
                takeTwoForks();
                eat();
                rightFork.unlock();
                leftFork.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
