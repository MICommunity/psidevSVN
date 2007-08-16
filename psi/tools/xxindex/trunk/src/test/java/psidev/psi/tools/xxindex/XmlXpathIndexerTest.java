package psidev.psi.tools.xxindex;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import psidev.psi.tools.xxindex.index.ByteRange;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;
import psidev.psi.tools.xxindex.index.XpathIndex;
import psidev.psi.tools.xxindex.StandardXmlElementExtractor;

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

//        fileList.add("test-ascii-wrong-header.xxindex");
//        fileList.add("test-ascii-wrong-header-flat.xxindex");
//        fileList.add("test-ascii-wo-header.xxindex");
//        fileList.add("test-ascii-wo-header-flat.xxindex");

//        fileList.add("test-juan-header.xxindex");
//        fileList.add("test-juan-wo-header.xxindex");

//        fileList.add("test-utf8-header.xxindex");
//        fileList.add("test-utf8-header2.xxindex");
//        fileList.add("test-utf8-header-flat.xxindex");
//        fileList.add("test-utf8-header-flat2.xxindex");
//        fileList.add("test-utf8-wo-header.xxindex");
//        fileList.add("test-utf8-wo-header2.xxindex");
//        fileList.add("test-utf8-wo-header-flat.xxindex");
//        fileList.add("test-utf8-wo-header-flat2.xxindex");

//        fileList.add("test-win1252-header.xxindex");
//        fileList.add("test-win1252-header-flat.xxindex");

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
                List<ByteRange> brList = index.getRange("first/second/third/fourth");
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
    
    ////////////////////
    // main

    public static void main(String[] args) {
        XmlXpathIndexerTest test = new XmlXpathIndexerTest();
        test.xmlXpathIndexerTest1();
    }

}
