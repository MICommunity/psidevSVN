package psidev.psi.ms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.rules.cvmapping.MappingRuleStatus;
import psidev.psi.tools.validator.util.ValidatorReport;

public class ExtendedValidatorReport extends ValidatorReport {

	private static final String NEW_LINE = System.getProperty("line.separator");;
	HashMap<String, ObjectRule> objectRulesNotChecked = new HashMap<String, ObjectRule>();
	HashMap<String, ObjectRule> objectRulesValid = new HashMap<String, ObjectRule>();
	HashMap<String, ObjectRule> objectRulesInvalid = new HashMap<String, ObjectRule>();

	public ExtendedValidatorReport(Collection<ObjectRule> objectRules) {
		super(new ArrayList<CvRule>());
		addToMap(objectRules, objectRulesNotChecked);
	}

	public Collection<ObjectRule> getObjectRulesNotChecked() {
		return objectRulesNotChecked.values();
	}

	public Collection<ObjectRule> getObjectRulesValid() {
		return objectRulesValid.values();
	}

	public Collection<ObjectRule> getObjectRulesInvalid() {
		return objectRulesInvalid.values();
	}

	public void setCvRules(Collection<CvRule> cvRules) {
		for (CvRule rule : cvRules) {
			if (rule.getStatus().compareTo(MappingRuleStatus.INVALID_XPATH) == 0)
				this.getCvRulesInvalidXpath().add(rule);
			else if (rule.getStatus().compareTo(MappingRuleStatus.NOT_CHECKED) == 0)
				this.getCvRulesNotChecked().add(rule);
			else if (rule.getStatus().compareTo(MappingRuleStatus.VALID_RULE) == 0)
				this.getCvRulesValid().add(rule);
			else if (rule.getStatus().compareTo(MappingRuleStatus.VALID_XPATH) == 0)
				this.getCvRulesValidXpath().add(rule);
		}
	}

	public void objectRuleExecuted(ObjectRule rule, Collection<ValidatorMessage> resultCheck) {
		boolean valid;
		if (resultCheck == null || resultCheck.isEmpty())
			valid = true;
		else
			valid = false;

		// remove from the list of rules not applied
		this.objectRulesNotChecked.remove(rule.getId());

		// if valid, add to the list of valid rules
		if (valid) {
			if (!this.objectRulesValid.containsKey(rule.getId()))
				this.objectRulesValid.put(rule.getId(), rule);
		} else {
			if (!this.objectRulesInvalid.containsKey(rule.getId()))
				this.objectRulesInvalid.put(rule.getId(), rule);

			this.objectRulesValid.remove(rule.getId());
		}
	}

	public void objectRuleExecuted(ObjectRule rule, ValidatorMessage resultCheck) {
		Collection<ValidatorMessage> messages = new ArrayList<ValidatorMessage>();
		messages.add(resultCheck);
		this.objectRuleExecuted(rule, messages);
	}

	public void setRuleAsSkipped(String ruleId) {
		ObjectRule objectRule = getObjectRuleById(ruleId);
		if (objectRule != null) {
			// add to NotChecked rules
			if (getObjectRuleById(ruleId, this.objectRulesNotChecked) == null)
				this.objectRulesNotChecked.put(objectRule.getId(), objectRule);
			// remove from the other collections
			if (getObjectRuleById(ruleId, this.objectRulesInvalid) != null)
				this.objectRulesInvalid.remove(objectRule.getId());
			if (getObjectRuleById(ruleId, this.objectRulesValid) != null)
				this.objectRulesValid.remove(objectRule.getId());
		}
		CvRule cvRule = getCvRuleById(ruleId);
		if (cvRule != null) {
			// add to NotChecked rules
			if (getCvRuleById(ruleId, this.getCvRulesNotChecked()) == null)
				this.getCvRulesNotChecked().add(cvRule);
			// remove from the other collections
			if (getCvRuleById(ruleId, this.getCvRulesInvalidXpath()) != null)
				this.getCvRulesInvalidXpath().remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValid()) != null)
				this.getCvRulesValid().remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValidXpath()) != null)
				this.getCvRulesValidXpath().remove(objectRule);
		}

	}

	private CvRule getCvRuleById(String ruleId, Collection<CvRule> rules) {
		for (CvRule cvRule : rules) {
			if (cvRule.getId().equals(ruleId))
				return cvRule;
		}
		return null;
	}

	/**
	 * Search for a rule in a collection of rules
	 * 
	 * @param ruleId
	 * @param map
	 * @return
	 */
	private ObjectRule getObjectRuleById(String ruleId, HashMap<String, ObjectRule> map) {
		return map.get(ruleId);
	}

	/**
	 * Search for a cvRule in all collections in the class
	 * 
	 * @param ruleId
	 * @return
	 */
	private CvRule getCvRuleById(String ruleId) {
		if (ruleId != null) {
			for (CvRule cvRule : this.getCvRulesInvalidXpath()) {
				if (ruleId.equals(cvRule.getId()))
					return cvRule;
			}
			for (CvRule cvRule : this.getCvRulesNotChecked()) {
				if (ruleId.equals(cvRule.getId()))
					return cvRule;
			}
			for (CvRule cvRule : this.getCvRulesValid()) {
				if (ruleId.equals(cvRule.getId()))
					return cvRule;
			}
			for (CvRule cvRule : this.getCvRulesValidXpath()) {
				if (ruleId.equals(cvRule.getId()))
					return cvRule;
			}
		}
		return null;
	}

	/**
	 * Search for an object rule in all collections in the class
	 * 
	 * @param ruleId
	 * @return
	 */
	private ObjectRule getObjectRuleById(String ruleId) {
		if (ruleId != null) {
			if (this.objectRulesInvalid.containsKey(ruleId))
				return this.objectRulesInvalid.get(ruleId);

			if (this.objectRulesNotChecked.containsKey(ruleId))
				return this.objectRulesNotChecked.get(ruleId);

			if (this.objectRulesValid.containsKey(ruleId))
				return this.objectRulesValid.get(ruleId);
		}
		return null;
	}

	private void printCvMappingRules(StringBuilder sb, String header, Collection<CvRule> rules) {
		sb.append(header + " (" + rules.size() + ")" + NEW_LINE);
		sb.append("-----------------------------------------------------").append(NEW_LINE);
		for (Rule rule : rules) {
			sb.append(rule.getId()).append(" - ").append(rule.getName()).append(NEW_LINE);
		}
		sb.append(NEW_LINE);
	}

	private void printObjectRules(StringBuilder sb, String header, Collection<ObjectRule> rules) {
		sb.append(header + " (" + rules.size() + ")" + NEW_LINE);
		sb.append("-----------------------------------------------------").append(NEW_LINE);
		for (Rule rule : rules) {
			sb.append(rule.getId()).append(" - ").append(rule.getName()).append(NEW_LINE);
		}
		sb.append(NEW_LINE);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		printCvMappingRules(sb, "Valid cvMapping rules", this.getCvRulesValid());
		printCvMappingRules(sb, "cvMapping Rules with valid Xpath that have not collected data",
				this.getCvRulesValidXpath());
		printCvMappingRules(sb, "cvMapping Rules with invalid Xpath", this.getCvRulesInvalidXpath());
		printCvMappingRules(sb, "cvMapping Rules that haven't been run",
				this.getCvRulesNotChecked());
		printObjectRules(sb, "Valid object rules", this.getObjectRulesValid());
		printObjectRules(sb, "Invalid object rules", this.getObjectRulesInvalid());
		printObjectRules(sb, "Object rules that haven't been run", this.getObjectRulesNotChecked());
		return sb.toString();
	}

	public void setRuleAsInvalid(String ruleId) {
		ObjectRule objectRule = getObjectRuleById(ruleId);
		if (objectRule != null) {
			// add to invalid rules
			if (getObjectRuleById(ruleId, this.objectRulesInvalid) == null)
				this.objectRulesInvalid.put(objectRule.getId(), objectRule);
			// remove from the other collections
			if (getObjectRuleById(ruleId, this.objectRulesNotChecked) != null)
				this.objectRulesNotChecked.remove(objectRule.getId());
			if (getObjectRuleById(ruleId, this.objectRulesValid) != null)
				this.objectRulesValid.remove(objectRule.getId());
		}
		CvRule cvRule = getCvRuleById(ruleId);
		if (cvRule != null) {
			// add to invalid rules
			if (getCvRuleById(ruleId, this.getCvRulesInvalidXpath()) == null)
				this.getCvRulesNotChecked().add(cvRule);
			// remove from the other collections
			if (getCvRuleById(ruleId, this.getCvRulesNotChecked()) != null)
				this.getCvRulesNotChecked().remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValid()) != null)
				this.getCvRulesValid().remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValidXpath()) != null)
				this.getCvRulesValidXpath().remove(objectRule);
		}

	}

	// UTIL
	private void addToMap(ObjectRule objectRule, HashMap<String, ObjectRule> map) {
		if (!map.containsKey(objectRule.getId()))
			map.put(objectRule.getId(), objectRule);
	}

	private void addToMap(Collection<ObjectRule> objectRules, HashMap<String, ObjectRule> map) {
		for (ObjectRule objectRule : objectRules) {
			this.addToMap(objectRule, map);
		}
	}
}
