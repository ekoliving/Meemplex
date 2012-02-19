/*
 * @(#)IConnection.java
 * Created on 16/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

/**
 * <code>IConnection</code> represents a connection between a source and a 
 * target <code>IConnectable</code>.
 * <p>
 * @author Kin Wong
 */
public interface IConnection {
	/**
	 * Sets the source connectable object to this connection.
	 * @param source The source connectable object of this connection.
	 */
	void setSource(IConnectable source);
	
	/**
	 * Gets the source connectable object of this connection.
	 * @return IConnectable The source connectable object of this connection.
	 */
	IConnectable getSource();
	
	/**
	 * Attaches the source connectable object from this connection.
	 */
	void attachSource();
	
	/**
	 * Detaches the source connectable object from this connection.
	 */
	void detachSource();
	
	/**
	 * Sets the target connectable object to this connection.
	 * @param target The target connectable object of this connection.
	 */
	void setTarget(IConnectable target);
	
	/**
	 * Gets the target connectable object of this connection.
	 * @return IConnectable The target connectable object of this connection.
	 */
	IConnectable getTarget();
	
	/**
	 * Attaches the target connectable object from this connection.
	 */
	void attachTarget();
	
	/**
	 * Detaches the target connectable object from this connection.
	 */
	void detachTarget();
}
