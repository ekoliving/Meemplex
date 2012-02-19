/*
 * @(#)MeemRegistryClientImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemregistry;

import java.util.TreeSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openmaji.implementation.tool.eclipse.Common;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.util.MeemComparator;
import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.registry.MeemRegistryClient;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemRegistryClientImpl implements MeemRegistryClient {
	private Set entries;
	
	Reference meemRegistryClientReference;
	Meem meemRegistryMeem;
	RegistriesContentProvider provider;
	
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemRegistryClientImpl(RegistriesContentProvider provider, Meem meemRegistry) {
		this.provider = provider;
		
		entries = new TreeSet(new MeemComparator());
		
		meemRegistryMeem =  meemRegistry;

		meemRegistryClientReference = Reference.spi.create("meemRegistryClient",
		        SecurityManager.getInstance().getGateway().getTargetFor(this, MeemRegistryClient.class), true, null);

		meemRegistryMeem.addOutboundReference(meemRegistryClientReference, false);
	}

	public void meemRegistered(Meem meem) {
		entries.add(meem);
		if (Common.TRACE_ENABLED)
		{
			logger.log(Level.FINE, this + " meemAdded: " + meem.getMeemPath().toString());
		}
		provider.update();
	}

	public void meemDeregistered(Meem meem) {
		entries.remove(meem);
		provider.update();
	}

	public Object[] getChildren() {
		return entries.toArray();
	}
	
	public boolean hasChildren() {
		return (entries.size() > 0);
	}
	
	public void dispose() {
		if (meemRegistryMeem != null && meemRegistryClientReference != null)
			meemRegistryMeem.removeOutboundReference(meemRegistryClientReference);
	}
}
