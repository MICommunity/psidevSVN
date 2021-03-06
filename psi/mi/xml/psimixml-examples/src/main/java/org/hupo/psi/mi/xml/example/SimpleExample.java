package org.hupo.psi.mi.xml.example;

import psidev.psi.mi.xml.PsimiXmlVersion;
import psidev.psi.mi.xml.PsimiXmlWriter;
import psidev.psi.mi.xml.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Example to show how to generate PSI-MI XML files programmatically.
 *
 * The PsiFactory class is extensively used as it simplifies the instantiation of the model objects.
 */
public class SimpleExample {

    public static void main(String[] args) throws Exception {
        File outputFile = new File("target/simpleExample.xml");

        // the source contains the organization information
        Source source = PsiFactory.createSource("myOrganizationName");

        // let's create some interaction data
        ///////////////////////////////////////

        Collection<Interaction> interactions = new ArrayList<Interaction>();

        // each interaction contains experimental information,
        // which is contained in the ExperimentDescription object below:
        InteractionDetectionMethod interactionDetMethod = PsiFactory.createInteractionDetectionMethod("MI:0434", "phosphatase assay");
        ParticipantIdentificationMethod participantIdentMethod = PsiFactory.createParticipantIdentificationMethod("MI:0396", "predetermined");
        Organism hostOrganism = PsiFactory.createOrganismInVitro();

        ExperimentDescription experiment1 = PsiFactory.createExperiment("experiment1", "1234567", interactionDetMethod,
                                                                        participantIdentMethod, hostOrganism);


        // an interaction has participants, and earch participant has specific biological or experimental roles
        // in that interaction
        Collection<Participant> participants = new ArrayList<Participant>();

        Organism human = PsiFactory.createOrganismHuman();

        // protein A
        Interactor proteinA = PsiFactory.createInteractorUniprotProtein("P49023", human);
        BiologicalRole bioRoleEnzymeTarget = PsiFactory.createBiologicalRole("MI:0502", "enzyme target");
        ExperimentalRole expRoleNeutral = PsiFactory.createExperimentalRole("MI:0497", "neutral component");

        Participant participantA = PsiFactory.createParticipant(proteinA, bioRoleEnzymeTarget, expRoleNeutral);

        // protein B
        Interactor proteinB = PsiFactory.createInteractorUniprotProtein("P49023", human);
        BiologicalRole bioRoleEnzyme = PsiFactory.createBiologicalRole("P23470", "enzyme");

        Participant participantB = PsiFactory.createParticipant(proteinB, bioRoleEnzyme, expRoleNeutral);

        InteractionType interactionType = PsiFactory.createInteractionType("MI:0203", "dephosphorylation reaction");

        // add the participants to the collection
        participants.add(participantA);
        participants.add(participantB);

        // with all the participants created,  an Interaction can now be instantiated
        Interaction interaction = PsiFactory.createInteraction("interaction1", experiment1,
                                                                interactionType, participants);

        // add the interactions to the collection
        interactions.add(interaction);

        // we put the collection of interactions in an entry
        Entry entry = PsiFactory.createEntry(source, interactions);

        // and finally we create the root object, the EntrySet, that contains the entries
        EntrySet entrySet = PsiFactory.createEntrySet(PsimiXmlVersion.VERSION_254, entry);


        // writing a PSI-MI XML file
        ///////////////////////////////////////

        PsimiXmlWriter psimiXmlWriter = new PsimiXmlWriter(PsimiXmlVersion.VERSION_254);
        Writer fileWriter = new FileWriter(outputFile);

        psimiXmlWriter.write(entrySet, fileWriter);
        
        fileWriter.close();

    }

}
