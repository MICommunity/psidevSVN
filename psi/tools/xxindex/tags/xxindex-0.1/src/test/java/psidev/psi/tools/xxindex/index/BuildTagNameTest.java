package psidev.psi.tools.xxindex.index;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Florian Reisinger
 * Date: 06-Aug-2007
 */
public class BuildTagNameTest {

    String controlTagName = "test";

    @Test
    public void buildTagNameTest() throws URISyntaxException, IOException {
        List<ByteBuffer> buffers = createTestByteBuffer();
        System.out.println( "Number of test-cases: " + buffers.size() );
        for ( ByteBuffer buffer : buffers ) {
            System.out.print("Full tag: <" + buffer.toString() + "> ");
            String tagName = XmlXpathIndexer.getTagName( buffer );
            System.out.println( "Extracted tag name: " + tagName );
            Assert.assertEquals("Extracted tag name does not match expected: ", controlTagName, tagName );

        }
    }

    private List<ByteBuffer> createTestByteBuffer() throws URISyntaxException, IOException {
        List<ByteBuffer> bBuf = new ArrayList<ByteBuffer>();
        URL url = BuildTagNameTest.class.getClassLoader().getResource( "testTagName" );
        File testDir = new File( url.toURI() ); // get the directory of the test files
        File[] files = testDir.listFiles();
        byte[] b = new byte[1];

        for (File file : files) {
//            System.out.println( "Processing file: " + file.getName() );
            ByteBuffer bb = new ByteBuffer();
            FileInputStream fis = new FileInputStream(file);
            while ( fis.read(b) > 0 ) { // read byte by byte
                bb.append( b[0] ); // append each byte to the ByteBuffer
            }
            bBuf.add( bb );
//            System.out.println("buffer: " + bb.toString());
        }
        return bBuf;
    }
    
}
