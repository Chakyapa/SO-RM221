import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static List<ProductivityTimerApplication> apps = new ArrayList<>();

    public static void main(String[] args) {
        newApp();
    }

    public static void newApp() {
        ProductivityTimerApplication app = new ProductivityTimerApplication();
        apps.add(app);
    }

    public static void closeApp(ProductivityTimerApplication app) {
        apps.remove(app);
        if (apps.isEmpty()) {
            System.exit(0);
        }
    }

}