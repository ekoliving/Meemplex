/*
 * @(#)LicensingFactory.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.session;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

/**
 * <p>This interface is a client conduit used to pass a message to a session
 * meem client wedge to create a remote, or server side session meem. When 
 * the meem is created, it provides a MeemPath back on the reciprocal 
 * <code>SessionMeemCreatorClient</code> conduit.</p>
 * 
 * <p>Similarly, if any errors are encountered whilist creating the remote 
 * session meem, an error will be reported back on the same conduit.</p>
 * 
 * @see org.openmaji.meem.session.SessionMeemCreatorClient
 */

public interface SessionMeemCreator extends Facet {
	
	/**
	 * <p>Specifies a meempath to the remote session meem factory, and instructs
	 * that factory to create a dedicated session meem instance, and supply
	 * its meempath back on the <code>SessionMeemCreatorCllient</code> conduit.</p> 
	 * 
	 * @param mp The meempath of the remote factory
	 * 
	 * Returns a meempath on the reciprocal <code>SessionMeemCreatorClient</code>
	 * 
	 * Throws a CreationException is thrown if the session meem cannot be 
	 * instatiated. 
	 * Throws a MeemLocationException if the factory meem is not found at the
	 * specified meempath.
	 */
	
	public void createServerSessionMeem(MeemPath mp);
	
}
