
public class ClientService {
    public static void main(String[] args) {
        int numOfClients = 50;

        System.out.println("Client Service has started");

        // создаём клиентов
        for (int i = 1; i <= numOfClients; i++) {
            new Thread(new Client(i)).start();
            try {
                Thread.sleep((int) (Math.random() * 800) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}