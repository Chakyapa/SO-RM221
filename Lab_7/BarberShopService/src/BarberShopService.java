import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BarberShopService {

    public static void main(String[] args) {
        int variant = 2;
        int group = 2;

        int numOfBarbers = variant <= 5 ? variant + 3 + group : variant - 3 + group;
        int numOfWaitingChairs = variant <= 5 ? 2 * variant + 5 + 2 * group : 2 * variant - 6 + group;

        int numOfClients = 50;

        //numOfBarbers = 2;
        //numOfWaitingChairs = 2;

        System.out.println("Barbers: " + numOfBarbers + ", Waiting Chairs: " + numOfWaitingChairs);

        BarberShop shop = new BarberShop(numOfBarbers, numOfWaitingChairs);

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Barbershop has started and is waiting clients on port 8080");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("BarberShop: new Client connection: " + clientSocket);

                    new Thread(new Client(clientSocket, shop)).start();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}