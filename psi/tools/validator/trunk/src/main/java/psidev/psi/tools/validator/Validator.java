package psidev.psi.tools.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import psidev.psi.tools.cvrReader.CvRuleReader;
import psidev.psi.tools.cvrReader.CvRuleReaderException;
import psidev.psi.tools.cvrReader.mapping.jaxb.CvMappingRules;
import psidev.psi.tools.ontology_manager.OntologyManager;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRuleManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <b>Semantic XML Validator</b>.
 * <p/>
 * Validates a XML document against a set of rules. </p>
 *
 * @author Matthias Oesterheld & Samuel Kerrien
 * @version $Id: Validator.java 668 2007-06-29 16:44:18 +0100 (Fri, 29 Jun 2007) skerrien $
 * @since 04.01.2006; 15:37:20
 */
public abstract class Validator {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( Validator.class );

    /**
     * User preferences.
     * <p/>
     * Initialise to the default values.
     */
//    protected UserPreferences userPreferences = new UserPreferences();

    protected OntologyManager ontologyMngr;

    /**
     * The set of rules specific to that Validator.
     * List of ObjectRuleS
     */
    private List<ObjectRule> rules;

    /**
     * List holding the CvRuleS.
     */
    private CvRuleManager cvRuleManager;

    //////////////////////
    // Constructor

    public Validator( InputStream ontoConfig, InputStream cvRuleConfig, InputStream oRuleConfig ) throws ValidatorException, OntologyLoaderException {
        // load the ontologies
        ontologyMngr = new OntologyManager( ontoConfig );

        // if specified, load cvRules
        if ( cvRuleConfig != null ) {
            try {
                setCvMappingRules( cvRuleConfig );
            } catch (CvRuleReaderException e) {
                throw new ValidatorException( "CvMappingException while trying to load the CvRules.", e );
            }
        }

        // if specified, load objectRules
        if ( oRuleConfig != null ) {
            setObjectRules( oRuleConfig );
        }
    }

    public Validator( InputStream ontoConfig, InputStream cvRuleConfig ) throws ValidatorException, OntologyLoaderException {
        // load the ontologies
        ontologyMngr = new OntologyManager( ontoConfig );

        // if specified, load cvRules
        if ( cvRuleConfig != null ) {
            try {
                setCvMappingRules( cvRuleConfig );
            } catch (CvRuleReaderException e) {
                throw new ValidatorException( "CvMappingException while trying to load the CvRules.", e );
            }
        }
    }

    public Validator( InputStream ontoConfig ) throws OntologyLoaderException {
        // load the ontologies
        ontologyMngr = new OntologyManager( ontoConfig );
    }

    ////////////////////////
    // Getters and Setters

    public OntologyManager getOntologyMngr() {
        return ontologyMngr;
    }

    public void setOntologyManager( InputStream ontoConfig ) throws ValidatorException, OntologyLoaderException {
        ontologyMngr = new OntologyManager( ontoConfig );
    }


    public CvMappingRules getCvMapping() {
        return cvRuleManager.getCvMappingRules();
    }

    public CvRuleManager getCvRuleManager() {
        return cvRuleManager;
    }

    /**
     * Set a cvMapping file and build the corresponding cvRuleManager.
     *
     * @param cvIs InputStream form the configuration file defining the CV Mapping to be applied as rule.
     * @throws CvRuleReaderException      if one cannot parse the given file.
     */
    public void setCvMappingRules( InputStream cvIs ) throws CvRuleReaderException {
        CvRuleReader reader = new CvRuleReader();
        cvRuleManager = new CvRuleManager( ontologyMngr, reader.read( cvIs ) );
    }


    public List<ObjectRule> getObjectRules() {
        return rules;
    }

    /**
     * Parse the configuration file and update the list of Rule of the current Validator.
     * <p/>
     * Each Rule is initialised with a Map of Ontologies that have been read from the config file.
     *
     * @param configFile the configuration file.
     * @throws ValidatorException Exception while trying to validate the input.
     */
    public void setObjectRules( InputStream configFile ) throws ValidatorException {
        rules = new ArrayList<ObjectRule>(); // set -> replace whatever there might have been
        // parse XML
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( configFile );
        } catch ( Exception e ) {
            throw new ValidatorException( "Error while parsing configuration file", e );
        }

        // instantiate rules with ontologies
        NodeList rs = document.getElementsByTagName( "rule" );
        for ( int i = 0; i < rs.getLength(); i++ ) {
            NodeList texts = rs.item( i ).getChildNodes();
            String className = texts.item( 0 ).getNodeValue();
            try {
                Class rule = Class.forName( className );
                Constructor c = rule.getConstructor( OntologyManager.class );
                ObjectRule r = (ObjectRule) c.newInstance( ontologyMngr );
                rules.add( r );
                log.info( "Added rule: " + r.getClass() );
            } catch ( Exception e ) {
                throw new ValidatorException( "Error instantiating rule (" + className + ")", e );
            }
        }
    }


    //////////////////////
    // Validation

    /**
     * Validates a document.
     * <p/>
     * The implementing class will have to take care of parsing the document and validate each entity.
     *
     * @param file InputStream holding document to be validated
     * @return Collection of validator messages.
     * @throws ValidatorException Exception while trying to validate the input.
     */
    public abstract Collection<ValidatorMessage> validate( InputStream file ) throws ValidatorException;

    /**
     * Validates a collection of objects against all the (object) rules.
     *
     * @param col collection of objects to check on.
     * @return collection of validator messages.
     * @throws ValidatorException Exception while trying to validate the input.
     */
    public Collection<ValidatorMessage> validate( Collection<?> col ) throws ValidatorException {
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();

        // Run the user defined rules
        for (ObjectRule rule : rules) {
            messages.addAll(validate(col, rule));
        }

        return messages;
    }

    /**
     * Validates a collection of objects against a single (object) rule.
     *
     * @param col collection of objects to check on.
     * @return collection of validator messages.
     * @param rule the Rule to check on
     * @throws ValidatorException Exception while trying to validate the input.
     */
    private Collection<ValidatorMessage> validate( Collection<?> col, ObjectRule rule ) throws ValidatorException {
        List<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        for (Object aCol : col) {
            if (rule.canCheck(aCol)) { // apply only if rule can handle this object
                // ToDo: try to resolve unchecked assignment
                messages.addAll(rule.check(aCol));
            } else {
                // what to do if can not check?
                // for now: just be quiet and do nothing, simply skip
                // maybe log debug statement
            }
        }
        return messages;
    }


    /**
     * Run a check on the CvMappingRules to ensure syntactically correct rules will be used for the CvMapping check.
     *
     * @return  collection of validator messages.
     * @throws ValidatorException Exception while trying to validate the input.
     */
    public Collection<ValidatorMessage> checkCvMappingRules() throws ValidatorException {
        if ( cvRuleManager != null ) {
            return cvRuleManager.checkCvMapping();
        } else {
            log.warn( "The CvRuleManager has not been set up yet." );
            return new ArrayList<ValidatorMessage>();
        }
    }

    /**
     * Run a check on the CvMapping.
     *
     * @param col collection of objects to check on.
     * @param xPath the xpath from the XML root to the object that is to be checked.
     * @return collection of validator messages.
     * @throws ValidatorException Exception while trying to validate the input.
     */
    public Collection<ValidatorMessage> checkCvMapping( Collection<?> col, String xPath ) throws ValidatorException {
        Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
        // Run cv mapping check
        if ( cvRuleManager != null ) {
            for (CvRule rule : cvRuleManager.getCvRules()) {
                for ( Object o : col ) {
                    if ( rule.canCheck(xPath) ) {
                        messages.addAll(rule.check(o, xPath));
                    }
                    // else: rule does not apply
                }
            }
        } else {
            log.warn( "The CvRuleManager has not been set up yet." );
        }
        return messages;
    }

    ///////////////////
    // Uitlities


}