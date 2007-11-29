package psidev.psi.tools.xxindex;

import cpdetector.io.CodepageDetectorProxy;
import cpdetector.io.ParsingDetector;
import cpdetector.io.JChardetFacade;
import cpdetector.io.ASCIIDetector;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Set;

import psidev.psi.tools.xxindex.index.ByteBuffer;
import psidev.psi.tools.xxindex.index.ByteRange;

/**
 * Author: florian
 * Date: 24-Jul-2007
 * Time: 17:47:45
 */
public class StandardXmlElementExtractor implements XmlElementExtractor {

    private boolean compareWithDetect;
    private boolean preferDetect;
    private Charset encoding;

    ////////////////////
    // Constructor

    public StandardXmlElementExtractor() {
        this.compareWithDetect = false;
        this.preferDetect = false;
        this.encoding = null;
    }

    public StandardXmlElementExtractor(Charset encoding) {
        this.compareWithDetect = false;
        this.preferDetect = false;
        this.encoding = encoding;
    }

    ////////////////////
    // Getter & Setter

    /**
     * @return true if comparing of the specified encoding with the detected encoding is enabled, false otherwise.
     */
    public boolean isCompareWithDetect() {
        return compareWithDetect;
    }

    /**
     * If set to true, the specified encoding will be checked and compared to the detected encoding on every
     * conversion from a byte array to a String.
     * A warning will be prouced if the encodings do not match.
     * @param compareWithDetect flag whether to check the endocing or not.
     */
    public void setCompareWithDetect(boolean compareWithDetect) {
        this.compareWithDetect = compareWithDetect;
    }

    /**
     *
     * @return true if the preference of the detected encoding over the given encoding is enabled, false otherwise
     */
    public boolean isPreferDetect() {
        return preferDetect;
    }

    /**
     * If set to true, the detected encoding will be used, irrespective of the specified encoding.
     * The encoding will be detected on the byte array to convert only, not the whole file. Therefore
     * it might not be correct!
     * @param preferDetect flag whether to prefer the detected encoding over the given encoding.
     */
    public void setPreferDetect(boolean preferDetect) {
        this.preferDetect = preferDetect;
    }

    /**
     *
     * @return the currently set encoding of null if non is set..
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     *
     * @param encoding the encoding to use when converting the read byte array into a String.
     */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    /**
     * This method will try to find a Charset for the given String.
     * @param encoding the encoding to use when converting the read byte array into a String.
     * @return 0 is returned on success, -1 if the specified encoding is not valid and -2 if
     * the specified encoding is not supported by this virtual machine.
     */
    public int setEncoding(String encoding) {
        int result;
        try {
            this.encoding = Charset.forName(encoding);
            result = 0;
        } catch (IllegalCharsetNameException icne) {
            System.out.println("Illegal encoding.");
            result = -1;
        } catch (UnsupportedCharsetException ucne) {
            System.out.println("Unsupported encoding.");
            result = -2;
        }
        return result;
    }

    ////////////////////
    // Methods


    /**
     * Same as readByteRange(long from, long to, File file), but start and stop wrapped in a ByteRange object.
     * @param br the ByteRange to read.
     * @param file the file to read from.
     * @return the XML element including start and stop tag in a String.
     * @throws IOException if an I/O error occurs while reading.
     */
    public String readByteRange(ByteRange br, File file) throws IOException {
        return readByteRange(br.getStart(), br.getStop(), file);
    }

    /**
     * Convenience method that combines the methods: getByteRange(), removeZeroBytes() and bytes2String().
     *
     * Read a String representing a XML element from the specified file (which will be opened read-only).
     * Will read from position 'from' for length 'to - from'.
     * @param from byte position of the start (incl. beginning of start tag) of the XML element.
     * @param to byte position of the end (incl. end of closing tag) of the XML element.
     * @param file the file containing the XML element.
     * @return the XML element including start and stop tag in a String.
//     * @throws IOException if an I/O error occurs while reading.
     */
    public String readByteRange(long from, long to, File file) {
        // retrieve the bytes from the given range in the file
        byte[] bytes = getByteRange(from, to, file);

        // remove all zero bytes (Mac filling bytes)
        byte[] newBytes = removeZeroBytes(bytes);

        // create a String from the rest using the given encoding if specified
        return bytes2String(newBytes);
    }

    /**
     * Retrieves bytes from the specified file from position 'from' for a length of 'to - from' bytes.
     * @param from where to start reading.
     * @param to how long to read.
     * @param file in which file to read.
     * @return what was read.
//     * @throws IOException if a I/O Exception during the reading process occurs.
     */
    public byte[] getByteRange(long from, long to, File file) {
        byte[] bytes = new byte[0];
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");

            // go to specified start position
            raf.seek(from);
            Long length = to - from;
            bytes = new byte[length.intValue()];

            // read into buffer
            raf.read(bytes, 0, length.intValue());
            raf.close();
        } catch ( IOException ioe ) {
          ioe.printStackTrace();
        } finally {
            try {
                if ( raf != null ) {
                    raf.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close RandomAccessFile while reading form Xml file: " + file.getName());
                // ToDo: throw new Exception
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * Convenience method to strip the byte array of zero bytes (such as filling bytes used on Mac OSX).
     * @param bytes byte array that may contain zero bytes (\u0000)
     * @return byte array free of zero bytes.
     */
    public byte[] removeZeroBytes(byte[] bytes) {
        ByteBuffer bb = new ByteBuffer();
        for (byte aByte : bytes) {
            if (aByte != 0) {
                bb.append(aByte);
            }
        }
        byte[] result = new byte[bb.size()];
        int j = 0;
        for (Byte aByte : bb) {
            result[j++] = aByte;
        }
        return result;
    }

    /**
     * Converts the specified byte array into a String, using the encoding of this StandardXmlElementExtractor or the
     * detected encoding if 'preferDetect' is enabled. If no encoding was set, the system default is used.
     * @param bytes the byte array to convert.
     * @return the String representation of the byte array.
//     * @throws UnsupportedEncodingException if the conversion could not be done with the given encoding.
     */
    public String bytes2String(byte[] bytes) {
        Charset detectedEnc = null;
        if (compareWithDetect) {
            detectedEnc = detectEncoding(bytes);
            if ( detectedEnc.compareTo(encoding) != 0 ) { // not the same name
                // if not the same name, check the aliases of the Charset
                Set<String> aliases = detectedEnc.aliases();
                if ( !aliases.contains(encoding.name()) ) {
                    System.out.println("WARNING: specified encoding is not the same as the detected one. " +
                            "Specified: " + encoding.name() + " detected: " + detectedEnc.name());
                }
            }
        }

        if (preferDetect) {
            if (detectedEnc != null) {
                encoding = detectedEnc;
            } else {
                encoding = detectEncoding(bytes);
            }
            System.out.println("Using detected encoding: " + encoding.name());
        }

        String result = null;
        if (encoding == null) {
            System.out.println("Using system default for encoding.");
            result = new String(bytes);
        } else {
            //System.out.println("Using encoding: " + encoding.name());
            try {
                result = new String(bytes, encoding.name());
            } catch (UnsupportedEncodingException e) {
                System.out.println("Specified encoding '" + encoding.name() + "' is not supported");
                // ToDo: throw new Exception
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param bytes the byte array to try to detect the encoding from.
     * @return the detected encoding or the system default if detection attempt raised a Exception.
     */
    public Charset detectEncoding(byte[] bytes) {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); // A singleton.
        detector.add(new ParsingDetector(false)); // be not verbose about parsing.
        detector.add(JChardetFacade.getInstance()); // Another singleton.
        detector.add(ASCIIDetector.getInstance()); // Fallback, see javadoc.

        Charset charset;
        try {
            charset = detector.detectCodepage(new ByteArrayInputStream(bytes), bytes.length);
        } catch (IOException e) {
            System.out.println("IOException trying to detect codepage from byte array.");
            e.printStackTrace();
            charset = Charset.defaultCharset();
        }

        return charset;
    }

    

    protected String detectFileEncoding(String filename) throws IOException {
        URL url = StandardXmlElementExtractor.class.getClassLoader().getResource(filename);
        return detectFileEncoding(url);
    }

    /**
     * Tries to auto-detect the file-encoding of the file specified by the URL.
     * @param fileLocation location of the file to check.
     * @return name of the detected Charset.
     * @throws IOException if an I/O error occurs.
     */
    public String detectFileEncoding(URL fileLocation) throws IOException {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); // A singleton.
        detector.add(new ParsingDetector(false)); // be verbose about parsing.
        detector.add(JChardetFacade.getInstance()); // Another singleton.
        detector.add(ASCIIDetector.getInstance()); // Fallback, see javadoc.

        java.nio.charset.Charset charset = detector.detectCodepage(fileLocation);

        String charsetName = null;
        if (charset != null) {
            charsetName = charset.name();
        }
        return charsetName;
    }

    /**
     * Tries to auto-detect the file-encoding of the file specified by the URL, using length number of bytes.
     * @param fileLocation location of the file to check.
     * @param length the amount of bytes to take into account.
     * @return name of the detected Charset.
     * @throws IOException if an I/O error occurs.
     */
    public String detectFileEncoding(URL fileLocation, int length) throws IOException {
        InputStream in = fileLocation.openStream();

        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); // A singleton.
        detector.add(new ParsingDetector(false)); // be verbose about parsing.
        detector.add(JChardetFacade.getInstance()); // Another singleton.
        detector.add(ASCIIDetector.getInstance()); // Fallback, see javadoc.

        java.nio.charset.Charset charset = detector.detectCodepage(in, length);

        String charsetName = null;
        if (charset != null) {
            charsetName = charset.name();
        }
        return charsetName;
    }

}
