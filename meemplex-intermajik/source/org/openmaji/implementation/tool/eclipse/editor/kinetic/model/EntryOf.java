/*
 * @(#)EntryOf.java
 * Created on 27/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;

import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.CategoryEntry;



/**
 * <code>EntryOf</code> represents the entry of a meem in a category.
 * The roles are shown as following digram: <p>
 *   Meem -> is an entry of -> Category <p>
 * (Source)                    (Target) <p>
 * @author Kin Wong
 */

public class EntryOf extends ConnectionElement {
	private static final long serialVersionUID = 6424227717462161145L;

	private CategoryEntry entry;
	
	/**
	 * Constructs a disconnected entry of connection.
	 */
	public EntryOf(CategoryEntry categoryEntry) {
		entry = categoryEntry;
	}
	
	public EntryOf() {
	}
	
	public void setCategoryEntry(CategoryEntry categoryEntry) {
		entry = categoryEntry;
	}

	public Serializable getId() {
		return entry;
	}
	
	/**
	 * Gets the category entry of this entry-of.
	 * @return CategoryEntry The category entry of this entry-of.
	 */
	public CategoryEntry getCategoryEntry() {
		return entry;
	}
	
	public void setName(String name) {
		if(entry.getName().equals(name)) return;
		entry = entry.rename(name);
		firePropertyChange(ID_NAME, null, entry.getName());
	}
	
	public void setMeemPath(MeemPath meemPath) {
		entry = entry.changeMeem(SecurityManager.getInstance().getGateway().getMeem(meemPath));
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		return entry.getName();
	}

	/**
	 * Gets the meem source of this entry-of connection.
	 * @return Meem The meem source of this entry-of connection.
	 */
	public Meem getMeem() {
		return (Meem)getSource();
	}
	/**
	 * Gets the category target of this entry-of connection.
	 * @return Category The category target of this entry-of connection.
	 */
	public Category getCategory() {
		return (Category)getTarget();
	}
	/**
	 * Sets the meem of this entry of connection.
	 * @param meem The meem as the source of this entry of connection.
	 */
	public void setMeem(Meem meem) {
		setSource(meem);
	}
	/**
	 * Sets the category of this entry of connection.
	 * @param category The category as the target of this entry of connection.
	 */
	public void setCategory(Category category) {
		setTarget(category);
	}
	
	/**
	 * Overridden to 
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection#setSource(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable)
	 */	
	public void setSource(IConnectable connectable) {
		Meem meem = (Meem)connectable;
		if (meem == null)
		{
			//entry = entry.changeMeem(null);
		}
		else
		{
			entry = entry.changeMeem(SecurityManager.getInstance().getGateway().getMeem(meem.getMeemPath()));
		}
		super.setSource(connectable);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement#setTarget(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable)
	 */
	public void setTarget(IConnectable connectable) {
		super.setTarget(connectable);
	}

	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement#isValidSource(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable, org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable)
	 */
	public boolean isValidSource(IConnectable target, IConnectable source) {
		if(!(source instanceof Meem)) return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement#isValidTarget(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable, org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable)
	 */
	public boolean isValidTarget(IConnectable source, IConnectable target) {
		if(!(target instanceof Category)) return false;
		return true;
	}
}
