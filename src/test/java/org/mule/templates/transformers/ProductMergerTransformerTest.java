/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.utils.Utils;
import org.mule.templates.utils.VariableNames;

@RunWith(MockitoJUnitRunner.class)
public class ProductMergerTransformerTest {

	@Mock
	private MuleContext muleContext;

	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> accountsSalesforce = new ArrayList<Map<String,String>>();

		Map<String, String> account0Salesforce = new HashMap<String, String>();
		account0Salesforce.put(VariableNames.ID, "0");
		account0Salesforce.put(VariableNames.NAME, "Sony");
		accountsSalesforce.add(account0Salesforce);

		Map<String, String> account1Salesforce = new HashMap<String, String>();
		account1Salesforce.put(VariableNames.ID, "1");
		account1Salesforce.put(VariableNames.NAME, "Generica");
		accountsSalesforce.add(account1Salesforce);
		
		List<Map<String, String>> accountsSiebel = new ArrayList<Map<String,String>>();
		
		Map<String, String> account1Siebel = new HashMap<String, String>();
		account1Siebel.put(VariableNames.ID, "1");
		account1Siebel.put(VariableNames.NAME, "Generica");
		accountsSiebel.add(account1Siebel);

		Map<String, String> account2Siebel = new HashMap<String, String>();
		account2Siebel.put("Id", "2");
		account2Siebel.put(VariableNames.NAME, "Global Voltage");
		account2Siebel.put("Industry", "Energetic");
		account2Siebel.put("NumberOfEmployees", "4160");
		accountsSiebel.add(account2Siebel);

		MuleMessage message = new DefaultMuleMessage(null, muleContext);
		message.setInvocationProperty(VariableNames.PRODUCTS_FROM_SALESFORCE, accountsSalesforce.iterator());
		message.setInvocationProperty(VariableNames.PRODUCTS_FROM_SAP, accountsSiebel.iterator());

		ProductMergerTransformer transformer = new ProductMergerTransformer();
		List<Map<String, String>> mergedList = Utils.buildList(transformer.transform(message, "UTF-8"));

		System.out.println(mergedList);
		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);
	}

	private List<Map<String, String>> createExpectedList() {
		Map<String, String> record0 = new HashMap<String, String>();
		record0.put(VariableNames.ID_IN_SALESFORCE, "0");
		record0.put(VariableNames.ID_IN_SAP, "");
		record0.put(VariableNames.NAME, "Sony");

		Map<String, String> record1 = new HashMap<String, String>();
		record1.put(VariableNames.ID_IN_SALESFORCE, "1");
		record1.put(VariableNames.ID_IN_SAP, "1");
		record1.put(VariableNames.NAME, "Generica");

		Map<String, String> record2 = new HashMap<String, String>();
		record2.put(VariableNames.ID_IN_SALESFORCE, "");
		record2.put(VariableNames.ID_IN_SAP, "2");
		record2.put(VariableNames.NAME, "Global Voltage");

		List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
		expectedList.add(record0);
		expectedList.add(record1);
		expectedList.add(record2);

		return expectedList;
	}

}
