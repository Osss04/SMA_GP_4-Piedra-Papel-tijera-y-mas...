import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
import java.io.StringWriter;

import static java.lang.System.exit;

public class Agente {

    Thread sendBroadcastThread; // Hilo del agente para enviar los mensajes de Broadcast
    Thread listenerThread; // Hilo del agente que escucha mensajes directos recibidos
    Thread listenerForBroadcast; // Hilo del agente que escucha mensajes de broadcast de otros agentes y registra su IP
    private InetAddress monitorAddress; // Dirección IP del monitor al que mandará cierta información el agente
    private Integer monitorPort; // Puerto por el que se comunicarán los agentes con el monitor
    // private DatagramSocket sendSocket;
    private ServerSocket listenSocket; // Socket para recibir mensajes directos
    private Integer listeningPort; // Puerto de listenSocket
    private List<InetAddress> ipList; // Listas de Ips disponibles encontradas por el agente
    private InetAddress ip; // Ip del agente
    private Integer netMask; // Máscara de la red en la que se encuentra el agente
    private InetAddress subNet; // Submáscara de la red en la que se encuentra el agente
    private List<AgenteConocido> agentesDescubiertos; // Lista de todos los agentes registrados de un agente con los que se ha comunicado
    private String equipoDelAgente; // Equipo al que pertenece el agente antes de empezar los duelos


    // Método constructor del agente donde se hará la búsqueda de nido y se asignará un puerto y IP para comunicarse
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
        getIp();
        getMascaraDeRed();
        getSubNet();
        this.ipList = getAvailableIPs();
        System.out.println("Dirección IP: " + this.ip);
        System.out.println("Máscara de red: " + this.netMask);
        System.out.println("Puerto de escucha: " + this.listeningPort);
        System.out.println("Numero de IPs disponibles: " + this.ipList.size());
    }

    // Función de replicación del agente (se usará una vez gane los duelos)
    // Permite que un agente se duplique tras ganar los duelos contra otro. Provocando que el agente sea del mismo equipo
    public void replicacionDelAgente() throws IOException {
        //ProcessBuilder processBuilder = new ProcessBuilder("java","-cp","C:/Users/Usuario/IdeaProjects/Proyecto SMA cuarto año/Codigo/src", "Agente");
        //ProcessBuilder processBuilder = new ProcessBuilder("java","-cp","C:/Users/Usuario/IdeaProjects/Proyecto SMA cuarto año/Codigo/out/production/cosoSMA", "Agente");
        ProcessBuilder processBuilder = new ProcessBuilder("java", "Agente");
        processBuilder.start();
    }

    // Función de autodestrucción del agente (se usará una vez pierda los duelos)
    private void autodestruccionDelAgente() throws InterruptedException {
        System.out.println("Me muero a");
        String msg = null;
        try {
            msg = MessageGenerator.createHeMuertoMessage(this.listeningPort,"Com1","Msg1","Agente_01",this.ip.toString(),"Monitor","192.168.1.168");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        this.sendToMonitor(msg);
        if(listenerThread != null){
            listenerThread.interrupt();
            listenerThread.join(2000);
        }
        if(listenerForBroadcast != null) {
            listenerForBroadcast.interrupt();
            listenerForBroadcast.join(2000);
        }
        if(sendBroadcastThread != null){
            sendBroadcastThread.interrupt();
            sendBroadcastThread.join(2000);
        }
        System.exit(0);
    }

    // Función de parada del agente (se usará una vez acaben los duelos y el agente acabe con vida con su equipo)
    private synchronized void paradaDelAgente() throws InterruptedException {
        System.out.println("Tomaaaaaaaa");
    }

    private void continuarAgente(){
        System.out.println("Allevoy");
    }

    //El agente obtendra la IP a la que asociarse
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

    //El agente obtendrá una vez tenida su IP, la máscara de red asociada
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

    //En esta función obtendrá el agente la subred en la que estará situado
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

    //Pasar el array de bytes de 4 elementos a un entero de 32 bits
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

    //El agente obtiene toda la lista de ips disponibles
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

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Hasta Aqui era todo preparar el agente
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    //Enviamos un mensaje por broadcast a cada una de las ips por cada uno de los puertos disponibles
    public void sendBroadcast(String message, int waitTime) throws InterruptedException{
        sendBroadcastThread = new Thread(() -> {
            while (!sendBroadcastThread.isInterrupted()) {
                for (InetAddress ip : this.ipList) {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        byte[] buffer = message.getBytes();
                        for (int puerto = 4000; puerto <= 4300; puerto++) {
                            if (puerto % 2 == 0) {
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, puerto);
                                socket.send(packet);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Di una vuelta a todas las IPS y puertos jejejjeje");

                try {
                    Thread.sleep(waitTime); // Esperar x segundos antes de enviar el siguiente mensaje
                } catch (InterruptedException e) {
                    System.out.println("Envio de mensajes cerrado");
                }
            }
        });
        sendBroadcastThread.start();
    }

    // Método para escuchar mensajes de broadcast de otros agentes y registrar su IP
    public void listenForBroadcast() {
        listenerForBroadcast = new Thread(() -> {
            try (DatagramSocket discoverySocket = new DatagramSocket(this.listeningPort)) {
                //System.out.println("Agente escuchando mensajes de broadcast en el puerto UDP " + this.listeningPort);
                byte[] buffer = new byte[1024];

                while (!listenerForBroadcast.isInterrupted()) {

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    discoverySocket.receive(packet);  // Recibe el mensaje de broadcast

                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress();
                    int senderPort = Integer.parseInt(message);

                    // Ignorar mensajes de broadcast enviados por este propio agente
                    if (senderAddress.equals(this.ip) && senderPort == this.listeningPort) {
                        System.out.println("Mensaje de broadcast ignorado: proviene del propio agente.");
                        continue; // Saltar a la siguiente iteración del bucle
                    }

                    System.out.println("Mensaje de broadcast recibido de " + senderAddress + ": [Port: " + senderPort + "]: " + message);

                    AgenteConocido nuevoAgente = new AgenteConocido(senderAddress, senderPort);
                    if (!agentesDescubiertos.contains(nuevoAgente)) {
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
        listenerThread = new Thread(() -> {
            try (ServerSocket listenSocket = new ServerSocket(this.listeningPort)) {
                System.out.println("Escuchando en el puerto " + this.listeningPort);

                while (!listenerThread.isInterrupted()) {

                    try (Socket clientSocket = listenSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        InetAddress senderAddress = clientSocket.getInetAddress();
                        int senderPort = clientSocket.getPort();

                        String receivedMessage;
                        while ((receivedMessage = in.readLine()) != null) {
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
                    }
                }
            } catch (BindException y) {
                System.out.println("Escucho mensajes");
            }
            catch (IOException e) {
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

    //Función específica de los agentes (Duelo de Piedra, Papel, Tijera, Lagarto, Spock, Bebé que tose, Papá Noel, Mesa de IKEA y Bomba de Hidrógeno)
    public void FuncionDeAgente(){
        System.out.println("UGHHH!");
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
        InetAddress monitorAddress = InetAddress.getByName("192.168.1.139"); // Reemplazar
        int monitorPort = 4300; // Puerto del monitor

        Agente agente = new Agente(monitorAddress, monitorPort);

        String msg = null;
        try {
            msg = MessageGenerator.createHeNacidoMessage(agente.listeningPort,"Com1","Msg1","Agente_01",agente.ip.toString(),"Monitor","192.168.1.139");
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
        agente.sendBroadcast("" + agente.listeningPort, 5000);

        //agente.replicacionDelAgente();

        agente.autodestruccionDelAgente();

        // Crear Hilo que mande por broadcast mensajes "Hola"
        // Crear Hilo que escuche mensajes
        // si es Hola que responda de vuelta
        // si es QuieroHacerELAquello que se llame en ambos agentes a la FuncionDeAgente
    }
}
