import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Client implements Runnable {

    private int id = 0;

    // сокет для общения с сервером
    private final Socket socket;

    // ссылка на объект барбершопа (обрабатывает логику обслуживания)
    private final BarberShop shop;

    // блокировки для управления ожиданием обслуживания
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    // флаг, указывающий, был ли клиент обслужен
    private boolean serviced = false;


    public Client(Socket socket, BarberShop shop) {
        this.socket = socket;
        this.shop = shop;
    }


    @Override
    public void run() {
        try {

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // получаем ID клиента от сервера
            id = in.readInt();

            System.out.println("Client " + id + " arrived.");

            // запрашиваем стрижку у барбершопа
            int barberId = shop.requestHaircut(this);

            // отправляем ID парикмахера клиенту
            out.writeInt(barberId);

            if (barberId >= 0) {
                // клиент сразу обслуживается
                System.out.println("Client " + id + " is getting haircut from barber " + barberId);
                waitUntilServiced();
                System.out.println("Client " + id + " has been serviced.");
                out.writeInt(1); // сообщаем серверу, что клиент обслужен
            } else if (barberId == -1) {
                System.out.println("Client " + id + " is waiting in queue.");
                waitUntilServiced(); // ждем, пока придет наша очередь
                System.out.println("Client " + id + " has been serviced.");
                out.writeInt(1);
            } else {

                System.out.println("Client " + id + " is leaving, everything is busy.");
            }

            System.out.println("Client " + id + " has left the shop.");
            socket.close(); // закрываем соединение
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getId() {
        return id;
    }

    // метод вызывается парикмахером или барбершопом, чтобы отметить, что клиент обслужен
    public void markServiced() {
        lock.lock();
        try {
            serviced = true;
            condition.signal(); // будим поток клиента
        } finally {
            lock.unlock();
        }
    }


    public void waitUntilServiced() {
        lock.lock();
        try {
            while (!serviced) {
                condition.await(); // ждем сигнала от barbershop
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
