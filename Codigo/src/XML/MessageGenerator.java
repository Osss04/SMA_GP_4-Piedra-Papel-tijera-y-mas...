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

    public static String createHeNacidoMessage(int puerto, String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP) throws TransformerException, ParserConfigurationException {
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
        typeProtocol.appendChild(doc.createTextNode("hola"));
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

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static String createHeMuertoMessage(int puerto,String comunicacion_id, String msgID, String origenID, String origenIP, String destinoID, String destinoIP) throws TransformerException, ParserConfigurationException {
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
        typeProtocol.appendChild(doc.createTextNode("Me mueroooo"));
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

        // Convertir a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}