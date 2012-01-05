package psidev.psi.ms;

import java.util.ArrayList;
import java.util.List;

import psidev.psi.tools.validator.Context;

/**
 * A context which cluster the different contexts for a same error message
 * 
 */
public class ClusteredContext extends Context {

	List<Context> contexts = new ArrayList<Context>();

	public ClusteredContext(String context) {
		super(context);
	}

	public ClusteredContext() {
		super(null);
	}

	public List<Context> getContexts() {
		return contexts;
	}

	public int getNumberOfContexts() {
		return contexts.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(128);

		for (Context context : contexts) {

			sb.append(context.toString());
			sb.append("\n");
		}

		sb.append(")");
		return sb.toString();
	}
}
