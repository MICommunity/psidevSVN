package psidev.psi.tools.xxindex.index;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import psidev.psi.tools.xxindex.StandardXmlElementExtractor;

/**
 * Author: florian
 * Date: 01-Aug-2007
 * Time: 16:52:13
 */
public class EncodingTest {

    @Test
    public void encodingTest() {

        List<String> fileList = createTestFileList();
        for (String s : fileList) {
            System.out.println("\n== " + s + " ===================================================");
            try {
                URL url = EncodingTest.class.getClassLoader().getResource(s);
                InputStream is = url.openStream();
                StandardXpathIndex index = XmlXpathIndexer.buildIndex(is);
                ByteRange range = index.getRange("/first/second/third/fourth").get(1);

                StandardXmlElementExtractor xee = new StandardXmlElementExtractor();
                xee.setEncoding(xee.detectFileEncoding(url));
                System.out.println("Specifying encoding: " + xee.getEncoding().name());
                xee.setCompareWithDetect(true);
                String test = xee.readByteRange(range.getStart(), range.getStop(), new File(url.toURI()));
                Assert.assertTrue("The String retrieved from the byte offset (" + range.getStart() + "-" + range.getStop() + ") did not start with the expected '<fourth>' element!", test.startsWith("<fourth>"));
                Assert.assertTrue("The String retrieved from the byte offset (" + range.getStart() + "-" + range.getStop() + ") did not end with the expected '</fourth>' element!", test.endsWith("</fourth>"));

                System.out.println("Checked file: " + url);
            } catch (Exception e) {
                System.out.println("Caught " + e.getClass().getName() );
                e.printStackTrace();
            }
        }
    }

    private List<String> createTestFileList() {
        List<String> fileList = new ArrayList<String>();

        fileList.add("test-ascii-wo-header.xml");
        fileList.add("test-ascii-wo-header-flat.xml");

        fileList.add("test-juan-header.xml");
        fileList.add("test-juan-wo-header.xml");

        fileList.add("test-utf8-header.xml");
        fileList.add("test-utf8-header2.xml");
        fileList.add("test-utf8-header-flat.xml");
        fileList.add("test-utf8-header-flat2.xml");
        fileList.add("test-utf8-wo-header.xml");
        fileList.add("test-utf8-wo-header2.xml");
        fileList.add("test-utf8-wo-header-flat.xml");
        fileList.add("test-utf8-wo-header-flat2.xml");

        fileList.add("test-win1252-header.xml");
        fileList.add("test-win1252-header-flat.xml");

        return fileList;
    }
}
