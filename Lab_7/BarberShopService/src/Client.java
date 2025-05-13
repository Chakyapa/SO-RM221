import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements Runnable {


    private int id = 0;

    private final Socket socket;
    private final BarberShop shop;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private boolean serviced = false;

    public Client(Socket socket, BarberShop shop)
    {
        this.socket = socket;
        this.shop = shop;
    }

    @Override
    public void run() {

        try {

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            id = in.readInt();

            System.out.println("Client " + id + " arrived.");

            int barberId = shop.requestHaircut(this);

            out.writeInt(barberId);

            if (barberId >= 0) {
                System.out.println("Client " + id + " is getting haircut from barber " + (barberId));
                waitUntilServiced();
                System.out.println("Client " + id + " has been serviced.");
                out.writeInt(1);
            } else if (barberId == -1) {
                System.out.println("Client " + id + " is waiting in queue.");
                waitUntilServiced();
                System.out.println("Client " + id + " has been serviced.");
                out.writeInt(1); // Клиент обслужен через очередь
            }
            else {
                System.out.println("Client " + id + " is leaving, everything is busy.");
            }

            System.out.println("Client " + id + " has left the shop.");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getId() {
        return id;
    }

    public void markServiced() {
        lock.lock();
        try {
            serviced = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void waitUntilServiced() {

        lock.lock();
        try {
            while (!serviced)
            {
                condition.await();
            }

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

    }


}