/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 15-Aug-2007
 * Time: 15:29:31
 */
package psidev.psi.ms;


import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.Validator;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.util.ValidatorReport;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.model.mzml.interfaces.MzMLObject;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

/**
 * This class represents the PSI mzML semantic validator.
 *
 * @author Lennart Martens, modified by Juan Antonio Vizcaino
 * @version $Id$
 */
public class MzMLValidator extends Validator {

    private MzMLValidatorGUI gui = null;
    private int progress = 0;
    private MessageLevel msgL = MessageLevel.WARN;
    private MzMLUnmarshaller unmarshaller = null;
    private Collection<ValidatorMessage> msgs = null;

    private URI schemaUri = null;
    private boolean skipValidation = false;

    //Constructor that passes the 3 configuration files to the generic validator:
    public MzMLValidator(InputStream aOntologyConfig, InputStream aCvMapping, InputStream aCodedRule)
                                                        throws ValidatorException, OntologyLoaderException {
        super(aOntologyConfig, aCvMapping, aCodedRule);
        msgs = new ArrayList<ValidatorMessage>();
        try {
            // ToDo: find better default value: e.g. official address or local file
            schemaUri = new URI("http://psidev.cvs.sourceforge.net/*checkout*/psidev/psi/psi-ms/mzML/schema/mzML1.1.0_idx.xsd");
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not create URI for schema location!", e);
        }
    }

    /**
     *
     * @param validatroGUI MzMLValidatorGUI that acts as the GUI parent of this validator.
     *                     Can be 'null' if ran from the command-line.
     */
    public void setValidatorGUI( MzMLValidatorGUI validatroGUI ) {
        this.gui = validatroGUI;
        this.progress = 0;
    }

    /**
     *
     * @param level MessageLevel with the minimal messagelevel to report.
     */
    public void setMessageReportLevel( MessageLevel level ) {
        this.msgL = level;
    }

    /**
     * Get the currently specified schema URI instance files are validated against.
     * @return the URI pointing to the mzML schema.
     */
    public URI getSchemaUri() {
        return schemaUri;
    }

    /**
     * Use this to overwrite default schema location and
     * specify your own schema to validate against.
     * Note: to be able to validate both indexed and non-indexed
     *       mzML files, the schema for the indexed mzML should be
     *       given.
     *
     * @param schemaUri the URI that points to the schema.
     */
    public void setSchemaUri(URI schemaUri) {
        this.schemaUri = schemaUri;
    }

    /**
     * Flag to skip the schema validation step.
     *
     * @return true if the schema validaton skep will be skipped with the current settings,
     *         false if a schema validation will be performed.
     */
    public boolean isSkipValidation() {
        return skipValidation;
    }

    /**
     * Flag to specify if a schema validation is to be performed.
     *
     * @param skipValidation set to true if the schema validation step should be skipped.
     */
    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    /**
     *
     * @param aInputStream
     * @return
     * @throws ValidatorException
     */
    public Collection<ValidatorMessage> validate(InputStream aInputStream) throws ValidatorException {
        return null;
    }

    /**
     * The entry point of the application.
     *
     * @param args Start-up arguments; five here:  the ontology configuration file,
     *                                             the CV mapping file,
     *                                             the coded rules file,
     *                                             the mzML file to validate, and
     *                                             the level of the error messages. 
     */
    public static void main(String[] args) {
        if(args == null || args.length != 5) {
            printUsage();
        }
        // Validate existence of input files.
        File ontology = new File(args[0]);
        if(!ontology.exists()) {
            printError("The ontology config file you specified '" + args[0] + "' does not exist!");
        } else if(ontology.isDirectory()) {
            printError("The ontology config file you specified '" + args[0] + "' is a folder, not a file!");
        }

        File cvMapping = new File(args[1]);
        if(!cvMapping.exists()) {
            printError("The CV mapping config file you specified '" + args[1] + "' does not exist!");
        } else if(cvMapping.isDirectory()) {
            printError("The CV mapping config file you specified '" + args[1] + "' is a folder, not a file!");
        }

        File objectRules = new File(args[2]);
        if(!objectRules.exists()) {
            printError("The object rules config file you specified '" + args[2] + "' does not exist!");
        } else if(objectRules.isDirectory()) {
            printError("The object rules config file you specified '" + args[2] + "' is a folder, not a file!");
        }

        //Validate file to test
        File mzML = new File(args[3]);
        if(!mzML.exists()) {
            printError("The mzML file you specified '" + args[3] + "' does not exist!");
        } else if(mzML.isDirectory()) {
            printError("The mzML file you specified '" + args[3] + "' is a folder, not a file!");
        }
       

        // Validate messagelevel.
        MessageLevel msgLevel = getMessageLevel(args[4]);
        if(msgLevel == null) {
            System.err.println("\n\n *** Unknown message level '" + args[4] + "' ***\n");
            System.err.println("\tTry one of the following:");
            System.err.println("\t\t - DEBUG");
            System.err.println("\t\t - INFO");
            System.err.println("\t\t - WARN");
            System.err.println("\t\t - ERROR");
            System.err.println("\t\t - FATAL");
            System.err.println(" !!! Defaulting to 'INFO' !!!\n\n");
            msgLevel = MessageLevel.INFO;

        }



        // OK, all validated. Let's get going!
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        // We create the validator here:
        MzMLValidator validator= null;
        
        try {
            InputStream ontInput = new FileInputStream(ontology);
            InputStream cvInput = new FileInputStream(cvMapping);
            InputStream objInput = new FileInputStream(objectRules);

            validator = new MzMLValidator(ontInput, cvInput, objInput);
            validator.setMessageReportLevel(msgLevel);

            //Add the messages to the ArrayList messages and use the startValidation() method (below).
            messages.addAll(validator.startValidation(mzML));
        } catch(Exception e) {
            System.err.println("\n\nException occurred: " + e.getMessage());
            e.printStackTrace();
        }

// ---------------- Print messages ---------------- //

           printMessages(messages);

            if(validator != null){
                ValidatorReport vr= validator.getReport();

                System.out.println("----- Rule application report ---------------------------------------------------");
                System.out.print("CvRules total             : " + validator.getCvRuleManager().getCvRules().size() + "\t( ");
                for (CvRule rule : validator.getCvRuleManager().getCvRules()) {
                    System.out.print(rule.getId()+" ");
                }
                System.out.print(")");

                System.out.print("\nCvRules with invalid XPath: " + vr.getCvRulesInvalidXpath().size() + "\t( ");
                for (CvRule rule : vr.getCvRulesInvalidXpath()) {
                    System.out.print(rule.getId()+" ");
                }
                System.out.print(")");

                System.out.print("\nCvRules not checked       : " + vr.getCvRulesNotChecked().size() + "\t( ");
                for (CvRule rule : vr.getCvRulesNotChecked()) {
                    System.out.print(rule.getId()+" ");
                }
                System.out.print(")");

                System.out.print("\nCvRules valid             : " + vr.getCvRulesValid().size() + "\t( ");
                for (CvRule rule : vr.getCvRulesValid()) {
                    System.out.print(rule.getId()+" ");
                }
                System.out.print(")");

                System.out.print("\nCvRules with valid XPath  : " + vr.getCvRulesValidXpath().size() + "\t( ");
                for (CvRule rule : vr.getCvRulesValidXpath()) {
                    System.out.print(rule.getId()+" ");
                }
                System.out.println(")");
                System.out.println("---------------------------------------------------------------------------------");
            }

    }

    /**
     *
     * @param mzMLFile  File with the mzML file to validate.
     * @return  Collection with validator messages.
     */
    public Collection<ValidatorMessage> startValidation(File mzMLFile) {
        // reset old validation results
        // this will currently reset the status of all CvRules to a "not run" status
        super.resetCvRuleStatus();

        // init gui
        if (this.gui != null) {
            progress = 0;
            // 10 steps: schema validation, reading cv-rules, fileDescription,
            //           sampleList, instrumentConfigurationList, software,
            //           dataProcessingList, chromatogram, spectrum, complete
            this.gui.initProgress(progress, 10, progress);
        }

        // first up we check if the file is actually valid against the schema (if not disabled)
        String guiProgressNote;
        if (skipValidation) {
            guiProgressNote = "Skipping schema validation!";
            if (this.gui != null) {
                this.gui.setProgress(++progress, guiProgressNote);
            } else {
                System.out.println(guiProgressNote);
            }
        } else { // validating
            guiProgressNote = "Validating against schema.";
            if (this.gui != null) {
                this.gui.setProgress(++progress, guiProgressNote);
            } else {
                System.out.println(guiProgressNote);
            }

            boolean valid;
            try {
                valid = isValidMzML(mzMLFile, schemaUri, this.msgs);
            } catch (SAXException e) {
                log.error("ERROR during schema validation.", e);
                this.msgs.add(new ValidatorMessage("ERROR during schema validation." + e.getMessage(), MessageLevel.ERROR));
                valid = false;
            }

            // handle validation failure
            if (!valid) {
                if (this.gui != null) {
                    return this.msgs;
                } else {
                    System.err.println("The provided file is not valid against the mzML schema!");
                    System.err.println("Input file     : " + mzMLFile.getAbsolutePath());
                    System.err.println("Schema location: " + schemaUri);
                    System.exit(-1);
                }
            }

        }



        // We create an MzMLUnmarshaller (this specialised Unmarshaller will internally use a XML index
        // to marshal the mzML file in snippets given by their Xpath)
        this.unmarshaller = new MzMLUnmarshaller(mzMLFile, false);


// ---------------- Internal consistency check of the CvMappingRules ---------------- //
        try {
            // Validate CV Mapping Rules
            if(this.gui != null) { // report progress to the GUI if present
                this.gui.setProgress(++progress, "Reading CV rules...");
            }
            addMessages(this.checkCvMappingRules(), this.msgL);
            // See if the mapping makes sense.
            if(this.msgs.size() != 0) {
                if(this.gui == null) {
                    System.err.println("\n\nThere were errors processing the CV mapping configuration file:\n");
                    for (ValidatorMessage lMessage : this.msgs) {
                        System.err.println("\t - " + lMessage);
                    }
                    System.err.println("\n\n");
                    printError("Unable to start validation due to configuration errors.\nSee above.");
                } else {
                    return this.msgs;
                }
            }


// ---------------- Validation work proper ---------------- //


            // Object rules that check internal Xrefs (e.g.: referenceableParamGroups and refs).
            // Failure = auto-exit.
            // @TODO Check internal references with object rules!


            // If we get here, the document is validatable. So proceed with the CV mapping rules.

            // xpath expression (defining the current XML elements)
            String xpath;
            // iterator to provide MzMLObjects
            MzMLObjectIterator<MzMLObject> mzMLIter;


            // -------------------- Validate the file description. -------------------- //
            // ToDo: check: if there is only one such element in the XML, we can use the unmarshalFromXpath() method!
            //This is the new way to do it (not considering the unmarshaller)
            validateElement("/mzML/fileDescription", FileDescription.class);

            // -------------------- Validate the sample list. -------------------- //
            validateElement("/mzML/sampleList", SampleList.class);

            // -------------------- Validate the instrument configuration list (substituting the former instrument list) -------------------- //
            validateElement("/mzML/instrumentConfigurationList", InstrumentConfigurationList.class);

            // -------------------- Validate the software list. -------------------- //
            validateElement("/mzML/softwareList/software", Software.class);

            // -------------------- Validate the data processing list. -------------------- //
            validateElement("/mzML/dataProcessingList", DataProcessingList.class);

            // -------------------- Validate the chromatogram binary data array. -------------------- //
            validateElement("/mzML/run/chromatogramList/chromatogram", Chromatogram.class);


            // -------------------- Validate each spectrum list. -------------------- //
           xpath = "/mzML/run/spectrumList/spectrum";
           if(this.gui != null) {
               this.gui.setProgress(++progress, "Validating " + xpath + " (this might take a while)...");
           }

            // -------------------- Validate each spectrum. -------------------- //
            mzMLIter = this.unmarshaller.unmarshalCollectionFromXpath("/mzML/run/spectrumList/spectrum", Spectrum.class);

            // create synchronized List to which all threads can write their Validator messages
            Collection<ValidatorMessage> sync_msgs = Collections.synchronizedList(new ArrayList<ValidatorMessage>());

            // Create lock.
            InnerLock lock =  new InnerLock();
            InnerIteratorSync<Spectrum> iteratorSync = new InnerIteratorSync(mzMLIter);
            Collection<InnerSpecValidator> runners = new ArrayList<InnerSpecValidator>();
            int processorCount = Runtime.getRuntime().availableProcessors();
            for(int i=0;i<processorCount;i++) {
                InnerSpecValidator runner = new InnerSpecValidator(iteratorSync, lock, i);
                runners.add(runner);
                new Thread(runner).start();
            }

            // Wait for it.
            lock.isDone(runners.size());
            // now we add all the collected messages from the spectra validators to the general message list
           addMessages(sync_msgs, this.msgL);
            if(this.gui != null) {
                this.gui.setProgress(++progress, "Validation complete, compiling output...");
            }
        } catch(Exception e) {
            if(this.gui == null) {
                System.err.println("\n\nException occurred: " + e.getMessage());
                e.printStackTrace();
            } else {
                this.gui.notifyOfError(e);
                return new ArrayList<ValidatorMessage>();
            }
        }
        return this.msgs;
    }

    private void validateElement(String xpath, Class clazz) throws ValidatorException {
        MzMLObjectIterator<MzMLObject> mzMLIter;
        if(this.gui != null) {
            this.gui.setProgress(++progress, "Validating " + xpath + "...");
        }
        mzMLIter = this.unmarshaller.unmarshalCollectionFromXpath(xpath, clazz);
        Collection<MzMLObject> toValidate = new ArrayList<MzMLObject>();
        while (mzMLIter.hasNext()) {
            toValidate.add(mzMLIter.next());
        }
        addMessages( this.checkCvMapping(toValidate, xpath), this.msgL);
    }

    private boolean isValidMzML(File mzML, URI schemaUri, Collection<ValidatorMessage> messages) throws SAXException {
        boolean valid;

        MzMLSchemaValidator mzMLValidator = new MzMLSchemaValidator();
        MzMLValidationErrorHandler errorHandler = null;
        try {
            mzMLValidator.setSchema(schemaUri);
            errorHandler = mzMLValidator.validate(new FileReader(mzML));
        } catch (FileNotFoundException e) {
            log.fatal("FATAL: Could not find the MzML instance file while trying to " +
                    "validate it! Its exisence should have been checked before!", e);
            System.exit(-1);
        } catch (MalformedURLException e) {
            log.fatal("FATAL: The MzML schema URI is not ");
            // should not happen! since the MzMLValidator should check first if the
            // schema URI is valid before trying to validate with it!
            e.printStackTrace();
            System.exit(-1);
        }

        // if we have no errors, the file is valid
        if ( errorHandler.noErrors() ) {
            valid = true;
        } else {
            messages.addAll(errorHandler.getErrorsAsValidatorMessages());
            valid = false;
        }

        return valid;
    }

    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
    private class InnerIteratorSync<T> {
        private Iterator<T> iter = null;

        public InnerIteratorSync(Iterator<T> aIterator) {
            iter = aIterator;
        }

         public synchronized T next() {
            T result = null;
            if(iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }
    }

    /**
     * Simple lock class so the main thread can detect worker threads' completion.
     */
    private class InnerLock {
        private int doneCount = 0;

        public synchronized void updateDoneCount() {
            doneCount++;
            notifyAll();
        }

        public synchronized boolean isDone(int totalCount) {
            while(doneCount < totalCount) {
                try {
                    wait();
                } catch(InterruptedException ie) {
                    System.err.println("I've been interrupted...");
                }
            }
            return true;
        }
    }

    /**
     * Runnable that requests the next spectrum from the synchronised iterator wrapper,
     * and validates it.
     */
    private class InnerSpecValidator<T> implements Runnable {
        InnerIteratorSync<T> iter = null;
        Collection messages = new ArrayList();
        InnerLock lock = null;
        int count = 0;
        int iNumber = -1;

        public InnerSpecValidator(InnerIteratorSync<T> aIterator, InnerLock aLock, int aNumber) {
            iter = aIterator;
            lock = aLock;
            iNumber = aNumber;
        }

        public void run() {
            T s = null;
            while ((s = iter.next()) != null) {
                //I changed SpectrumType for Spectrum (according to the new PRIDE object model)
                ArrayList<T> tovalidate = new ArrayList(1);
                tovalidate.add(s);
                try {
                    addMessages(checkCvMapping(tovalidate, "/mzML/run/spectrumList/spectrum"), msgL);
                    count++;
                } catch(ValidatorException ve) {
                    ve.printStackTrace();
                }
            }
            lock.updateDoneCount();
        }

        public Collection getMessages() {
            return messages;
        }

        public int getCount() {
            return count;
        }
    }

    private static void printUsage() {
        printError("Usage:\n\n\t" + MzMLValidator.class.getName() + " <ontology_config_file> <cv_mapping_config_file> <coded_rules_config_file> <mzml_file_to_validate> <message_level>\n\n\t\tWhere message level can be:\n\t\t - DEBUG\n\t\t - INFO\n\t\t - WARN\n\t\t - ERROR\n\t\t - FATAL");
    }

    private static void printError(String aMessage) {
        System.err.println("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }

    private static void printMessages(Collection aMessages) {
        if(aMessages.size() != 0) {
            System.out.println("\n\nThe following messages were obtained during the validation of your XML file:\n");
            for (Object aMessage : aMessages) {
                ValidatorMessage lMessage = (ValidatorMessage) aMessage;
                System.out.println(" * " + lMessage + "\n");
            }
        } else {
            System.out.println("\n\nCongratulations! Your XML file passed the semantic validation!\n\n");
        }
    }

    private void addMessages(Collection<ValidatorMessage> aNewMessages, MessageLevel aLevel) {
        for (ValidatorMessage aNewMessage : aNewMessages) {
            if (aNewMessage.getLevel().isHigher(aLevel) || aNewMessage.getLevel().isSame(aLevel)) {
                this.msgs.add(aNewMessage);
            }
        }
    }

    private static MessageLevel getMessageLevel(String aLevel) {
        aLevel = aLevel.trim();
        MessageLevel result = null;
        if(aLevel.equals("DEBUG")) {
            result = MessageLevel.DEBUG;
        } else if(aLevel.equals("INFO")) {
            result = MessageLevel.INFO;
        } else if(aLevel.equals("WARN")) {
            result = MessageLevel.WARN;
        } else if(aLevel.equals("ERROR")) {
            result = MessageLevel.ERROR;
        } else if(aLevel.equals("FATAL")) {
            result = MessageLevel.FATAL;
        }

        return result;
    }

    /**
     * Method to reset all the fields in this validator.
     */
    protected void reset() {
        // reset the collection of ValidatorMessages
        if ( this.msgs != null ) {
            this.msgs.clear();
        }
        // reset the GUI
        this.gui = null;
        // reset the message reporting level to the default
        this.msgL = MessageLevel.WARN;
        // reset the unmarshaller
        this.unmarshaller = null;
        // reset the progress counter
        this.progress = 0;
    }
}
