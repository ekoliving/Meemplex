/*
 * @(#)TestClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.search.test;

import java.util.logging.Logger;

import org.openmaji.implementation.tool.eclipse.Common;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.resolver.MeemResolverClient;



/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class TestClient implements MeemResolverClient {

	private Logger logger = Logger.getAnonymousLogger();

	public void meemResolved(MeemPath meemPath, Meem meem) {
		logger.info("resolvedMeemPath");
		if (meem != null) {
			logger.info("meemPath: " + meemPath + " : meem: " + meem);
		}
	}

}
