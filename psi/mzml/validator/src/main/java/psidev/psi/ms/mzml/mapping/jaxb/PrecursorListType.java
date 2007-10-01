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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;


/**
 * List and descriptions of precursors to the spectrum currently being described.
 * 
 * <p>Java class for PrecursorListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrecursorListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="precursor" type="{http://psi.hupo.org/schema_revision/mzML_0.93}PrecursorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="count" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrecursorListType", propOrder = {
    "precursor"
})
public class PrecursorListType {

    @XmlElement(required = true)
    protected List<PrecursorType> precursor;
    @XmlAttribute(required = true)
    protected BigInteger count;


    private String elementName = "precursorList";

    public PrecursorListType() {}

    public PrecursorListType(String xmlSnippet) {
        parseXml(xmlSnippet);
    }

    public PrecursorListType(Element element) {
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

    private void create(Node node) {
        Element ele = (Element) node;
        if (ele != null) {
            // load attributes
            String count = ele.getAttribute("count");
            if (!count.equals("")) { // getAttribute returns empty String if no value was specified
                setCount(new BigInteger(count));
            }
            // create other elements
            NodeList nl = ele.getElementsByTagName("precursor");
            for ( int i = 0; i < nl.getLength(); i++ ) {
                this.getPrecursor().add(new PrecursorType((Element) nl.item(i)));
            }

        }
    }

    ////////////////////
    // Getter + Setter

    /**
     * Gets the value of the precursor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the precursor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrecursor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrecursorType }
     * 
     * 
     */
    public List<PrecursorType> getPrecursor() {
        if (precursor == null) {
            precursor = new ArrayList<PrecursorType>();
        }
        return this.precursor;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

}
