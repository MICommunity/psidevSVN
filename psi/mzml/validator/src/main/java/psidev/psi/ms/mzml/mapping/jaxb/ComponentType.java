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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;


/**
 * <p>Java class for ComponentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ComponentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://psi.hupo.org/schema_revision/mzML_0.93}ParamGroupType">
 *       &lt;attribute name="order" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComponentType")
public class ComponentType
    extends ParamGroupType
{

    @XmlAttribute(required = true)
    protected int order;


    private String elementName1 = "detector";
    private String elementName2 = "source";
    private String elementName3 = "analyzer";

    public ComponentType() {}

    public ComponentType(String xmlSnippet) {
        parseXml(xmlSnippet);
    }

    public ComponentType(Element element) {
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
        NodeList nl = document.getElementsByTagName(elementName1);
        if (nl.getLength() == 0) { // if this element name did not match, try the next
            nl = document.getElementsByTagName(elementName2);
        }
        if (nl.getLength() == 0) { // if this element name did not match, try the next
            nl = document.getElementsByTagName(elementName3);
        }
        if (nl.getLength() == 1) { // we found one element! now we can create the Object
            Element ele = (Element) nl.item(0);
            create(ele);
        } else {
            throw new IllegalStateException("Expected one '" + elementName1 + "' element " +
                                                  "or one '" + elementName2 + "' element " +
                                                  "or one '" + elementName3 + "' element " +
                                                  ", but found: " + nl.getLength());
        }
    }

    protected void create(Node node) {
        Element ele = (Element) node;
        if (ele != null) {
            // load attributes
            String order = ele.getAttribute("order");
            if (!order.equals("")) { // getAttribute returns empty String if no value was specified
                setOrder(Integer.parseInt(order));
            }
            // load elements
            // inherited form ParamGroupType
            super.create(node);
        }
    }

    ////////////////////
    // Getter + Setter

    /**
     * Gets the value of the order property.
     * 
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     */
    public void setOrder(int value) {
        this.order = value;
    }

}
