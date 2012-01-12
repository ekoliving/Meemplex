/*
 * @(#)Dependency.java
 * Created on 23/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;

import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.features.util.ClassHelper;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;



/**
 * <code>Dependency</code> represents a dependency between two meem facets.
 * <p>
 * @author Kin Wong
 */
public class Dependency extends ConnectionElement {
	private static final long serialVersionUID = 6424227717462161145L;

	static public String ID_ATTRIBUTE = "attribute";
	private DependencyAttribute attribute;
	
	/**
	 * Constructs an instance of <code>Dependency</code>.<p>
	 * @param attribute
	 */
	public Dependency(DependencyAttribute attribute) {
			this.attribute = (DependencyAttribute)attribute.clone();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return getAttributekey();
	}
	
	/*
	 * Get the dependency attribute key.<p>
	 */
	public Serializable getAttributekey() {
		return attribute.getKey();		
	}
	
	public DependencyAttribute getAttribute() {
		return attribute;
	}
	
	public void updateAttribute(DependencyAttribute attribute) {
		if(!this.attribute.getKey().equals(attribute.getKey())) return;
		if(this.attribute.contentEquals(attribute)) return; // Nothing to change
		
		this.attribute = (DependencyAttribute)attribute.clone();
		firePropertyChange(ID_ATTRIBUTE, null, this.attribute);
	}

	public boolean isStrong() {
		DependencyType type = getAttribute().getDependencyType();
		return 	type.equals(DependencyType.STRONG) || 
						type.equals(DependencyType.STRONG_MANY);
	}
	
	public boolean isWeak() {
		DependencyType type = getAttribute().getDependencyType();
		return 	type.equals(DependencyType.WEAK) || 
						type.equals(DependencyType.WEAK_MANY);
	}

	public boolean isSingle() {
		DependencyType type = getAttribute().getDependencyType();
		return 	type.equals(DependencyType.STRONG) || 
						type.equals(DependencyType.WEAK);
	}
	
	public boolean isMultiple() {
		DependencyType type = getAttribute().getDependencyType();
		return 	type.equals(DependencyType.STRONG_MANY) || 
						type.equals(DependencyType.WEAK_MANY);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		Facet sourceFacet = getSourceFacet();
		if(sourceFacet == null) return "";
		return ClassHelper.
				getClassNameFromFullName(sourceFacet.getAttribute().getInterfaceName()) +
			"::" + 
			sourceFacet.getName();
	}
	
	public Meem getSourceMeem() {
		if(getSourceFacet() != null) return getSourceFacet().getMeem();
		return null;
	}
	/**
	 * Gets the shorten version of the dependency name.
	 * <p>
	 * @return String The shorten version of the dependency name.
	 */
	public String getShortName() {
		Facet sourceFacet = getSourceFacet();
		if(sourceFacet == null)	 return "";
		return sourceFacet.getName();
	}
	/**
	 * Gets the source facet of this dependency.<p>
	 * @return Facet The source facet of this dependency.
	 */
	public Facet getSourceFacet() {
		return (Facet)getSource();
	}
	
	/**
	 * Sets the source facet of this dependency.<p>
	 * <p>
	 * @param facet The source facet of this dependency.
	 */
	public void setSourceFacet(Facet facet) {
		setSource(facet);
	}
}
