import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Agente {

    private InetAddress monitorAddress;
    private int monitorPort;
    private DatagramSocket broadcastSocket;
    private int listeningPort;
    private ServerSocket serverSocket; // Socket para recibir mensajes directos

    public Agente(InetAddress monitorAddress, int monitorPort) throws IOException {
        this.monitorAddress = monitorAddress;
        this.monitorPort = monitorPort;
        this.broadcastSocket = new DatagramSocket(); // El puerto será dinámico
        this.serverSocket = new ServerSocket(0); // Asignación de puerto dinámico
        this.listeningPort = serverSocket.getLocalPort(); // Obtener el puerto asignado dinámicamente
    }

    // Enviar mensaje por broadcast
    public void sendBroadcast(String message) throws IOException {
        String fullMessage = message + " PORT:" + listeningPort; // Incluir el puerto dinámico
        byte[] buffer = fullMessage.getBytes();
        InetAddress group = InetAddress.getByName("255.255.255.255"); // Broadcast
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 9876);
        broadcastSocket.send(packet);
    }

    // Escuchar mensajes broadcast y responder
    public void listenForBroadcast() {
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(9876)); // Reutilización de puerto para todos los agentes
            socket.setSoTimeout(1000); // 1 segundo de timeout para esperar respuestas
            byte[] buffer = new byte[256];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet); // Intentar recibir mensaje
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress();

                    // Extraer el puerto dinámico del mensaje recibido
                    String[] messageParts = receivedMessage.split(" ");
                    int senderPort = Integer.parseInt(messageParts[1].split(":")[1]);

                    System.out.println("Recibido mensaje: " + receivedMessage + " de " + senderAddress);

                    // Si recibo "Estoy Aqui", envío "Hola"
                    if (receivedMessage.contains("Estoy Aqui")) {
                        sendDirectMessage("Hola", senderAddress, senderPort);
                        sendToMonitor("Enviado Hola a: " + senderAddress.getHostAddress());
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("No se recibieron mensajes. Esperando...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enviar respuesta directa a otro agente
    public void sendDirectMessage(String message, InetAddress address, int port) {
        try (Socket socket = new Socket(address, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enviar copia del mensaje al monitor
    public void sendToMonitor(String message) {
        try (Socket socket = new Socket(monitorAddress, monitorPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Escuchar mensajes directos enviados al agente
    public void listenForDirectMessages() {
        System.out.println("Agente escuchando mensajes directos en puerto: " + listeningPort);
        System.out.println("--DEBUG--");
        while (true) {
            System.out.println("--DEBUG2--");
            ///////////////////////////////////////////
            // PETA AQUÍ //////////////////////////////
            ///////////////////////////////////////////
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("--DEBUG3--");
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("--DEBUG4--");
                String receivedMessage = in.readLine();
                System.out.println("Mensaje directo recibido: " + receivedMessage);

                // Si recibo "Hola", envío "Adios"
                if (receivedMessage.equals("Hola")) {
                    InetAddress senderAddress = clientSocket.getInetAddress();
                    int senderPort = clientSocket.getPort();
                    sendDirectMessage("Adios", senderAddress, senderPort);
                    sendToMonitor("Enviado Adios a: " + senderAddress.getHostAddress());
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        // ESTA ES LA IP QUE YO TENIA PUESTA, TENEIS QUE CAMBIARLA POR LA VUESTRA PARA
        // PROBAR
        InetAddress monitorAddress = InetAddress.getByName("192.168.56.1"); // Reemplazar
        int monitorPort = 9999; // Puerto del monitor

        Agente agente = new Agente(monitorAddress, monitorPort);

        // Enviar broadcast
        // Por ahora solo envía "Estoy Aqui"
        agente.sendBroadcast("Estoy Aqui");

        // Escuchar broadcast y responder
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(agente::listenForBroadcast, 0, 2, TimeUnit.SECONDS);

        // Escuchar mensajes directos
        executor.submit(agente::listenForDirectMessages);
    }
}
