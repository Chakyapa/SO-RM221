import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Barber {
    private static final int MAX_WAITING_CHAIRS = 14;
    private static final BlockingQueue<String> waitingRoom = new ArrayBlockingQueue<>(MAX_WAITING_CHAIRS);

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // HTTP endpoint dor client
        server.createContext("/client", new HttpHandler() {
            public void handle(HttpExchange exchange) {
                try {
                    String response;
        
                    if ("POST".equals(exchange.getRequestMethod())) {
                        InputStream is = exchange.getRequestBody();
                        String client = new String(is.readAllBytes());
                        
                        boolean accepted = waitingRoom.offer(client);
        
                        if (accepted) {
                            response = "Client accepted";
                            System.out.println(client + " entered waiting room");
                        } else {
                            response = "No space. Client left.";
                            System.out.println(client + " left. No chairs.");
                        }
        
                        exchange.sendResponseHeaders(200, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
        
                    } else {
                        exchange.sendResponseHeaders(405, 0L);
                        exchange.close(); // Ensure exchange is closed on error
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Barber server started on port 8080.");

        for(int i = 1; i <= 15; i++) {
            int id = i;
            new Thread(() -> {
                while(true) {
                    try {
                        String client = waitingRoom.take();
                        System.out.println("Barber-" + id + " is cutting " + client);
                        Thread.sleep(3000);
                        System.out.println("Barber-" + id + " finished " + client);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}