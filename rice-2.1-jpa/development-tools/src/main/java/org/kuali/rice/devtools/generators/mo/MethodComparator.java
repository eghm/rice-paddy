package org.kuali.rice.devtools.generators.mo;

import java.util.Comparator;

public class MethodComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		String f1 = methodName(o1);
		String f2 = methodName(o2);
		return f1.compareTo(f2);
	}

	protected String methodName(String signature) {
		String method = signature.substring(0, signature.indexOf("(")).trim();
		method = method.substring(method.lastIndexOf(" "), method.length()).trim();
		return method;
	}
}
