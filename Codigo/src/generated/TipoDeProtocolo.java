//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.11 a las 08:58:49 AM CET 
//


package generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_de_protocolo.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="tipo_de_protocolo">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="reproducete"/>
 *     &lt;enumeration value="heNacido"/>
 *     &lt;enumeration value="parate"/>
 *     &lt;enumeration value="parado"/>
 *     &lt;enumeration value="continua"/>
 *     &lt;enumeration value="continuo"/>
 *     &lt;enumeration value="autodestruyete"/>
 *     &lt;enumeration value="meMuero"/>
 *     &lt;enumeration value="hola"/>
 *     &lt;enumeration value="estoy"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tipo_de_protocolo")
@XmlEnum
public enum TipoDeProtocolo {

    @XmlEnumValue("reproducete")
    REPRODUCETE("reproducete"),
    @XmlEnumValue("heNacido")
    HE_NACIDO("heNacido"),
    @XmlEnumValue("parate")
    PARATE("parate"),
    @XmlEnumValue("parado")
    PARADO("parado"),
    @XmlEnumValue("continua")
    CONTINUA("continua"),
    @XmlEnumValue("continuo")
    CONTINUO("continuo"),
    @XmlEnumValue("autodestruyete")
    AUTODESTRUYETE("autodestruyete"),
    @XmlEnumValue("meMuero")
    ME_MUERO("meMuero"),
    @XmlEnumValue("hola")
    HOLA("hola"),
    @XmlEnumValue("estoy")
    ESTOY("estoy");
    private final String value;

    TipoDeProtocolo(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TipoDeProtocolo fromValue(String v) {
        for (TipoDeProtocolo c: TipoDeProtocolo.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
