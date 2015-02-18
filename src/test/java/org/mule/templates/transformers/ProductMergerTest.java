/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.transformers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.utils.VariableNames;

@RunWith(MockitoJUnitRunner.class)
public class ProductMergerTest {

	@Mock
	private MuleContext muleContext;

	static List<Map<String, String >> prepareSFDCProducts(){
		List<Map<String, String>> sfdcProducts = new ArrayList<Map<String,String>>();

		Map<String, String> account0Salesforce = new HashMap<String, String>();
		account0Salesforce.put(VariableNames.ID, "0");
		account0Salesforce.put(VariableNames.NAME, "Sony");
		sfdcProducts.add(account0Salesforce);

		Map<String, String> account1Salesforce = new HashMap<String, String>();
		account1Salesforce.put(VariableNames.ID, "1");
		account1Salesforce.put(VariableNames.NAME, "Generica");
		sfdcProducts.add(account1Salesforce);
		return sfdcProducts;
	}
	
	static List<Map<String, String >> prepareSAPProducts(){
		List<Map<String, String>> sapProducts = new ArrayList<Map<String,String>>();
		
		Map<String, String> account1Siebel = new HashMap<String, String>();
		account1Siebel.put(VariableNames.ID, "1");
		account1Siebel.put(VariableNames.NAME, "Generica");
		sapProducts.add(account1Siebel);

		Map<String, String> account2Siebel = new HashMap<String, String>();
		account2Siebel.put("Id", "2");
		account2Siebel.put(VariableNames.NAME, "Global Voltage");
		account2Siebel.put("Industry", "Energetic");
		account2Siebel.put("NumberOfEmployees", "4160");
		sapProducts.add(account2Siebel);
		return sapProducts;
	}
	
	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> sfdcList = prepareSFDCProducts();
		List<Map<String, String>> sapList = prepareSAPProducts();

		ProductMerger oppMerge = new ProductMerger();
		List<Map<String, String>> mergedList = oppMerge.mergeList(sfdcList, sapList);

		assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);
	}

	 static List<Map<String, String>> createExpectedList() {
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
