import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {
        Timer timer = new Timer();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer 1: Acest mesaj apare la fiecare 2 secunde.");
            }
        }, 0, 2000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer 2: Acest mesaj apare o singură dată după 5 secunde.");
            }
        }, 5000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer 3: Acest mesaj apare la fiecare 3 secunde, începând după 1 secundă.");
            }
        }, 1000, 3000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Programul se va închide acum.");
                System.exit(0);
            }
        }, 10000);

    }
}
