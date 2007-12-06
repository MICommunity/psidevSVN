package psidev.psi.tools.xxindex;

import junit.framework.Assert;
import org.junit.Test;
import psidev.psi.tools.xxindex.index.ByteRange;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;
import psidev.psi.tools.xxindex.index.XpathIndex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Author: Florian Reisinger
 * Date: 31-Jul-2007
 */
public class XmlXpathIndexerTest {

    ////////////////////
    // Utilities

    private String readByteRange(long from, long to, String filename, String encoding) {
        URL url = XmlXpathIndexerTest.class.getClassLoader().getResource(filename);
        String result = null;
        try {
            File f = new File(url.toURI());
            StandardXmlElementExtractor xee = new StandardXmlElementExtractor();
            xee.setEncoding(encoding);
            result = xee.readByteRange(from, to, f);
        } catch (URISyntaxException e) {
            System.out.println("Caught URISyntaxException trying to create File from URL: " + url);
            e.printStackTrace();
        }
        return result;
    }

    private List<String> createTestFileList() {
        List<String> fileList = new ArrayList<String>();

        fileList.add("test-ascii-wrong-header.xml");
        fileList.add("test-ascii-wrong-header-flat.xml");
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

    private String detectFileEncoding(String filename) {
        URL url = XmlXpathIndexerTest.class.getClassLoader().getResource(filename);
        String result = null;
        try {
            StandardXmlElementExtractor xee = new StandardXmlElementExtractor();
            result = xee.detectFileEncoding(url);
        } catch (IOException e) {
            System.out.println("Caught IOException while trying to detect file encoding of file: " + url);
            e.printStackTrace();
        }
        return result;
    }


    ////////////////////
    // Tests

    @Test
    public void xmlXpathIndexerTest1() {
        List<String> fileList = createTestFileList();
        for (String s : fileList) {
            System.out.println("\n== " + s + " ===================================================");
            try {
                InputStream is = XmlXpathIndexerTest.class.getClassLoader().getResourceAsStream(s);
                System.out.println("__INDEX_______________________________________________________");
                long startTime = System.currentTimeMillis();
                XpathIndex index = null;
                try {
                    index = XmlXpathIndexer.buildIndex(is);
                    is.close();
                } catch (IOException e) {
                    System.out.println("Caught IOException trying to build the index.");
                    e.printStackTrace();
                }
                long stopTime = System.currentTimeMillis();
                System.out.println("Start time of index creation: " + startTime);
                System.out.println("Stop time of index creation: " + stopTime);
                System.out.println("Total time of index creation: " + (stopTime - startTime) + "ms");
                System.out.println(index);
                System.out.println("__XML-ELEMENTS________________________________________________");
                List<ByteRange> brList = index.getRange("/first/second/third/fourth");
                String encoding = detectFileEncoding(s);
                System.out.println("Detected encoding: " + encoding);
                if (encoding == null) { System.out.println("Using default for decoding byte range.");
                } else { System.out.println("Using " + encoding + " for decoding byte range.");}
                for (ByteRange range : brList) {
                    System.out.println(readByteRange(range.getStart(), range.getStop(), s, encoding));
                }
            } catch (Exception e) {
                System.out.println("Caught " + e.getClass().getName());
            }
        }
    }


    // ToDo: more tests on parsing, indexing and reading bits
    // e.g. check correct building of tag names (attributes, spaces, new line,...)

     @Test
     public void xmlXpathIndexerTest2() {
         List<String> fileList = createTestFileList();
         // Create an inclusion set in this case.
         HashSet<String> xpathInclusionSet = new HashSet<String>(2);
         xpathInclusionSet.add("/first/second");
         xpathInclusionSet.add("/first/second/third/");

        for (String s : fileList) {
            System.out.println("\n== " + s + " ===================================================");
            try {
                InputStream is = XmlXpathIndexerTest.class.getClassLoader().getResourceAsStream(s);
                System.out.println("__INDEX_______________________________________________________");
                long startTime = System.currentTimeMillis();
                XpathIndex index = null;
                try {
                    index = XmlXpathIndexer.buildIndex(is, xpathInclusionSet);
                    is.close();
                } catch (IOException e) {
                    System.out.println("Caught IOException trying to build the index.");
                    e.printStackTrace();
                }
                long stopTime = System.currentTimeMillis();
                System.out.println("Start time of index creation: " + startTime);
                System.out.println("Stop time of index creation: " + stopTime);
                System.out.println("Total time of index creation: " + (stopTime - startTime) + "ms");
                System.out.println(index);
                System.out.println("__XML-ELEMENTS________________________________________________");
                Assert.assertFalse("The xpath '/first/second/third/fourth' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", index.containsXpath("/first/second/third/fourth"));
                List<ByteRange> brList = index.getRange("/first/second/third/fourth");
                Assert.assertEquals("The xpath '/first/second/third/fourth' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", 0, brList.size());
                // This seemingly repetitive test checks that the retrieval of a non-existing
                // xpath does not influence the functionality of the 'contains' method.
                Assert.assertFalse("The xpath '/first/second/third/fourth' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", index.containsXpath("/first/second/third/fourth"));
                brList = index.getRange("/first");
                Assert.assertEquals("The xpath '/first' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", 0, brList.size());
                brList = index.getRange("/first/second");
                Assert.assertEquals("The xpath '/first/second' should result in 2 index entries, yet gave " + brList.size() + " instead!", 2, brList.size());
                brList = index.getRange("/first/second/third");
                Assert.assertEquals("The xpath '/first/second/third' should result in 4 index entries, yet gave " + brList.size() + " instead!", 4, brList.size());
                String encoding = detectFileEncoding(s);
                System.out.println("Detected encoding: " + encoding);
                if (encoding == null) { System.out.println("Using default for decoding byte range.");
                } else { System.out.println("Using " + encoding + " for decoding byte range.");}
                for (ByteRange range : brList) {
                    System.out.println(readByteRange(range.getStart(), range.getStop(), s, encoding));
                }
            } catch (Exception e) {
                System.out.println("Caught " + e.getClass().getName());
            }
        }
     }
}
