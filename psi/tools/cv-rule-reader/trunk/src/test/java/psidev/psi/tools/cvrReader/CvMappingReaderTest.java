package psidev.psi.tools.cvrReader;

import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.cvrReader.mapping.jaxb.CvMappingRule;
import psidev.psi.tools.cvrReader.mapping.jaxb.CvMappingRules;
import psidev.psi.tools.cvrReader.mapping.jaxb.CvTerm;

/**
 * CvMappingReader Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: CvMappingReaderTest.java 669 2007-06-29 16:45:04 +0100 (Fri, 29 Jun 2007) skerrien $
 * @since TODO artifact version
 */
public class CvMappingReaderTest {

    ////////////////////////////////
    // Compatibility with JUnit 3

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( CvMappingReaderTest.class );
    }

    //////////////////////////
    // Initialisation

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    ////////////////////
    // Tests

    @Test
    public void read() throws Exception {
        String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                       "<!-- test cv rule file -->\n" +
                                       "<CvMappingRules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                       "xsi:noNamespaceSchemaLocation=\"CvRules.xsd\" >\n" +
                                       "\t<CvMappingRule elementPath=\"/mzML/dx:sampleList/dx:sample/dx:cvParam@accession\" requirementLevel=\"MAY\">\n" +
                                       "\t\t<CvTerm termAccession=\"PSI:1000010\" useTermName=\"true\" useTerm=\"false\" termName=\"Sample Description\" allowChildren=\"true\" cvIdentifier=\"MS\"/>\n" +
                                       "\t\t<CvTerm termAccession=\"PATO:0001241\" useTerm=\"false\" termName=\"quality of an object\" allowChildren=\"true\" cvIdentifier=\"PATO\"/>\n" +
                                       "\t\t<CvTerm termAccession=\"GO:0005575 \" useTerm=\"false\" termName=\"cellular_component\" allowChildren=\"true\" cvIdentifier=\"GO\" isRepeatable=\"true\" scope=\"/mzML/dx:sampleList/dx:sample\\\"/>\n" +
                                       "\t\t<CvTerm termAccession=\"1 \" useTerm=\"false\" termName=\"Root node of taxonomy\" allowChildren=\"true\" cvIdentifier=\"NEWT\"/>\n" +
                                       "\t</CvMappingRule>" +
                                       "</CvMappingRules>";

        CvRuleReader reader = new CvRuleReader();
        System.out.println( "Testing on following XML:\n" + testXml );
        CvMappingRules cvrs = reader.read( testXml );
        Assert.assertNotNull( cvrs );
        Assert.assertEquals( "Expected number of CvRules is 1!", 1, cvrs.getCvMappingRule().size() );
        CvMappingRule rule = cvrs.getCvMappingRule().iterator().next();
        Assert.assertEquals( "Expected number of CvTerms is 4!", 4, rule.getCvTerm().size() );

        // get first CvTerm and check the setting for useTermName
        CvTerm cvterm = cvrs.getCvMappingRule().get(0).getCvTerm().get(0);
        Assert.assertEquals( "Value for attribute 'useTermName' should be 'true'!", true, cvterm.isUseTermName() );

        // get second CvTerm and check the default setting for useTermName
        cvterm = cvrs.getCvMappingRule().get(0).getCvTerm().get(1);
        Assert.assertEquals( "Default value for attribute 'useTermName' should be 'false'!", false, cvterm.isUseTermName() );

        // get third CvTerm and check the setting for isRepeatable
        cvterm = cvrs.getCvMappingRule().get(0).getCvTerm().get(2);
        Assert.assertEquals( "Value for attribute 'isRepeatable' should be 'false'!", false, cvterm.isUseTermName() );
        Assert.assertEquals( "Value for attribute 'scope' should be '/mzML/dx:sampleList/dx:sample\\'!", "/mzML/dx:sampleList/dx:sample\\", cvterm.getScope());

    }
}