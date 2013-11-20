/*
 * @(#)LicensingFactory.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.session;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

/**
 * <p>This facet establishes the session meem factory pattern used to create and 
 * destroy license store session meems for each connecting client. When a 
 * client wishes to create a session with the session factory, this facet is 
 * first used to contact the factory meem, which provides a 
 * MeemPath back to the client on a reciprocal <code>SessionMeemFactoryClient</code>
 * facet.</p>
 * 
 * <p>The session factory is responible for implementing both the create and destroy
 * methods, both of which are client initiated.</p>
 */

public interface SessionMeemFactory extends Facet {

	/**
	 * <p>A call to this method will instruct the session meem factory
	 * to establish a new instance, to which the MeemPath of this new 
	 * instance will be returned on the SessionMeemFactoryClient.</p>
	 */
	
	public void createSessionMeem();
	
	/**
	 * <p>A call to this method will instruct the session meem factory
	 * to destroy the session meem which is held by the client. Notification
	 * that the factory has properly disposed of the meem will be provided
	 * on the reciprocoal SessionMeemFactoryClient facet.</p> 
	 */
	
	public void destroySessionMeem(MeemPath mp);
}