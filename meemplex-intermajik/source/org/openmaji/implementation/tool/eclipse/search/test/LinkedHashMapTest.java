/*
 * @(#)LinkedHashMapTest.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.search.test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LinkedHashMapTest {
	
	public LinkedHashMapTest() {
		LRU m = new LRU(10);
		
		m.put("a", null);
		m.put("a/1", null);
		m.put("a/2", null);
		m.put("a/1/a", null);
		m.put("a/1/b", null);
		m.put("a/1/c", null);
		m.put("a/2/a", null);
		m.put("a/2/a/1", null);
		m.put("a/2/b", null);
		m.put("a/2/a/2", null);
		
		m.get("a");
		
		m.put("a/3/a/1", null);
		m.put("a/3/a/2", null);
		m.put("a/3/a/3", null);
		
		System.out.println("Size = " + m.size());
		
				
		for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry)i.next();
	
			System.out.println("entry = " + entry);	
			
			String key = (String)entry.getKey();
			
			if (key.startsWith("a/1"))
				i.remove();		
		}
		
		System.out.println("Size = " + m.size());
		
		for (Iterator i = m.keySet().iterator(); i.hasNext();) {
			String key = (String)i.next();
			
			System.out.println("key = " + key);			
		}
	}
	
	public static void main(String[] args) {
//		LinkedHashMapTest test =
		new LinkedHashMapTest();		
	}
	
	private final class LRU extends LinkedHashMap {
		private static final long serialVersionUID = 6424227717462161145L;

		int maxEntries = 10;
		
		public LRU(int maxEntries) {
			super(10, 0.75f, true);
			this.maxEntries = maxEntries;
		}

		protected boolean removeEldestEntry(Entry eldest) {
			if (size() > maxEntries)
				return true;
			return false;
		}
	}
	
}
