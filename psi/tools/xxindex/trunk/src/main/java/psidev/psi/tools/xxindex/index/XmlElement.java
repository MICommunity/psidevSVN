package psidev.psi.tools.xxindex.index;

import psidev.psi.tools.xxindex.index.ByteRange;

/**
 * Author: florian
 * Date: 23-Jul-2007
 * Time: 15:40:43
 */
public class XmlElement {

    private String name;
    private ByteRange range;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ByteRange getRange() {
        return range;
    }

    public void setRange(ByteRange range) {
        this.range = range;
    }
}
