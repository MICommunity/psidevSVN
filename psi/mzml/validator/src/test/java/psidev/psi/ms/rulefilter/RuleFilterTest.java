package psidev.psi.ms.rulefilter;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import psidev.psi.ms.rulefilter.jaxb.CvMappingRuleCondition;
import psidev.psi.ms.rulefilter.jaxb.CvMappingRuleToSkip;
import psidev.psi.ms.rulefilter.jaxb.ObjectRuleCondition;
import psidev.psi.ms.rulefilter.jaxb.ObjectRuleToSkip;
import psidev.psi.ms.rulefilter.jaxb.ReferencedRules;
import psidev.psi.ms.rulefilter.jaxb.RuleFilter;
import psidev.psi.ms.rulefilter.jaxb.RulesToSkipRef;
import psidev.psi.ms.rulefilter.jaxb.UserCondition;
import psidev.psi.ms.rulefilter.jaxb.UserOption;

public class RuleFilterTest {
	private static final String FILE_RULE_FILTER = "ruleFilter_MS.xml";

	@Test
	public void ruleFilterTest() {

		try {
			File file = new File(FILE_RULE_FILTER);
			final RuleFilterManager filterManager = new RuleFilterManager(file);
			printRuleFilter(filterManager.getFilter());
			HashMap<String, String> selectedOptions = new HashMap<String, String>();
			selectedOptions.put(MaldiOrEsiCondition.getID(), MaldiOrEsiCondition.ESI.getOption());
			filterManager.getCVMappingRulesToSkipByUserOptions(selectedOptions);
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void ruleFilterTest2() {

		try {
			System.out.println("\n\n---------------------");
			File file = new File(FILE_RULE_FILTER);
			final RuleFilterManager filterManager = new RuleFilterManager(file);
			HashMap<String, String> selectedOptions = new HashMap<String, String>();
			selectedOptions.put(MaldiOrEsiCondition.getID(), MaldiOrEsiCondition.ESI.getOption());
			final Set<String> cvMappingRulesToExclude = filterManager
					.getCVMappingRulesToSkipByUserOptions(selectedOptions);
			for (String ruleId : cvMappingRulesToExclude) {
				System.out.println(ruleId);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			fail();
		}
	}

	private void printRuleFilter(RuleFilter ruleFilter) {
		for (UserCondition condition : ruleFilter.getUserConditions().getUserCondition()) {
			System.out.println("\nCondition: " + condition.getId());
			for (UserOption option : condition.getUserOption()) {
				System.out.println("Option " + option.getId());
				for (CvMappingRuleToSkip cvMappingRule : option.getCvMappingRuleToSkip()) {
					System.out.println("mapping rule id=" + cvMappingRule.getId());
				}
				for (ObjectRuleToSkip objectRule : option.getObjectRuleToSkip()) {
					System.out.println("object rule id=" + objectRule.getId());
				}
				if (option.getRulesToSkipRef() != null) {
					for (RulesToSkipRef ruleSetReference : option.getRulesToSkipRef()) {
						if (ruleFilter.getReferences() != null) {
							for (ReferencedRules referencedRuleSet : ruleFilter.getReferences()
									.getReferencedRules()) {
								if (referencedRuleSet.getId().equals(ruleSetReference.getRef())) {
									for (CvMappingRuleToSkip cvMappingRule : referencedRuleSet
											.getCvMappingRuleToSkip()) {
										System.out.println("mapping rule id="
												+ cvMappingRule.getId());
									}
									for (ObjectRuleToSkip objectRule : referencedRuleSet
											.getObjectRuleToSkip()) {
										System.out.println("object rule id=" + objectRule.getId());
									}
								}
							}
						}
					}
				}
			}
		}
		if (ruleFilter.getObjectRuleConditions() != null)
			for (ObjectRuleCondition objectRuleCondition : ruleFilter.getObjectRuleConditions()
					.getObjectRuleCondition()) {
				System.out.println("\nObject rule condition: " + objectRuleCondition.getId()
						+ " isValid=" + objectRuleCondition.isValid());
				for (ObjectRuleToSkip objectRule : objectRuleCondition.getObjectRuleToSkip()) {
					System.out.println("\t\tobject rule to skip=" + objectRule.getId());
				}
				for (CvMappingRuleToSkip cvMappingRule : objectRuleCondition
						.getCvMappingRuleToSkip()) {
					System.out.println("\t\tcvMapping rule to skip=" + cvMappingRule.getId());
				}
			}
		if (ruleFilter.getCvMappingRuleConditions() != null)
			for (CvMappingRuleCondition cvMappingCondition : ruleFilter
					.getCvMappingRuleConditions().getCvMappingRuleCondition()) {
				System.out.println("\ncvMapping rule condition: " + cvMappingCondition.getId()
						+ " isValid=" + cvMappingCondition.isValid());
				for (ObjectRuleToSkip objectRule : cvMappingCondition.getObjectRuleToSkip()) {
					System.out.println("\t\tobject rule to skip=" + objectRule.getId());
				}
				for (CvMappingRuleToSkip cvMappingRule : cvMappingCondition
						.getCvMappingRuleToSkip()) {
					System.out.println("\t\tcvMapping rule to skip=" + cvMappingRule.getId());
				}
			}

	}
}
