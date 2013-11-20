/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

/**
 * Something that can be told to connect or disconnect.
 * 
 * @author Warren Bloomer
 *
 */
public interface Connectable
{
	/**
	 * Tell the object to connect.
	 */
	void connect();

	/**
	 * Tell the object to disconnect.
	 */
	void disconnect();
}
