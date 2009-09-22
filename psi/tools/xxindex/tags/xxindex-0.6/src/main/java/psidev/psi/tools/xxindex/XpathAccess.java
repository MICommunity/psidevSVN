package psidev.psi.tools.xxindex;

import psidev.psi.tools.xxindex.index.XpathIndex;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

/**
 * Author: Florian Reisinger
 * Date: 03-Aug-2007
 */
public interface XpathAccess {

    public List<String> getXmlSnippets(String xpath) throws IOException;

    public Iterator<String> getXmlSnippetIterator(String xpath);

    XpathIndex getIndex();
}
