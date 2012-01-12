/*
 * @(#)LicensingFactory.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meem.session;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

/**
 * <p>This interface is a client facet which is used by client meems to 
 * establish connections to a session meem factory. When a reference is created
 * by a client meem on the factory, a <it>session meem</it> is
 * created, and its meem path is returned to the requesting client. The
 * server side session meem is then made available for exclusive use by the 
 * requesting client.</p>
 * 
 * <p>For each new session that is created to the factory, a new session
 * meem will be created. Each meem will have dedicated access to this meem, 
 * until the client destroys it. Notifications will be provided back to the
 * client that the session meem has been destroyed.</p>
 */

public interface SessionMeemFactoryClient extends Facet {
	
	
	public void sessionMeemCreated(MeemPath mp);
	
	public void sessionMeemDestroyed(MeemPath mp);

}
