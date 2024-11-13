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
 * <p>Clase Java para header_origin_info complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="header_origin_info">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="origin_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="origin_ip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="origin_port_UDP" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="origin_port_TCP" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="origin_time" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "header_origin_info", propOrder = {
    "originId",
    "originIp",
    "originPortUDP",
    "originPortTCP",
    "originTime"
})
public class HeaderOriginInfo {

    @XmlElement(name = "origin_id", required = true)
    protected String originId;
    @XmlElement(name = "origin_ip", required = true)
    protected String originIp;
    @XmlElement(name = "origin_port_UDP", required = true)
    protected BigInteger originPortUDP;
    @XmlElement(name = "origin_port_TCP", required = true)
    protected BigInteger originPortTCP;
    @XmlElement(name = "origin_time")
    protected long originTime;

    /**
     * Obtiene el valor de la propiedad originId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * Define el valor de la propiedad originId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginId(String value) {
        this.originId = value;
    }

    /**
     * Obtiene el valor de la propiedad originIp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginIp() {
        return originIp;
    }

    /**
     * Define el valor de la propiedad originIp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginIp(String value) {
        this.originIp = value;
    }

    /**
     * Obtiene el valor de la propiedad originPortUDP.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOriginPortUDP() {
        return originPortUDP;
    }

    /**
     * Define el valor de la propiedad originPortUDP.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOriginPortUDP(BigInteger value) {
        this.originPortUDP = value;
    }

    /**
     * Obtiene el valor de la propiedad originPortTCP.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOriginPortTCP() {
        return originPortTCP;
    }

    /**
     * Define el valor de la propiedad originPortTCP.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOriginPortTCP(BigInteger value) {
        this.originPortTCP = value;
    }

    /**
     * Obtiene el valor de la propiedad originTime.
     * 
     */
    public long getOriginTime() {
        return originTime;
    }

    /**
     * Define el valor de la propiedad originTime.
     * 
     */
    public void setOriginTime(long value) {
        this.originTime = value;
    }

}
