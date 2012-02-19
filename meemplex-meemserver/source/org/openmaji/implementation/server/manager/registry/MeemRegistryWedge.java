/*
 * @(#)MeemRegistryWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Complete JavaDoc.
 * - Consider whether registerMeem() should replace existing Meem or complain.
 * - Allow Meem searches (Filter match) to be performed by "interface type".
 * - Allow in-bound (only ?) Meem Facets to be exported as a Jini Service.
 */

package org.openmaji.implementation.server.manager.registry;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.system.manager.registry.MeemRegistry;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.MeemPath
 */

public class MeemRegistryWedge implements MeemRegistry, MeemDefinitionProvider, FilterChecker, Wedge {

	public MeemCore meemCore;

	/**
	 * Collection of registered Meems
	 */
	private Map<MeemPath, Meem> meems = new HashMap<MeemPath, Meem>();

	/**
	 * listener conduit.
	 */
	public MeemRegistryClient meemRegistryClientConduit;

	/**
	 * MeemRegistryClient (out-bound Facet)
	 */

	public MeemRegistryClient meemRegistryClient;

	public final ContentProvider meemRegistryClientProvider = new ContentProvider() {
		public synchronized void sendContent(Object target, Filter filter) {

			MeemRegistryClient meemRegistryClient = (MeemRegistryClient) target;

			if (filter instanceof ExactMatchFilter) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;

				if (exactMatchFilter.getTemplate() instanceof MeemPath) {
					MeemPath meemPath = (MeemPath) exactMatchFilter.getTemplate();

					Meem meem = meems.get(meemPath);

					if (meem == null) {
						meem = locateRemoteMeem(meemPath);
					}

					if (meem != null) {
						meemRegistryClient.meemRegistered(meem);
					}
				}
			}
			else {
				for (Meem meem : meems.values()) {
					meemRegistryClient.meemRegistered(meem);
				}
			}
		}
	};

	/**
	 * <p>
	 * ...
	 * </p>
	 * 
	 * @param meem
	 *            Bound Meem Reference to be registered
	 */

	public void registerMeem(Meem meem) {
		//LogTools.info(logger, "registerMeem(): " + meem);

		MeemPath meemPath = meem.getMeemPath();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMREGISTRY) {
			LogTools.trace(logger, logLevel, "registerMeem(): " + meemPath + " : " + meem);
		}

		if (meemPath == null) {
			throw new IllegalArgumentException("Can't register Meem with a null MeemPath: " + meem);
		}

		if (meemPath.isDefinitive() == false) {
			LogTools.warn(logger, "Can't register Meem with a non-storage MeemPath: " + meem);
		}

		Meem oldMeem = (Meem) meems.put(meemPath, meem);

		if (oldMeem != null) {
			meemRegistryClient.meemDeregistered(oldMeem);
			meemRegistryClientConduit.meemDeregistered(oldMeem);
		}

		meemRegistryClient.meemRegistered(meem);
		meemRegistryClientConduit.meemRegistered(meem);
	}

	/**
	 * <p>
	 * ...
	 * </p>
	 * 
	 * @param meem
	 *            Meem Reference to be deregistered
	 */

	public void deregisterMeem(Meem meem) {

		MeemPath meemPath = meem.getMeemPath();

		Meem oldMeem = (Meem) meems.remove(meemPath);

		if (oldMeem != null) {
			if (Common.TRACE_ENABLED && Common.TRACE_MEEMREGISTRY) {
				LogTools.trace(logger, logLevel, "deregisterMeem(): " + meemPath + " : " + meem);
			}

			meemRegistryClient.meemDeregistered(meem);
			meemRegistryClientConduit.meemDeregistered(meem);
		}
	}

	public synchronized boolean invokeMethodCheck(Filter filter, String methodName, Object[] args) throws IllegalFilterException {

		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		if (methodName.equals("meemRegistered") || methodName.equals("meemDeregistered")) {

			Meem meem = (Meem) args[0];
			if (meem != null) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				return exactMatchFilter.getTemplate().equals(meem.getMeemPath());
			}
		}

		return false;
	}

	protected Meem locateRemoteMeem(MeemPath meemPath) {

		return (null);
	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final int logLevel = Common.getLogLevelVerbose();
}