package Comunicacion;

import generated.Message;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtils {

    // Método para serializar (convertir objeto Java a XML)
    public static String serializeMessage(Message message) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(message, writer);
        return writer.toString();
    }

    // Método para deserializar (convertir XML a objeto Java)
    public static Message deserializeMessage(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Message.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        return (Message) unmarshaller.unmarshal(reader);
    }
}