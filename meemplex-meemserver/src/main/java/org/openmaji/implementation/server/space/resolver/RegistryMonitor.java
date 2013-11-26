/*
 * @(#)RegistryMonitor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.resolver;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.core.MeemCore;



/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class RegistryMonitor implements MeemRegistryClient {

	private MeemResolverWedge meemResolver;
	private MeemCore meemCore;
	
	private Meem meemRegistryGatewayMeem;
	
	private Map<MeemPath, Reference<?>> references = new HashMap<MeemPath, Reference<?>>();

	public RegistryMonitor(MeemResolverWedge meemResolver, MeemCore meemCore) {
		this.meemResolver = meemResolver;
		this.meemCore = meemCore;
		
		meemRegistryGatewayMeem = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());
	}

	public void addPathToWatch(MeemPath meemPath) {
		Facet proxy = meemCore.getTargetFor(this, MeemRegistryClient.class);
		Filter filter = ExactMatchFilter.create(meemPath);
		Reference<?> reference = Reference.spi.create("meemRegistryClient", proxy, false, filter);
		
		references.put(meemPath, reference);
		
		meemRegistryGatewayMeem.addOutboundReference(reference, false);
	}

	public void stopWatchingPath(MeemPath meemPath) {
		
		Reference<?> reference = references.get(meemPath);
		
		if (reference != null) {
			((MeemCoreImpl) meemCore).revokeTargetProxy(reference.getTarget(), this);
			meemRegistryGatewayMeem.removeOutboundReference(reference);
		}

	}

	/* -------- MeemRegistryClient methods ---------- */

	/**
	 */
	public void meemDeregistered(Meem meem) {
		meemResolver.handleMeemDeregistered(meem.getMeemPath());
	}

	/**
	 */
	public void meemRegistered(Meem meem) {
		meemResolver.handleMeemRegistered(meem.getMeemPath(), meem);
	}


}
