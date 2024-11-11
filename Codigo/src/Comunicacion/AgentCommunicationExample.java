package Comunicacion;

import generated.Message;

import generated.Message;
import generated.Message.Header;
import generated.HeaderOriginInfo;
import generated.HeaderDestinationInfo;
import java.math.BigInteger;

public class AgentCommunicationExample {

    public static void main(String[] args) {
        try {
            // Crear un ejemplo de mensaje
            Message message = new Message();
            message.setComuncId("comunication123");
            message.setMsgId("message456");

            // Crear y configurar Header
            Header header = new Header();
            header.setComunicationProtocol("ProtocolExample");
            header.setProtocolStep(BigInteger.valueOf(1));

            // Crear y configurar Origin y Destination
            HeaderOriginInfo originInfo = new HeaderOriginInfo();
            originInfo.setOriginId("origin123");
            originInfo.setOriginIp("192.168.0.1");
            originInfo.setOriginPortUDP(BigInteger.valueOf(-3383));
            originInfo.setOriginPortTCP(BigInteger.valueOf(-3079));
            originInfo.setOriginTime(4983L);

            HeaderDestinationInfo destinationInfo = new HeaderDestinationInfo();
            destinationInfo.setDestinationId("dest123");
            destinationInfo.setDestinationIp("192.168.0.2");
            destinationInfo.setDestinationPortUDP(BigInteger.valueOf(135));
            destinationInfo.setDestinationPortTCP(BigInteger.valueOf(2022));
            destinationInfo.setDestinationTime(1713L);

            header.setOrigin(originInfo);
            header.setDestination(destinationInfo);
            message.setHeader(header);

            // Serializar el mensaje a XML
            String xml = XMLUtils.serializeMessage(message);
            System.out.println("XML Serializado:\n" + xml);

            // Deserializar el XML de vuelta a un objeto Message
            Message deserializedMessage = XMLUtils.deserializeMessage(xml);
            System.out.println("\nDeserializado: " + deserializedMessage.getComuncId() + ", " + deserializedMessage.getMsgId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}