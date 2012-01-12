/*
 * @(#)FixedSizeList.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic;

import java.util.LinkedList;

/**
 * @author mg
 */
public class FixedSizeList {

	private int size;
	private LinkedList list = new LinkedList();
	
	public FixedSizeList(int size) {
		this.size = size;		
	}

	public void add(Object element) {
		synchronized(list) {
			if (list.size() == size) {
				list.removeFirst();
			}
			list.addLast(element);
		}
	}
	
	protected Object[] getListArray(Object[] array) {
		synchronized(list) {
			return list.toArray(array);
		}
	}
	
	public void clear() {
		synchronized (list) {
			list.clear();
		}
	}
}
