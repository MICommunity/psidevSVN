package psidev.psi.tools.xxindex.index;

/**
 * Author: florian
 * Date: 23-Jul-2007
 * Time: 15:41:34
 */
public class ByteRange {

    // ToDo: check that stop is bigger than start?
    private long start;
    private long stop;


    ////////////////////
    // Constructor

    public ByteRange() {
        this.start = -1;
        this.stop = -1;
    }

    public ByteRange(long start, long stop) {
        this.start = start;
        this.stop = stop;
    }


    ////////////////////
    // Getter & Setter

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


    ////////////////////
    // Convenience methods

    public long getLength() {
        return ( stop - start );
    }
}
