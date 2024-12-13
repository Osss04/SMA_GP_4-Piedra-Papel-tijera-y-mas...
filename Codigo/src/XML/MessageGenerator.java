package XML;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MessageGenerator {

    public static String createHeNacidoMessage(int puerto, String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP, String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("heNacido"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortUDP = doc.createElement("origin_port_UDP");
        originPortUDP.appendChild(doc.createTextNode(String.valueOf(puerto+1)));  // Puerto UDP, puede ser dinámico
        origin.appendChild(originPortUDP);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode(String.valueOf(puerto+1)));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createHeMuertoMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP, String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("meMuero"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createUDPMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("UDP"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("UDP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortUDP = doc.createElement("origin_port_UDP");
        originPortUDP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto UDP, puede ser dinámico
        origin.appendChild(originPortUDP);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto-1)));  // Puerto UDP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode("Info"));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createDueloMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP,String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("duelo"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
    public static String createAceptoDueloMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP, String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("aceptoDuelo"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createRechazoDueloMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP,String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("rechazoDuelo"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createGanoDueloMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP,String equipo) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Crear el documento XML base
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Message");
        doc.appendChild(rootElement);

        // Elementos de la secuencia
        Element comuncId = doc.createElement("comunc_id");
        comuncId.appendChild(doc.createTextNode(comunicacion_id));
        rootElement.appendChild(comuncId);

        Element msgId = doc.createElement("msg_id");
        msgId.appendChild(doc.createTextNode(msgID));
        rootElement.appendChild(msgId);

        Element header = doc.createElement("header");
        rootElement.appendChild(header);

        Element typeProtocol = doc.createElement("type_protocol");
        typeProtocol.appendChild(doc.createTextNode("gano_duelo"));
        header.appendChild(typeProtocol);

        Element protocolStep = doc.createElement("protocol_step");
        protocolStep.appendChild(doc.createTextNode("1"));
        header.appendChild(protocolStep);

        Element comunicationProtocol = doc.createElement("comunication_protocol");
        comunicationProtocol.appendChild(doc.createTextNode("TCP"));
        header.appendChild(comunicationProtocol);

        // Crear la sección de `origin` con IP de origen
        Element origin = doc.createElement("origin");
        header.appendChild(origin);

        Element originId = doc.createElement("origin_id");
        originId.appendChild(doc.createTextNode(origenID));  // Identificador único del agente
        origin.appendChild(originId);

        String localIp = null;  // Obtener la IP local del agente
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Element originIp = doc.createElement("origin_ip");
        originIp.appendChild(doc.createTextNode(localIp));
        origin.appendChild(originIp);

        Element originPortTCP = doc.createElement("origin_port_TCP");
        originPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));  // Puerto TCP, puede ser dinámico
        origin.appendChild(originPortTCP);

        Element originTime = doc.createElement("origin_time");
        originTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        origin.appendChild(originTime);

        Element destination = doc.createElement("destination");
        header.appendChild(destination);

        Element destinationId = doc.createElement("destination_id");
        destinationId.appendChild(doc.createTextNode(destinoID));
        destination.appendChild(destinationId);

        Element destinationIp = doc.createElement("destination_ip");
        destinationIp.appendChild(doc.createTextNode(destinoIP));
        destination.appendChild(destinationIp);

        Element destinationPortUDP = doc.createElement("destination_port_UDP");
        destinationPortUDP.appendChild(doc.createTextNode("0"));
        destination.appendChild(destinationPortUDP);

        Element destinationPortTCP = doc.createElement("destination_port_TCP");
        destinationPortTCP.appendChild(doc.createTextNode(String.valueOf(puerto)));
        destination.appendChild(destinationPortTCP);

        Element destinationTime = doc.createElement("destination_time");
        destinationTime.appendChild(doc.createTextNode(String.valueOf(System.currentTimeMillis())));
        destination.appendChild(destinationTime);

        Element body = doc.createElement("body");
        rootElement.appendChild(body);

        Element bodyInfo = doc.createElement("body_info");
        bodyInfo.appendChild(doc.createTextNode(equipo));
        body.appendChild(bodyInfo);

        Element commonContent = doc.createElement("common_content");
        rootElement.appendChild(commonContent);

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}