/*
 * @(#)ActivationClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.activation;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface ActivationClient {
	
	public void activated(MeemPath meemPath, Meem meem, MeemDefinition meemDefinition);
	public void activationFailed(MeemPath meemPath);
	
}
