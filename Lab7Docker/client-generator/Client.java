import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 50; i++) {
            URL url = new URL("http://barber_service:8080/client");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            String data = "Client " + i;


            OutputStream os = con.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Client " + i + "  - Response: " + responseCode);


            Thread.sleep(1000);
        }
    }
}
