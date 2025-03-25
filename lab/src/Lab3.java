import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Lab3 {
    private static final int NUMAR_FILOZOFI = 18;
    private static final Filosof[] filosofi = new Filosof[NUMAR_FILOZOFI];
    private static final Betisoare betisoare = Betisoare.getInstance(NUMAR_FILOZOFI);
    private static final Lock consoleLock = new ReentrantLock();
    private static JTextArea textArea;
    private static ExecutorService executor;
    private static final AtomicInteger filosofiTerminati = new AtomicInteger(0);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dining Philosophers - Eat 3 Times Each");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        panel.add(startButton);
        panel.add(stopButton);
        frame.add(panel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startSimulation());

        frame.setVisible(true);
    }

    private static void startSimulation() {
        executor = Executors.newFixedThreadPool(NUMAR_FILOZOFI);

        for (int i = 0; i < NUMAR_FILOZOFI; i++) {
            filosofi[i] = new Filosof(i, betisoare);
            executor.execute(filosofi[i]);
        }
    }

    static class Betisoare {
        private static Betisoare instance;
        private final Semaphore[] betisoare;

        private Betisoare(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Numărul de bețișoare trebuie să fie pozitiv.");
            }
            betisoare = new Semaphore[n];
            for (int i = 0; i < n; i++) {
                betisoare[i] = new Semaphore(1);
            }
        }

        public static synchronized Betisoare getInstance(int n) {
            if (instance == null || instance.betisoare.length != n) {
                instance = new Betisoare(n);
            }
            return instance;
        }

        public void iaBetisoare(int id) throws InterruptedException {
            if (id % 2 == 0) {
                betisoare[id].acquire();
                betisoare[(id + 1) % NUMAR_FILOZOFI].acquire();
            } else {
                betisoare[(id + 1) % NUMAR_FILOZOFI].acquire();
                betisoare[id].acquire();
            }
        }

        public void lasaBetisoare(int id) {
            betisoare[id].release();
            betisoare[(id + 1) % NUMAR_FILOZOFI].release();
        }
    }

    enum Actiune {
        GANDESTE("gândește"),
        MANANCA("mănâncă");

        private final String descriere;

        Actiune(String descriere) {
            this.descriere = descriere;
        }

        public String getDescriere() {
            return descriere;
        }
    }

    static class Filosof implements Runnable {
        private final int id;
        private final Betisoare betisoare;
        private int mancariRamase = 3;

        public Filosof(int id, Betisoare betisoare) {
            this.id = id;
            this.betisoare = betisoare;
        }

        @Override
        public void run() {
            try {
                while (mancariRamase > 0) {
                    actiune(Actiune.GANDESTE);
                    betisoare.iaBetisoare(id);
                    actiune(Actiune.MANANCA);
                    betisoare.lasaBetisoare(id);
                    mancariRamase--;
                    printMessage("Filozoful " + id + " a mai rămas cu " + mancariRamase + " mese.");
                }
                printMessage("Filozoful " + id + " a terminat de mâncat de 3 ori.");
                if (filosofiTerminati.incrementAndGet() == NUMAR_FILOZOFI) {
                    printMessage("Toți filozofii au terminat. Oprire executor.");
                    executor.shutdown();
                }
            } catch (InterruptedException e) {
                printMessage("Filozoful " + id + " a fost întrerupt.");
                Thread.currentThread().interrupt();
            }
        }

        private void actiune(Actiune actiune) throws InterruptedException {
            printMessage(() -> "Filozoful " + id + " " + actiune.getDescriere() + "...");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void printMessage(Supplier<String> mesajSupplier) {
            consoleLock.lock();
            try {
                String mesaj = mesajSupplier.get();
                SwingUtilities.invokeLater(() -> textArea.append(mesaj + "\n"));
            } finally {
                consoleLock.unlock();
            }
        }

        private void printMessage(String mesaj) {
            consoleLock.lock();
            try {
                SwingUtilities.invokeLater(() -> textArea.append(mesaj + "\n"));
            } finally {
                consoleLock.unlock();
            }
        }
    }
}
