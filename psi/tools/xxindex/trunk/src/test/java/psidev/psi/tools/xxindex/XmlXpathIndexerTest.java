package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Author: Florian Reisinger
 * Date: 31-Jul-2007
 */
public class XmlXpathIndexerTest {

    boolean DEBUG = false;

    ////////////////////
    // Utilities

    private String readByteRange(long from, long to, String filename, String encoding) throws IOException {
        URL url = XmlXpathIndexerTest.class.getClassLoader().getResource(filename);
        String result = null;
        try {
            File f = new File(url.toURI());
            StandardXmlElementExtractor xee = new StandardXmlElementExtractor();
            xee.setEncoding(encoding);
            result = xee.readString(from, to, f);
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
    public void xmlXpathIndexerTest1() throws IOException {
        List<String> fileList = createTestFileList();
        for (String s : fileList) {
            System.out.println("\n== File: " + s + " ===================================================");

            InputStream is = XmlXpathIndexerTest.class.getClassLoader().getResourceAsStream(s);
            String encoding = detectFileEncoding(s);
            if (DEBUG) System.out.println("Detected file encoding: " + encoding);

            // ----- index creation ------ //
            if (DEBUG) System.out.println("__INDEX_______________________________________________________");
            long startTime = System.currentTimeMillis();
            StandardXpathIndex index = null;
            try {
                index = XmlXpathIndexer.buildIndex(is);
                is.close();
            } catch (IOException e) {
                System.out.println("Caught IOException trying to build the index on file: " + s);
                e.printStackTrace();
            }
            Assert.assertNotNull("Index was not created!", index);
            long stopTime = System.currentTimeMillis();
            if (DEBUG) System.out.println("Time of index creation: " + (stopTime - startTime) + "ms");
            if (DEBUG) System.out.println(index);

            // ----- extraction of XML snippets using index and extractor ------ //
            if (DEBUG) System.out.println("__XML-SNIPPETS________________________________________________");
            String xpath = "/first/second/third/fourth";
            Assert.assertTrue("The xpath '" + xpath + "' should be in the index!", index.containsXpath("/first/second/third/fourth"));
            List<IndexElement> brList = index.getElements(xpath);
            Assert.assertEquals("Unexpected number of entries for " + xpath + "!", 5,  brList.size());
            for (IndexElement element : brList) {
                String test= readByteRange(element.getStart(), element.getStop(), s, encoding);
                if (DEBUG) System.out.println(test);
                Assert.assertTrue("The String retrieved from the byte offset (" + element.getStart() + "-" + element.getStop()
                        + ") did not start with the expected '<fourth>' element!", test.startsWith("<fourth>"));
                Assert.assertTrue("The String retrieved from the byte offset (" + element.getStart() + "-" + element.getStop()
                        + ") did not end with the expected '</fourth>' element!", test.endsWith("</fourth>"));
            }

            // ----- check if the line number calculation works correct ----- //
            if (DEBUG) System.out.println("__XML-ELEMENTS________________________________________________");
            List<IndexElement> xeList = index.getElements("/first/second/third");
            if (DEBUG) {
                for (IndexElement element : xeList) {
                    System.out.println("The XML element from position " + element.getStart()
                            + " to position " + element.getStop()
                            + " starts at line " + element.getLineNumber() + ":");
                    System.out.println(readByteRange(element.getStart(), element.getStop(), s, encoding));
                    System.out.println("");
                }
            }
            IndexElement firstElement = xeList.get(0);
            IndexElement fourthElement = xeList.get(3);
            // we have to distinguish 3 file layouts:  all in one line, with header and without header
            if (s.contains("header-flat")) { // all in one line
                Assert.assertEquals("The first XML element does not start where expected!", 1, firstElement.getLineNumber());
                Assert.assertEquals("The fourth XML element does not start where expected!", 1, fourthElement.getLineNumber());
            } else if (s.contains("-wo-header")) { // without header
                Assert.assertEquals("The first XML element does not start where expected!", 3, firstElement.getLineNumber());
                Assert.assertEquals("The fourth XML element does not start where expected!", 15, fourthElement.getLineNumber());
            } else if (s.contains("header")) { // the rest should be with header
                Assert.assertEquals("The first XML element does not start where expected!", 4, firstElement.getLineNumber());
                Assert.assertEquals("The fourth XML element does not start where expected!", 16, fourthElement.getLineNumber());
            } else {
                System.out.println("WARNING: This file was not expected!");
            }

            // ----- tests for current file done ----- //
            System.out.println("File: " + s + "  OK.");
        }
    }


    @Test
    public void xmlXpathIndexerTest2() throws IOException {
        List<String> fileList = createTestFileList();

        // ----- Create an inclusion set in this case ----- //
        HashSet<String> xpathInclusionSet = new HashSet<String>(2);
        xpathInclusionSet.add("/first/second");
        xpathInclusionSet.add("/first/second/third/");

        // ----- now check each file in the list ----- //
        for (String s : fileList) {
            System.out.println("\n== File: " + s + " ===================================================");

            InputStream is = XmlXpathIndexerTest.class.getClassLoader().getResourceAsStream(s);
            String encoding = detectFileEncoding(s);
            if (DEBUG) System.out.println("Detected file encoding: " + encoding);

            // ----- index creation ------ //
            if (DEBUG) System.out.println("__INDEX_______________________________________________________");
            long startTime = System.currentTimeMillis();
            XpathIndex index = null;
            try {
                index = XmlXpathIndexer.buildIndex(is, xpathInclusionSet);
                is.close();
            } catch (IOException e) {
                System.out.println("Caught IOException trying to build the index on file: " + s);
                e.printStackTrace();
            }
            Assert.assertNotNull("Index was not created!", index);
            long stopTime = System.currentTimeMillis();
            if (DEBUG) System.out.println("Time of index creation: " + (stopTime - startTime) + "ms");
            if (DEBUG) System.out.println(index);

            // ----- extraction of XML snippets using index and extractor ------ //
            if (DEBUG) System.out.println("__XML-ELEMENTS________________________________________________");
            List<IndexElement> brList;
            String xpath;

            xpath = "/first/second/third/fourth";
            Assert.assertFalse("The xpath '" + xpath + "' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", index.containsXpath(xpath));
            brList = index.getElements(xpath);
            Assert.assertEquals("The xpath '" + xpath + "' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", 0, brList.size());
            // This seemingly repetitive test checks that the retrieval of a non-existing
            // xpath does not influence the functionality of the 'contains' method.
            Assert.assertFalse("The xpath '" + xpath + "' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", index.containsXpath(xpath));
            xpath = "/first";
            brList = index.getElements(xpath);
            Assert.assertEquals("The xpath '" + xpath + "' was in the index whereas it should not have been indexed since an inclusion list lacking this xpath was present!", 0, brList.size());
            xpath = "/first/second";
            brList = index.getElements(xpath);
            Assert.assertEquals("The xpath '" + xpath + "' should result in 2 index entries, yet gave " + brList.size() + " instead!", 2, brList.size());
            xpath = "/first/second/third";
            brList = index.getElements(xpath);
            Assert.assertEquals("The xpath '" + xpath + "' should result in 4 index entries, yet gave " + brList.size() + " instead!", 4, brList.size());
            for (IndexElement element : brList) {
                String test= readByteRange(element.getStart(), element.getStop(), s, encoding);
                if (DEBUG) System.out.println(test);
                Assert.assertTrue("The String retrieved from the byte offset (" + element.getStart() + "-" + element.getStop() + ") did not start with the expected '<third>' element!", test.startsWith("<third>"));
                Assert.assertTrue("The String retrieved from the byte offset (" + element.getStart() + "-" + element.getStop() + ") did not end with the expected '</third>' element!", test.endsWith("</third>"));
            }
            System.out.println("File: " + s + "  OK.");
        }
    }


    // ToDo: more tests on parsing, indexing and reading bits
    // e.g. check correct building of tag names (tags with attributes, spaces, new line,...)


}
