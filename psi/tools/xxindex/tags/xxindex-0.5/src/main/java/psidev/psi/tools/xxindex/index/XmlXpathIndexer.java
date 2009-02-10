package psidev.psi.tools.xxindex.index;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

/**
 * Indexes XML data so we know the begin and end position of specific elements. 
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Florian Reisinger
 *
 * @since 0.3
 * Date: 11-Jan-2008
 */
public class XmlXpathIndexer {

    private static final Log log = LogFactory.getLog( XmlXpathIndexer.class );

    ////////////////////
    // Index methods

    /**
     * This method indexes the XML file accessible via the specified inputstream.
     * All xpaths encountered will be included in the index!
     *
     * @param is    inputstream to the XML file to index.
     * @return the LineXpathIndex for the XML file.
     * @throws IOException when a IOException occurs during XML file access.
     */
    public static StandardXpathIndex buildIndex(InputStream is) throws IOException {
        return XmlXpathIndexer.buildIndex(is, null);
    }

    /**
     * This method indexes the XML file accessible via the specified inputstream.
     * All xpaths that do not correspond to one of the xpaths included
     * in the xpath exclusion set will be ignored and therefore omitted from the index!
     * Note: a value of 'null' is allowed for the aXpathInclusionSet parameter and will
     * produce the same result as buildIndex(InputStream is).  
     *
     * @param is    inputstream to the XML file to index.
     * @param aXpathInclusionSet    Set with the String representation
     *                              of the xpaths to include in the index.
     *                              <b>Note</b> that these xpaths should have
     *                              their trailing '/' removed!
     *                              <b>Also note</b> that any xpath not included
     *                              in this list will <b>not</b> be added
     *                              to the index! Can be 'null' to ensure
     *                              inclusion of all xpaths.
     * @return the LineXpathIndex for the XML file.
     * @throws IOException when a IOException occurs during XML file access.
     * @see this#buildIndex(java.io.InputStream) 
     */
    public static StandardXpathIndex buildIndex(InputStream is, Set<String> aXpathInclusionSet) throws IOException {
        return buildIndex(is, aXpathInclusionSet, true);
    }

    /**
     * This method indexes the XML file accessible via the specified inputstream.
     * All xpaths that do not correspond to one of the xpaths included
     * in the xpath exclusion set will be ignored and therefore omitted from the index!
     * Note: a value of 'null' is allowed for the aXpathInclusionSet parameter and will
     * produce the same result as buildIndex(InputStream is).
     *
     * @param is    inputstream to the XML file to index.
     * @param aXpathInclusionSet    Set with the String representation
     *                              of the xpaths to include in the index.
     *                              <b>Note</b> that these xpaths should have
     *                              their trailing '/' removed!
     *                              <b>Also note</b> that any xpath not included
     *                              in this list will <b>not</b> be added
     *                              to the index! Can be 'null' to ensure
     *                              inclusion of all xpaths.
     * @param recordLineNumber boolean flag to swith line number recording on or off.
     *                         If switched off, the created index will need less memory.
     * @return the LineXpathIndex for the XML file.
     * @throws IOException when a IOException occurs during XML file access.
     * @see this#buildIndex(java.io.InputStream)
     */
    public static StandardXpathIndex buildIndex(InputStream is, Set<String> aXpathInclusionSet, boolean recordLineNumber) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);

        CountingInputStream cis = new CountingInputStream( bis );

        StandardXpathIndex index = new StandardXpathIndex(aXpathInclusionSet);

        // create a index that will or will not record the line number according to the specification
        if ( log.isDebugEnabled() ) {
            log.debug( "Indexing " + (recordLineNumber ? "and" : "without") + " keeping track of line numbers." );
        }

        index.setRecordLineNumber(recordLineNumber);

        Stack<TmpIndexElement> stack = new Stack<TmpIndexElement>();
        byte[] buf = new byte[1];
        byte read = ' ';
        byte oldRead;
        long startPos = 0;
        long stopPos;
        boolean recording = false;
        boolean closingTag = false;
        boolean startTag = false;
        boolean inQuote = false;
        ByteBuffer bb = new ByteBuffer();

        long lineNum = 1; // initial line number (we start in the first line)

        while ( (nextByte(cis, buf)) != -1 ) {
            oldRead = read; // save previous byte
            read = buf[0];
            // first keep track of all the line breaks, so we can count the line numbers
            if (read == '\n') {
                lineNum++;
            }
            if (oldRead == '\r' && read != '\n') {
                lineNum++;
            }
            // now check for XML tags
            if ( read == '<' && !inQuote ) { // possible start tag
                startPos = cis.getByteCount() -1; // we want the '<' included
                oldRead = read; // save previous byte
                nextByte(cis, buf);
                read = buf[0];
                if ( read == '!' || read == '?' ) {
                    // we don't bother with header and comments
                    // read till next '<' WITHOUT recording
                    // will also skip CDATA sections
                    startPos = -1; // reset position
                } else if ( read == '/' ) { // we have the start of a closing tag -> begin recording
                    closingTag = true;
                    recording = true;
                } else { // we have the start of a start tag -> begin recording
                    startTag = true;
                    recording = true;
                }
            }
            if (read == '"' && recording) {
                if (inQuote) {
                    inQuote = false;
                } else {
                    inQuote = true;
                }
            }
            if ( read == '>' && !inQuote ) {
                stopPos = cis.getByteCount();
                if ( startTag ) { // end of start tag
                    if ( oldRead == '/' ) { // self closing start tag
                        String tagName = getTagName(bb);
                        bb.clear();
                        // since it is a self closing start tag, we can set the stop position already
                        TmpIndexElement element = new TmpIndexElement(tagName, startPos, stopPos, lineNum);
                        stack.push(element);
                        String xpath = createPathFromStack(stack);
                        stack.pop();
                        index.put(xpath, element.getStart(), element.getStop(), element.getLineNumber());
                    } else { // end of regular start tag
                        String tagName = getTagName(bb);
                        bb.clear();
                        // only set start, since we don't know yet where this element ends
                        TmpIndexElement element = new TmpIndexElement(tagName, startPos, -1L, lineNum);
                        stack.push(element);
                    }
                    recording = false;
                    startTag = false;
                    // reset startPos ?
                    bb.clear();
                } else if ( closingTag ) { // end of regular closing tag
                    String tagName = getTagName(bb);
                    bb.clear();
                    recording = false;
                    closingTag = false;
                    String xpath = createPathFromStack(stack);
                    TmpIndexElement element = stack.pop();
                    // check if found name is the last on stack
                    if ( !element.getName().equalsIgnoreCase(tagName) ) {
                        //ToDo: change to throw Exception, if this goes wrong, the index will be incorrect !!
                        StringBuilder sb = new StringBuilder( 256 );
                        sb.append( "Tag name mismatch! Found '" + tagName + "' but '" + element.getName() + "' on stack." );
                        sb.append( "\n State of the Stack:\n" );
                        ListIterator<TmpIndexElement> iter = stack.listIterator();
                        while (iter.hasNext()) {
                            TmpIndexElement tmpIndexElement = iter.next();
                            sb.append("[");
                            sb.append(tmpIndexElement.getName());
                            sb.append(" at line ");
                            sb.append(tmpIndexElement.getLineNumber());
                            sb.append("]\n");
                        }
                        log.error( sb.toString() );
                        throw new IllegalStateException("Internal stack of XML tags was corrupted!");
                    }
                    element.setStop(stopPos);
                    index.put(xpath, element.getStart(), element.getStop(), element.getLineNumber());
                    // reset stopPos ?
                }
            }
            if ( recording ) {
                bb.append(read);
            }
        } // end of reading

        cis.close();
        is.close();
        return index;
    }

    ////////////////////
    // Utilities

    /**
     * Convenients method to skip filling bytes. Returns the next useful byte.
     * @param cis the CountingInputStream of the file to index.
     * @param buf byte array to use as read buffer.
     * @return the total number of bytes read into the buffer, -1 if end of stream.
     * @throws IOException if an I/O error occurs.
     */
    private static int nextByte(CountingInputStream cis, byte[] buf) throws IOException {
        int result = cis.read(buf);
        while (result != -1 && buf[0] == 0 ){
            result = cis.read(buf);
        }
        return result;
    }

    /**
     * Method to extract the tag name out of the ByteBuffer containing all the bytes of the tag
     * (including attributes).
     * @param bb ByteBuffer of all the bytes between '<' and '>'
     * @return String of the tag name.
     */
    protected static String getTagName(ByteBuffer bb) {
        ByteBuffer bbTmp = new ByteBuffer();
        // get name (every byte till first blank)
        for (Byte aByte : bb) {
            // append till name-attribute separating character (#x20 | #x9 | #xD | #xA) found
            if ( aByte == ' ' || aByte == '\t' || aByte == '\n' || aByte == '\r' ) {
                break;
            }
            bbTmp.append(aByte);
        }
        // might still contain a leading or trailing '/' from self closing start tags or regular closing tags
        // get rid of leading '/' if closing tag
        if ( bbTmp.get(0) == '/' ) {
            bbTmp.remove(0);
        }
        // get rid of trailing '/' if slef closing start tag
        if ( bbTmp.get(bbTmp.size()-1) == '/' ) {
            bbTmp.remove( bbTmp.size()-1 );
        }
        // forget about the rest in the buffer (e.g. attributes)
        return bbTmp.toString();
    }

    /**
     * Creates a xpath of the element names on the stack.
     * @param stack the stack of XmlElements to create the xpath from.
     * @return a xpath expression representing the elements in the stack.
     */
    private static String createPathFromStack(Stack<TmpIndexElement> stack) {
        StringBuilder path = new StringBuilder(100);
        path.append("/");
        for (TmpIndexElement element : stack) {
            path.append(element.getName()).append("/");
        }
        // Get rid of the trailing '/'.
        return path.substring(0, path.length()-1);
    }

    /**
     * Specialised convenience class only used within this indexer.
     * Extends the IndexElement class with a String containing the
     * name of the XML element (used to generate the xpath for the index)).
     */
    private static class TmpIndexElement extends LineNumberedByteRange {

        private String name;

        public TmpIndexElement(String name, long start, long stop, long lineNumber) {
            this.setValues(start, stop, lineNumber);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}