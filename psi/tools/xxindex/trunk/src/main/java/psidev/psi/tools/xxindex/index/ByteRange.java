package psidev.psi.tools.xxindex.index;

/**
 * Author: florian
 * Date: 18-Jan-2008
 * Time: 13:42:54
 */
public class ByteRange implements IndexElement {

    private long start;
    private long stop;

    public ByteRange() {
        this.start = -1;
        this.stop = -1;
    }

    public ByteRange(long start, long stop, long lineNumber) {
        this.start = start;
        this.stop = stop;
        // this ByteRange does not record the line number
    }

    public void setValues(long start, long stop, long lineNumber) {
        this.start = start;
        this.stop = stop;
        // this ByteRange does not record the line number
    }

    public long getStart() {
        return start;
    }
    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }
    public void setStop(long stop) {
        this.stop = stop;
    }

    public long getLineNumber() {
        // return default value -1, since we don't record the line number
        return -1;
    }
    public void setLineNumber(long lineNumber) {
        // this ByteRange does not record the line number
    }


    public String toString() {
        return "ByteRange{" +
                "start=" + start +
                ", stop=" + stop +
                '}';
    }
}
