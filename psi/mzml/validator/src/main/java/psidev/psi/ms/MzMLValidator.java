/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 15-Aug-2007
 * Time: 15:29:31
 */
package psidev.psi.ms;

import psidev.psi.ms.mzml.mapping.jaxb.*;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.MessageLevel;
import psidev.psi.tools.validator.Validator;
import psidev.psi.tools.validator.ValidatorException;
import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.XpathAccess;
import psidev.psi.tools.xxindex.index.XpathIndex;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/*
 * CVS information:
 *
 * $Revision$
 * $Date$
 */

/**
 * This class represents the PSI mzML semantic validator.
 *
 * @author Lennart Martens
 * @version $Id$
 */
public class MzMLValidator extends Validator {

    public MzMLValidator(InputStream aOntologyConfig, InputStream aCvMapping, InputStream aCodedRule)
                                                        throws ValidatorException, OntologyLoaderException {
        super(aOntologyConfig, aCvMapping, aCodedRule);
    }

    public Collection<ValidatorMessage> validate(InputStream aInputStream) throws ValidatorException {
        return null;
    }

    /**
     * The entry point of the application.
     *
     * @param args Start-up arguments; four here:  the ontology configuration file,
     *                                             the CV mapping file,
     *                                             the coded rules file, and
     *                                             the mzML file to validate.
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
        try {
            InputStream ontInput = new FileInputStream(ontology);
            InputStream cvInput = new FileInputStream(cvMapping);
            InputStream objInput = new FileInputStream(objectRules);

            MzMLValidator validator = new MzMLValidator(ontInput, cvInput, objInput);
            messages.addAll(validator.startValidation(mzML, null));
        } catch(Exception e) {
            System.err.println("\n\nException occurred: " + e.getMessage());
            e.printStackTrace();
        }

// ---------------- Print messages ---------------- //

           printMessages(messages, msgLevel);
    }

    /**
     *
     * @param mzMLFile  File with the mzML file to validate.
     * @param aParent   MzMLValidatorGUI that acts as the GUI parent of this validator.
     *                  Can be 'null' if ran from the command-line.
     * @return  Collection with validator messages.
     */
    public Collection<ValidatorMessage> startValidation(File mzMLFile, MzMLValidatorGUI aParent) {
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        try {
            messages.addAll(this.checkCvMappingRules());
            if(aParent != null) {
                aParent.initProgress(0, 13, 0);
                aParent.setProgress(1, "Reading CV rules...");
            }
            // See if the mapping makes sense.
            if(messages.size() != 0) {
                if(aParent == null) {
                    System.err.println("\n\nThere were errors processing the CV mapping configuration file:\n");
                    for (Iterator<ValidatorMessage> lIterator = messages.iterator(); lIterator.hasNext();) {
                        ValidatorMessage lMessage = lIterator.next();
                        System.err.println("\t - " + lMessage);
                    }
                    System.err.println("\n\n");
                    printError("Unable to start validation due to configuration errors.\nSee above.");
                } else {
                    return messages;
                }
            }

            if(aParent != null) {
                aParent.setProgress(2, "Indexing mzML file (this might take a while)...");
            }

            // In getting here we should be ready to create an index.
            XpathAccess xml = new StandardXpathAccess(mzMLFile);

            // Now get a dom parser for these elements.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if(aParent != null) {
                aParent.setProgress(3, "Determining mzML root tag...");
            }

// ---------------- Validation work proper ---------------- //

            // Decide whether the thing is 'indexedmzML' or regular 'mzML'.
            String root = "mzML";
            XpathIndex index = xml.getIndex();
            if(!index.containsXpath(root)) {
                root = "indexedmzML/mzML";
                if(!index.containsXpath(root)) {
                    printError("Could not find known mzML root elements ('mzML' or 'indexedmzML/mzML')!\n\nExiting.");
                }
            }

            // Do a schema validation first.
            // Failure = auto-exit.
            // @TODO schema validation!

            // Object rules that check internal Xrefs (e.g.: referenceableParamGroups and refs).
            // Failure = auto-exit.
            // @TODO Check internal references with object rules!


            // If we get here, the document is validatable. So proceed with the CV mapping rules.

            // -------------------- Validate the ReferenceableParamGroup list. -------------------- //
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! //
            // !! THIS HAS TO BE DONE FIRST IN ORDER TO ALLOW LOOKUP OF THE REFERENCES LATER ON !! //
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! //

            String xpath = root + "/referenceableParamGroupList";
            if(aParent != null) {
                aParent.setProgress(4, "Validating " + xpath + "...");
            }
            Collection tovalidate = new ArrayList();
            Collection xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new ReferenceableParamGroupListType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/referenceableParamGroupList/"));


            // -------------------- Validate the CV list. -------------------- //
            xpath = root + "/cvList";
            if(aParent != null) {
                aParent.setProgress(6, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new CVListType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/cvList/"));

           // -------------------- Validate the file description. -------------------- //
            xpath = root + "/fileDescription";
            if(aParent != null) {
                aParent.setProgress(5, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new FileDescriptionType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/fileDescription/"));



            // -------------------- Validate the sample list. -------------------- //
            xpath = root + "/sampleList";
            if(aParent != null) {
                aParent.setProgress(7, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new SampleListType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/sampleList/"));


            // -------------------- Validate the instrument list. -------------------- //
            xpath = root + "/instrumentList";
            if(aParent != null) {
                aParent.setProgress(8, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new InstrumentListType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/instrumentList/"));


            // -------------------- Validate the software list. -------------------- //
            xpath = root + "/softwareList/software";
            if(aParent != null) {
                aParent.setProgress(9, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new SoftwareListType.Software(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/softwareList/software/"));


            // -------------------- Validate the data processing list. -------------------- //
            xpath = root + "/dataProcessingList";
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new DataProcessingListType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/dataProcessingList/"));


            // -------------------- Validate the run CV param list. -------------------- //
            xpath = root + "/run/cvParam";
            if(aParent != null) {
                aParent.setProgress(10, "Validating " + xpath + "...");
            }
            tovalidate = new ArrayList();
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate.add(new CVParamType(s));
            }
            messages.addAll(this.checkCvMapping(tovalidate, "/mzML/run/cvParam/"));


            // -------------------- Validate each spectrum list. -------------------- //
            xpath = root + "/run/spectrumList/spectrum";
            if(aParent != null) {
                aParent.setProgress(11, "Validating " + xpath + " (this might take a while)...");
            }
            xmlStrings = xml.getXmlElements(xpath);
            for (Iterator lIterator = xmlStrings.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                tovalidate = new ArrayList(1);
                tovalidate.add(new SpectrumType(s));
                messages.addAll(this.checkCvMapping(tovalidate, "/mzML/run/spectrumList/spectrum/"));
            }
            if(aParent != null) {
                aParent.setProgress(12, "Validation complete, compiling output...");
            }
        } catch(Exception e) {
            if(aParent == null) {
                System.err.println("\n\nException occurred: " + e.getMessage());
                e.printStackTrace();
            } else {
                aParent.notifyOfError(e);
                return new ArrayList<ValidatorMessage>();
            }
        }
        return messages;
    }

    private static void printUsage() {
        printError("Usage:\n\n\t" + MzMLValidator.class.getName() + " <ontology_config_file> <cv_mapping_config_file> <coded_rules_config_file> <mzml_file_to_validate> <message_level>\n\n\t\tWhere message level can be:\n\t\t - DEBUG\n\t\t - INFO\n\t\t - WARN\n\t\t - ERROR\n\t\t - FATAL");
    }

    private static void printError(String aMessage) {
        System.err.println("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }

    private static void printMessages(Collection aMessages, MessageLevel aLevel) {
        Collection messages = new ArrayList();
        for (Iterator lIterator = aMessages.iterator(); lIterator.hasNext();) {
            ValidatorMessage lMessage = (ValidatorMessage)lIterator.next();
            if(lMessage.getLevel().isHigher(aLevel) || lMessage.getLevel().isSame(aLevel)) {
                messages.add(lMessage);
            }
        }
        
        if(messages.size() != 0) {
            System.out.println("\n\nThe following messages were obtained during the validation of your XML file:\n");
            for (Iterator lIterator = messages.iterator(); lIterator.hasNext();) {
                ValidatorMessage lMessage = (ValidatorMessage)lIterator.next();
                System.out.println(" * " + lMessage + "\n");
            }
        } else {
            System.out.println("\n\nCongratulations! Your XML file passed the semantic validation!\n\n");
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
}
