import java.io.*;
import java.net.*;

// Esto por ahora va regular, se enciende y se pone a escuchar, pero no hace nada

public class Monitor {

    private int port;

    public Monitor(int port) {
        this.port = port;
    }

    // Escuchar mensajes enviados por los agentes
    public void listenToMessages() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Monitor escuchando en el puerto: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = in.readLine();
                System.out.println("Monitor recibió: " + message);

                logMessage(message); // Registrar el mensaje
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loguear mensaje en un archivo de texto
    public void logMessage(String message) {
        try (FileWriter fw = new FileWriter("monitor_logs.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            out.println(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int monitorPort = 9999; // Puerto en el que escucha el monitor

        Monitor monitor = new Monitor(monitorPort);
        monitor.listenToMessages(); // Iniciar monitor para recibir mensajes
    }
}
