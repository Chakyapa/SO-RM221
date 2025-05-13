
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barber extends Thread {

    public enum State {
        SLEEPING, WORKING
    }

    private final int id;
    private final BarberShop shop;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private volatile Client assignedClient = null;

    private volatile State state = State.SLEEPING;

    public Barber(int id, BarberShop shop) {
        this.id = id;
        this.shop = shop;
    }

    public boolean isBusy() {
        return state == State.WORKING;
    }

    public void assignClient(Client client) {
        lock.lock();
        try {
            this.assignedClient = client;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                while (assignedClient == null)
                {
                    if (shop.getWaitingClient(this))
                    {
                        break;
                    }
                    else {
                        state = State.SLEEPING;
                        System.out.println("Barber " + id + " is sleeping.");
                        condition.await();
                    }

                }

                state = State.WORKING;
                System.out.println("Barber " + id + " is cutting hair of client " + assignedClient.getId());
            } catch (InterruptedException e) {
                break;
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(10200);
            } catch (InterruptedException e) {
                break;
            }

            System.out.println("Barber " + id + " finished with client " + assignedClient.getId());
            assignedClient.markServiced();
            assignedClient = null;
        }
    }
}