package psidev.psi.ms;

import java.util.ArrayList;
import java.util.Collection;

import psidev.psi.tools.validator.ValidatorMessage;
import psidev.psi.tools.validator.rules.Rule;
import psidev.psi.tools.validator.rules.codedrule.ObjectRule;
import psidev.psi.tools.validator.rules.cvmapping.CvRule;
import psidev.psi.tools.validator.rules.cvmapping.MappingRuleStatus;
import psidev.psi.tools.validator.util.ValidatorReport;

public class ExtendedValidatorReport extends ValidatorReport {

	private static final String NEW_LINE = System.getProperty("line.separator");;
	Collection<ObjectRule> objectRulesNotChecked = new ArrayList<ObjectRule>();
	Collection<ObjectRule> objectRulesValid = new ArrayList<ObjectRule>();
	Collection<ObjectRule> objectRulesInvalid = new ArrayList<ObjectRule>();

	public ExtendedValidatorReport(Collection<ObjectRule> objectRules) {
		super(new ArrayList<CvRule>());
		this.objectRulesNotChecked.addAll(objectRules);
	}

	public Collection<ObjectRule> getObjectRulesNotChecked() {
		return objectRulesNotChecked;
	}

	public Collection<ObjectRule> getObjectRulesValid() {
		return objectRulesValid;
	}

	public Collection<ObjectRule> getObjectRulesInvalid() {
		return objectRulesInvalid;
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
		this.objectRulesNotChecked.remove(rule);

		// if valid, add to the list of valid rules
		if (valid) {
			if (!this.objectRulesValid.contains(rule))
				this.objectRulesValid.add(rule);
		} else {
			if (!this.objectRulesInvalid.contains(rule))
				this.objectRulesInvalid.add(rule);
			if (this.objectRulesValid.contains(rule))
				this.objectRulesValid.remove(rule);
		}
	}

	public void setRuleAsSkipped(String ruleId) {
		ObjectRule objectRule = getObjectRuleById(ruleId);
		if (objectRule != null) {
			// add to NotChecked rules
			if (getObjectRuleById(ruleId, this.objectRulesNotChecked) == null)
				this.objectRulesNotChecked.add(objectRule);
			// remove from the other collections
			if (getObjectRuleById(ruleId, this.objectRulesInvalid) != null)
				this.objectRulesInvalid.remove(objectRule);
			if (getObjectRuleById(ruleId, this.objectRulesValid) != null)
				this.objectRulesValid.remove(objectRule);
		}
		CvRule cvRule = getCvRuleById(ruleId);
		if (cvRule != null) {
			// add to NotChecked rules
			if (getCvRuleById(ruleId, this.getCvRulesNotChecked()) == null)
				this.getCvRulesNotChecked().add(cvRule);
			// remove from the other collections
			if (getCvRuleById(ruleId, this.getCvRulesInvalidXpath()) != null)
				this.objectRulesValid.remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValid()) != null)
				this.objectRulesValid.remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValidXpath()) != null)
				this.objectRulesValid.remove(objectRule);
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
	 * @param rules
	 * @return
	 */
	private ObjectRule getObjectRuleById(String ruleId, Collection<ObjectRule> rules) {
		for (ObjectRule objectRule : rules) {
			if (objectRule.getId().equals(ruleId))
				return objectRule;
		}
		return null;
	}

	/**
	 * Search for a cvRule in all collections in the class
	 * 
	 * @param ruleId
	 * @return
	 */
	private CvRule getCvRuleById(String ruleId) {
		for (CvRule cvRule : this.getCvRulesInvalidXpath()) {
			if (cvRule.getId().equals(ruleId))
				return cvRule;
		}
		for (CvRule cvRule : this.getCvRulesNotChecked()) {
			if (cvRule.getId().equals(ruleId))
				return cvRule;
		}
		for (CvRule cvRule : this.getCvRulesValid()) {
			if (cvRule.getId().equals(ruleId))
				return cvRule;
		}
		for (CvRule cvRule : this.getCvRulesValidXpath()) {
			if (cvRule.getId().equals(ruleId))
				return cvRule;
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
		for (ObjectRule objectRule : this.objectRulesInvalid) {
			if (objectRule.getId().equals(ruleId))
				return objectRule;
		}
		for (ObjectRule objectRule : this.objectRulesNotChecked) {
			if (objectRule.getId().equals(ruleId))
				return objectRule;
		}
		for (ObjectRule objectRule : this.objectRulesValid) {
			if (objectRule.getId().equals(ruleId))
				return objectRule;
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
		printObjectRules(sb, "Valid object rules", this.objectRulesValid);
		printObjectRules(sb, "Invalid object rules", this.getObjectRulesInvalid());
		printObjectRules(sb, "Object rules that haven't been run", this.getObjectRulesNotChecked());
		return sb.toString();
	}

	public void setRuleAsInvalid(String ruleId) {
		ObjectRule objectRule = getObjectRuleById(ruleId);
		if (objectRule != null) {
			// add to invalid rules
			if (getObjectRuleById(ruleId, this.objectRulesInvalid) == null)
				this.objectRulesNotChecked.add(objectRule);
			// remove from the other collections
			if (getObjectRuleById(ruleId, this.objectRulesNotChecked) != null)
				this.objectRulesInvalid.remove(objectRule);
			if (getObjectRuleById(ruleId, this.objectRulesValid) != null)
				this.objectRulesValid.remove(objectRule);
		}
		CvRule cvRule = getCvRuleById(ruleId);
		if (cvRule != null) {
			// add to invalid rules
			if (getCvRuleById(ruleId, this.getCvRulesInvalidXpath()) == null)
				this.getCvRulesNotChecked().add(cvRule);
			// remove from the other collections
			if (getCvRuleById(ruleId, this.getCvRulesNotChecked()) != null)
				this.objectRulesValid.remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValid()) != null)
				this.objectRulesValid.remove(objectRule);
			if (getCvRuleById(ruleId, this.getCvRulesValidXpath()) != null)
				this.objectRulesValid.remove(objectRule);
		}

	}
}
