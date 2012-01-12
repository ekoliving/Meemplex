/*
 * @(#)Diagram.java
 * Created on 29/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;

import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.meem.MeemPath;



/**
 * <code>Diagram</code> is an abstract class that represents a collection of 
 * meems interconnecting each others (Meem Graph) in a category. <p>
 * @author Kin Wong
 */

abstract public class Diagram extends ElementContainer {
	protected MeemClientProxy category;
	/**
	 * Constructs an instance of <code>Diagram</code>.
	 * <p>
	 * @param category The category proxy associates with this diagram.
	 */
	protected Diagram(MeemClientProxy category)	{
		this.category = category;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return category.getMeemPath();
	}
	
	public MeemClientProxy getProxy() {
		return category;
	}
 	
 	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		return category.getMeemPath().toString();
	}

	/**
	 * Gets the category proxy associates with this diagram.
	 * @return CategoryProxy The category facet proxy associates with this 
	 * diagram.
	 */
	public CategoryProxy getCategory() {
		return category.getCategoryProxy();
	}
	
	/**
	 * Finds the meem identified by the specified meempath in this diagram.
	 */
	public Meem findMeem(MeemPath meemPath) {
		return (Meem)findElement(meemPath);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer#isValidNewChild(java.lang.Object)
	 */
	public boolean isValidNewChild(Object child) {
		if(!super.isValidNewChild(child)) return false;
		return(	(child instanceof Meem) 	|| 
				(child instanceof Category) 		|| 
				(child instanceof MeemPlex));
	}
}
