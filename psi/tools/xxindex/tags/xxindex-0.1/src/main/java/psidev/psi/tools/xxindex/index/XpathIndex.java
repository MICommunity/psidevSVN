package psidev.psi.tools.xxindex.index;

import psidev.psi.tools.xxindex.index.ByteRange;

import java.util.Set;
import java.util.List;

/**
 * Author: florian
 * Date: 03-Aug-2007
 * Time: 13:55:42
 */
public interface XpathIndex {
    
    Set<String> getKeys();

    List<ByteRange> getRange(String xpath);

    void put(String path, ByteRange range);

    int getRangeCount(String xpath);

    boolean containsXpath(String xpath);

    String print();
}
