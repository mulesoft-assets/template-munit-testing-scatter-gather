/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.utils.VariableNames;
import org.mule.util.UUID;

import com.google.common.collect.Lists;
import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private List<Map<String, Object>> createdAccountsInSalesforce = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdAccountsInSap = new ArrayList<Map<String, Object>>();

	@BeforeClass
	public static void init() {
		System.setProperty("mail.subject", "Accounts Report");
		System.setProperty("mail.body", "Please find attached your Accounts Report");
		System.setProperty("attachment.name", "AccountsReport.csv");
	}
	
	@Before
	public void setUp() throws Exception {
		createAccounts();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestAccountFromSandBox(createdAccountsInSalesforce, "deleteAccountsFromSalesforceFlow");
		deleteTestAccountFromSandBox(createdAccountsInSap, "deleteAccountsFromSapFlow");
	}

	@Override
	protected String getConfigResources() {
		return super.getConfigResources() + getTestFlows();
	}

	private void createAccounts() throws Exception {
		SubflowInterceptingChainLifecycleWrapper createAccountInSalesforceFlow = getSubFlow("createAccountsInSalesforceFlow");
		createAccountInSalesforceFlow.initialise();

		Map<String, Object> salesforceAccount = new HashMap<String, Object>();
		salesforceAccount.put("Name", "Name_Salesforce_0_" + TEMPLATE_NAME + "_" + UUID.getUUID());
		createdAccountsInSalesforce.add(salesforceAccount);

		MuleEvent event = createAccountInSalesforceFlow.process(getTestEvent(createdAccountsInSalesforce, MessageExchangePattern.REQUEST_RESPONSE));
		List<?> results = (List<?>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdAccountsInSalesforce.get(i).put(VariableNames.ID, ((SaveResult) results.get(i)).getId());
		}

		Map<String, Object> sapAccount = new HashMap<String, Object>();
		sapAccount.put(VariableNames.NAME, "Name_Sap_0_" + TEMPLATE_NAME + "_" + UUID.getUUID());
		sapAccount.put(VariableNames.ID, "s2s-prod-agg-" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX));
		createdAccountsInSap.add(sapAccount);

		runFlow("createAccountsInSapFlow", createdAccountsInSap);
	}

	protected void deleteTestAccountFromSandBox(List<Map<String, Object>> createdAccounts, String deleteFlow) throws Exception {
		List<String> idList = new ArrayList<String>();

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();
		for (Map<String, Object> c : createdAccounts) {
			idList.add((String) c.get(VariableNames.ID));
		}
		flow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGatherDataFlow() throws Exception {
		MuleEvent event = runFlow("gatherDataFlow");
		List<Map<String, String>> mergedList = Lists.newArrayList((Iterator<Map<String, String>>)event.getMessage().getPayload());
		
		Assert.assertTrue("There should be products from source A or source B.", mergedList.size() > 0);
	}

}
