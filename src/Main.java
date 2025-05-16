
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.locks.*;

class BarberShop {
    private final int waitingChairs;
    final int barberChairs;
    final Queue<Client> waitingRoom;
    final Semaphore barberChairAccess;
    final List<Barber> barbers;
    final Lock lock = new ReentrantLock();
    final Condition clientAvailable = lock.newCondition();

    private volatile boolean shopOpen = true;

    public BarberShop(int numBarbers, int barberChairs, int waitingChairs) {
        this.waitingChairs = waitingChairs;
        this.barberChairs = barberChairs;
        this.waitingRoom = new ArrayDeque<>(waitingChairs);
        this.barberChairAccess = new Semaphore(barberChairs);
        this.barbers = new ArrayList<>();
        for (int i = 0; i < numBarbers; i++) {
            Barber barber = new Barber(i, this);
            barbers.add(barber);
            new Thread(barber).start();
        }
    }

    public void clientArrives(Client client) {
        lock.lock();
        try {
            for (Barber barber : barbers) {
                if (barber.isSleeping() && barberChairAccess.tryAcquire()) {
                    barber.wakeUp(client);
                    return;
                }
            }

            if (waitingRoom.size() < waitingChairs) {
                waitingRoom.add(client);
                System.out.println("Client " + client.getId() + " is waiting.");
                clientAvailable.signalAll();
            } else {
                System.out.println("Client " + client.getId() + " left (no waiting space).");
            }
        } finally {
            lock.unlock();
        }
    }

    public Client ifChariFree() {
        lock.lock();
        try {
            if (!waitingRoom.isEmpty() && barberChairAccess.tryAcquire()) {
                return waitingRoom.poll();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void BchairEmpty(int barberId) {
        barberChairAccess.release();
        System.out.println("Barber " + barberId + "'s chair is now free.");
    }

    public void waitForClients() {
        lock.lock();
        try {
            while (waitingRoom.isEmpty() && shopOpen) {
                clientAvailable.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public boolean isOpen() {
        return shopOpen;
    }

    public void closeShop() {
        lock.lock();
        try {
            shopOpen = false;
            clientAvailable.signalAll(); // разбудить барберов для завершения
            for (Barber barber : barbers) {
                barber.wakeUp(null); // Разбудить всех, если они спят
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BarberShop shop = new BarberShop(3, 3, 5);

        ExecutorService clients = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            int clientId = i;
            clients.execute(() -> {
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                shop.clientArrives(new Client(clientId));
            });
        }

        clients.shutdown();
        clients.awaitTermination(2, TimeUnit.MINUTES);


// Ждем, пока очередь опустеет и освободятся все кресла
        while (true) {
            Thread.sleep(500);
            shop.lock.lock();
            try {
                boolean emptyQueue = shop.waitingRoom.isEmpty();
                boolean allChairsFree = (shop.barberChairAccess.availablePermits() == shop.barberChairs);
                if (emptyQueue && allChairsFree) {
                    break;
                }
            } finally {
                shop.lock.unlock();
            }
        }

        // Закрываем магазин — барберы закончат работу
        shop.closeShop();

        // Немного подождём, чтобы барберы смогли завершить работу
        Thread.sleep(2000);

        System.out.println("Barber shop is closed. All clients served.");
    }
}

class Client {
    private final int id;

    public Client(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

class Barber implements Runnable {
    private final int id;
    private final BarberShop shop;
    private final Lock lock = new ReentrantLock();
    private final Condition whenWakeUp = lock.newCondition();
    private boolean sleeping = true;
    private Client currentClient = null;

    public Barber(int id, BarberShop shop) {
        this.id = id;
        this.shop = shop;
    }

    public boolean isSleeping() {
        lock.lock();
        try {
            return sleeping;
        } finally {
            lock.unlock();
        }
    }

    public void wakeUp(Client client) {
        lock.lock();
        try {
            this.currentClient = client;
            this.sleeping = false;
            whenWakeUp.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                while (currentClient == null && shop.isOpen()) {
                    System.out.println("Barber " + id + " is sleeping.");
                    sleeping = true;
                    whenWakeUp.await();
                }
                if (!shop.isOpen() && currentClient == null) {
                    System.out.println("Barber " + id + " is going home.");
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }

            if (currentClient != null) {
                cutHair(currentClient);
                shop.BchairEmpty(id);
            }

            while (true) {
                currentClient = shop.ifChariFree();
                if (currentClient != null) {
                    cutHair(currentClient);
                    shop.BchairEmpty(id);
                } else {
                    break;
                }
            }

            lock.lock();
            try {
                sleeping = true;
                currentClient = null;
                System.out.println("Barber " + id + " goes to sleep (no client/chair).");
            } finally {
                lock.unlock();
            }

            shop.waitForClients();
        }
    }

    private void cutHair(Client client) {
        System.out.println("Barber " + id + " is cutting hair of Client " + client.getId());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Barber " + id + " finished cutting Client " + client.getId());
    }
}