package psidev.psi.tools.xxindex.index;

import psidev.psi.tools.xxindex.index.ByteRange;
import psidev.psi.tools.xxindex.index.XpathIndex;

import java.util.*;

/**
 * Author: florian
 * Date: 24-Jul-2007
 * Time: 10:08:29
 */
public class StandardXpathIndex implements XpathIndex {

    private Map<String, List<ByteRange>> index;

    private Set<String> xpathInclusionSet = null;

    ////////////////////
    // Constructors

    /**
     * Default constructor, will result in the creation of an index containing
     * all xpaths.
     */
    public StandardXpathIndex() {
        this(null);
    }

    /**
     * This constructor takes a set of xpaths to include in the index.
     * All xpaths that do not correspond to one of the xpaths included
     * in this set will be ignored and therefore omitted from the index!
     *
     * @param aXpathInclusionSet    Set with the String representation
     *                              of the xpaths to include in the index.
     *                              <b>Note</b> that these xpaths should have
     *                              their trailing '/' removed!
     *                              <b>Also note</b> that any xpath not included
     *                              in this list will <b>not</b> be added
     *                              to the index! Can be 'null' to ensure
     *                              inclusion of all xpaths.
     */
    public StandardXpathIndex(Set<String> aXpathInclusionSet) {
        index = new HashMap<String, List<ByteRange>>();
        // If we have a 'non-null' inclusion set, check it for trailing '/'
        // while initializing the inclusion set instance variable.
        // If the inclusion set is 'null', leave the instance variable 'null'as well -
        // it will then be ignored.
        if(aXpathInclusionSet != null) {
            xpathInclusionSet = new HashSet<String>(aXpathInclusionSet.size());
            for (Iterator stringIterator = aXpathInclusionSet.iterator(); stringIterator.hasNext();) {
                String s = (String) stringIterator.next();
                if(s.endsWith("/")) {
                    s = s.substring(0, s.length()-1);
                }
                xpathInclusionSet.add(s);
            }
        }
    }


    ////////////////////
    // Getter + Setter

    public Set<String> getKeys() {
        return index.keySet();
    }

    public List<ByteRange> getRange(String xpath) {
        if(xpath.endsWith("/")) {
            xpath = xpath.substring(0, xpath.length()-1);
        }
        List<ByteRange> list =  index.get(xpath);
        if (list == null) {
            list = new ArrayList<ByteRange>();
            index.put(xpath, list);
        }
        return list;
    }

    public void put(String path, ByteRange range) {
        // Check whether we have an inclusion list.
        if (xpathInclusionSet != null && !xpathInclusionSet.contains(path)) {
            return;
        }
        this.getRange(path).add(range);
    }

    ////////////////////
    // Utilities

    /**
     * Thid method will return the number of elements (ByteRange) stored for the specified xpath expression.
     * The number of ByteRangeS represets the number of XML elements in the document the index was created on.
     * @param xpath the xpath to the element of interest (one key of the index).
     * @return the number of elements stored under this key or -1 if no entry with the specified key exists.
     */
    public int getRangeCount(String xpath) {
        if(xpath.endsWith("/")) {
            xpath = xpath.substring(0, xpath.length()-1);
        }
        int cnt = -1;
        List <ByteRange> ranges = index.get(xpath);
        if ( ranges != null ) {
            cnt = ranges.size();
        }
        return cnt;
    }

    public boolean containsXpath(String xpath) {
        if(xpath.endsWith("/")) {
            xpath = xpath.substring(0, xpath.length()-1);
        }
        boolean result = false;
        if(index.containsKey(xpath)) {
            int size = index.get(xpath).size();
            if(size > 0) {
                result = true;
            }
        }
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for ( String key : index.keySet() ) {
            List<ByteRange> entries = getRange(key);
            sb.append("xPath: ");
            sb.append(key);
            sb.append(" entries: " + entries.size());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String print() {
        StringBuffer sb = new StringBuffer();
        for ( String key : index.keySet() ) {
            List<ByteRange> entries = getRange(key);
            sb.append("xPath: ");
            sb.append(key);
            sb.append("\n");
            for (ByteRange range : entries) {
                sb.append("\tLocation : ");
                sb.append(range.getStart());
                sb.append("-");
                sb.append(range.getStop());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
