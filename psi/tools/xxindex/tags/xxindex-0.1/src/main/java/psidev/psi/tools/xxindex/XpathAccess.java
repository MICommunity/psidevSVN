package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.XpathIndex;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

/**
 * Author: florian
 * Date: 03-Aug-2007
 * Time: 14:31:12
 */
public interface XpathAccess {

    public List<String> getXmlElements(String xpath) throws IOException;

    public Iterator getXmlElementIterator(String xpath);

    XpathIndex getIndex();
}
