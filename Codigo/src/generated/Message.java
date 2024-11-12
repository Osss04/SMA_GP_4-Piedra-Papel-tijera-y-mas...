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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="comunc_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="msg_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="type_protocol" type="{}tipo_de_protocolo"/>
 *                   &lt;element name="protocol_step" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="comunication_protocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="origin" type="{}header_origin_info"/>
 *                   &lt;element name="destination" type="{}header_destination_info"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="body">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="body_info" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;any minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="common_content">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "comuncId",
    "msgId",
    "header",
    "body",
    "commonContent"
})
@XmlRootElement(name = "Message")
public class Message {

    @XmlElement(name = "comunc_id", required = true)
    protected String comuncId;
    @XmlElement(name = "msg_id", required = true)
    protected String msgId;
    @XmlElement(required = true)
    protected Message.Header header;
    @XmlElement(required = true)
    protected Message.Body body;
    @XmlElement(name = "common_content", required = true)
    protected Message.CommonContent commonContent;

    /**
     * Obtiene el valor de la propiedad comuncId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComuncId() {
        return comuncId;
    }

    /**
     * Define el valor de la propiedad comuncId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComuncId(String value) {
        this.comuncId = value;
    }

    /**
     * Obtiene el valor de la propiedad msgId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Define el valor de la propiedad msgId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Obtiene el valor de la propiedad header.
     * 
     * @return
     *     possible object is
     *     {@link Message.Header }
     *     
     */
    public Message.Header getHeader() {
        return header;
    }

    /**
     * Define el valor de la propiedad header.
     * 
     * @param value
     *     allowed object is
     *     {@link Message.Header }
     *     
     */
    public void setHeader(Message.Header value) {
        this.header = value;
    }

    /**
     * Obtiene el valor de la propiedad body.
     * 
     * @return
     *     possible object is
     *     {@link Message.Body }
     *     
     */
    public Message.Body getBody() {
        return body;
    }

    /**
     * Define el valor de la propiedad body.
     * 
     * @param value
     *     allowed object is
     *     {@link Message.Body }
     *     
     */
    public void setBody(Message.Body value) {
        this.body = value;
    }

    /**
     * Obtiene el valor de la propiedad commonContent.
     * 
     * @return
     *     possible object is
     *     {@link Message.CommonContent }
     *     
     */
    public Message.CommonContent getCommonContent() {
        return commonContent;
    }

    /**
     * Define el valor de la propiedad commonContent.
     * 
     * @param value
     *     allowed object is
     *     {@link Message.CommonContent }
     *     
     */
    public void setCommonContent(Message.CommonContent value) {
        this.commonContent = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="body_info" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;any minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "bodyInfo",
        "any"
    })
    public static class Body {

        @XmlElement(name = "body_info", required = true)
        protected String bodyInfo;
        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Obtiene el valor de la propiedad bodyInfo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBodyInfo() {
            return bodyInfo;
        }

        /**
         * Define el valor de la propiedad bodyInfo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBodyInfo(String value) {
            this.bodyInfo = value;
        }

        /**
         * Obtiene el valor de la propiedad any.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Define el valor de la propiedad any.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class CommonContent {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Obtiene el valor de la propiedad any.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Define el valor de la propiedad any.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="type_protocol" type="{}tipo_de_protocolo"/>
     *         &lt;element name="protocol_step" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="comunication_protocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="origin" type="{}header_origin_info"/>
     *         &lt;element name="destination" type="{}header_destination_info"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "typeProtocol",
        "protocolStep",
        "comunicationProtocol",
        "origin",
        "destination"
    })
    public static class Header {

        @XmlElement(name = "type_protocol", required = true)
        @XmlSchemaType(name = "string")
        protected TipoDeProtocolo typeProtocol;
        @XmlElement(name = "protocol_step", required = true)
        protected BigInteger protocolStep;
        @XmlElement(name = "comunication_protocol", required = true)
        protected String comunicationProtocol;
        @XmlElement(required = true)
        protected HeaderOriginInfo origin;
        @XmlElement(required = true)
        protected HeaderDestinationInfo destination;

        /**
         * Obtiene el valor de la propiedad typeProtocol.
         * 
         * @return
         *     possible object is
         *     {@link TipoDeProtocolo }
         *     
         */
        public TipoDeProtocolo getTypeProtocol() {
            return typeProtocol;
        }

        /**
         * Define el valor de la propiedad typeProtocol.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoDeProtocolo }
         *     
         */
        public void setTypeProtocol(TipoDeProtocolo value) {
            this.typeProtocol = value;
        }

        /**
         * Obtiene el valor de la propiedad protocolStep.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getProtocolStep() {
            return protocolStep;
        }

        /**
         * Define el valor de la propiedad protocolStep.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setProtocolStep(BigInteger value) {
            this.protocolStep = value;
        }

        /**
         * Obtiene el valor de la propiedad comunicationProtocol.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getComunicationProtocol() {
            return comunicationProtocol;
        }

        /**
         * Define el valor de la propiedad comunicationProtocol.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setComunicationProtocol(String value) {
            this.comunicationProtocol = value;
        }

        /**
         * Obtiene el valor de la propiedad origin.
         * 
         * @return
         *     possible object is
         *     {@link HeaderOriginInfo }
         *     
         */
        public HeaderOriginInfo getOrigin() {
            return origin;
        }

        /**
         * Define el valor de la propiedad origin.
         * 
         * @param value
         *     allowed object is
         *     {@link HeaderOriginInfo }
         *     
         */
        public void setOrigin(HeaderOriginInfo value) {
            this.origin = value;
        }

        /**
         * Obtiene el valor de la propiedad destination.
         * 
         * @return
         *     possible object is
         *     {@link HeaderDestinationInfo }
         *     
         */
        public HeaderDestinationInfo getDestination() {
            return destination;
        }

        /**
         * Define el valor de la propiedad destination.
         * 
         * @param value
         *     allowed object is
         *     {@link HeaderDestinationInfo }
         *     
         */
        public void setDestination(HeaderDestinationInfo value) {
            this.destination = value;
        }

    }

}
