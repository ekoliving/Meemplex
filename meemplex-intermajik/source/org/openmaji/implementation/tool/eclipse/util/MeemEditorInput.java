/*
 * @(#)MeemEditorInput.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.openmaji.meem.MeemPath;


/**
 * @author mg
 * Created on 21/01/2003
 */
public class MeemEditorInput implements IEditorInput {
	
	private MeemPath meemPath;
	private String name;
	private int view = 0;
	
	public MeemEditorInput(MeemPath meemPath, int view) {
		this.meemPath = meemPath;
		name = meemPath.toString();
		this.view = view;
	}
	
	public MeemEditorInput(MeemPath meemPath) {
		this(meemPath, 0);
	}
	
	
	public int getViewOrdinal() {
		return view;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		
		if(!(obj instanceof MeemEditorInput)) return false;
		MeemEditorInput that = (MeemEditorInput)obj;
		if(view != that.view) return false;
		if(!meemPath.equals(that.meemPath)) return false;
		return name.equals(that.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return meemPath.hashCode() + name.hashCode() + view;
	}

	/**
	 * Returns whether the editor input exists.  
	 * <p>
	 * This method is primarily used to determine if an editor input should 
	 * appear in the "File Most Recently Used" menu.  An editor input will appear 
	 * in the list until the return value of <code>exists</code> becomes 
	 * <code>false</code> or it drops off the bottom of the list.
	 *
	 * @return <code>true</code> if the editor input exists; <code>false</code>
	 *		otherwise
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * Returns the name of the input.
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return meemPath.toString();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public MeemPath getMeemPath(){
		return meemPath;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
