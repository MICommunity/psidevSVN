package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.*;

import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

/**
 * Author: Florian Reisinger
 * Date: 14-Jan-2008
 */
public class StandardXpathAccess implements XpathAccess {

    private File file;
    private XpathIndex index;
    private XmlElementExtractor extractor;


    ////////////////////
    // Constructors

    /**
     * This constructor creates an xpath index (LineXpathIndex) from the specified XML file,
     * and will include all encountered xpaths in this index.
     *
     * @param file  File with the XML file to index
     * @throws IOException  when the file could not be accessed
     */
    public StandardXpathAccess(File file) throws IOException {
        this(file, null);
    }

    /**
     * This constructor creates an xpath index (LineXpathIndex) from the specified XML file,
     * and takes a set of xpaths to include in this index.
     * All xpaths that do not correspond to one of the xpaths included
     * in this set will be ignored and therefore omitted from the index!
     *
     * @param file  File with the XML file to index.
     * @param aXpathInclusionSet    Set with the String representation
     *                              of the xpaths to include in the index.
     *                              <b>Note</b> that these xpaths should have
     *                              their trailing '/' removed!
     *                              <b>Also note</b> that any xpath not included
     *                              in this list will <b>not</b> be added
     *                              to the index! Can be 'null' to ensure
     *                              inclusion of all xpaths.
     * @throws  IOException when the file could not be accessed
     */
    public StandardXpathAccess(File file, Set<String> aXpathInclusionSet) throws IOException {
        this(file, aXpathInclusionSet, true);
    }

    public StandardXpathAccess(File file, Set<String> aXpathInclusionSet, boolean recordLineNumbers) throws IOException {
        this.file = file;
        this.index = XmlXpathIndexer.buildIndex(new FileInputStream(file), aXpathInclusionSet, recordLineNumbers);
        this.extractor = new StandardXmlElementExtractor();
        String enc = extractor.detectFileEncoding(file.toURL());
        extractor.setEncoding(enc);
    }

    ////////////////////
    // Getter & Setter

    /**
     * This will return the index for the XML file of this XpathAccess.
     * Note: this index is a LineXpathIndex and for compatibility reasons return a XpathIndex object.
     * (the index can therefore be cased to LineXpathIndex to get access to more specific functionality)
     * @return the created index.
     */
    public XpathIndex getIndex() {
        return index;
    }


    ////////////////////
    // Method

    /**
     * This mehtod will retrieve XML snippets for the specified xpath. The xpath defines the path from
     * the root element to the XML element to extract.
     * @param xpath a xpath expression valid for the XML file.
     * @return a List of Strings representing the XML elements specified with the xpath.
     * @throws IOException when IO Error while reading from the XML file.
     */
    public List<String> getXmlSnippets(String xpath) throws IOException {
        List<String> results = new ArrayList<String>();
        // check xpath
        // check if xpath in index
        if ( index.containsXpath(xpath) ) {
            // retrieve ByteRange from index
            List<IndexElement> ranges = index.getElements(xpath);
            // get String for ByteRange
            for (IndexElement range : ranges) {
                results.add( extractor.readString( range.getStart(), range.getStop(), file) );
            }
        } else {
            // Error message
            System.out.println("ERROR: the index does not contain any entry for the requested xpath: " + xpath);
        }
        return results;
    }

    /**
     * This method can be used whenever the expected XML elementss are very large, since theys are
     * not read all once, but rather every call of next() will read another XML element.
     * @param xpath a xpath expression valid for the XML file.
     * @return a Iterator over the Strings representing the XML elements specified with the xpath.
     */
    public Iterator<String> getXmlSnippetIterator(String xpath) {
        Iterator<String> iter;
        if ( index.containsXpath(xpath) ) {
            // retrieve ByteRange from index
            List<IndexElement> ranges = index.getElements(xpath);

            iter = new XmlSnippetIterator( ranges, extractor, file );
        } else {
            // Error message
            System.out.println("ERROR: the index does not contain any entry for the requested xpath: " + xpath);
            // return iterator over empty list
            List<String> s = Collections.emptyList();
            iter = s.iterator();
        }
        return iter;
    }

    /**
     *
     * @param xpath the xpath expression of the XML element of interest.
     * @return the number of elements that correspond to the specified xpath or -1 if the xpath is not recognized.
     */
    public int getXmlElementCount(String xpath) {
        return index.getElementCount(xpath);
    }

    private class XmlSnippetIterator implements Iterator<String> {

        private Iterator<IndexElement> iterator;
        private XmlElementExtractor extractor;
        private File file;

        public XmlSnippetIterator( List<IndexElement> ranges , XmlElementExtractor extractor, File file ) {
            this.iterator = ranges.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * This will throw a runtime exception if an IOException occurs during reading from the file.
         * @return the String represetation of the XML snippet.
         */
        public String next() {
            String result;
            IndexElement range = iterator.next();
            try {
                result = extractor.readString( range.getStart(), range.getStop(), file );
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException while reading from file: " + file.getName());
            }
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }




    /**
     * This mehtod will retrieve XML snippets for the specified xpath bundeled with the
     * line number in which the xml snippet started. The xpath defines the path from
     * the root element to the XML element to extract.
     * @param xpath a xpath expression valid for the XML file.
     * @return a List of LineXmlElement representing the XML elements specified with
     * the xpath and their starting line number.
     * @throws IOException when IO Error while reading from the XML file.
     */
    public List<XmlElement> getXmlElements(String xpath) throws IOException {
        List<XmlElement> results = new ArrayList<XmlElement>();
        // check xpath
        // check if xpath in index
        // if yes, transform (range + line number) into (xml snippet + line number)
        if ( index.containsXpath(xpath) ) {
            // retrieve the xml element (range + line number) from index
            List<IndexElement> elements = index.getElements(xpath);
            // get String for ByteRange and get the line number for the range
            for (IndexElement element : elements) {
                String tmp = extractor.readString( element.getStart(), element.getStop(), file);
                long posTmp = element.getLineNumber();
                results.add( new XmlElement(tmp, posTmp) );
            }
        } else {
            // Error message
            System.out.println("ERROR: the index does not contain any entry for the requested xpath: " + xpath);
        }
        return results;
    }

    /**
     * This method can be used whenever the expected XML elements are very large, since theys are
     * not read all at once, but rather every call of next() will read another XML element.
     * @param xpath a xpath expression valid for the XML file.
     * @return a Iterator over the LineXmlElement representing the XML elements specified with
     * the xpath and their starting line number.
     */
    public Iterator<XmlElement> getXmlElementIterator(String xpath) {
        Iterator<XmlElement> iter;
        if ( index.containsXpath(xpath) ) {
            // retrieve ByteRange from index
            List<IndexElement> elements = index.getElements(xpath);

            iter = new XmlElementIterator( elements, extractor, file );
        } else {
            // Error message
            System.out.println("ERROR: the index does not contain any entry for the requested xpath: " + xpath);
            // return iterator over empty list
            List<XmlElement> s = Collections.emptyList();
            iter = s.iterator();
        }
        return iter;
    }

    private class XmlElementIterator implements Iterator<XmlElement> {

        private Iterator<IndexElement> iterator;
        private XmlElementExtractor extractor;
        private File file;

        public XmlElementIterator( List<IndexElement> elements , XmlElementExtractor extractor, File file ) {
            this.iterator = elements.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * This will throw a runtime exception if an IOException occurs during reading from the file.
         * @return the LineXmlElement represetating of the XML snippet and line number it started..
         */
        public XmlElement next() {
            XmlElement result;
            IndexElement element = iterator.next();
            try {
                String xmlSnippet = extractor.readString( element.getStart(), element.getStop(), file );
                long position = element.getLineNumber();
                result = new XmlElement(xmlSnippet, position);
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException while reading from file: " + file.getName());
            }
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // for quick testing
    public static void main(String[] args) throws IOException {

//        File file = new File("C:\\Documents and Settings\\florian\\Desktop\\samplea_01_raw.mzML");
        File file = new File("C:\\Documents and Settings\\florian\\Desktop\\tiny1.mzML0.99.1.mzML");
        long start = System.currentTimeMillis();
        System.out.println("staring at " + new Date(start));
        StandardXpathAccess access = new StandardXpathAccess(file, null, false);
        long stop = System.currentTimeMillis();
        System.out.println("in ms: " + (stop-start));

        System.out.println("max mem: " + Runtime.getRuntime().maxMemory());
        System.out.println("free mem: " + Runtime.getRuntime().freeMemory());
        System.out.println("total mem: " + Runtime.getRuntime().totalMemory());
        System.out.println("mem usage: " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024));

        Set<String> keys = access.getIndex().getKeys();
        System.out.println("index keys: " + keys.size());

        for (String key : keys) {
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("xpath: " + key);
            int cnt = access.getIndex().getElementCount(key);
            System.out.println("  Elements for this xpath: " + cnt);
            IndexElement ele = access.getIndex().getElements(key).get(0);
            System.out.println("    Example element for this xpath: " + ele);
            if (key.endsWith("cvParam")) {
                System.out.println("XML snippet: '" + access.extractor.readString(ele.getStart(), ele.getStop(), file) + "'");
            }
        }

//        String key = access.getIndex().getKeys().iterator().next();
//        System.out.println("Example xpath: " + key);
//        int cnt = access.getIndex().getElementCount(key);
//        System.out.println("Elements for this xpath: " + cnt);
//        IndexElement ele = access.getIndex().getElements(key).get(0);
//        System.out.println("Example element for this xpath: " + ele);
//        System.out.println("XML snippet: " + access.extractor.readString(ele.getStart(), ele.getStop(), file));
//        IndexElement ele2 = access.getIndex().getElements(key).get(20);
//        System.out.println("Example element for this xpath: " + ele2);
//        System.out.println("XML snippet: " + access.extractor.readString(ele2.getStart(), ele2.getStop(), file));
//        IndexElement ele3 = access.getIndex().getElements(key).get(200);
//        System.out.println("Example element for this xpath: " + ele3);
//        System.out.println("XML snippet: " + access.extractor.readString(ele3.getStart(), ele3.getStop(), file));
    }
    
}
