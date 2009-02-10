package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.XmlElement;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;
import psidev.psi.tools.xxindex.index.XpathIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Author: Florian Reisinger
 * Date: 14-Jan-2008
 */
public class StandardXpathAccess implements XpathAccess {

    private static final Log log = LogFactory.getLog( StandardXpathAccess.class );

    private File file;
    private XpathIndex index;
    private XmlElementExtractor extractor;

    ////////////////////
    // Constructors

    /**
     * This constructor creates an xpath index (LineXpathIndex) from the specified XML file,
     * and will include all encountered xpaths in this index.
     *
     * @param file File with the XML file to index
     * @throws IOException when the file could not be accessed
     */
    public StandardXpathAccess( File file ) throws IOException {
        this( file, null );
    }

    /**
     * This constructor creates an xpath index (XpathIndex) from the specified XML file,
     * and takes a set of xpaths to include in this index.
     * All xpaths that do not correspond to one of the xpaths included
     * in this set will be ignored and therefore omitted from the index!
     *
     * @param file               File with the XML file to index.
     * @param aXpathInclusionSet Set with the String representation of the xpaths to include in the index.
     *                           <b>Note</b> that these xpaths should have their trailing '/' removed!
     *                           <b>Also note</b> that any xpath not included in this list will <b>not</b> be added
     *                           to the index! Can be 'null' to ensure inclusion of all xpaths.
     * @throws IOException when the file could not be accessed
     */
    public StandardXpathAccess( File file, Set<String> aXpathInclusionSet ) throws IOException {
        this( file, aXpathInclusionSet, true );
    }

    /**
     * This constructor creates an xpath index for the specified XML file, considering the given
     * xpath inclusion list. It will record the line numbers of the XML starting tags according to the
     * specified value of the recordLineNumbers parameter.
     *
     * @param file               File with the XML file to index.
     * @param aXpathInclusionSet Set with the String representation of the xpaths to include in the index.
     * @param recordLineNumbers  flag whether to record the line numbers of the XML starting tags.
     * @throws IOException when the file could not be accessed
     * @see this#StandardXpathAccess(java.io.File, java.util.Set)
     */
    public StandardXpathAccess( File file, Set<String> aXpathInclusionSet, boolean recordLineNumbers ) throws IOException {
        this.file = file;
        this.index = XmlXpathIndexer.buildIndex( new FileInputStream( file ), aXpathInclusionSet, recordLineNumbers );
        this.extractor = new StandardXmlElementExtractor();
        String enc = extractor.detectFileEncoding( file.toURL() );
        extractor.setEncoding( enc );
    }

    ////////////////////
    // Getter & Setter

    /**
     * This method gives access to the XpathIndex that is used internally in this XpathAccess implementation.
     * Note: the used index is a StandardXpathIndex and the returned Object can therefore be cased to
     * StandardXpathIndex to get access to more specific functionality.
     *
     * @return the created index.
     * @see psidev.psi.tools.xxindex.index.StandardXpathIndex
     */
    public XpathIndex getIndex() {
        return index;
    }

    ////////////////////
    // Method

    /**
     * This method will retrieve XML snippets for the specified xpath. The xpath defines the path from
     * the root element to the XML element to extract.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @return a List of Strings representing the XML elements specified with the xpath.
     * @throws IOException when IO Error while reading from the XML file.
     */
    public List<String> getXmlSnippets( String xpath ) throws IOException {
        return getXmlSnippets( xpath, null, null );
    }

    /**
     * This method will retrieve XML snippets for the specified xpath. The xpath defines the path from
     * the root element to the XML element to extract.
     * Only elements that are located <b>between</b> the specified start and stop byte positions
     * will be returned.
     * <pre>
     * Requirement for a XML element to be returned:
     *     element.getStart() >= start && element.getStop() <= stop
     * Note: the start and stop parameters can be null, in which case snippets for <b>all</b> the XML elements
     *     for the specified xpath will be returned.
     * </pre>
     *
     * @param xpath a xpath expression valid for the XML file.
     * @param start the start byte position, before which no elements will be returned.
     * @param stop  the stop byte position, after which no elements will be returned.
     * @return a List of Strings representing the XML elements specified with the xpath and within the specified range.
     * @throws IOException when IO Error while reading from the XML file.
     * @see this#getXmlSnippets(String)
     */
    public List<String> getXmlSnippets( String xpath, Long start, Long stop ) throws IOException {
        List<String> results = new ArrayList<String>();
        // check xpath
        // check if xpath in index
        if ( index.containsXpath( xpath ) ) {
            // retrieve ByteRange from index
            List<IndexElement> ranges = index.getElements( xpath );
            // get String for ByteRange
            for ( IndexElement range : ranges ) {
                if ( ( start == null || range.getStart() >= start ) && ( stop == null || range.getStop() <= stop ) ) {
                    results.add( extractor.readString( range.getStart(), range.getStop(), file ) );
                }
            }
        } else {
            // Error message
            log.error( "The index does not contain any entry for the requested xpath: " + xpath );
        }
        return results;
    }

    /**
     * This method can be used whenever the expected list of XML snippets is very long. The snippets
     * are not read all at once, but rather every call of next() will read another XML snippet.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @return a Iterator over the Strings representing the XML elements specified with the xpath.
     */
    public Iterator<String> getXmlSnippetIterator( String xpath ) {
        return getXmlSnippetIterator( xpath, null, null );
    }

    /**
     * This method can be used whenever the expected list of XML snippets is very long. The snippets
     * are not read all at once, but rather every call of next() will read another XML snippet.
     * Only elements that are located <b>between</b> the specified start and stop byte positions
     * will be returned.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @param start the start byte position, before which no elements will be returned.
     * @param stop  the stop byte position, after which no elements will be returned.
     * @return a Iterator over the Strings representing the XML elements specified with the xpath.
     * @see this#getXmlSnippets(String, Long, Long)
     */
    public Iterator<String> getXmlSnippetIterator( String xpath, Long start, Long stop ) {
        Iterator<String> iter;
        if ( index.containsXpath( xpath ) ) {
            // retrieve ByteRange from index
            List<IndexElement> ranges = index.getElements( xpath );

            iter = new XmlSnippetIterator( ranges, extractor, file, start, stop );
        } else {
            log.error( "The index does not contain any entry for the requested xpath: " + xpath );
            // return iterator over empty list
            List<String> s = Collections.emptyList();
            iter = s.iterator();
        }
        return iter;
    }

    /**
     * @param xpath the xpath expression of the XML element of interest.
     * @return the number of elements that correspond to the specified xpath or -1 if the xpath is not recognized.
     */
    public int getXmlElementCount( String xpath ) {
        return index.getElementCount( xpath );
    }

    /**
     * + Private Iterator implementation that allows the iteration over XML snippets as Strings.
     */
    private class XmlSnippetIterator implements Iterator<String> {

        private Iterator<IndexElement> iterator;
        private XmlElementExtractor extractor;
        private File file;

        public XmlSnippetIterator( List<IndexElement> ranges, XmlElementExtractor extractor, File file ) {
            this.iterator = ranges.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public XmlSnippetIterator( List<IndexElement> elements, XmlElementExtractor extractor, File file, Long start, Long stop ) {
            List<IndexElement> validElements; // the list of elements we will iterate over

            // if both borders are unspecified, use all elements (initial list)
            if ( start == null && stop == null ) {
                validElements = elements;
            } else { // if at least one border is specified, we need a new list containing only valid elements
                validElements = new ArrayList<IndexElement>();
                // iterate over the initial list and add only the valid elements to the new list
                for ( IndexElement element : elements ) {
                    if ( ( start == null || element.getStart() >= start ) && ( stop == null || element.getStop() <= stop ) ) {
                        validElements.add( element );
                    }
                }
            }
            // only use the IndexElements that are in the valid range
            this.iterator = validElements.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * This will throw a runtime exception if an IOException occurs during reading from the file.
         *
         * @return the String represetation of the XML snippet.
         */
        public String next() {
            String result;
            IndexElement range = iterator.next();
            try {
                result = extractor.readString( range.getStart(), range.getStop(), file );
            } catch ( IOException e ) {
                throw new IllegalStateException( "Caught IOException while reading from file: " + file.getName(), e );
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
     *
     * @param xpath a xpath expression valid for the XML file.
     * @return a List of LineXmlElement representing the XML elements specified with
     *         the xpath and their starting line number.
     * @throws IOException when IO Error while reading from the XML file.
     */
    public List<XmlElement> getXmlElements( String xpath ) throws IOException {
        return getXmlElements( xpath, null, null );
    }

    /**
     * This mehtod will retrieve XML snippets for the specified xpath bundeled with the
     * line number in which the XML snippet started. The xpath defines the path from
     * the root element to the XML element to extract.
     * Only elements that are located <b>between</b> the specified start and stop byte positions
     * will be returned.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @param start the start byte position, before which no elements will be returned.
     * @param stop  the stop byte position, after which no elements will be returned.
     * @return a List of LineXmlElement representing the XML elements specified with
     *         the xpath and their starting line number.
     * @throws IOException when IO Error while reading from the XML file.
     * @see this#getXmlSnippets(String, Long, Long)
     */
    public List<XmlElement> getXmlElements( String xpath, Long start, Long stop ) throws IOException {
        List<XmlElement> results = new ArrayList<XmlElement>();
        // check xpath
        // check if xpath in index
        // if yes, transform (range + line number) into (xml snippet + line number)
        if ( index.containsXpath( xpath ) ) {
            // retrieve the xml element (range + line number) from index
            List<IndexElement> elements = index.getElements( xpath );
            // get String for ByteRange and get the line number for the range
            for ( IndexElement element : elements ) {
                if ( ( start == null || element.getStart() >= start ) && ( stop == null || element.getStop() <= stop ) ) {
                    String tmp = extractor.readString( element.getStart(), element.getStop(), file );
                    long posTmp = element.getLineNumber();
                    results.add( new XmlElement( tmp, posTmp ) );
                }
            }
        } else {
            // Error message
            log.error( "The index does not contain any entry for the requested xpath: " + xpath );
        }
        return results;
    }

    /**
     * This method can be used whenever the expected list of XmlElements is very long. The elements
     * are not produced all at once, but rather every call of next() will create another XmlElement.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @return a Iterator over the LineXmlElement representing the XML elements specified with
     *         the xpath and their starting line number.
     */
    public Iterator<XmlElement> getXmlElementIterator( String xpath ) {
        return getXmlElementIterator( xpath, null, null );
    }

    /**
     * This method can be used whenever the expected list of XmlElements is very long. The elements
     * are not produced all at once, but rather every call of next() will create another XmlElement.
     * Only elements that are located <b>between</b> the specified start and stop byte positions
     * will be returned.
     *
     * @param xpath a xpath expression valid for the XML file.
     * @param start the start byte position, before which no elements will be returned.
     * @param stop  the stop byte position, after which no elements will be returned.
     * @return a Iterator over the LineXmlElement representing the XML elements specified with
     *         the xpath and their starting line number.
     */
    public Iterator<XmlElement> getXmlElementIterator( String xpath, Long start, Long stop ) {
        Iterator<XmlElement> iter;
        if ( index.containsXpath( xpath ) ) {
            // retrieve ByteRange from index
            List<IndexElement> elements = index.getElements( xpath );

            iter = new XmlElementIterator( elements, extractor, file, start, stop );
        } else {
            // Error message
            log.error( "The index does not contain any entry for the requested xpath: " + xpath );
            // return iterator over empty list
            List<XmlElement> s = Collections.emptyList();
            iter = s.iterator();
        }
        return iter;
    }

    /**
     * Private Iterator implementation that allows the iteration over IndexElement Objects.
     */
    private class XmlElementIterator implements Iterator<XmlElement> {

        private Iterator<IndexElement> iterator;
        private XmlElementExtractor extractor;
        private File file;

        public XmlElementIterator( List<IndexElement> elements, XmlElementExtractor extractor, File file ) {
            this.iterator = elements.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public XmlElementIterator( List<IndexElement> elements, XmlElementExtractor extractor, File file, Long start, Long stop ) {
            List<IndexElement> validElements; // the list of elements we will iterate over
            // if both borders are unspecified, iterate over all elements (initial list)
            if ( start == null && stop == null ) {
                validElements = elements;
            } else { // if at least one borders is specified, we need a new list containing only valid elements
                validElements = new ArrayList<IndexElement>();
                // iterate over the initial list and only add the valid elements to the new list
                for ( IndexElement element : elements ) {
                    if ( ( start == null || element.getStart() >= start ) && ( stop == null || element.getStop() <= stop ) ) {
                        validElements.add( element );
                    }
                }
            }

            // only use the IndexElements that are in the valid range
            this.iterator = validElements.iterator();
            this.extractor = extractor;
            this.file = file;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        /**
         * This will throw a runtime exception if an IOException occurs during reading from the file.
         *
         * @return the LineXmlElement represetating of the XML snippet and line number it started..
         */
        public XmlElement next() {
            XmlElement result;
            IndexElement element = iterator.next();
            try {
                String xmlSnippet = extractor.readString( element.getStart(), element.getStop(), file );
                long position = element.getLineNumber();
                result = new XmlElement( xmlSnippet, position );
            } catch ( IOException e ) {
                throw new IllegalStateException( "Caught IOException while reading from file: " + file.getName() );
            }
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
