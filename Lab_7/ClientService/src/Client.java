import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements Runnable {

    private static final String BARBER_HOST = System.getenv("BARBER_HOST") != null ?
            System.getenv("BARBER_HOST") : "barber";

    private final int id;

    public Client(int id)
    {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Client " + id + " arrived.");

        try {

            Socket socket = new Socket(BARBER_HOST, 8080);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeInt(id);

            int response = in.readInt();

            if (response >= 0) {
                System.out.println("Client " + id + " is getting haircut from barber " + (response));
                in.readInt();
                System.out.println("Client " + id + " has been serviced.");
            } else if (response == -1) {
                System.out.println("Client " + id + " is waiting in queue.");
                in.readInt();
                System.out.println("Client " + id + " has been serviced.");
            } else {
                System.out.println("Client " + id + " is leaving, everything is busy.");
            }

            socket.close();

        } catch (ConnectException e) {
            System.out.println("Client " + id + " could not be connected to the BarberShop.");
        } catch (Exception e) {
            System.out.println("Client " + id + " Error: " + e.getMessage());
        }
    }


}