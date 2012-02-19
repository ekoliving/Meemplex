/*
 * @(#)VariableMapProxy.java
 * Created on 1/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.openmaji.common.VariableMap;
import org.openmaji.common.VariableMapClient;
import org.openmaji.meem.Facet;



/**
 * <code>VariableMapProxy</code>.
 * <p>
 * @author Kin Wong
 */
public class VariableMapProxy extends FacetProxy implements VariableMap {
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(VariableMap.class, "variableMap"),
			new FacetOutboundSpecification(VariableMapClient.class, "variableMapClient"));

	private Map<Serializable, Serializable> variables;
	
	//=== Internal VariableMapClient =============================================
	public static class LocalVariableMapClient
		implements VariableMapClient
	{
		VariableMapProxy p;
		
		public LocalVariableMapClient(
			VariableMapProxy	p)
		{
			this.p = p;
		}

		public void removed(final Serializable key) {
			p.variables.remove(key);
			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() { p.fireRemoved(key); }
			});
		}
		
		/* (non-Javadoc)
		 * @see org.openmaji.common.VariableMapClient#changed(java.util.Map.Entry[])
		 */
		public void changed(final Entry<Serializable, Serializable>[] entries) {
			/*
			System.out.println("Change(s): " + entries.length);
			for(int i=0; i < entries.length; i++) {
				System.out.println("[" + i + "]" + entries[i].getKey().toString() + ", " + entries[i].getValue());
			}
			*/
			synchronized(p.variables) {
				for(int i=0; i < entries.length; i++)
				p.variables.put(entries[i].getKey(), entries[i].getValue());
			}

			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() { p.fireChanged(entries);}
			});
		}
	}
	
	private VariableMapClient variableMapClient;

	/**
	 * Constructs an instance of <code>VariableMapProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 * @param specs
	 */
	public VariableMapProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}

	public VariableMapProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		if (variableMapClient == null) {
			variableMapClient = new LocalVariableMapClient(this);			
		}

		return variableMapClient;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		variables = new HashMap<Serializable, Serializable>();
		variableMapClient = null;
	}
	
	/**
	 * Gets the value from key.<p>
	 * @return The value for the key, null if no value is defined for the key.
	 */
	public Object get(Object key) {
		return variables.get(key);
	}
	
	protected VariableMap getVariableMap() {
		return (VariableMap)getInboundReference();
	}
	
	//=== Client Management ======================================================
	/**
	 * Notifies all clients an update has occurred.
	 * @param key The key of the variable.
	 * @param value The value of the variable.
	 */
	protected void fireChanged(Map.Entry<Serializable, Serializable>[] entries) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			VariableMapClient client = (VariableMapClient)clients[i];
			client.changed(entries);
		}
	}
	
	/**
	 * Notifies all clients a variable has been removed.
	 * @param key The key of the variable.
	 */
	public void fireRemoved(Serializable key) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			VariableMapClient client = (VariableMapClient)clients[i];
			client.removed(key);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	synchronized protected void realizeClientContent(Object client) {
		VariableMapClient variableMapClient = (VariableMapClient)client;
		Set<Entry<Serializable, Serializable>> entrySet = variables.entrySet();
		Map.Entry<Serializable, Serializable>[] entries = (Map.Entry<Serializable, Serializable>[]) entrySet.toArray(new Map.Entry[0]);
		variableMapClient.changed(entries);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */	
	synchronized protected void clearClientContent(Object client) {
		VariableMapClient variableMapClient = (VariableMapClient)client;
		HashSet<Map.Entry<Serializable, Serializable>> entrySet = 
			new HashSet<Map.Entry<Serializable, Serializable>>(variables.entrySet());
		Iterator<Map.Entry<Serializable, Serializable>> it = entrySet.iterator();
		
		while(it.hasNext()) {
			Map.Entry<Serializable, Serializable> entry = it.next();
			variableMapClient.removed(entry.getKey());
		}
	}

	//=== External VariableMap ===================================================
	/* (non-Javadoc)
	 * @see org.openmaji.common.VariableMap#update(java.lang.Object, java.lang.Object)
	 */
	public void update(final Serializable key, final Serializable value) {
		if(isReadOnly()) return;
		
		getVariableMap().update(key, value);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.common.VariableMap#remove(java.lang.Object)
	 */
	public void remove(final Serializable key) {
		if(isReadOnly()) return;
		
		getVariableMap().remove(key);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.common.VariableMap#merge(java.lang.Object, java.lang.Object)
	 */
	public void merge(final Serializable key, final Serializable delta) {
		if(isReadOnly()) return;

		getVariableMap().merge(key, delta);
	}
	
	//=== Debug Helpers ==========================================================
	public synchronized void printAllKeys() {
		System.out.println("=== VariableMapProxy Keys ===");
		for (Iterator iter = variables.keySet().iterator(); iter.hasNext();) {
			System.out.println(iter.next().toString());
		}			
	}
}
