/*
 * @(#)MeemContent.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.meem.definition;

import java.io.Serializable;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

/**
 * MeemContent is the object that is stored when a meem is persisted. 
 * It consists of a Map of Maps, one entry per wedge. The Maps it contains 
 * are the field names and values to persist.<br> 
 * @author  mg
 * @version 1.0
 */
public class MeemContent implements Serializable{
	private static final long serialVersionUID = 8258125944041447316L;

	private Map<String, Map<String,Serializable>> wedges = new HashMap<String, Map<String,Serializable>>();

	public synchronized void addPersistentField(String wedgeIdentifier, String persistentFieldIdentifier, Serializable persistentFieldValue) {
		Map<String,Serializable> wedgeFieldsMap = wedges.get(wedgeIdentifier);
		if (wedgeFieldsMap == null) {
			wedgeFieldsMap = new HashMap<String,Serializable>();
			wedges.put(wedgeIdentifier, wedgeFieldsMap);
		}
		wedgeFieldsMap.put(persistentFieldIdentifier, persistentFieldValue);
	}

	/**
	 * Used to retrieve the list of wedges specified in the content map. 
	 * The collection contents are used as keys for {@link #getPersistentFields(String) getPersistentFields(String)} <br>
	 * @return Unmodifiable collection of Strings 
	 */
	public synchronized Collection<String> getWedgeIdentifiers() {
		return new ArrayList<String>(wedges.keySet());
	}
	
	/**
	 * 
	 * @param wedgeIdentifier
	 * @return Unmodifiable map of field names and values
	 */
	public synchronized Map<String, Serializable> getPersistentFields(String wedgeIdentifier) {
		Map<String, Serializable> fields = wedges.get(wedgeIdentifier);
		return fields == null ? null : new HashMap<String, Serializable>(fields);
	}
}
