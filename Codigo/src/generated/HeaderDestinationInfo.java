//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.11 a las 08:58:49 AM CET 
//


package generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para header_destination_info complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="header_destination_info">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destination_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destination_ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destination_port_UDP" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="destination_port_TCP" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="destination_time" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "header_destination_info", propOrder = {
    "destinationId",
    "destinationIp",
    "destinationPortUDP",
    "destinationPortTCP",
    "destinationTime"
})
public class HeaderDestinationInfo {

    @XmlElement(name = "destination_id", required = true)
    protected String destinationId;
    @XmlElement(name = "destination_ip", required = true)
    protected String destinationIp;
    @XmlElement(name = "destination_port_UDP", required = true)
    protected BigInteger destinationPortUDP;
    @XmlElement(name = "destination_port_TCP", required = true)
    protected BigInteger destinationPortTCP;
    @XmlElement(name = "destination_time")
    protected long destinationTime;

    /**
     * Obtiene el valor de la propiedad destinationId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationId() {
        return destinationId;
    }

    /**
     * Define el valor de la propiedad destinationId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationId(String value) {
        this.destinationId = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationIp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationIp() {
        return destinationIp;
    }

    /**
     * Define el valor de la propiedad destinationIp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationIp(String value) {
        this.destinationIp = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationPortUDP.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDestinationPortUDP() {
        return destinationPortUDP;
    }

    /**
     * Define el valor de la propiedad destinationPortUDP.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDestinationPortUDP(BigInteger value) {
        this.destinationPortUDP = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationPortTCP.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDestinationPortTCP() {
        return destinationPortTCP;
    }

    /**
     * Define el valor de la propiedad destinationPortTCP.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDestinationPortTCP(BigInteger value) {
        this.destinationPortTCP = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationTime.
     * 
     */
    public long getDestinationTime() {
        return destinationTime;
    }

    /**
     * Define el valor de la propiedad destinationTime.
     * 
     */
    public void setDestinationTime(long value) {
        this.destinationTime = value;
    }

}
