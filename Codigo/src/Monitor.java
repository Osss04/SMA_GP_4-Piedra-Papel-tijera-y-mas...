

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import XML.MessageGenerator;
import XML.XMLValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Esto por ahora salvo error, ya está listo	    

public class Monitor {

    final String ANSI_GREEN = "\u001B[32m"; //Poner mensajes en color verde :)
    final String ANSI_RESET = "\u001B[0m";
    private int port;
    private List<AgenteConocido> agentesJuego; //Todos los agentes disponibles en el juego
    private ServerSocket serverSocket;

    private HashMap<String, Integer> contadorAgentes;

    javax.swing.Timer contadorParada = new Timer(30000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                if (condicionParada()){
                    detenerTodos();
                }
            } catch(IllegalArgumentException | ParserConfigurationException | TransformerException x){
                contadorParada.start();
            }
        }
    });

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
        this.contadorAgentes = new HashMap<>();
        initializeLogFile(); // Inicializar el archivo CSV con encabezados
    }

    /*
    Autor: Óscar
    Fecha: 17/12/2024
    Función: evalúa si sólo queda un equipo con vida, si es así, el monitor dejará de escuchar y se parará.
     */
    public boolean condicionParada() {
        int equiposConMiembros = 0;
        String nombreEquipoGanador = null;

        for (String equipo : contadorAgentes.keySet()) {
            if (contadorAgentes.get(equipo) > 0) {
                equiposConMiembros++;
                if (equiposConMiembros > 1) {
                    // Hay más de un equipo con miembros
                    return false;
                }
                nombreEquipoGanador = equipo;
            }
        }
        // Si hay 0 o 1 equipos con miembros, cumple la condición
        System.out.println(ANSI_GREEN+"PARAD, EL EQUIPO QUE HA GANADO EL BATTLE ROYALE HA SIDO: \n" +nombreEquipoGanador+"\n¡¡¡¡ENHORABUENA!!!!"+ANSI_RESET);
        return equiposConMiembros == 1;
    }

    /*
    Autor: Óscar
    Fecha: 17/12/2024
    Función: para todos los agentes del sistema.
    */

    public void detenerTodos() throws ParserConfigurationException, TransformerException {
        for (AgenteConocido agente : agentesJuego) {
            String mensajeEnviar = MessageGenerator.createGanoDueloMessage(this.port, "Com1", "Msg1", "Monitor",
                    agente.getIp().toString(), "dest1",agente.getIp().toString(), "");

            try (Socket socket = new Socket(agente.getIp(), agente.getPuerto()+1);
                 OutputStream outputStream = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(outputStream, true)) {

                // Enviar el mensaje
                writer.println(mensajeEnviar);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error al enviar el mensaje de que huyo.");
            }
            // Detener cada agente
        }

        System.out.println("Todos los agentes detenidos.");
    }




    /*
    Autor: Iván
    Fecha: 13/12/2024
    Función: Permite al monitor saber con exactitud el número de agentes que quedan de cada uno de los
    equipos en función de los mensajes que le van llegando.
    */
    private void contadorEquipos(String equipoAgente, int protocolo){
        if(protocolo == 1 && !this.contadorAgentes.containsKey(equipoAgente)){
            this.contadorAgentes.put(equipoAgente,1);
        }
        else if(protocolo == 1){
            this.contadorAgentes.replace(equipoAgente, this.contadorAgentes.get(equipoAgente)+1);
        } else {
            this.contadorAgentes.replace(equipoAgente, this.contadorAgentes.get(equipoAgente)-1);
            if(this.contadorAgentes.get(equipoAgente) == 0){
                this.contadorAgentes.remove(equipoAgente);
            }
        }

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
                    // Capturar la IP y el puerto del emisor
                    String senderAddress = clientSocket.getInetAddress().getHostAddress();
                    Document xmlDoc = parseXMLFromString(message);
                    File xsdFile = new File("esquema_AG_basico.xsd"); // Ruta del archivo XSD
                    boolean isValid = XMLValidator.validateXMLSchema(xsdFile, xsdFile);

                    if (isValid) {
                        System.out.println("Es validooooooo");
                    }
                    String typeProtocol = getElementValue(xmlDoc, "type_protocol");
                    int senderPort = Integer.parseInt(getElementValue(xmlDoc, "origin_port_TCP"));
                    switch (typeProtocol){
                        case "heNacido":
                            addAgent(senderAddress, senderPort);
                            String mensaje = ("[" + senderAddress + " : " + senderPort + "] --> "+ typeProtocol+"]");
                            System.out.println("[" + senderAddress + " : " + senderPort + "] --> " + typeProtocol);
                            String equipoAgente = getElementValue(xmlDoc,"body_info"); //Obtenemos el equipo del agente
                            System.out.println("Equipo del agente que ha nacido:"+equipoAgente);
                            contadorEquipos(equipoAgente, 1); // Método que actualizará aumentando el contador del equipo en cuestión
                            logMessage(senderAddress, senderPort, mensaje);
                            break;
                        case "meMuero":
                            System.out.println("Se eliminará el agente correspondiente:");
                            removeAgent(senderAddress,senderPort);
                            String equipoAgenteMuerto = getElementValue(xmlDoc,"body_info"); //Obtenemos el equipo del agente
                            contadorEquipos(equipoAgenteMuerto, 2); //Método que actualizará disminuyendo el contador del equipo en cuestión
                            System.out.println("Equipo del agente que ha muerto:"+equipoAgenteMuerto);
                            break;
                    }

                    this.contadorParada.start();
                    System.out.println("------------------LISTA TORNEO---------------------\n");
                    for (String i: this.contadorAgentes.keySet()) {
                        System.out.println(i + ": " + this.contadorAgentes.get(i));
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

    public void addAgent(String ip, int port) throws IOException{
        // Crear y agregar un nuevo objeto Agente a la lista
        AgenteConocido nuevoAgente = new AgenteConocido(InetAddress.getByName(ip), port);
        agentesJuego.add(nuevoAgente); // Agregar el agente a la lista
        System.out.println("Agente agregado: " + nuevoAgente);
    }

    /*
    Autor: Óscar
    Fecha: 26/11/2024
    Función: Elimina un objeto agente de la clase monitor en caso de que se reciba un
    mensaje de Me mueroooooo
     */
    public void removeAgent(String ip, int port) throws UnknownHostException {
        // Crear un objeto temporal Agente para buscar
        AgenteConocido agenteAEliminar = new AgenteConocido(InetAddress.getByName(ip) , port);

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
