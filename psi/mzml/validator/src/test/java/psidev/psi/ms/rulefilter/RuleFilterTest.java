package psidev.psi.ms.rulefilter;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class RuleFilterTest {
	private static final String FILE_RULE_FILTER = "ruleFilterMIAPE.xml";

	@Test
	public void ruleFilterTest() {

		try {
			File file = new File(FILE_RULE_FILTER);
			final RuleFilterManager filterManager = new RuleFilterManager(file);
			filterManager.printRuleFilter();
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

}
