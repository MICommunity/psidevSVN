package psidev.psi.tools.xxindex.index;

/**
 * Author: florian
 * Date: 18-Jan-2008
 * Time: 12:59:35
 */
public class LineNumberedByteRange extends ByteRange implements IndexElement {

    private long lineNumber;

    public LineNumberedByteRange() {
        this.setStart(-1);
        this.setStop(-1);
        this.lineNumber = -1;
    }

    public LineNumberedByteRange(long start, long stop, long lineNumber) {
        this.setStart(start);
        this.setStop(stop);
        this.lineNumber = lineNumber;
    }

    public void setValues(long start, long stop, long lineNumber) {
        this.setStart(start);
        this.setStop(stop);
        this.lineNumber = lineNumber;
    }

    public long getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }


    public String toString() {
        return "ByteRange{" +
                "start=" + getStart() +
                ", stop=" + getStop() +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
