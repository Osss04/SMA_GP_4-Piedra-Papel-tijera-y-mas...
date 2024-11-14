package XML;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;

public class XMLValidator {

    public static boolean validateXMLSchema(File xsdPath, File xmlPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdPath);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlPath));
            return true;
        } catch (IOException | SAXException e) {
            System.out.println("Error de validación: " + e.getMessage());
            return false;
        }
    }
}