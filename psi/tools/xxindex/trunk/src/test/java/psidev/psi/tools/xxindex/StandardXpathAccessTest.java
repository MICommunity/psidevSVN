package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.XpathIndex;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XmlElement;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author: Florian Reisinger
 * Date: 28-Jan-2008
 */
public class StandardXpathAccessTest {

    boolean DEBUG = false;

    @Test
    public void testConstrainedXmlAccession() throws IOException, URISyntaxException {

        URL url = this.getClass().getResource( "/test-win1252-wo-header.xml" );
        File file = new File(url.toURI());
        StandardXpathAccess access = new StandardXpathAccess(file);
        XpathIndex index = access.getIndex();
        long testStartTime = System.currentTimeMillis();
        String xpath = "/first/second";
        if (index.containsXpath(xpath)) {
            List<IndexElement> elements = index.getElements(xpath);
            int i = 1;

            // -------------------------------------------------------------------------------------------
            // check that XML snippet Iterator and List of XML snippets contain the same XML
            for (IndexElement element : elements) {
                Iterator iter = access.getXmlSnippetIterator(xpath + "/third/fourth", element.getStart(), element.getStop());
                for (String snippet : access.getXmlSnippets(xpath + "/third/fourth", element.getStart(), element.getStop())) {
                    Assert.assertEquals( iter.next(), snippet);
                }
            }

            // -------------------------------------------------------------------------------------------
            // check that XML element Iterator and List of XML elements contain the same elements
            for (IndexElement element : elements) {
                Iterator<XmlElement> iter = access.getXmlElementIterator(xpath + "/third/fourth", element.getStart(), element.getStop());
                for (XmlElement xmlElement : access.getXmlElements(xpath + "/third/fourth", element.getStart(), element.getStop())) {
                    XmlElement currentElement = iter.next();
                    Assert.assertEquals( currentElement.getXmlSnippet(), xmlElement.getXmlSnippet());
                    Assert.assertEquals( currentElement.getStartPos(), xmlElement.getStartPos());
                }
            }

            // -------------------------------------------------------------------------------------------
            // check the number of <third> and <fourth> elements within the 1. <second> element
            IndexElement element = elements.get(0);
            long start = element.getStart();
            long stop = element.getStop();
            // the first 'second' element has 3 'third' elements
            Assert.assertEquals( 3, access.getXmlElements(xpath + "/third", start, stop).size());
            // and 4 'fourth' elements
            Assert.assertEquals( 4, access.getXmlElements(xpath + "/third/fourth", start, stop).size());

            // now test the same thing using the XML snippets instead of the XML elements
            // the first 'second' element has 3 'third' snippets
            Assert.assertEquals( 3, access.getXmlSnippets(xpath + "/third", start, stop).size());
            // and 4 'fourth' snippets
            Assert.assertEquals( 4, access.getXmlSnippets(xpath + "/third/fourth", start, stop).size());

            // -------------------------------------------------------------------------------------------
            // check the number of <third> and <fourth> elements within the 2. <second> element
            element = elements.get(1);
            start = element.getStart();
            stop = element.getStop();
            // the second 'second' element has 1 'third' element
            Assert.assertEquals( 1, access.getXmlElements(xpath + "/third", start, stop).size());
            // and 1 'fourth' element
            Assert.assertEquals( 1, access.getXmlElements(xpath + "/third/fourth", start, stop).size());

            // now test the same thing using the XML snippets instead of the XML elements
            // the second 'second' element has 1 'third' snippet
            Assert.assertEquals( 1, access.getXmlSnippets(xpath + "/third", start, stop).size());
            // and 1 'fourth' snippet
            Assert.assertEquals( 1, access.getXmlSnippets(xpath + "/third/fourth", start, stop).size());
        }
    }
}
