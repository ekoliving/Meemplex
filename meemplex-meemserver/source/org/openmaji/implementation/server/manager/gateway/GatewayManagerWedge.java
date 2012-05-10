/*
 * @(#)GatewayManagerImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.gateway;

import java.util.logging.Logger;

import org.openmaji.implementation.server.meem.core.MeemCoreImpl;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.meem.core.MeemCore;

/**
 * Provide a means to convert delegate classes into proper facets.
 */

public class GatewayManagerWedge implements GatewayManager, MeemDefinitionProvider, Wedge {
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemCore meemCore;

	private static GatewayManagerWedge gatewayManagerWedgeSingleton = null;

	private static int count = 0;

	public LifeCycle lifeCycleConduit;

	/**
	 * To say this is bizzare is to miss the point - this is necessary due to the MeemDefinition api and the fact we don't yet have a good way of generating singletons.
	 * 
	 */
	public GatewayManagerWedge() {
		count++;
		if (count > 2) {
			logger.info("attempt to create extra gateway manager.");
			throw new IllegalArgumentException("attempt to create extra gateway manager.");
		}
		gatewayManagerWedgeSingleton = this;
	}

	public static synchronized <T extends Facet> T getTargetFor(final T facet, final Class<T> specification) {

		if (gatewayManagerWedgeSingleton == null) {
			throw new IllegalStateException("GatewayManager Meem is not ready for use.");
		}

		return gatewayManagerWedgeSingleton.makeIntoTarget(facet, specification);
	}

	// public static synchronized Facet getNonblockingTargetFor(
	// final Facet facet,
	// final Class specification) {
	//
	// if (gatewayManagerWedgeSingleton == null) {
	// throw new IllegalStateException(
	// "GatewayManager Meem is not ready for use."
	// );
	// }
	//
	// return gatewayManagerWedgeSingleton.makeIntoNonBlockingTarget(facet, specification);
	// }

	// public static synchronized Facet getTargetFor(
	// final Meem meem,
	// final Facet facet,
	// final Class specification)
	// {
	// if (gatewayManagerWedgeSingleton == null) {
	// throw new IllegalStateException(
	// "GatewayManager Meem is not ready for use."
	// );
	// }
	//
	// return gatewayManagerWedgeSingleton.makeIntoTarget(facet, specification);
	// }

	public static synchronized void revokeTarget(final Facet proxy, final Facet implementation) {
		if (gatewayManagerWedgeSingleton == null) {
			throw new IllegalStateException("GatewayManager Meem is not ready for use.");
		}

		gatewayManagerWedgeSingleton.unmakeTarget(proxy, implementation);

		// Subject.doAs(MeemCoreRootAuthority.getSubject(), new PrivilegedAction()
		// {
		// public Object run()
		// {
		// gatewayManagerWedgeSingleton.unmakeTarget(proxy, implementation);
		// return null;
		// }
		// });
	}

	private <T extends Facet> T makeIntoTarget(T facet, Class<T> specification) {
		return (meemCore.getLimitedTargetFor(facet, specification));
	}

	/*
	 * private Facet makeIntoNonBlockingTarget( Facet facet, Class specification) { return(meemCore.getNonBlockingTargetFor(facet, specification)); }
	 */

	private void unmakeTarget(Facet proxy, Facet implementation) {
		((MeemCoreImpl) meemCore).revokeTargetProxy(proxy, implementation);
	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}
}