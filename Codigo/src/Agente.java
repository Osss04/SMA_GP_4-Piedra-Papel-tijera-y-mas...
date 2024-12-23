import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import XML.MessageGenerator;
import com.sun.tools.javac.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import XML.XMLValidator;
import org.w3c.dom.Element;

import static java.lang.System.exit;
import static java.lang.System.out;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Agente {

    Timer contadorDuelo = new Timer(10000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                FuncionDeAgente();
            } catch(IllegalArgumentException x){
                contadorDuelo.start();
            }
        }
    });

    final String ANSI_GREEN = "\u001B[32m";
    final String ANSI_RESET = "\u001B[0m";
    final String ANSI_YELLOW = "\u001B[33m";
    final String ANSI_BLUE = "\u001B[34m";

    Thread sendBroadcastThread; // Hilo del agente para enviar los mensajes de Broadcast
    Thread listenerThread; // Hilo del agente que escucha mensajes directos recibidos
    Thread listenerForBroadcast; // Hilo del agente que escucha mensajes de broadcast de otros agentes y registra
                                 // su IP

    private InetAddress monitorAddress; // Dirección IP del monitor al que mandará cierta información el agente
    private Integer monitorPort; // Puerto por el que se comunicarán los agentes con el monitor

    // private DatagramSocket sendSocket;
    private ServerSocket listenSocket; // Socket para recibir mensajes directos
    private Integer listeningPort; // Puerto de listenSocket
    private Integer listeningPortUDP; // Puerto de listenSocket para UDP
    private List<InetAddress> ipList; // Listas de Ips disponibles encontradas por el agente
    private InetAddress ip; // Ip del agente
    private Integer netMask; // Máscara de la red en la que se encuentra el agente
    private InetAddress subNet; // Submáscara de la red en la que se encuentra el agente

    private List<AgenteConocido> agentesDescubiertos; // Lista de todos los agentes registrados de un agente con los que
                                                      // se ha comunicado

    private String equipoDelAgente; // Equipo al que pertenece el agente antes de empezar los duelos
    private String equipoFachada; // Equipo que el agente envía originalmente que puede o no ser el mismo que el
                                  // equipo del agente
    private double probabilidadMandarFachada; // Probabilidad de que el agente mande su equipo fachada
    private double probabilidadCreer;  // Probabilidad de que el agente se crea la fachada del rival
    private double probabilidadHuir;  // Probabilidad de que el agente escape del duelo

    private String idAgente;

    // Método constructor del agente donde se hará la búsqueda de nido y se asignará
    // un puerto y IP para comunicarse
    public Agente(InetAddress monitorAddress, int monitorPort) throws IOException {
        this.monitorAddress = monitorAddress;
        this.monitorPort = monitorPort;

        // Inicializar la lista de agentes conocidos
        this.agentesDescubiertos = new ArrayList<>();

        int port = 4000;
        while (port <= 4200) {
            if (port % 2 == 0) {
                try {
                    this.listenSocket = new ServerSocket(port);
                    break;
                } catch (IOException e) {
                    port += 2; // Intentar con el siguiente puerto par
                }
            } else {
                port += 1; // Asegurarse de que el puerto sea par
            }
        }
        if (this.listenSocket == null) {
            throw new IOException("No se pudo asignar un puerto par entre 4000 y 4200");
        }
        this.listeningPort = listenSocket.getLocalPort(); // Obtener el puerto asignado a listenSocket
        this.listeningPortUDP = this.listeningPort + 1; // Obtener puerto de escucha UDP (+1 al TCP)
        getIp();
        getMascaraDeRed();
        getSubNet();
        this.ipList = getAvailableIPs();
        System.out.println("Dirección IP: " + this.ip);
        System.out.println("Máscara de red: " + this.netMask);
        System.out.println("Puerto de escucha: " + this.listeningPort);
        System.out.println("Puerto de escucha UDP: " + this.listeningPortUDP);
        System.out.println("Numero de IPs disponibles: " + this.ipList.size());

        // Escoger equipo del agente
        Random random = new Random();

        List<String> opciones = new ArrayList<String>();
        opciones.add("Piedra");
        opciones.add("Papel");
        opciones.add("Tijera");
        opciones.add("Lagarto");
        opciones.add("Spock");
        opciones.add("Bebe_que_tose");
        opciones.add("Santa_Claus");
        opciones.add("Mesa_de_IKEA");
        opciones.add("Bomba_de_Hidrogeno");

        // se generan 2 opciones al azar
        int i1 = random.nextInt(opciones.size());
        int i2 = random.nextInt(opciones.size());

        this.equipoDelAgente = opciones.get(i1);
        this.equipoFachada = opciones.get(i2);

        // Probabilidad de mandar fachada en lugar del equipo real (Redondeado a dos decimas)
        // Random (30% - 60%)
        this.probabilidadMandarFachada = Math.round((0.3 + (0.6 - 0.3) * random.nextDouble()) * 100.0) / 100.0;

        // Probabilidad de creerse la fachada de un rival (Redondeado a dos decimas)
        //Random (30% - 80%)
        this.probabilidadCreer = Math.round((0.3 + (0.8 - 0.3) * random.nextDouble()) * 100.0) / 100.0;
        
        // Probabilidad de huir satisfactoriamente de un encuentro (Redondeado a dos decimas)
        // Random (30% - 60%)
        this.probabilidadHuir = Math.round((0.3 + (0.6 - 0.3) * random.nextDouble()) * 100.0) / 100.0;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
        String time = now.format(formatter);

        this.idAgente = this.ip.toString() + "_" + this.listeningPortUDP.toString() + "_" + time;
    }

    // Función de replicación del agente (se usará una vez gane los duelos)
    // Permite que un agente se duplique tras ganar los duelos contra otro.
    // Provocando que el agente sea del mismo equipo
    public void replicacionDelAgente() throws IOException {
        // ProcessBuilder processBuilder = new
        // ProcessBuilder("java","-cp","C:/Users/Usuario/IdeaProjects/Proyecto SMA
        // cuarto año/Codigo/src", "Agente");
        // ProcessBuilder processBuilder = new
        // ProcessBuilder("java","-cp","C:/Users/Usuario/IdeaProjects/Proyecto SMA
        // cuarto año/Codigo/out/production/cosoSMA", "Agente");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ProcessBuilder processBuilder = new ProcessBuilder("java", "Agente", this.equipoDelAgente);
        processBuilder.start();
    }

    // Función de autodestrucción del agente (se usará una vez pierda los duelos)
    void autodestruccionDelAgente() {
        System.out.println("Me muero a");

        if (sendBroadcastThread != null) {
            sendBroadcastThread.interrupt();
            try {
                sendBroadcastThread.join(1000);
            } catch (InterruptedException e) {
                sendBroadcastThread.interrupt();
            }
        }

        if (listenerForBroadcast != null) {
            listenerForBroadcast.interrupt();
            try {
                listenerForBroadcast.join(1000);
            } catch (InterruptedException e) {
                listenerForBroadcast.interrupt();
            }
        }

        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                listenerThread.join(1000);
            } catch (InterruptedException e) {
                listenerThread.interrupt();
            }
        }

        String msg = null;
        try {
            msg = MessageGenerator.createHeMuertoMessage(this.listeningPort, "Com1", "Msg1", "Agente_01",
                    this.ip.toString(), "Monitor", String.valueOf(this.monitorAddress), this.equipoDelAgente);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        this.sendToMonitor(msg);

        for(int i=0; i<agentesDescubiertos.size();i++){
            AgenteConocido ag = agentesDescubiertos.get(i);
            try (Socket socket = new Socket(ag.getIp(), ag.getPuerto());
                 OutputStream outputStream = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(outputStream, true)) {

                msg = MessageGenerator.createHeMuertoMessage(this.listeningPort + 1, "Com1", "Msg1", this.idAgente,
                        this.ip.toString(), "dest1", ag.getIp().toString(), this.equipoFachada);

                //Enviamos el mensaje
                writer.println(msg);

            } catch (Exception e) {
                //e.printStackTrace();
                System.err.println("Error al enviar el mensaje de que me estoy muriendo pibe.");
            }
        }

        /*
        for (InetAddress ip : this.ipList) {
            for (int i = 4000; i < 4201; i+=2) {
                try (Socket socket = new Socket(ip, i);
                     OutputStream outputStream = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(outputStream, true)) {

                    msg = MessageGenerator.createHeMuertoMessage(this.listeningPort + 1, "Com1", "Msg1", this.idAgente,
                            this.ip.toString(), "dest1", ip.toString(), this.equipoFachada);

                    //Enviamos el mensaje
                    writer.println(msg);

                } catch (Exception e) {
                    //e.printStackTrace();
                    System.err.println("Error al enviar el mensaje.");
                }
            }
        }

         */

        System.exit(0);
    }

    // Función de parada del agente (se usará una vez acaben los duelos y el agente
    // acabe con vida con su equipo)
    private synchronized void paradaDelAgente() throws InterruptedException {
        System.out.println("Tomaaaaaaaa");
    }

    private void continuarAgente() {
        System.out.println("Allevoy");
    }

    // El agente obtendra la IP a la que asociarse
    public void getIp() {
        try {
            // Obtener las interfaces de red
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Filtrar para encontrar la interfaz Wi-Fi
                if (networkInterface.isUp() && !networkInterface.isLoopback() &&
                        (networkInterface.getName().contains("wlan") || networkInterface.getName().contains("wifi"))) {
                    // Obtener direcciones IP de la interfaz
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        // Filtrar solo direcciones IPv4
                        if (address.getAddress().length == 4) {
                            this.ip = address;
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // El agente obtendrá una vez tenida su IP, la máscara de red asociada
    public void getMascaraDeRed() {
        try {
            // Obtener la interfaz de red asociada a la dirección IP proporcionada
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(this.ip);
            if (networkInterface != null) {
                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    if (address.getAddress() instanceof Inet4Address && address.getAddress().equals(this.ip)) {
                        // Asignar la máscara de red
                        this.netMask = (int) address.getNetworkPrefixLength();
                        return;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Si no se encontró la máscara, se puede manejar como se desee
        System.out.println("No se pudo encontrar la máscara de red para la dirección IP: " + this.ip);
    }

    // En esta función obtendrá el agente la subred en la que estará situado
    public void getSubNet() {
        try {
            InetAddress ip = this.ip;
            System.out.println("Dirección IP: " + ip.getHostAddress());

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);

            if (networkInterface != null) {
                for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    if (address.getAddress().equals(ip)) {
                        int prefixLength = address.getNetworkPrefixLength();
                        int mask = 0xffffffff << (32 - prefixLength);

                        // Calcular dirección de red
                        byte[] ipBytes = ip.getAddress();
                        byte[] maskBytes = new byte[] {
                                (byte) ((mask >>> 24) & 0xff),
                                (byte) ((mask >>> 16) & 0xff),
                                (byte) ((mask >>> 8) & 0xff),
                                (byte) (mask & 0xff)
                        };

                        byte[] networkBytes = new byte[4];
                        for (int i = 0; i < 4; i++) {
                            networkBytes[i] = (byte) (ipBytes[i] & maskBytes[i]);
                        }

                        InetAddress networkAddress = InetAddress.getByAddress(networkBytes);

                        // Retorno de la dirección de red
                        this.subNet = networkAddress;
                    }
                }
            } else {
                System.out.println("No se encontró la interfaz de red.");
            }
        } catch (UnknownHostException e) {
            System.out.println("No se pudo obtener la dirección IP.");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Error al acceder a la interfaz de red.");
            e.printStackTrace();
        }
    }

    // Pasar el array de bytes de 4 elementos a un entero de 32 bits
    private int bytesToInt(byte[] bytes) {
        int result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    // Convierte un entero de 32 bits a un array de bytes de 4 elementos
    private byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    // El agente obtiene toda la lista de ips disponibles
    public List<InetAddress> getAvailableIPs() {
        List<InetAddress> availableIPs = new ArrayList<>();

        // Convertimos la subnet a un entero de 32 bits para manipularlo
        byte[] subnetBytes = subNet.getAddress();
        int subnetInt = bytesToInt(subnetBytes);

        // Calculamos el rango de direcciones con la máscara
        int numberOfHosts = (1 << (32 - netMask)) - 2; // Restamos 2 para excluir la dirección de red y broadcast
        int firstHost = (subnetInt & (0xFFFFFFFF << (32 - netMask))) + 1; // Primera IP disponible
        int lastHost = firstHost + numberOfHosts - 1; // Última IP disponible

        // Recorremos desde la primera hasta la última dirección IP
        for (int i = firstHost; i <= lastHost; i++) {
            try {
                availableIPs.add(InetAddress.getByAddress(intToBytes(i)));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return availableIPs;
    }

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

    private String getElementValue(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return null; // Elemento no encontrado
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Hasta Aqui era todo preparar el agente
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    // Enviamos un mensaje por broadcast a cada una de las ips por cada uno de los
    // puertos disponibles
    public void sendBroadcast(int waitTime) throws InterruptedException {
        sendBroadcastThread = new Thread(() -> {
            while (!sendBroadcastThread.isInterrupted()) {
                for (InetAddress ip : this.ipList) {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        
                        for (int puerto = 4001; puerto <= 4199; puerto++) {
                            if (puerto % 2 == 1) {
                                String message = MessageGenerator.createUDPMessage(this.listeningPortUDP, "Com1", "Msg1", "Agente_01", this.ip.toString(), idAgente, ip.toString());
                                byte[] buffer = message.getBytes();
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, puerto);
                                socket.send(packet);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Di una vuelta a todas las IPS y puertos jejejjeje");
                try {
                    Thread.sleep(waitTime); // Esperar x segundos antes de enviar el siguiente mensaje
                } catch (InterruptedException e) {
                    System.out.println("Envio de mensajes cerrado");
                    sendBroadcastThread.interrupt();
                }
            }
        });
        sendBroadcastThread.start();
    }

    // Método para escuchar mensajes de broadcast de otros agentes y registrar su IP
    public void listenForBroadcast() {
        listenerForBroadcast = new Thread(() -> {
            try (DatagramSocket discoverySocket = new DatagramSocket(this.listeningPortUDP)) {
                // System.out.println("Agente escuchando mensajes de broadcast en el puerto UDP
                // " + this.listeningPort);
                byte[] buffer = new byte[1024];

                while (!listenerForBroadcast.isInterrupted()) {

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    discoverySocket.receive(packet); // Recibe el mensaje de broadcast

                    String message = new String(packet.getData(), 0, packet.getLength());
                    Document xmlDoc = parseXMLFromString(message);
                    
                    int senderPort = Integer.parseInt(getElementValue(xmlDoc, "origin_port_UDP"));
                    InetAddress senderAddress = packet.getAddress();

                    System.out.println("Puerto recibido: " + senderPort);

                    // Ignorar mensajes de broadcast enviados por este propio agente
                    if (senderAddress.equals(this.ip) && (senderPort == this.listeningPort || senderPort == this.listeningPortUDP)) {
                        System.out.println("Mensaje de broadcast ignorado: proviene del propio agente.");
                        continue; // Saltar a la siguiente iteración del bucle
                    }

                    System.out.println("Mensaje de broadcast recibido de " + senderAddress + ": [Port: " + senderPort
                            + "]: " + message);

                    AgenteConocido nuevoAgente = new AgenteConocido(senderAddress, senderPort);
                    AgenteConocido nuevoAgente2 = new AgenteConocido(senderAddress, senderPort-1);
                    if (!agentesDescubiertos.contains(nuevoAgente) && !agentesDescubiertos.contains(nuevoAgente2)) {
                        agentesDescubiertos.add(nuevoAgente);
                        System.out.println("Agente añadido a la lista de conocidos: " + nuevoAgente);
                    } else {
                        System.out.println("Agente ya conocido: " + nuevoAgente);
                    }

                    // Mostrar el estado actual de agentesDescubiertos
                    System.out.println("Lista actual de agentes conocidos: " + agentesDescubiertos);
                }
            } catch (IOException e) {
                System.err.println("Error en la escucha de broadcast: " + e.getMessage());
                e.printStackTrace();
            }
        });
        listenerForBroadcast.start();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Hasta aquí la Parte de Broadcast
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    // Escuchar mensajes directos enviados al agente
    public void listenMessages() {
        listenerThread = new Thread(() -> { //antes era this.listeningPort ####################################################DISCLAIMER
            try (ServerSocket listenSocket = new ServerSocket(this.listeningPort+1)) {
                System.out.println("Escuchando en el puerto " + this.listeningPort);
                System.out.println("aqui");
                //realiza función del agente

                while (!listenerThread.isInterrupted()) {

                    try (Socket clientSocket = listenSocket.accept();
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()));
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        InetAddress senderAddress = clientSocket.getInetAddress();


                        String receivedMessage;
                        System.out.println("aqui va");
                        while ((receivedMessage = in.readLine()) != null) {
                            Document xmlDoc = parseXMLFromString(receivedMessage);
                            File xsdFile = new File("esquema_AG_basico.xsd"); // Ruta del archivo XSD
                            String protocolo = getElementValue(xmlDoc, "type_protocol");
                            String ipOrigen = getElementValue(xmlDoc, "origin_ip");
                            InetAddress ipRec = InetAddress.getByName(ipOrigen);
                            String oriPuerto = getElementValue(xmlDoc, "origin_port_TCP");

                            String equipo_reci = getElementValue(xmlDoc, "body_info");
                            //System.out.println(ANSI_BLUE +"IP Y PUERTO DEL QUE RECIBE_"+ipRec+","+oriPuerto+ANSI_RESET);
                            switch (protocolo){
                                case "duelo":
                                    //tomar decision de si luchar o no, en caso,
                                    String decision = resolverDecision(equipo_reci); //FUNCION DE COPETE PARA PROCESAR SI PELEAR O NO
                                    if (decision.equals("Atacar")) {
                                        String mensaje = MessageGenerator.createAceptoDueloMessage(this.listeningPort, "Com1", "Msg1", this.idAgente,
                                                this.ip.toString(), "dest1", ipRec.toString(), this.equipoDelAgente);
                                        try (Socket socket = new Socket(ipRec, Integer.parseInt(oriPuerto));
                                             OutputStream outputStream = socket.getOutputStream();
                                             PrintWriter writer = new PrintWriter(outputStream, true)) {

                                            // Enviar el mensaje
                                            writer.println(mensaje);
                                            System.out.println("mensaje enviado a" + ipRec + ":" + oriPuerto);

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            System.err.println("Error al enviar el mensaje de que le ataco.");
                                        }
                                    }
                                    if (decision.equals("Huir"))    {
                                        System.out.println(ANSI_BLUE+"SALIO CAGONETA, NO PELEO"+ANSI_RESET);
                                        String mensajeHuye = MessageGenerator.createRechazoDueloMessage(this.listeningPort, "Com1", "Msg1", this.idAgente,
                                                this.ip.toString(), "dest1",ipRec.toString(), this.equipoDelAgente);
                                        //manda mensaje
                                        try (Socket socket = new Socket(ipRec, Integer.parseInt(oriPuerto));
                                             OutputStream outputStream = socket.getOutputStream();
                                             PrintWriter writer = new PrintWriter(outputStream, true)) {

                                            // Enviar el mensaje
                                            writer.println(mensajeHuye);
                                            System.out.println("mensaje enviado a"+ipRec+":"+oriPuerto);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.err.println("Error al enviar el mensaje de que huyo.");
                                        }
                                    }
                                    //FuncionDeAgente();
                                    break;
                                case "rechazoDuelo":
                                    System.out.println(ANSI_BLUE +ipRec+":"+oriPuerto+" ME HA RECHAZADO EL DUELO NOOOOOOOOOOOOOOOOOOO"+ANSI_RESET);
                                    System.out.println(ANSI_BLUE +"NI MODO, TOCA PELEAR OTRA VEZ "+ANSI_RESET);
                                    FuncionDeAgente();
                                    break;
                                case "aceptoDuelo":
                                    System.out.println(ANSI_BLUE +ipRec+":"+oriPuerto+" ME HA ACEPTADO EL DUELO, LUCHEMOS!"+ANSI_RESET);
                                    System.out.println(ANSI_BLUE+"VAMOS AQUI, MI EQUIPO:"+this.equipoDelAgente+"VS"+equipo_reci+ANSI_RESET);
                                    int resultado = resolverDuelo(equipo_reci);

                                    if (resultado == 1){
                                        System.out.println(ANSI_BLUE +"TE GANE JAJAJAJAJ"+ANSI_RESET);
                                        String mensajeEnviar = MessageGenerator.createGanoDueloMessage(this.listeningPort, "Com1", "Msg1", this.idAgente,
                                                this.ip.toString(), "dest1",ipRec.toString(), this.equipoDelAgente); //mensaje para indicar al otro agente que debe morirse
                                        //manda mensaje
                                        try (Socket socket = new Socket(ipRec, Integer.parseInt(oriPuerto)+1);
                                             OutputStream outputStream = socket.getOutputStream();
                                             PrintWriter writer = new PrintWriter(outputStream, true)) {

                                            // Enviar el mensaje
                                            writer.println(mensajeEnviar);
                                            System.out.println("mensaje enviado a"+ipRec+":"+oriPuerto);

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            System.err.println("Error al enviar el mensaje de que he ganado yo.");
                                        }
                                        replicacionDelAgente();
                                    }
                                    else if(resultado == 0){
                                        System.out.println(ANSI_BLUE+"ME CACHIS, PERDI :C"+ANSI_RESET);
                                        String mensajeEnviar = MessageGenerator.createGanasDueloMessage(this.listeningPort, "Com1", "Msg1", this.idAgente,
                                                this.ip.toString(), "dest1",ipRec.toString(), this.equipoDelAgente); //mensaje para indicar al otro agente que debe replicarse
                                        //manda mensaje
                                        try (Socket socket = new Socket(ipRec, Integer.parseInt(oriPuerto)+1);
                                             OutputStream outputStream = socket.getOutputStream();
                                             PrintWriter writer = new PrintWriter(outputStream, true)) {

                                            // Enviar el mensaje
                                            writer.println(mensajeEnviar);
                                            System.out.println("mensaje enviado a"+ipRec+":"+oriPuerto);

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            System.err.println("Error al enviar el mensaje de que ha ganado el.");
                                        }
                                        autodestruccionDelAgente();
                                        /*
                                        DISCLAIMER: AQUi HAY QUE ELIMINAR AL AGENTE Y REPLICARLO CON EL EQUIPO
                                         //NO HACE FALTA MANDAR MENSAJE AL MONITOR, CUANDO EL NUEVO REPLICADO HAGA EL HENACIDO YA SE LO DEBERiA COMUNICAR AL MONITOR
                                         */
                                    }
                                    else{
                                        System.out.println(ANSI_BLUE+"BRO QUE SOMOS DEL MISMO EQUIPO CHILL"+ANSI_RESET);
                                        //toca luchar otra vez, si somos del mismo equipo bro dahhh
                                    }
                                    FuncionDeAgente();
                                    break;
                                case "ganoDuelo":
                                    //si recibo un mensaje de gano_duelo es porque evidentemente me han ganado
                                    System.out.println(ANSI_BLUE+"ME HAS HECHO TRAMPAS SEGURO"+ANSI_RESET);
                                    autodestruccionDelAgente();
                                    /*
                                        DISCLAIMER: AQUi HAY QUE ELIMINAR AL AGENTE Y REPLICARLO CON EL EQUIPO
                                         //NO HACE FALTA MANDAR MENSAJE AL MONITOR, CUANDO EL NUEVO REPLICADO HAGA EL HENACIDO YA SE LO DEBERiA COMUNICAR AL MONITOR
                                    */

                                    break;
                                case "ganasDuelo":
                                    //si recibo un mensaje de ganas_duelo es porque evidentemente he ganado
                                    System.out.println(ANSI_BLUE+"SI ES QUE SOY BUENISIMO"+ANSI_RESET);
                                    replicacionDelAgente();
                                    break;

                                case "meMuero":
                                    int senderPort = Integer.parseInt(oriPuerto);
                                    System.out.println("Sa matao Paco, concretamente el agente: " +senderAddress+ ". En el puerto: " + senderPort);
                                    AgenteConocido ag = new AgenteConocido(senderAddress, senderPort);
                                    this.agentesDescubiertos.remove(ag);
                                    System.out.println("Lista actual de agentes conocidos: " + agentesDescubiertos);


                            }


                            System.out.println("Mensaje recibido de " + senderAddress + ": " + receivedMessage);

                            if ("Hola".equals(receivedMessage)) {
                                out.println("Hola de vuelta desde el agente del puerto " + this.listeningPort);
                            } else if ("QuieroHacerELAquello".equals(receivedMessage)) {
                                // Aquí llama a tu función específica para manejar este mensaje
                                FuncionDeAgente();
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
            } catch (BindException y) {
                System.out.println("Escucho mensajes");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        listenerThread.start();
    }

    // Enviar respuesta directa a otro agente
    public void sendDirectMessage(String message, InetAddress address, int puertoDestino) {
        
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Hasta aqúi la comunicación Agente-Agente
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Método para resolver un duelo entre dos agentes
     * 
     * @param equipoRival: Equipo del agente rival
     * 
     * @return: 0 si el agente (yo) pierde, 1 si el agente (yo) gana, 2 si hay
     * empate
     */

    public int resolverDuelo(String equipoRival) {

        // Si el agente rival es del mismo equipo que el agente (yo)
        if (this.equipoDelAgente.equals(equipoRival)) {
            return 2;
        }

        // En caso de que sean equipos distintos

        // Si el agente es Piedra
        if (this.equipoDelAgente.equals("Piedra") && equipoRival.equals("Bebe_que_tose")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Piedra") && equipoRival.equals("Bomba_de_Hidrogeno")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Piedra") && equipoRival.equals("Lagarto")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Piedra") && equipoRival.equals("Tijera")) {
            return 1;
        }

        // Si el agente es Papel
        if (this.equipoDelAgente.equals("Papel") && equipoRival.equals("Piedra")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Papel") && equipoRival.equals("Spock")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Papel") && equipoRival.equals("Bomba_de_Hidrogeno")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Papel") && equipoRival.equals("Santa_Claus")) {
            return 1;
        }

        // Si el agente es Tijera
        if (this.equipoDelAgente.equals("Tijera") && equipoRival.equals("Papel")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Tijera") && equipoRival.equals("Lagarto")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Tijera") && equipoRival.equals("Mesa_de_IKEA")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Tijera") && equipoRival.equals("Santa_Claus")) {
            return 1;
        }

        // Si el agente es Lagarto
        if (this.equipoDelAgente.equals("Lagarto") && equipoRival.equals("Papel")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Lagarto") && equipoRival.equals("Spock")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Lagarto") && equipoRival.equals("Santa_Claus")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Lagarto") && equipoRival.equals("Bebe_que_tose")) {
            return 1;
        }

        // Si el agente es Spock
        if (this.equipoDelAgente.equals("Spock") && equipoRival.equals("Piedra")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Spock") && equipoRival.equals("Tijera")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Spock") && equipoRival.equals("Mesa_de_IKEA")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Spock") && equipoRival.equals("Bomba_de_Hidrogeno")) {
            return 1;
        }

        // Si el agente es Bebe_que_tose
        if (this.equipoDelAgente.equals("Bebe_que_tose") && equipoRival.equals("Papel")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bebe_que_tose") && equipoRival.equals("Spock")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bebe_que_tose") && equipoRival.equals("Santa_Claus")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bebe_que_tose") && equipoRival.equals("Tijera")) {
            return 1;
        }

        // Si el agente es Bomba_de_Hidrogeno
        if (this.equipoDelAgente.equals("Bomba_de_Hidrogeno") && equipoRival.equals("Lagarto")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bomba_de_Hidrogeno") && equipoRival.equals("Beba_que_tose")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bomba_de_Hidrogeno") && equipoRival.equals("Mesa_de_IKEA")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Bomba_de_Hidrogeno") && equipoRival.equals("Tijera")) {
            return 1;
        }

        // Si el agente es Santa_Claus
        if (this.equipoDelAgente.equals("Santa_Claus") && equipoRival.equals("Piedra")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Santa_Claus") && equipoRival.equals("Mesa_de_IKEA")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Santa_Claus") && equipoRival.equals("Bomba_de_Hidrogeno")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Santa_Claus") && equipoRival.equals("Spock")) {
            return 1;
        }

        // Si el agente es Mesa_de_IKEA
        if (this.equipoDelAgente.equals("Mesa_de_IKEA") && equipoRival.equals("Piedra")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Mesa_de_IKEA") && equipoRival.equals("Lagarto")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Mesa_de_IKEA") && equipoRival.equals("Papel")) {
            return 1;
        }
        if (this.equipoDelAgente.equals("Mesa_de_IKEA") && equipoRival.equals("Bebe_que_tose")) {
            return 1;
        }

        // si no se cumple ninguna de las anteriores, es que este agente (yo) ha perdido
        return 0;
    }

    /*
     * Método para resolver la decision del agente sobre si creerse o no la tapadera y huir conforme toque
     * Importante tener en cuenta que la probabilidad es CREERME la tapadera, y no lo contrario.
     * 
     * @param equipoRival: Equipo del agente rival
     * 
     * @return: Decision del agente
     *          "Huir": El agente decide y logra huir del duelo
     *          "Atacar": El agente decide completar el duelo
     *          "Nada": El agente no ve como amenaza al rival porque lo considera de su equipo
     *          "Error": El agente no sabe que hacer
     * empate
     */
    public String resolverDecision(String equipoRival){
        Random random = new Random();

        // Lanzamos los dados para comprobar si se activa la probabilidad de creerse la tapadera
        double destinoTapadera = Math.round(random.nextDouble() * 100.0) / 100.0;

        // Lanzamos los dados para comprobar si se activa la probabilidad de huir
        double destinoHuida = Math.round(random.nextDouble() * 100.0) / 100.0;
        
        // Primer Caso: Me creo la tapadera, comprobar si le gano a la tapadera
        //              Si pierdo: Intentar Huir
        //              Si gano: No se huye
        //              Si somos del mismo equipo: No hacer nada
        if(destinoTapadera <= this.probabilidadCreer){  // Me creo la tapadera
            // Comprobamos si nos alzamos con la victoria en caso de pelear
            int resultadoDuelo = resolverDuelo(equipoRival); 
            switch(resultadoDuelo){
                case 0: // Pierdo el hipotetico duelo
                    if(destinoHuida <= this.probabilidadHuir){  // El agente logra huir
                        System.out.println("Decido huir");
                        return "Huir";
                    } else{ // El agente no logra huir
                        System.out.println("Quiero huir pero no puedo, tendre que atacar");
                        return "Atacar";
                    }

                case 1: // Gano el hipotetico duelo
                    System.out.println("Decido atacar");
                    return "Atacar";

                case 2: // Empato el hipotetico duelo (Me creo que es de mi equipo)
                    System.out.println("Decido no hacer nada, es de mi equipo");
                    return "Nada";

                default:
                    System.out.println("No se que hacer");
                    return "Error";
            }

        } else{  // Segundo Caso: No me creo la tapadera
            if(destinoTapadera < 0.5){
                System.out.println("Tengo dudas. Decido atacar");
                return "Atacar";
            } else{
                System.out.println("Tengo dudas. Decido huir");

                if(destinoHuida < probabilidadHuir){
                    return "Huir";
                } else{
                    return "Atacar";
                }
            }
        }
    }


    // Función específica de los agentes (Duelo de Piedra, Papel, Tijera, Lagarto,
    // Spock, Bebé que tose, Papá Noel, Mesa de IKEA y Bomba de Hidrógeno)


    /*
    FUNCION DEL AGENTE
    FECHA MODIFICACIÓN: 13/12/2024
    MODIFICADO POR: OSCAR E IVAN
     */
    private Random random = new Random(); // Inicializa una sola vez porque si no no genera contrincantes random
    public void FuncionDeAgente() {
        String msg = null;
        //Random random = new Random();
        double probabilidad = random.nextDouble(); // Genera un número entre 0 y 1 para la mandar la fachada o no

        /*
        //OBTIENE AGENTE CONOCIDO ALEATORIO
        while(this.agentesDescubiertos.size()<1){
            //System.out.println(ANSI_YELLOW+"Esperando a que empiece la partida"+ANSI_RESET);

        }
         */





        int indiceAleatorio = random.nextInt(this.agentesDescubiertos.size());
        AgenteConocido ag = this.agentesDescubiertos.get(indiceAleatorio);
        System.out.println(ANSI_GREEN+"BUSCANDO COMBATE"+ANSI_RESET);
        InetAddress destIP = ag.getIp();
        int destPort = ag.getPuerto();



        try {
            if (probabilidad <= this.probabilidadMandarFachada) {
                msg = MessageGenerator.createDueloMessage(this.listeningPort+1, "Com1", "Msg1", this.idAgente,
                        this.ip.toString(), "dest1" ,destIP.toString(), this.equipoFachada); // Enviar fachada, en puerto mas 1 porque le sume 1 en el hilo de listenMessages
            } else {
                msg = MessageGenerator.createDueloMessage(this.listeningPort+1, "Com1", "Msg1", this.idAgente,
                        this.ip.toString(), "dest1" ,destIP.toString(), this.equipoDelAgente); // Enviar equipo real, en puerto mas 1 porque le sume 1 en el hilo de listenMessages

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


        //LE ENVIA EL MENSAJE DE DUELO A UN AGENTE ALEATORIO:
        try (Socket socket = new Socket(destIP, destPort);
             OutputStream outputStream = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(outputStream, true)) {

            // Enviar el mensaje
            System.out.println(ANSI_BLUE+"VOY A RETAR A" +destIP+":"+destPort+ANSI_RESET);
            writer.println(msg);
            System.out.println("mensaje enviado a"+destIP+":"+destPort);

        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("Error al enviar el mensaje de querer hacer un duelo.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Hasta aqúi la Funcion del Agente
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    // Enviar copia del mensaje al monitor
    public void sendToMonitor(String message) {
        try (Socket socket = new Socket(monitorAddress, monitorPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// MAIN
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException, InterruptedException {
        // ESTA ES LA IP QUE YO TENIA PUESTA, TENEIS QUE CAMBIARLA POR LA VUESTRA PARA
        // PROBAR
        String ipMonitor = "192.168.1.187";
        InetAddress monitorAddress = InetAddress.getByName(ipMonitor); // Reemplazar
        int monitorPort = 4300; // Puerto del monitor

        Agente agente = new Agente(monitorAddress, monitorPort);
        System.out.println("Soy un agente con ID: " + agente.idAgente + "\n");

        //Si es un agente replicado, tendrá el mismo equipo que el agente que se replica
        if(args.length > 0){
            agente.equipoDelAgente = args[0];
        }

        System.err.println("Hola soy un pelotudo de equipo: " + agente.equipoDelAgente);
        System.err.println("Hola soy un pelotudo de equipo fachada: " + agente.equipoFachada + "\n");

        System.out.println("Hola soy un agente que te engaña con una probabilidad de: " + agente.probabilidadMandarFachada * 100 + "%");
        System.out.println("Hola soy un agente con un % de inocencia del " + agente.probabilidadCreer * 100 + "%");
        System.out.println("Hola soy un agente con probabilidad de salir por patas de: " + agente.probabilidadHuir * 100 + "%");

        agente.resolverDecision(agente.equipoFachada);

        String msg = null;
        try {
            msg = MessageGenerator.createHeNacidoMessage(agente.listeningPort, "Com1", "Msg1", "Agente_01",
                    agente.ip.toString(), "Monitor", ipMonitor, agente.equipoDelAgente);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        agente.sendToMonitor(msg);

        // Crear hilo para escuchar los mensajes de broadcast
        agente.listenForBroadcast();


        // Crear hilo para escuchar mensajes TCP
        agente.listenMessages();

        // Crear hilo de envío de broadcast
        // Crear mensaje XML
        
        agente.sendBroadcast(5000);

        agente.contadorDuelo.start();

        

        //agente.replicacionDelAgente();

        //agente.autodestruccionDelAgente();

        // Crear Hilo que mande por broadcast mensajes "Hola"
        // Crear Hilo que escuche mensajes
        // si es Hola que responda de vuelta
        // si es QuieroHacerELAquello que se llame en ambos agentes a la FuncionDeAgente
    }
}
