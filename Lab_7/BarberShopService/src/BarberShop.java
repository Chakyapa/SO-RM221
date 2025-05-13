
import java.util.LinkedList;
import java.util.Queue;


public class BarberShop {
    private final int barberCount;
    private final int waitingChairs;
    private final Barber[] barbers;
    private final Queue<Client> waitingQueue;

    public BarberShop(int barberCount, int waitingChairs) {
        this.barberCount = barberCount;
        this.waitingChairs = waitingChairs;
        this.barbers = new Barber[barberCount];
        this.waitingQueue = new LinkedList<>();

        for (int i = 0; i < barberCount; i++) {
            barbers[i] = new Barber(i + 1, this);
            barbers[i].start();
        }
    }

    public synchronized int requestHaircut(Client client)
    {
        for (int i = 0; i < barberCount; i++)
        {
            if (!barbers[i].isBusy()) {
                barbers[i].assignClient(client);
                return i;
            }
        }

        if (waitingQueue.size() < waitingChairs) {
            waitingQueue.offer(client);
            return -1;
        }

        return -2;
    }

    public synchronized boolean getWaitingClient(Barber barber)
    {
        if (waitingQueue.isEmpty()) {
            return false;
        }

        barber.assignClient(waitingQueue.poll());
        return true;
    }
}