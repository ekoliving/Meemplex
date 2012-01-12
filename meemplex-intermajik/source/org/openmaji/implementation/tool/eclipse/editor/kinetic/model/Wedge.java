/*
 * @(#)Wedge.java
 * Created on 16/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.editor.common.model.Collapsible;
import org.openmaji.meem.definition.WedgeAttribute;



/**
 * <code>Wedge</code>.
 * <p>
 * @author Kin Wong
 */
public class Wedge extends Collapsible {
	
	private static final long serialVersionUID = 6424227717462161145L;

	private WedgeAttribute attribute;

	/**
	 * Constructs an instance of <code>Wedge</code>.
	 * <p>
	 */
	public Wedge() {
	}
	
	/**
	 * Constructs an instance of <code>Wedge</code>.
	 * <p>
	 * @param attribute
	 */	
	public Wedge(WedgeAttribute attribute) {
		this.attribute = (WedgeAttribute)attribute.clone();
		setCollapse(true);
	}
	
	/**
	 * Checks whether the wedge is a sytem defined wedge.
	 * <p>
	 * @return boolean true is the wedge is a system wedge, false otherwise.
	 */
	public boolean isSystemWedge() {
		if(getAttribute() == null) return false;
		return getAttribute().isSystemWedge();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return getAttributeIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		WedgeAttribute attr = getAttribute();
		if(attr == null) return "";
		return attr.getIdentifier();
	}

	/**
	 * Gets the wedge identifier associated with this wedge.
	 * @return Object The wedge identifier associated with this wedge.
	 */
	public Serializable getAttributeIdentifier() {
		return attribute.getIdentifier();
	}
	
	/**
	 * Gets the meem of this wedge.
	 * @return Meem
	 */
	public Meem getMeem() {
		return (Meem)getParent();
	}
	
	/**
	 * Gets the wedge attribute associates with this wedge.
	 * @return WedgeAttribute The wedge attribute associates with this wedge.
	 */
	public WedgeAttribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Gets all dependencies in all facet that connects to this wedge as source.
	 * @param dependencyMap The map that returns all the dependencies.
	 */
	public void getOutputDependencies(Map dependencyMap) {
		Iterator it = getChildren().iterator();
		while (it.hasNext()) {
//			Facet facet = (Facet) it.next();
		//	facet.getOutputs(dependencyMap);
		}
	}
	
	public void innerSourceConnectionChange() {
		firePropertyChange(Meem.ID_INNER_SOURCE_CONNECTIONS, null, null);
		if(getMeem() != null) getMeem().innerSourceConnectionChange();
	}
	
	public void innerTargetConnectionChange() {
		firePropertyChange(Meem.ID_INNER_TARGET_CONNECTIONS, null, null);
		if(getMeem() != null) getMeem().innerTargetConnectionChange();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer#isValidNewChild(java.lang.Object)
	 */
	public boolean isValidNewChild(Object child) {
		if(!super.isValidNewChild(child)) return false;
		return (child instanceof Facet);
	}
}
