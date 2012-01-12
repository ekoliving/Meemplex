/*
 * @(#)Category.java
 * Created on 25/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection;


/**
 * <code>Category</code> represents a meem that expose category facet. 
 * It derives from Meem and is extended to support "entry of" connectivity 
 * from other meems.
 * <p>
 * @author Kin Wong
 */
public class Category extends Meem {
	private static final long serialVersionUID = 6424227717462161145L;

	static public String ID_CATEGORY_ENTRY = "category entry";
	static public String ID_DEPENDENCY_TARGET = "dependency target";
	
	private HashMap entryOfs;			// Entries of this Category (Target)
	private HashMap dependencies;	// Dependency (Many) of this Category (target)
	
	/**
	 * Constructs an instance of <code>Category</code>.
	 * <p>
	 * @param proxy The category proxy associates with this category.
	 */
	public Category(MeemClientProxy proxy) {
		super(proxy);
	}
	
	/**
	 * Gets all meem entries of this category as a collection.
	 * @return Collection All meem entries of this category as a collection.
	 */
	public Collection getMeemEntries() {
		if(entryOfs == null) return Collections.EMPTY_LIST;
		return entryOfs.values();
	}
	
	public Collection getDependencies() {
		if(dependencies == null) return Collections.EMPTY_LIST;
		return dependencies.values();
	}
	
	public EntryOf getEntry(Object id) {
		if(entryOfs == null) return null;
		return (EntryOf)entryOfs.get(id);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#connectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void connectTarget(IConnection connection) throws Exception {
		if(connection instanceof EntryOf) {
			// Connect to Category Entry as Target
			EntryOf entryOf = (EntryOf)connection;
			if(entryOfs == null) entryOfs = new HashMap();
			entryOfs.put(entryOf.getId(), entryOf);
			firePropertyChange(ID_CATEGORY_ENTRY, null, entryOf);
		}
		else
		if(connection instanceof Dependency){
			// Connect to Dependency as Target
			Dependency dependency = (Dependency)connection;
			if(dependencies == null) dependencies = new HashMap();
			dependencies.put(dependency.getId(), dependency);
			firePropertyChange(ID_DEPENDENCY_TARGET, null, dependency);
		}
		else
		throw new Exception("Invalid target connection type: " + connection);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#disconnectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void disconnectTarget(IConnection connection) {
		if(connection == null) return;
		
		if(connection instanceof EntryOf) {
			if(entryOfs != null) {
				EntryOf entryOf = (EntryOf)connection;
				if(null != entryOfs.remove(entryOf.getId())) {
					firePropertyChange(ID_CATEGORY_ENTRY, entryOf, null);
					if(entryOfs.isEmpty()) entryOfs = null;
				}
			}
		}
		else
		if(connection instanceof Dependency) {
			if(dependencies != null) {
				Dependency dependency = (Dependency)connection;
				if(null != dependencies.remove(dependency.getId())) {
					firePropertyChange(ID_DEPENDENCY_TARGET, dependency, null);
					if(dependencies.isEmpty()) dependencies = null;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem#getTargetConnections()
	 */
	public List getTargetConnections() {
		List targets = new ArrayList();
		if(entryOfs != null) targets.addAll(entryOfs.values());
		if(dependencies != null) targets.addAll(dependencies.values());
		return targets;
	}
}
