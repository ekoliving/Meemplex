/*
 * @(#)IModelContainer.java
 * Created on 15/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.containment;

import java.util.List;

/**
 * <code>IModelContainer</code> defines a container that stores a collection of
 * children.
 * <p>
 * @author Kin Wong
 */
public interface IModelContainer {
	/**
	 * Adds a new child model to the model container.
	 * @param index The position where the new child is inserted. Using a negative 
	 * value will insert the child model to the end of the child list.
	 * @param child The new child model to be added to the model container.
	 * @return boolean true if the child model has been added, false otherwise.
	 */
	boolean addChild(int index, Object child);

	/**
	 * Removes an existing child model from the model container.
	 * @param child The child model to be removed from the model container.
	 * @return boolean true if the child model has been removed, false otherwise.
	 */
	boolean removeChild(Object child);
	
	/**
	 * Moves the child at fromIndex to toIndex
	 * @param fromIndex The index of the child to be moved.
	 * @param toIndex The destination of the index.
	 * @return boolean true if the child model has been moved, false otherwise.
	 */
	boolean moveChild(int fromIndex, int toIndex);

	/**
	 * Returns a list of child models of this model container.
	 * @return List The list contains all child models.
	 */
	List getChildren();

	/**
	 * Finds the index of a child.
	 * @param child The child object.
	 * @return int The index of the child.
	 */
	int childIndexOf(Object child);
	
	/**
	 * Returns whether the child is a valid child for the container.
	 * @param child A child object about to be added to the container.
	 * @return boolean true is the child is a valid child, false otherwise.
	 */
	boolean isValidNewChild(Object child);
}
