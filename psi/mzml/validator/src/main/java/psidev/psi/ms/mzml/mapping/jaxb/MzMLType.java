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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;


/**
 * This schema can capture the use of a mass spectrometer, the data generated, and the initial processing of that data (to the level of the peak list). Peak lists are processed data from a mass spectrometry experiment. There can be multiple peak lists in an mzML file, which might be related via a separation, or just in sequence from an automated run. Any one peak list (mass spectrum) may also be composed of a number of acquisitions, which can be described individually herein.
 * 
 * <p>Java class for mzMLType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mzMLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileDescription" type="{http://psi.hupo.org/schema_revision/mzML_0.93}FileDescriptionType"/>
 *         &lt;element name="cvList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}CVListType"/>
 *         &lt;element name="referenceableParamGroupList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}ReferenceableParamGroupListType" minOccurs="0"/>
 *         &lt;element name="sampleList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}SampleListType" minOccurs="0"/>
 *         &lt;element name="instrumentList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}InstrumentListType"/>
 *         &lt;element name="softwareList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}SoftwareListType"/>
 *         &lt;element name="dataProcessingList" type="{http://psi.hupo.org/schema_revision/mzML_0.93}DataProcessingListType"/>
 *         &lt;element name="run" type="{http://psi.hupo.org/schema_revision/mzML_0.93}RunType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="accession" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mzMLType", propOrder = {
    "fileDescription",
    "cvList",
    "referenceableParamGroupList",
    "sampleList",
    "instrumentList",
    "softwareList",
    "dataProcessingList",
    "run"
})
public class MzMLType {

    @XmlElement(required = true)
    protected FileDescriptionType fileDescription;
    @XmlElement(required = true)
    protected CVListType cvList;
    protected ReferenceableParamGroupListType referenceableParamGroupList;
    protected SampleListType sampleList;
    @XmlElement(required = true)
    protected InstrumentListType instrumentList;
    @XmlElement(required = true)
    protected SoftwareListType softwareList;
    @XmlElement(required = true)
    protected DataProcessingListType dataProcessingList;
    @XmlElement(required = true)
    protected RunType run;
    @XmlAttribute
    protected String accession;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String version;


    private String elementName = "mzML";

    public MzMLType() {}

    public MzMLType(String xmlSnippet) {
        parseXml(xmlSnippet);
    }

    public MzMLType(Element element) {
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
            String id = ele.getAttribute("id");
            if (!id.equals("")) { // getAttribute returns empty String if no value was specified
                setId(id);
            }
            String accession = ele.getAttribute("accession");
            if (!accession.equals("")) { // getAttribute returns empty String if no value was specified
                setAccession(accession);
            }
            String version = ele.getAttribute("version");
            if (!version.equals("")) { // getAttribute returns empty String if no value was specified
                setVersion(version);
            }
            // load elements
            NodeList nl = ele.getElementsByTagName("fileDescription");
            // we only expect one such element!
            if (nl.getLength() == 1) {
                fileDescription = new FileDescriptionType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'fileDescription' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("cvList");
            if (nl.getLength() == 1) {
                cvList = new CVListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'cvList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("referenceableParamGroupList");
            if (nl.getLength() == 1) {
                referenceableParamGroupList = new ReferenceableParamGroupListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'referenceableParamGroupList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("sampleList");
            if (nl.getLength() == 1) {
                sampleList = new SampleListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'sampleList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("instrumentList");
            if (nl.getLength() == 1) {
                instrumentList = new InstrumentListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'instrumentList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("softwareList");
            if (nl.getLength() == 1) {
                softwareList = new SoftwareListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'softwareList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("dataProcessingList");
            if (nl.getLength() == 1) {
                dataProcessingList = new DataProcessingListType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'dataProcessingList' element, but found: " + nl.getLength());
            }
            nl = null;
            nl = ele.getElementsByTagName("run");
            if (nl.getLength() == 1) {
                run = new RunType((Element) nl.item(0));
            } else {
                throw new IllegalStateException("Expected only one 'run' element, but found: " + nl.getLength());
            }

        }
    }

    ////////////////////
    // Getter + Setter

    /**
     * Gets the value of the fileDescription property.
     * 
     * @return
     *     possible object is
     *     {@link FileDescriptionType }
     *     
     */
    public FileDescriptionType getFileDescription() {
        return fileDescription;
    }

    /**
     * Sets the value of the fileDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileDescriptionType }
     *     
     */
    public void setFileDescription(FileDescriptionType value) {
        this.fileDescription = value;
    }

    /**
     * Gets the value of the cvList property.
     * 
     * @return
     *     possible object is
     *     {@link CVListType }
     *     
     */
    public CVListType getCvList() {
        return cvList;
    }

    /**
     * Sets the value of the cvList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CVListType }
     *     
     */
    public void setCvList(CVListType value) {
        this.cvList = value;
    }

    /**
     * Gets the value of the referenceableParamGroupList property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceableParamGroupListType }
     *     
     */
    public ReferenceableParamGroupListType getReferenceableParamGroupList() {
        return referenceableParamGroupList;
    }

    /**
     * Sets the value of the referenceableParamGroupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceableParamGroupListType }
     *     
     */
    public void setReferenceableParamGroupList(ReferenceableParamGroupListType value) {
        this.referenceableParamGroupList = value;
    }

    /**
     * Gets the value of the sampleList property.
     * 
     * @return
     *     possible object is
     *     {@link SampleListType }
     *     
     */
    public SampleListType getSampleList() {
        return sampleList;
    }

    /**
     * Sets the value of the sampleList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleListType }
     *     
     */
    public void setSampleList(SampleListType value) {
        this.sampleList = value;
    }

    /**
     * Gets the value of the instrumentList property.
     * 
     * @return
     *     possible object is
     *     {@link InstrumentListType }
     *     
     */
    public InstrumentListType getInstrumentList() {
        return instrumentList;
    }

    /**
     * Sets the value of the instrumentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstrumentListType }
     *     
     */
    public void setInstrumentList(InstrumentListType value) {
        this.instrumentList = value;
    }

    /**
     * Gets the value of the softwareList property.
     * 
     * @return
     *     possible object is
     *     {@link SoftwareListType }
     *     
     */
    public SoftwareListType getSoftwareList() {
        return softwareList;
    }

    /**
     * Sets the value of the softwareList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SoftwareListType }
     *     
     */
    public void setSoftwareList(SoftwareListType value) {
        this.softwareList = value;
    }

    /**
     * Gets the value of the dataProcessingList property.
     * 
     * @return
     *     possible object is
     *     {@link DataProcessingListType }
     *     
     */
    public DataProcessingListType getDataProcessingList() {
        return dataProcessingList;
    }

    /**
     * Sets the value of the dataProcessingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataProcessingListType }
     *     
     */
    public void setDataProcessingList(DataProcessingListType value) {
        this.dataProcessingList = value;
    }

    /**
     * Gets the value of the run property.
     * 
     * @return
     *     possible object is
     *     {@link RunType }
     *     
     */
    public RunType getRun() {
        return run;
    }

    /**
     * Sets the value of the run property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunType }
     *     
     */
    public void setRun(RunType value) {
        this.run = value;
    }

    /**
     * Gets the value of the accession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccession(String value) {
        this.accession = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}