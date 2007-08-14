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

    ////////////////////
    // Constructor

    public StandardXpathIndex() {
        index = new HashMap<String, List<ByteRange>>();
    }

    ////////////////////
    // Getter + Setter

    public Set<String> getKeys() {
        return index.keySet();
    }

    public List<ByteRange> getRange(String xpath) {
        List<ByteRange> list =  index.get(xpath);
        if (list == null) {
            list = new ArrayList<ByteRange>();
            index.put(xpath, list);
        }
        return list;
    }

    public void put(String path, ByteRange range) {
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
        int cnt = -1;
        List <ByteRange> ranges = index.get(xpath);
        if ( ranges != null ) {
            cnt = ranges.size();
        }
        return cnt;
    }

    public boolean containsXpath(String xpath) {
        boolean result = false;
        for (String s : index.keySet()) {
            if ( xpath.equalsIgnoreCase(s) ) { result = true; }
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
