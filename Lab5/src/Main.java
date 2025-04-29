import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                configureWindowsFirewall();
                Thread.sleep(3000); 
                //removeWindowsFirewallRule();
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                configureLinuxFirewall();
                Thread.sleep(3000);
                removeLinuxFirewallRule();
            } else {
                System.out.println("Unknown operating system: " + os);
            }

            checkNetworkCommands();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void configureWindowsFirewall() throws IOException, InterruptedException {
        System.out.println("Adding firewall rule on Windows for port 8080...");
        String command = "netsh advfirewall firewall add rule name=\"Allow8080\" dir=in action=allow protocol=TCP localport=8080";
        executeCommand(command);
    }

    private static void removeWindowsFirewallRule() throws IOException, InterruptedException {
        System.out.println("Removing firewall rule on Windows...");
        String command = "netsh advfirewall firewall delete rule name=\"Allow8080\"";
        executeCommand(command);
    }

    private static void configureLinuxFirewall() throws IOException, InterruptedException {
        System.out.println("Adding iptables rule on Linux for port 8080...");
        String command = "sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT";
        executeCommand(command);
    }

    private static void removeLinuxFirewallRule() throws IOException, InterruptedException {
        System.out.println("Removing iptables rule on Linux for port 8080...");
        String command = "sudo iptables -D INPUT -p tcp --dport 8080 -j ACCEPT";
        executeCommand(command);
    }

    private static void checkNetworkCommands() throws IOException, InterruptedException {
        System.out.println("\n--- Running ipconfig (Windows) / ip a (Linux) ---");
        executeCommand(System.getProperty("os.name").toLowerCase().contains("win") ? "ipconfig" : "ip a");

        System.out.println("\n--- Running ping to 8.8.8.8 ---");
        executeCommand("ping -c 4 8.8.8.8");

        System.out.println("\n--- Running tracert / traceroute to 8.8.8.8 ---");
        executeCommand(System.getProperty("os.name").toLowerCase().contains("win") ? "tracert 8.8.8.8" : "traceroute 8.8.8.8");
    }

    private static void executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    }
}
