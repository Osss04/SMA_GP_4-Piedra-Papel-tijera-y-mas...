import Comunicacion.XMLUtils;
import generated.Message;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Esto por ahora salvo error, ya está listo	    

public class Monitor {

    private int port;
    private List<Agent> agentesJuego; //Todos los agentes disponibles en el juego
    private ServerSocket serverSocket;

    ///////////////////////////////////////////////////////////
    //////////Clase interna de agente
    //////////////////////////////////////////////////////////

    private class Agent {
        String ip;
        int port;

        //Constructor
        Agent(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }//constructor

        //ToString
        @Override
        public String toString() {
            return "Agente{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    '}';
        }//to string

    }//Agent class

    ///////////////////////////////////////////////////////////
    //////////Constructor de la clase
    //////////////////////////////////////////////////////////

    public Monitor(int port) {
        this.port = port;
        this.agentesJuego = new ArrayList<>();
        initializeLogFile(); // Inicializar el archivo CSV con encabezados
    }


    ///////////////////////////////////////////////////////////
    //////////Inicializa archivo CSV con encabezados
    //////////////////////////////////////////////////////////

    private void initializeLogFile() {
        File logFile = new File("monitor_logs.csv");
        if (!logFile.exists()) {
            try (FileWriter fw = new FileWriter(logFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                 // Agregar encabezados
                 out.println("Time,IP,Port,Msg");
            } catch (IOException e) {
                System.err.println("Error al crear el archivo de log CSV: " + e.getMessage());
            }
        }
    }

    ///////////////////////////////////////////////////////////
    //////////Escucha msgs enviados por los agentes
    //////////////////////////////////////////////////////////

    public void listenToMessages() {
        try{
            serverSocket = new ServerSocket(this.port);
            System.out.println("Monitor escuchando en el puerto: " + this.port);

            while (true) {
                try(Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
                    StringBuilder messageBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        messageBuilder.append(line).append("\n");
                    }
                    String message = messageBuilder.toString();
                    // Capturar la IP y el puerto del emisor
                    String senderAddress = clientSocket.getInetAddress().getHostAddress();
                    int senderPort = clientSocket.getPort();
                    // Agregar el agente a la lista
                    addAgent(senderAddress, senderPort);
                    // Deserializar el mensaje XML
                    try {
                        Message receivedMessage = XMLUtils.deserializeMessage(message);
                        //System.out.println("Mensaje"+receivedMessage);
                        System.out.println(receivedMessage);
                        String mensaje_string = "ID_Comunicacion "+receivedMessage.getComuncId()+". ID_Msg: "+receivedMessage.getMsgId()+". Protocolo: "+receivedMessage.getHeader().getComunicationProtocol()+". Step: "+receivedMessage.getHeader().getProtocolStep()+". ID_origen: "+receivedMessage.getHeader().getOrigin().getOriginId()+".IP_orig: "+receivedMessage.getHeader().getOrigin().getOriginIp()+". Port_orig: "+receivedMessage.getHeader().getOrigin().getOriginPortTCP();
                        System.out.println(mensaje_string);
                        //System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + receivedMessage);
                        //System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + message);
                    logMessage(senderAddress, senderPort, mensaje_string);
                    } catch (JAXBException e) {
                        e.printStackTrace();  // Imprime el stack trace de la excepción
                        System.err.println("Error al obtener el mensaje XML: " + e.getMessage());  // Mensaje de error
                    }
                } catch (IOException e) {
                    System.err.println("Error al procesar el mensaje: " + e.getMessage());
                }//catch

            }//while
        } catch (IOException e) {
            System.err.println("Error al iniciar el ServerSocket en el puerto " + this.port + ": " + e.getMessage());
            System.out.println("Reintentando en 5 segundos...");
            try {
                Thread.sleep(5000); // Espera de 5 segundos antes de reintentar
            } catch (InterruptedException ie) {
                System.err.println("Monitor interrumpido durante el tiempo de espera de reconexión.");
            }//catch
        }//cathc
    }//listenToMessages

    ///////////////////////////////////////////////////////////
    //////////Registar agente en la lista de agentes
    //////////////////////////////////////////////////////////

    public void addAgent(String ip, int port) {
        // Crear y agregar un nuevo objeto Agente a la lista
        Agent nuevoAgente = new Agent(ip, port);
        agentesJuego.add(nuevoAgente); // Agregar el agente a la lista
        System.out.println("Agente agregado: " + nuevoAgente);
    }
    ///////////////////////////////////////////////////////////
    //////////Registar mensaje en el monitor
    //////////////////////////////////////////////////////////

    public void logMessage(String ip, int port, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        File logFile = new File("monitor_logs.csv");
        try (FileWriter fw = new FileWriter(logFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

                // Registrar id,time,IP, puerto y mensaje
                out.printf("%s,%s,%d,%s%n", timestamp, ip, port, message); // Formato CSV
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de log CSV: " + e.getMessage());
        }
    }

    ///////////////////////////////////////////////////////////
    //////////MAIN
    //////////////////////////////////////////////////////////

    public static void main(String[] args) {
        int monitorPort = 4300; // Puerto en el que escucha el monitor

        Monitor monitor = new Monitor(monitorPort);
        monitor.listenToMessages(); // Iniciar monitor para recibir mensajes
    }
}
