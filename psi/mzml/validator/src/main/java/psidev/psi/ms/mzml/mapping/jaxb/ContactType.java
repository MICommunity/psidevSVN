//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.1-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.17 at 01:20:51 PM BST 
//


package psidev.psi.ms.mzml.mapping.jaxb;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;


/**
 * Audit information concerning the means by which the originator/owner of this mzML file can be identified, and contacted if necessary.
 * 
 * <p>Java class for ContactType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="institution" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="URI" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */


public class ContactType 
	extends ParamGroupType
{


    private String elementName = "contact";

    public ContactType() {}

    public ContactType(String xmlSnippet) {
        parseXml(xmlSnippet);
    }

    public ContactType(Element element) {
        create(element);
    }

    ///////////////////
    // utilities

    private void parseXml(String xmlSnippet) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream str = new ByteArrayInputStream(xmlSnippet.getBytes());
            document = builder.parse( str );
        } catch ( Exception e ) {
            e.printStackTrace();
        }


        // we always expect a String representing a single xml tag, not a list of xml tags!
        if (document != null) {
            NodeList nl = document.getElementsByTagName(elementName);
            if (nl.getLength() == 1) {
                Element ele = (Element) nl.item(0);
                create(ele);
            } else {
                throw new IllegalStateException("Expected only one '" + elementName + "' element, but found: " + nl.getLength());
            }
        }

    }




}
