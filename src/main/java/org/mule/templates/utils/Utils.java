/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */
package org.mule.templates.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mule.api.MuleMessage;

import com.google.common.collect.Lists;

/**
 * 
 * @author martin
 *
 */
public final class Utils {
	
	private Utils() {
		throw new AssertionError();
	}

	public static List<Map<String, String>> buildList(MuleMessage message, String propertyName) {
		return buildList(message.getInvocationProperty(propertyName));
	}

	public static List<Map<String, String>> buildList(Object object) {
		Iterable<?> iterable = object instanceof Iterable ? ((Iterable<?>) object) : Lists.newArrayList((Iterator<?>) object);
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		for (Object item : iterable) {
			Map<?, ?> map = (Map<?, ?>) item;
			Map<String, String> out = new HashMap<String, String>();
			for (Entry<?, ?> entry : map.entrySet()) {
				out.put((String) entry.getKey(), (String) entry.getValue());
			}
			list.add(out);
		}
		
		return list;
	}

}
