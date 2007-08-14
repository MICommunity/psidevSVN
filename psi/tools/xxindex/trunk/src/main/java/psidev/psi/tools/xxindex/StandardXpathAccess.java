package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.ByteRange;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;
import psidev.psi.tools.xxindex.index.XpathIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/**
 * Author: Florian Reisinger
 * Date: 02-Aug-2007
 */
public class StandardXpathAccess implements XpathAccess {

    private File file;
    private XpathIndex index;
    private XmlElementExtractor extractor;


    ////////////////////
    // Constructor

    public StandardXpathAccess(File file) throws IOException {
        this.file = file;
        this.index = XmlXpathIndexer.buildIndex(new FileInputStream(file));
        this.extractor = new StandardXmlElementExtractor();
        String enc = extractor.detectFileEncoding(file.toURL());
        extractor.setEncoding(enc);
    }


    ////////////////////
    // Getter & Setter

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
    public List<String> getXmlElements(String xpath) throws IOException {
        List<String> results = new ArrayList<String>();
        // check xpath
        // check if xpath in index
        if ( index.containsXpath(xpath) ) {
            // retrieve ByteRange from index
            List<ByteRange> ranges = index.getRange(xpath);
            // get String for ByteRange
            for (ByteRange range : ranges) {
                results.add( extractor.readByteRange( range.getStart(), range.getStop(), file) );
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
    public Iterator getXmlElementIterator(String xpath) {
        Iterator iter = null;
        if ( index.containsXpath(xpath) ) {
            // retrieve ByteRange from index
            List<ByteRange> ranges = index.getRange(xpath);

            iter = new XmlElementIterator( ranges, extractor, file );
        } else {
            // Error message
            System.out.println("ERROR: the index does not contain any entry for the requested xpath: " + xpath);
            // return iterator over empty list
            iter = Collections.emptyList().iterator();
        }
        return iter;
    }

    /**
     *
     * @param xpath the xpath expression of the XML element of interest.
     * @return the number of elements that correspond to the specified xpath or -1 if the xpath is not recognized.
     */
    public int getXmlElementCount(String xpath) {
        return index.getRangeCount(xpath);
    }

    private class XmlElementIterator implements Iterator {

        private Iterator<ByteRange> iterator;
        private XmlElementExtractor extractor;
        private File file;

        public XmlElementIterator( List<ByteRange> ranges , XmlElementExtractor extractor, File file ) {
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
            ByteRange range = iterator.next();
            try {
                result = extractor.readByteRange( range.getStart(), range.getStop(), file );
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException while reading from file: " + file.getName());
            }
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
