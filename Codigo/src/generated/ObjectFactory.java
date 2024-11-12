//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.11 a las 08:58:49 AM CET 
//


package generated;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Message }
     * 
     */
    public Message createMessage() {
        return new Message();
    }

    /**
     * Create an instance of {@link Message.Header }
     * 
     */
    public Message.Header createMessageHeader() {
        return new Message.Header();
    }

    /**
     * Create an instance of {@link Message.Body }
     * 
     */
    public Message.Body createMessageBody() {
        return new Message.Body();
    }

    /**
     * Create an instance of {@link Message.CommonContent }
     * 
     */
    public Message.CommonContent createMessageCommonContent() {
        return new Message.CommonContent();
    }

    /**
     * Create an instance of {@link HeaderDestinationInfo }
     * 
     */
    public HeaderDestinationInfo createHeaderDestinationInfo() {
        return new HeaderDestinationInfo();
    }

    /**
     * Create an instance of {@link HeaderOriginInfo }
     * 
     */
    public HeaderOriginInfo createHeaderOriginInfo() {
        return new HeaderOriginInfo();
    }

}
