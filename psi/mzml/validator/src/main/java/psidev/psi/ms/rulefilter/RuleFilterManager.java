package psidev.psi.ms.rulefilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import psidev.psi.ms.rulefilter.jaxb.CvMappingRuleCondition;
import psidev.psi.ms.rulefilter.jaxb.CvMappingRuleToSkip;
import psidev.psi.ms.rulefilter.jaxb.ObjectRuleCondition;
import psidev.psi.ms.rulefilter.jaxb.ObjectRuleToSkip;
import psidev.psi.ms.rulefilter.jaxb.ReferencedRules;
import psidev.psi.ms.rulefilter.jaxb.RuleFilter;
import psidev.psi.ms.rulefilter.jaxb.RulesToSkipRef;
import psidev.psi.ms.rulefilter.jaxb.UserCondition;
import psidev.psi.ms.rulefilter.jaxb.UserOption;

public class RuleFilterManager {

	// END CONDITIONS AND OPTIONS

	private JAXBContext jc;
	private RuleFilter filter = null;

	public RuleFilterManager(File xmlFile) throws JAXBException {
		// check if null
		if (xmlFile == null)
			throw new IllegalArgumentException("Provide a no null file!");

		// check if exists
		if (!xmlFile.exists())
			throw new IllegalArgumentException(xmlFile.getAbsolutePath() + " doesn't exist!");
		jc = JAXBContext.newInstance("psidev.psi.ms.rulefilter.jaxb");
		this.filter = (RuleFilter) jc.createUnmarshaller().unmarshal(xmlFile);

	}

	public void setFilter(RuleFilter filter) {
		this.filter = filter;
	}

	public RuleFilter getFilter() {
		return filter;
	}

	/**
	 * Look for the chosen options in the rule filter file and returns a list
	 * with the identifiers of the rules to exclude
	 * 
	 * @param selectedOptions
	 *            : the key is the identifier of the condition and the value is
	 *            the choosed option
	 * @return the list of identifiers of rules to exclude
	 */
	public Set<String> getCVMappingRulesToSkipByUserOptions(HashMap<String, String> selectedOptions) {
		Set<String> cvMappingRulesIds = new HashSet<String>();

		for (String conditionId : selectedOptions.keySet()) {
			UserCondition userCondition = getCondition(conditionId);
			if (userCondition != null) {
				List<CvMappingRuleToSkip> rules = getMappingRulesToSkip(userCondition,
						selectedOptions.get(conditionId));
				if (rules != null) {
					for (CvMappingRuleToSkip cvMappingRule : rules) {
						cvMappingRulesIds.add(cvMappingRule.getId());
					}
				}
			}
		}
		return cvMappingRulesIds;
	}

	private List<CvMappingRuleToSkip> getMappingRulesToSkip(UserCondition userCondition,
			String optionId) {
		List<CvMappingRuleToSkip> cvMappingRulesToSkip = new ArrayList<CvMappingRuleToSkip>();
		if (optionId != null)
			for (UserOption option : userCondition.getUserOption()) {
				if (option.getId().equals(optionId)) {
					if (option.getCvMappingRuleToSkip() != null) {
						cvMappingRulesToSkip.addAll(option.getCvMappingRuleToSkip());
					}
					if (option.getRulesToSkipRef() != null) {
						for (RulesToSkipRef rulesToSkipRef : option.getRulesToSkipRef()) {
							if (filter.getReferences() != null
									&& filter.getReferences().getReferencedRules() != null) {
								for (ReferencedRules referencedRuleSet : filter.getReferences()
										.getReferencedRules()) {
									if (referencedRuleSet.getId().equals(rulesToSkipRef.getRef())) {
										if (referencedRuleSet.getCvMappingRuleToSkip() != null) {
											cvMappingRulesToSkip.addAll(referencedRuleSet
													.getCvMappingRuleToSkip());
										}
									}
								}
							}
						}
					}
				}
			}
		return cvMappingRulesToSkip;
	}

	private UserCondition getCondition(String conditionId) {
		if (conditionId == null || "".equals(conditionId) || filter == null)
			return null;
		for (UserCondition userCondition : filter.getUserConditions().getUserCondition()) {
			if (userCondition.getId().equals(conditionId))
				return userCondition;
		}
		return null;
	}

	/**
	 * Look for the the chosen options in the rule filter file and returns a
	 * list with the identifiers of the rules to exclude
	 * 
	 * @param selectedOptions
	 *            : the key is the identifier of the condition and the value is
	 *            the choosed option
	 * @return the list of identifiers of rules to exclude
	 */
	public Set<String> getObjectRulesToSkipByUserOptions(HashMap<String, String> selectedOptions) {
		Set<String> ret = new HashSet<String>();

		for (String conditionId : selectedOptions.keySet()) {
			UserCondition condition = getCondition(conditionId);
			if (condition != null) {
				List<ObjectRuleToSkip> rules = getObjectRulesToSkip(condition,
						selectedOptions.get(conditionId));
				if (rules != null) {
					for (ObjectRuleToSkip objectRule : rules) {
						ret.add(objectRule.getId());
					}
				}
			}
		}

		return ret;
	}

	private List<ObjectRuleToSkip> getObjectRulesToSkip(UserCondition condition, String optionId) {
		List<ObjectRuleToSkip> ret = new ArrayList<ObjectRuleToSkip>();
		if (optionId != null)
			for (UserOption option : condition.getUserOption()) {
				if (option.getId().equals(optionId)) {
					if (option.getObjectRuleToSkip() != null) {
						ret.addAll(option.getObjectRuleToSkip());
					}
					if (option.getRulesToSkipRef() != null) {
						for (RulesToSkipRef ruleToSkipRef : option.getRulesToSkipRef()) {
							if (filter.getReferences() != null
									&& filter.getReferences().getReferencedRules() != null) {
								for (ReferencedRules referencedRuleSet : filter.getReferences()
										.getReferencedRules()) {
									if (referencedRuleSet.equals(ruleToSkipRef.getRef())) {
										ret.addAll(referencedRuleSet.getObjectRuleToSkip());
									}
								}
							}
						}
					}
				}
			}
		return ret;
	}

	public List<String> getObjectRulesToSkipByObjectRule(String ruleId, boolean valid) {
		List<String> ret = new ArrayList<String>();

		if (this.filter.getObjectRuleConditions() != null) {
			for (ObjectRuleCondition objectRuleCondition : this.filter.getObjectRuleConditions()
					.getObjectRuleCondition()) {
				if (objectRuleCondition.getId().equals(ruleId)) {
					if ((valid && objectRuleCondition.isValid())
							|| (!valid && !objectRuleCondition.isValid())) {
						if (objectRuleCondition.getObjectRuleToSkip() != null) {
							for (ObjectRuleToSkip objectRule : objectRuleCondition
									.getObjectRuleToSkip()) {
								ret.add(objectRule.getId());
							}
						}
					}
				}
			}
		}
		return ret;
	}

	public List<String> getCvMappingRulesToSkipByObjectRule(String ruleId, boolean valid) {
		List<String> ret = new ArrayList<String>();

		if (this.filter.getObjectRuleConditions() != null) {
			for (ObjectRuleCondition objectRuleCondition : this.filter.getObjectRuleConditions()
					.getObjectRuleCondition()) {
				if (objectRuleCondition.getId().equals(ruleId)) {
					if ((valid && objectRuleCondition.isValid())
							|| (!valid && !objectRuleCondition.isValid())) {
						if (objectRuleCondition.getCvMappingRuleToSkip() != null) {
							for (CvMappingRuleToSkip cvMappingRule : objectRuleCondition
									.getCvMappingRuleToSkip()) {
								ret.add(cvMappingRule.getId());
							}
						}
					}
				}
			}
		}
		return ret;
	}

	public List<String> getCvMappingRulesToSkipByCvMappingRule(String ruleId, boolean valid) {
		List<String> ret = new ArrayList<String>();

		if (this.filter.getCvMappingRuleConditions() != null) {
			for (CvMappingRuleCondition cvMappingRule : this.filter.getCvMappingRuleConditions()
					.getCvMappingRuleCondition()) {
				if (cvMappingRule.getId().equals(ruleId)) {
					if ((valid && cvMappingRule.isValid()) || (!valid && !cvMappingRule.isValid())) {
						if (cvMappingRule.getCvMappingRuleToSkip() != null) {
							for (CvMappingRuleToSkip cvMappingRule2 : cvMappingRule
									.getCvMappingRuleToSkip()) {
								ret.add(cvMappingRule2.getId());
							}
						}
					}
				}
			}
		}
		return ret;
	}

	public List<String> getObjectRulesToSkipByCvMappingRule(String ruleId, boolean valid) {
		List<String> ret = new ArrayList<String>();

		if (this.filter.getCvMappingRuleConditions() != null) {
			for (CvMappingRuleCondition cvMappingRule : this.filter.getCvMappingRuleConditions()
					.getCvMappingRuleCondition()) {
				if (cvMappingRule.getId().equals(ruleId)) {
					if ((valid && cvMappingRule.isValid()) || (!valid && !cvMappingRule.isValid())) {
						if (cvMappingRule.getObjectRuleToSkip() != null) {
							for (ObjectRuleToSkip objectRule : cvMappingRule.getObjectRuleToSkip()) {
								ret.add(objectRule.getId());
							}
						}
					}
				}
			}
		}
		return ret;
	}
}
