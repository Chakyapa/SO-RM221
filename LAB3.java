import java.util.concurrent.Semaphore;

public class LAB3 {
    static class Betisoare {
    private static Betisoare instance;    private final Semaphore[] betisoare;
    private Betisoare(int n) {
        if (n <= 0) {            throw new IllegalArgumentException("Numărul de betisoare trebuie să fie pozitiv.");
        }        betisoare = new Semaphore[n];
        for (int i = 0; i < n; i++) {            betisoare[i] = new Semaphore(1);
        }    }

    public static synchronized Betisoare getInstance(int n) {        if (instance == null || instance.betisoare.length != n) {
        instance = new Betisoare(n);        }
        return instance;    }
    /**
     * Ia betisoarele necesare pentru a mânca.     * @param id ID-ul filozofului
     * @throws InterruptedException dacă este întrerupt     */
    public void iaBetisoare(int id) throws InterruptedException {        if (id % 2 == 0) {
        betisoare[id].acquire();            betisoare[(id + 1) % NUMAR_FILOZOFI].acquire();
    } else {            betisoare[(id + 1) % NUMAR_FILOZOFI].acquire();
        betisoare[id].acquire();        }
    }
    /**     * Lasă betisoarele după ce a mâncat.
     * @param id ID-ul filozofului     */
    public void lasaBetisoare(int id) {
        betisoare[id].release();        betisoare[(id + 1) % NUMAR_FILOZOFI].release();
    }}
    enum Actiune {
        GANDESTE("gândește"),    MANANCA("mănâncă");
        private final String descriere;
        Actiune(String descriere) {
            this.descriere = descriere;    }
        public String getDescriere() {
            return descriere;    }
    }
}
