/*
 * @(#)IConnectable.java
 * Created on 5/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

/**
 * <code>IConnectable</code> defines the contract of an object that can be used
 * as source and/or target of a connection.
 * <p>
 * @author Kin Wong
 */
public interface IConnectable {
	/**
	 * Connects a connection object in which this IConnectable is the output 
	 * (Source of the connection) of the connection.
	 * @param connection The connection object in which this connectable 
	 * will act as the output.
	 */
	public void connectSource(IConnection connection) throws Exception;

	/**
	 * Disconnects the connection object which the connectable object was the output 
	 * (Source of the connection) of the connection.
	 * @param connection The connection object to be disconnected.
	 */
	public void disconnectSource(IConnection connection);

	/**
	 * Connects an <code>IConnection</code> object in which this IConnectable is 
	 * the target of the connection.
	 * @param connection The connection object in which this connectable
	 * will act as the target.
	 */
	public void connectTarget(IConnection connection) throws Exception;
	
	/**
	 * Disconnects <code>IConnection</code> object in which this IConnectable is 
	 * the target of the connection.
	 * @param connection The connection object in which this connectable to be
	 * disconnected from.
	 */
	public void disconnectTarget(IConnection connection);
}
