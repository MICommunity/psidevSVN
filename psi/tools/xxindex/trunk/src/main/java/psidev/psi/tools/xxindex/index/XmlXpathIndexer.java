package psidev.psi.tools.xxindex.index;

import org.apache.commons.io.input.CountingInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import psidev.psi.tools.xxindex.index.ByteBuffer;
import psidev.psi.tools.xxindex.index.ByteRange;
import psidev.psi.tools.xxindex.index.StandardXpathIndex;
import psidev.psi.tools.xxindex.index.XmlElement;

/**
 * Author: Florian Reisinger
 * Date: 23-Jul-2007
 */
public class XmlXpathIndexer {

    ////////////////////
    // Index method

    public static StandardXpathIndex buildIndex(InputStream is) throws IOException {
        CountingInputStream cis = new CountingInputStream( is );

        StandardXpathIndex index = new StandardXpathIndex();

        Stack<XmlElement> stack = new Stack<XmlElement>();
        byte[] buf = new byte[1];
        byte read = ' ';
        byte oldRead;
        long startPos = 0;
        long stopPos;
        boolean recording = false;
        boolean closingTag = false;
        boolean startTag = false;
        ByteBuffer bb = new ByteBuffer();

        while ( (nextByte(cis, buf)) != -1 ) {
            oldRead = read; // save previous byte
            read = buf[0];
            if ( read == '<' ) { // possible start tag
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
            if ( read == '>' ) {
                stopPos = cis.getByteCount();
                if ( startTag ) { // end of start tag
                    if ( oldRead == '/' ) { // self closing start tag
                        String tagName = getTagName(bb);
                        bb.clear();
                        // since it is a self closing start tag, we can set the stop position already
                        XmlElement element = createElement(tagName, startPos, stopPos);
                        stack.push(element);
                        String xpath = createPathFromStack(stack);
                        stack.pop();
                        index.put(xpath, element.getRange());
                    } else { // end of regular start tag
                        String tagName = getTagName(bb);
                        bb.clear();
                        // only set start, since we don't know yet where this element ends
                        XmlElement element = createElement(tagName, startPos, -1L);
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
                    XmlElement element = stack.pop();
                    // check if found name is the last on stack
                    if ( !element.getName().equalsIgnoreCase(tagName) ) {
                        System.out.println("ERROR: Tag name mismatch! Found '" + tagName +
                                           "' but '" + element.getName() + "' on stack|");
                    }
                    element.getRange().setStop(stopPos);
                    index.put(xpath, element.getRange());
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
//        if (buf.length != 1) {
//            throw new IllegalArgumentException("The byte array needs to be of length 1 !");
//        }
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
        // ToDo: find a more efficient way then using another buffer
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
     * Convenience method to create a XmlElement.
     * @param name name of the element (tag name).
     * @param start start byte position of this tag in the XML file.
     * @param stop stop byte position of this tag in the XML file.
     * @return a new XmlElement with the specified values..
     */
    private static XmlElement createElement(String name, long start, long stop) {
        ByteRange range = new ByteRange(start, stop);
        // ToDo: check range? e.g. not bigger than file itself, stop > start, not negative, ...

        XmlElement element = new XmlElement();
        element.setName(name);
        element.setRange(range);

        return element;
    }

    /**
     * Creates a xpath of the element names on the stack.
     * @param stack the stack of XmlElements to create the xpath from.
     * @return a xpath expression representing the elements in the stack.
     */
    private static String createPathFromStack(Stack<XmlElement> stack) {
        String path = ""; //"./";
        for (XmlElement element : stack) {
            path += element.getName() + "/";
        }
        return path.substring(0, path.length()-1); // get rid of last '/'
    }


}
