import java.io.*;
import java.net.*;

// Esto por ahora salvo error, ya está listo	    

public class Monitor {

    private int port;

    public Monitor(int port) {
        this.port = port;
    }

    // Escuchar mensajes enviados por los agentes
    public void listenToMessages() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Monitor escuchando en el puerto: " + this.port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = in.readLine();

                // Capturar la IP y el puerto del emisor
                String senderAddress = clientSocket.getInetAddress().getHostAddress();
                int senderPort = clientSocket.getPort();

                System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + message);
                logMessage(senderAddress, senderPort, message);

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logMessage(String ip, int port, String message) {
        File logFile = new File("Codigo/out/monitor_logs.txt");
        try (FileWriter fw = new FileWriter(logFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            // Registrar IP, puerto y mensaje
            out.println("[" + ip + " : " + port + "] " + "--> " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int monitorPort = 4300; // Puerto en el que escucha el monitor

        Monitor monitor = new Monitor(monitorPort);
        monitor.listenToMessages(); // Iniciar monitor para recibir mensajes
    }
}
