public class Main {
    public static void main(String[] args) {
        int numBarbers = 18; // k
        int numChairs = 35;  // n

        Barbershop shop = new Barbershop(numChairs);

        // Pornim frizerii
        for (int i = 1; i <= numBarbers; i++) {
            new Barber(shop, "Frizer-" + i).start();
        }

        // Generăm clienți periodic
        for (int i = 1; i <= 100; i++) {
            new Client(shop, "Client-" + i).start();
            try {
                Thread.sleep(300); // clienți noi vin la 300ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Client extends Thread {
    private final Barbershop shop;

    public Client(Barbershop shop, String name) {
        super(name);
        this.shop = shop;
    }

    @Override
    public void run() {
        if (!shop.enterBarbershop(this)) {
            System.out.println(getName() + " pleacă. Nu sunt scaune libere.");
        }
    }
}

public class Barber extends Thread {
    private final Barbershop shop;

    public Barber(Barbershop shop, String name) {
        super(name);
        this.shop = shop;
    }

    @Override
    public void run() {
        while (true) {
            Client client = shop.nextClient();
            if (client != null) {
                System.out.println(getName() + " tunde " + client.getName());
                try {
                    Thread.sleep(2000); // tunsul durează 2 secunde
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
public class Barbershop {
    private final BlockingQueue<Client> waitingChairs;

    public Barbershop(int n) {
        this.waitingChairs = new ArrayBlockingQueue<>(n);
    }

    public boolean enterBarbershop(Client client) {
        boolean added = waitingChairs.offer(client);
        if (added) {
            System.out.println(client.getName() + " așteaptă.");
        }
        return added;
    }

    public Client nextClient() {
        try {
            return waitingChairs.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
