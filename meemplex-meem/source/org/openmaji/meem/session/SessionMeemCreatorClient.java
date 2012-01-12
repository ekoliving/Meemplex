/*
 * @(#)SessionMeemCreatorClient.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.session;

import org.openmaji.meem.MajiException;
import org.openmaji.meem.MeemPath;

/**
 * <p>This is the return client conduit for creating remote server session
 * meems from a session meem factory. It provides an interface to recieve the
 * meempath of the newly instantiated session meem, and also a method to handle
 * exceptions which may be thrown as a result.</p>
 * 
 * @see org.openmaji.meem.session.SessionMeemCreator
 */

public interface SessionMeemCreatorClient {

	/**
	 * <p>Returns a valid meempath which represents the newly created session
	 * meem instance. Never returns null.</p>
	 * 
	 * @param mp The valid meempath of the instantiated session meem
	 */
	
	public void sessionMeemCreated(MeemPath mp);
	
	/**
	 * <p>Returns an exception that may be thrown as a result of the attempt to
	 * create the new session meem.</p>
	 * 
	 * <p>Throws a CreationException if the remote factory was unable to instantiate
	 * a dedicated session meem.</p>
	 * 
	 * <p>Throws a LocationException if the remote session meem factory cannot be 
	 * found.</p>
	 */
	
	public void sessionMeemCreationError(MajiException e);

	
}
