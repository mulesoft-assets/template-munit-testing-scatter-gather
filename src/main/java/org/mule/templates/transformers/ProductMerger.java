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

import org.mule.templates.utils.VariableNames;

/**
 * This merge class will take two lists as input and create a third one that
 * will be the merge of the previous two. The identity of list's element is
 * defined by its Name.
 * 
 * @author damian.sima
 */
public class ProductMerger {

	private static final String EMPTY = "";

	/**
	 * The method will merge the products from the two lists creating a new one.
	 * 
	 * @param productsFromSalesforce
	 *            products from Salesforce
	 * @param productsFromSiebel
	 *            products from SAP
	 * @return a list with the merged content of the to input lists
	 */
	List<Map<String, String>> mergeList(List<Map<String, String>> productsFromSalesforce, List<Map<String, String>> productsFromSiebel) {
		List<Map<String, String>> mergedProductList = new ArrayList<Map<String, String>>();

		// Put all products from Salesforce in the merged contactList
		for (Map<String, String> productFromSalesforce : productsFromSalesforce) {
			Map<String, String> mergedProduct = createMergedProduct(productFromSalesforce);
			mergedProduct.put(VariableNames.ID_IN_SALESFORCE, productFromSalesforce.get(VariableNames.ID));
			mergedProductList.add(mergedProduct);
		}

		// Add the new products from Siebel and update the exiting ones
		for (Map<String, String> productFromSiebel : productsFromSiebel) {
			Map<String, String> productFromSalesforce = findProductInList(productFromSiebel.get(VariableNames.IDENTITY_FIELD_KEY), mergedProductList);
			if (productFromSalesforce != null) {
				productFromSalesforce.put(VariableNames.ID_IN_SAP, productFromSiebel.get(VariableNames.ID));
			} else {
				Map<String, String> mergedProduct = createMergedProduct(productFromSiebel);
				mergedProduct.put(VariableNames.ID_IN_SAP, productFromSiebel.get(VariableNames.ID));
				mergedProductList.add(mergedProduct);
			}

		}
		return mergedProductList;
	}

	private static Map<String, String> createMergedProduct(Map<String, String> product) {
		Map<String, String> mergedProduct = new HashMap<String, String>();
		mergedProduct.put(VariableNames.IDENTITY_FIELD_KEY, product.get(VariableNames.IDENTITY_FIELD_KEY));
		mergedProduct.put(VariableNames.ID_IN_SALESFORCE, EMPTY);
		mergedProduct.put(VariableNames.ID_IN_SAP, EMPTY);
		return mergedProduct;
	}

	private static Map<String, String> findProductInList(String productName, List<Map<String, String>> productList) {
		for (Map<String, String> product : productList) {
			String name = product.get(VariableNames.IDENTITY_FIELD_KEY);
			if (name != null && name.equals(productName)) {
				return product;
			}
		}
		return null;
	}

}
