/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.transformers;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.templates.utils.VariableNames;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;

/**
 * This transformer will sort a list of map defining a weight for each map base
 * on the value of its keys.
 * 
 * @author damian.sima
 * @author martin
 */
public final class SortProductList extends AbstractMessageTransformer {
	
	private static final Comparator<Map<String, String>> PRODUCT_MAP_COMPARATOR = new Comparator<Map<String, String>>() {

		@Override
		public int compare(Map<String, String> product1, Map<String, String> product2) {
			String key1 = buildKey(product1);
			String key2 = buildKey(product2);

			return key1.compareTo(key2);
		}

		private String buildKey(Map<String, String> product) {
			StringBuilder key = new StringBuilder();

			if (StringUtils.isNotBlank(product.get(VariableNames.ID_IN_SALESFORCE)) && StringUtils.isNotBlank(product.get(VariableNames.ID_IN_SAP))) {
				key.append("~~~").append(product.get(VariableNames.IDENTITY_FIELD_KEY));
			}

			if (StringUtils.isNotBlank(product.get(VariableNames.ID_IN_SALESFORCE)) && StringUtils.isBlank(product.get(VariableNames.ID_IN_SAP))) {
				key.append("~").append(product.get(VariableNames.IDENTITY_FIELD_KEY));
			}

			if (StringUtils.isBlank(product.get(VariableNames.ID_IN_SALESFORCE)) && StringUtils.isNotBlank(product.get(VariableNames.ID_IN_SAP))) {
				key.append("~~").append(product.get(VariableNames.IDENTITY_FIELD_KEY));
			}

			return key.toString();
		}

	};

	@SuppressWarnings("unchecked")
	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> sortedUsersList = Lists.newArrayList((Iterator<Map<String, String>>) message.getPayload());

		Collections.sort(sortedUsersList, PRODUCT_MAP_COMPARATOR);

		return sortedUsersList;
	}
	
}
