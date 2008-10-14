/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 14-Oct-2008
 * Time: 16:06:58
 */
package psidev.psi.ms;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;

import java.io.*;

import com.sun.msv.verifier.jarv.TheFactoryImpl;
/*
 * CVS information:
 *
 * $Revision$
 * $Date$
 */

/**
 * This class
 *
 * @author martlenn
 * @version $Id$
 */
public class MzMLSchemaValidator {
        /**
     * This static object is used by the concrete implementation to create the Schema object
     * used for validation.
     */
    protected static final VerifierFactory VERIFIER_FACTORY = new TheFactoryImpl();

    /**
     * This method must be implemented to create a suitable Schema object for the
     * xsd file in question.
     *
     * @param xmlFileReader the XML file being validated as a Stream (Reader)
     * @return an XMLValidationErrorHandler that can be queried to return all of the
     *         error in the XML file as plain text or HTML.
     */
//	public static abstract XMLValidationErrorHandler validate(InputStream xmlFileReader) throws IOException, VerifierConfigurationException, SAXException;

    /**
     * This method carries out the work of validating the XML file passed in through
     * 'inputStream' against the compiled XML schema 'schema'.  This method is a helper
     * method called by the implementation of this abstract class.
     *
     * @param reader being a java.io.Reader from the complete XML file being validated.
     * @param schema being a compiled schema object built from the appropriate xsd (
     *               performed by the implementing sub-class of this abstract class.)
     * @return an XMLValidationErrorHandler that can be queried for details of any
     *         parsing errors to retrieve plain text or HTML
     * @throws java.io.IOException                    if the XML file cannot be accessed / read.
     * @throws org.iso_relax.verifier.VerifierConfigurationException
     * @throws org.xml.sax.SAXException
     */
    protected static XMLValidationErrorHandler validate(Reader reader, Schema schema)
            throws IOException, VerifierConfigurationException, SAXException {

        final XMLValidationErrorHandler xmlValidationErrorHandler = new XMLValidationErrorHandler();
        Verifier schemaVerifier = schema.newVerifier();
        schemaVerifier.setErrorHandler(xmlValidationErrorHandler);
        try {
            schemaVerifier.verify(new InputSource(reader));
        } catch (SAXParseException e) {
            //this will catch well-formedness exceptions that might be thrown
            //by the SAX parser
            xmlValidationErrorHandler.error(e);
        }
        return xmlValidationErrorHandler;
    }

    private static Schema SCHEMA = null;

    private static final String SCHEMA_NAME = "mzML1.0.0_idx.xsd";

    /**
     * This method must be implemented to create a suitable Schema object for the
     * xsd file in question.
     *
     * @param reader the XML file being validated as a Stream (Reader)
     * @return an XMLValidationErrorHandler that can be queried to return all of the
     *         error in the XML file as plain text or HTML.
     */
    public static XMLValidationErrorHandler validate(Reader reader) throws IOException, VerifierConfigurationException, SAXException {
        if (SCHEMA == null) {
            SCHEMA = VERIFIER_FACTORY.compileSchema(new FileInputStream(SCHEMA_NAME));
        }
        return validate(reader, SCHEMA);
    }

    public static void main(String[] args) {

        if(args == null || args.length != 1) {
            printUsage();
            System.exit(1);
        }

        // Check input file and output folder.
        File inputFolder = new File(args[0]);
        if(!inputFolder.exists()){
            System.err.println("\nUnable to find the input folder you specified: '" + args[0] + "'!\n");
            System.exit(1);
        }
        if(!inputFolder.isDirectory()) {
            System.err.println("\nThe input folder you specified ('" + args[0] + "') was a file, not a folder!\n");
            System.exit(1);
        }

        BufferedReader br = null;
        try {
            System.out.println("\nRetrieving files from '" + inputFolder.getAbsolutePath() + "'...");
            File[] inputFiles = inputFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    boolean result = false;
                    if(name.toLowerCase().endsWith("mzml") || name.toLowerCase().endsWith("xml")) {
                        result = true;
                    }
                    return result;
                }
            });
            System.out.println("Found " + inputFiles.length + " input files.\n");
            System.out.println("Validating files...");
            for (int i = 0; i < inputFiles.length; i++) {
                File inputFile = inputFiles[i];
                System.out.println("  - Validating file '" + inputFile.getAbsolutePath() + "'...");
                br = new BufferedReader(new FileReader(inputFile));
                XMLValidationErrorHandler xveh = validate(br);
                if(xveh.noErrors()) {
                    System.out.println("    File is valid!");
                } else {
                    System.err.println("    * Errors detected: ");
                    System.err.println(xveh.getErrorsFormattedAsPlainText());
                }
                br.close();
            }
            System.out.println("\nAll done!\n");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(br != null) {
                    br. close();
                }
            } catch(IOException ioe) {
                // Do nothing.
            }
        }
    }

    private static void printUsage() {
        StringBuffer out = new StringBuffer();
        out.append("\n\nUsage: java ").append(MzMLSchemaValidator.class.getName());
        out.append(" <inputfolder> ");
        System.out.println(out.toString());
    }

    private static class XMLValidationErrorHandler implements ErrorHandler {

            public XMLValidationErrorHandler() {
                super();
            }

            private StringBuffer errorMessageBuffer = null;

            public boolean noErrors() {
                return errorMessageBuffer == null;
            }

            public String getErrorsFormattedAsPlainText() {
                if (noErrors()) {
                    return null;
                }
                return errorMessageBuffer.toString();
            }

            public String getErrorsFormattedAsHTML() {
                if (noErrors()) {
                    return null;
                }
                return (errorMessageBuffer.toString().replaceAll("<", "&lt;")).replaceAll(">", "&gt;").replaceAll("\\n", "<br/>");
            }


            private void initialiseErrorMessageBuffer() {
                if (errorMessageBuffer == null) {
                    errorMessageBuffer = new StringBuffer("Unfortunately, your XML document does not conform to the XML schema for the following reasons:\n");
                }
            }

            /**
             * Receive notification of a recoverable error.
             * <p/>
             * <p>This corresponds to the definition of "error" in section 1.2
             * of the W3C XML 1.0 Recommendation.  For example, a validating
             * parser would use this callback to report the violation of a
             * validity constraint.  The default behaviour is to take no
             * action.</p>
             * <p/>
             * <p>The SAX parser must continue to provide normal parsing events
             * after invoking this method: it should still be possible for the
             * application to process the document through to the end.  If the
             * application cannot do so, then the parser should report a fatal
             * error even if the XML 1.0 recommendation does not require it to
             * do so.</p>
             * <p/>
             * <p>Filters may use this method to report other, non-XML errors
             * as well.</p>
             *
             * @param exception The error information encapsulated in a
             *                  SAX parse exception.
             * @throws org.xml.sax.SAXException Any SAX exception, possibly
             *                                  wrapping another exception.
             * @see org.xml.sax.SAXParseException
             */
            public void error(SAXParseException exception) throws SAXException {
                initialiseErrorMessageBuffer();
                errorMessageBuffer.append("\n\nNon-fatal XML Parsing error detected on line ")
                        .append(exception.getLineNumber())
                        .append("\nError message: ")
                        .append(exception.getMessage());
            }

            /**
             * Receive notification of a non-recoverable error.
             * <p/>
             * <p>This corresponds to the definition of "fatal error" in
             * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
             * parser would use this callback to report the violation of a
             * well-formedness constraint.</p>
             * <p/>
             * <p>The application must assume that the document is unusable
             * after the parser has invoked this method, and should continue
             * (if at all) only for the sake of collecting addition error
             * messages: in fact, SAX parsers are free to stop reporting any
             * other events once this method has been invoked.</p>
             *
             * @param exception The error information encapsulated in a
             *                  SAX parse exception.
             * @throws org.xml.sax.SAXException Any SAX exception, possibly
             *                                  wrapping another exception.
             * @see org.xml.sax.SAXParseException
             */
            public void fatalError(SAXParseException exception) throws SAXException {
                initialiseErrorMessageBuffer();
                errorMessageBuffer.append("\n\nFATAL XML Parsing error detected on line ")
                        .append(exception.getLineNumber())
                        .append("\nFatal Error message: ")
                        .append(exception.getMessage());
            }

            /**
             * Receive notification of a warning.
             * <p/>
             * <p>SAX parsers will use this method to report conditions that
             * are not errors or fatal errors as defined by the XML 1.0
             * recommendation.  The default behaviour is to take no action.</p>
             * <p/>
             * <p>The SAX parser must continue to provide normal parsing events
             * after invoking this method: it should still be possible for the
             * application to process the document through to the end.</p>
             * <p/>
             * <p>Filters may use this method to report other, non-XML warnings
             * as well.</p>
             *
             * @param exception The warning information encapsulated in a
             *                  SAX parse exception.
             * @throws org.xml.sax.SAXException Any SAX exception, possibly
             *                                  wrapping another exception.
             * @see org.xml.sax.SAXParseException
             */
            public void warning(SAXParseException exception) throws SAXException {
                initialiseErrorMessageBuffer();
                errorMessageBuffer.append("\n\nWarning: Validation of the XMl has detected the following condition on line ")
                        .append(exception.getLineNumber())
                        .append("\nWarning message: ")
                        .append(exception.getMessage());
            }
        }
}
