public class ClientService {
    public static void main(String[] args) {
        // общее количество клиентов
        int numOfClients = 50;

        System.out.println("Client Service has started");

        // создаём клиентов
        for (int i = 1; i <= numOfClients; i++) {
            // создаем и запускаем новый поток с клиентом, передаём его номер
            new Thread(new Client(i)).start();

            try {

                Thread.sleep((int) (Math.random() * 800) + 500);
            } catch (InterruptedException e) {
                // обработка исключения, если поток был прерван во время сна
                e.printStackTrace();
            }
        }
    }
}
