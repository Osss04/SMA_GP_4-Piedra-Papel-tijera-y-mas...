

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import XML.XMLValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

        // Equals
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Agent agent = (Agent) o;
            return port == agent.port && ip.equals(agent.ip);
        }

        // HashCode
        @Override
        public int hashCode() {
            return Objects.hash(ip, port);
        }
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
                    String message = in.readLine();
                    System.out.println(message);
                    // Capturar la IP y el puerto del emisor
                    String senderAddress = clientSocket.getInetAddress().getHostAddress();
                    //int senderPort = clientSocket.getPort();
                    Document xmlDoc = parseXMLFromString(message);
                    File xsdFile = new File("esquema_AG_basico.xsd"); // Ruta del archivo XSD
                    //boolean isValid = XMLValidator.validateXMLSchema(xsdFile, xsdFile);
                    String typeProtocol = getElementValue(xmlDoc, "type_protocol");
                    int senderPort = Integer.parseInt(getElementValue(xmlDoc, "origin_port_TCP"));
                    switch (typeProtocol){
                        case "hola":
                            addAgent(senderAddress, senderPort);
                            String mensaje = ("[" + senderAddress + " : " + senderPort + "] --> "+ typeProtocol+"]");
                            System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + typeProtocol);
                            //System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + receivedMessage);
                            //System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + message);
                            logMessage(senderAddress, senderPort, mensaje);
                            break;
                        case "Me mueroooo":
                            System.out.println("Se eliminará el agente correspondiente:");
                            removeAgent(senderAddress,senderPort);
                            break;
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
        }//catch
    }//listenToMessages

    ///////////////////////////////////////////////////
    //Parsear el XML de String a Document
    //////////////////////////////////////////////////
    private Document parseXMLFromString(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new java.io.ByteArrayInputStream(xmlString.getBytes()));
        } catch (Exception e) {
            System.err.println("Error al parsear el XML: " + e.getMessage());
            return null;
        }
    }

    //////////////////////////////////////////////////
    //Obtener el valor de un elemento por su nombre en el XML
    //////////////////////////////////////////////////
    private String getElementValue(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return null; // Elemento no encontrado
        }
    }


    ///////////////////////////////////////////////////////////
    //////////Registar agente en la lista de agentes
    //////////////////////////////////////////////////////////

    public void addAgent(String ip, int port) {
        // Crear y agregar un nuevo objeto Agente a la lista
        Agent nuevoAgente = new Agent(ip, port);
        agentesJuego.add(nuevoAgente); // Agregar el agente a la lista
        System.out.println("Agente agregado: " + nuevoAgente);
    }

    /*
    Autor: Óscar
    Fecha: 26/11/2024
    Función: Elimina un objeto agente de la clase monitor en caso de que se reciba un
    mensaje de Me mueroooooo
     */
    public void removeAgent(String ip, int port) {
        // Crear un objeto temporal Agente para buscar
        Agent agenteAEliminar = new Agent(ip, port);

        // Intentar eliminar el agente de la lista
        if (agentesJuego.remove(agenteAEliminar)) {
            System.out.println("Agente eliminado: " + agenteAEliminar);
        } else {
            System.out.println("No se encontró el agente: " + agenteAEliminar);
        }
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
