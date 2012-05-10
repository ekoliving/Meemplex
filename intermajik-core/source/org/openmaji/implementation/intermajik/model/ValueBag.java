/*
 * @(#)ValueBag.java
 * Created on 4/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.intermajik.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.common.Mergeable;

/**
 * <code>ValueBag</code> represents a generic bag of variables that can be
 * collectively identified with an <code>ElementPath</code>.
 * <p>
 * <code>ValueBag</code> can be used to pass group of variables in a 
 * distributed manner with <code>VariableMap</code> and 
 * <code>VariableMapClient</code>.
 * <p>
 * @see org.openmaji.implementation.intermajik.model.ElementPath
 * @see org.openmaji.common.VariableMap
 * @see org.openmaji.common.VariableMapClient
 * @author Kin Wong
 */
public class ValueBag implements Serializable, Mergeable {
	private static final long serialVersionUID = 6424227717462161145L;

	private final Map<Serializable, Serializable> valueMap = new HashMap<Serializable, Serializable>();

	/**
	 * Constructs an instance of <code>ValueBag</code>.
	 * <p>
	 */
	public ValueBag() {
	}

	public boolean isEmpty() {
		return valueMap.isEmpty();
	}

	/**
	 * Adds a value and its corresponding id to this variable bag.
	 * @param idValue
	 * @param value
	 */
	public void add(Serializable idValue, Serializable value) {
		if (idValue == null) {
			return;
		}
		valueMap.put(idValue, value);
	}

	public boolean merge(ValueBag bag) {
		Iterator<Serializable> it = bag.getIds();
		if (it.hasNext()) {
			do {
				Serializable id = it.next();
				valueMap.put(id, bag.get(id));
			} while ((it.hasNext()));
			return true;
		}
		return false;
	}

	public Serializable get(Serializable idValue) {
		if (idValue == null) {
			return null;
		}
		return valueMap.get(idValue);
	}

	/**
	 * Returns an iterator that can be used to iterate through all variable ids.
	 * @return Iterator An iterator that can be used to iterate through all 
	 * variable ids.
	 */
	public Iterator<Serializable> getIds() {
		return valueMap.keySet().iterator();
	}

	/* (non-Javadoc)
	 * @see net.majitek.domain.common.Mergeable#merge(java.lang.Object)
	 */
	public boolean merge(Object delta) {
		if (!(delta instanceof ValueBag)) {
			return false;
		}
		return merge((ValueBag) delta);
	}

	public String toString() {
		return valueMap.toString();
	}
}
