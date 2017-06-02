package org.kuali.rice.devtools.generators.mo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.devtools.generators.mo.ImmutableJaxbGenerator.FieldModel;


public class StudentPropOrderComparator implements Comparator {

	private static final String[] PROP_ORDER_FIRST =  new String[]{"key", "id", "typeKey", "stateKey", "name", "descr", "code",
		"holdIssueId", "personId",  "reqCompFields", "naturalLanguageTranslation", "effectiveDate", "expirationDate", "isSuccess", "message",
		"applicationEffectiveTermId", "applicationExpirationTermId",
		"organizationId", "isHoldIssueTermBased", "firstAppliedDate", "lastAppliedDate","firstApplicationTermId", "lastApplicationTermId",
        "maintainHistoryOfApplicationOfHold","holdCode", "startDate", "endDate", "messageTemplateId", "bulkMessageRequestId",
        "messageCategoryKey", "defaultTemplate", "messageParameterIds", "contactTypeKey", "senderPrincipalId", "recipientPersonId", "ccPersonIds", "sendDate",
		"expireDate", "messageSubject", "contentTypeKey", "messageContent"};
	private static final String[] PROP_ORDER_LAST =  new String[]{"meta", "attributes", "_futureElements"};
	private static final List PROPS_ORDER_FIRST = new LinkedList(Arrays.asList(PROP_ORDER_FIRST)); 
	private static final List PROPS_ORDER_LAST = new LinkedList(Arrays.asList(PROP_ORDER_LAST)); 
	
	@Override
	public int compare(Object o1, Object o2) {
		String f1 = ((FieldModel)o1).fieldName;
		String f2 = ((FieldModel)o2).fieldName;
		if (PROPS_ORDER_FIRST.contains(f1) && PROPS_ORDER_FIRST.contains(f2)) {
			int result = (PROPS_ORDER_FIRST.indexOf(f1) < PROPS_ORDER_FIRST.indexOf(f2)) ? -1 : 1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}
		if (PROPS_ORDER_FIRST.contains(f1) && !PROPS_ORDER_FIRST.contains(f2)) {
			int result = -1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}		
		if (!PROPS_ORDER_FIRST.contains(f1) && PROPS_ORDER_FIRST.contains(f2)) {
			int result = 1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}

		if (PROPS_ORDER_LAST.contains(f1) && PROPS_ORDER_LAST.contains(f2)) {
			int result = (PROPS_ORDER_LAST.indexOf(f1) < PROPS_ORDER_LAST.indexOf(f2)) ? -1 : 1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}
		if (PROPS_ORDER_LAST.contains(f1) && !PROPS_ORDER_LAST.contains(f2)) {
			int result = 1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}		
		if (!PROPS_ORDER_LAST.contains(f1) && PROPS_ORDER_LAST.contains(f2)) {
			int result = -1;
//			System.out.println(f1 + " compared to " + f2 + " result returned " + result);
			return result;
		}
		
		return 0;
	}
}
