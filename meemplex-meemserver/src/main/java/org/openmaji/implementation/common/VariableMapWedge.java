/*
 * @(#)VariableMapWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openmaji.common.Mergeable;
import org.openmaji.common.VariableMap;
import org.openmaji.common.VariableMapClient;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class VariableMapWedge implements VariableMap, Wedge {

	public Map<Serializable, Serializable> map = new HashMap<Serializable, Serializable>();

    public VariableMapClient variableMapClient;
    public final ContentProvider<VariableMapClient> variableMapClientProvider = new ContentProvider<VariableMapClient>() {
        public void sendContent(VariableMapClient client, Filter filter) {
            Set<Entry<Serializable, Serializable>> entrySet = map.entrySet();
            
            Entry<Serializable, Serializable>[] entries = new Entry[entrySet.size()];
            entrySet.toArray(entries);
            VariableMapEntry[] variableMapEntries = new VariableMapEntry[entries.length];
            for (int i = 0; i < entries.length; i++) {
            	variableMapEntries[i] = new VariableMapEntry(entries[i].getKey(), entries[i].getValue());
            }
            client.changed(variableMapEntries);
        }
    };

	/**
	 * @see org.openmaji.common.VariableMap#remove(Serializable)
	 */
	public void remove(Serializable key) {
		if (map.remove(key) != null) {
			variableMapClient.removed(key);
		}
	}

	/**
	 * @see org.openmaji.common.VariableMap#update(Serializable, Serializable)
	 */
	public void update(Serializable key, Serializable value) {
		map.put(key, value);
		Map.Entry<Serializable, Serializable>[] entry = new VariableMapEntry[]{new VariableMapEntry(key, value)};
		variableMapClient.changed(entry);
	}
	
	/**
	 * @see org.openmaji.common.VariableMap#merge(Serializable, Serializable)
	 */
	public void merge(Serializable key, Serializable delta) {
		Serializable value = map.get(key);
		if(value == null) return;
		
		if(!(value instanceof Mergeable)) return;
		Mergeable mergeable = (Mergeable)value;
		if(mergeable.merge(delta)) {
			Map.Entry<Serializable, Serializable>[] entry = new VariableMapEntry[]{new VariableMapEntry(key, value)};
			variableMapClient.changed(entry);
		}
	}
	
	private static final class VariableMapEntry implements Map.Entry<Serializable, Serializable>, Serializable {
		private static final long serialVersionUID = 6424227717462161145L;
		
		private Serializable key;
		private Serializable value;
		
		public VariableMapEntry(Serializable key, Serializable value) {
			this.key = key;
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getKey()
		 */
		public Serializable getKey() {
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getValue()
		 */
		public Serializable getValue() {
			return value;
		}

		/** (non-Javadoc)
		 * @see java.util.Map.Entry#setValue(Object)
		 */
		public Serializable setValue(Serializable value) {
			Serializable oldValue = this.value;
			this.value = value;
			return oldValue;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#equals(java.lang.Object)
		 */
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof VariableMapEntry)) return false;
			VariableMapEntry that = (VariableMapEntry)o;
			return key.equals(that.key) && value.equals(that.value);
		}
		
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#hashCode()
		 */
		public int hashCode() {
			return key.hashCode() + value.hashCode();
		}
	}
}
