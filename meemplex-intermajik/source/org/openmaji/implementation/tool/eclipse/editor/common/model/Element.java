/*
 * @(#)Element.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.core.runtime.Assert;
import org.openmaji.implementation.intermajik.model.ElementPath;


/**
 * @author Kin Wong
 * @author MG
 */
abstract public class Element implements Cloneable, Serializable {
	public static final String ID_NAME = "name";
	public static final String ID_PARENT = "parent";
	public static final String ID_REFRESH_VISUALS = "refresh visuals";

	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	protected ElementContainer parent;
	
	/**
	 * Constructs an instance of <code>Element</code>.
	 * <p>
	 */
	protected Element() {
	}

	public void refreshVisuals() {
		firePropertyChange(ID_REFRESH_VISUALS, null, null);
	}
	/**
	 * Gets the root element container.
	 * @return ElementContainer The root element container.
	 */	
	public ElementContainer getRoot() {
		ElementContainer root = getParent();
		if(root == null) return null;
		ElementContainer rootroot = root.getParent();
		while(rootroot != null) {
			root = rootroot;
			rootroot = root.getParent();
		}
		return root;
	}

	/**
	 * Returns the identifier of this element. This identifier must uniquely 
	 * identifies this object in its containing parent.
	 * <p>
	 * @return Object The identifier of this element that uniquely identifies 
	 * it in its containing parent.
	 */
	abstract public Serializable getId();
	
	/**
	 * Returns the name of this element.
	 * <p>
	 * @return String the name of this element.
	 */
	abstract public String getName();

	/**
	 * Returns ths parent of this element.
	 * @return ElementContainer The parent of this element.
	 */
	public ElementContainer getParent() {
		return parent;
	}
	
	/**
	 * Builds an element path that identifies this element.
	 * @param path An element path that identifies this element.
	 */
	protected void buildPath(ElementPath path) {
		if(getParent() != null) {
			getParent().buildPath(path);
		}
		path.pushTail(getId());
	}
	
	/**
	 * Gets an element path that identifies this element.
	 * @return ElementPath An element path that identifies this element.
	 */
	public ElementPath getPath() {
		ElementPath path = new ElementPath();
		buildPath(path);
		return path;
	}
	
	/**
	 * Parses the element path and resolves it to an element identified by the 
	 * element path
	 * @param path The element path the specfies element.
	 * @return Element The element identified by the element path.
	 */	
	public Element parsePath(ElementPath path) {
		Assert.isNotNull(path);
		Object id = path.popHead();
		if(id.equals(getId())) return this;
		else
		return null;
	}
	
	/**
	 * Sets the parent of this element.
	 * @param parent The parent of this element.
	 */
	protected void setParent(ElementContainer parent) {
		if(this.parent == parent) return;
		ElementContainer oldParent = this.parent;
		this.parent = parent;

		firePropertyChange(	ID_PARENT, 
												(oldParent == null)? null:oldParent.getId(), 
												(parent == null)? null:parent.getId());
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	/**
	 * Overridden to instantiate transient data members.
	 * @param in The stream that contains the serialized element.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in)throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		listeners = new PropertyChangeSupport(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ID = " + getId();
	}
}
