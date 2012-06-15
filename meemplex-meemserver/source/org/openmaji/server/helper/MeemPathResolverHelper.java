/*
 * @(#)SearchManagerClientHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mg Created on 20/01/2003
 */
public class MeemPathResolverHelper {
	private static MeemPathResolverHelper instance = new MeemPathResolverHelper();

	private MeemPathResolverHelper() {
	}

	public static MeemPathResolverHelper getInstance() {
		return instance;
	}

	public Meem resolveMeemPath(final MeemPath meemPath) {
		return meemPath.isDefinitive() ? resolveDefinitive(meemPath) : resolveHyperSpace(meemPath);
	}

	/**
	 * Asynchronous version
	 * 
	 * @param meemPath
	 * @param callback
	 */
	public void resolveMeemPath(final MeemPath meemPath, AsyncCallback<Meem> callback) {
		if (meemPath.isDefinitive()) {
			resolveDefinitive(meemPath, callback);
		}
		else {
			resolveHyperSpace(meemPath, callback);
		}
	}

	/**
	 * 
	 * @param meemPath
	 * @return
	 */
	private Meem resolveDefinitive(MeemPath meemPath) {
		PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();

		MeemRegistryClient meemRegistryClient = new MeemRegistryClientImpl(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(meemRegistryClient, MeemRegistryClient.class);

		Filter filter = new ExactMatchFilter(meemPath);

		Reference reference = Reference.spi.create("meemRegistryClient", proxy, true, filter);

		Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());

		meemRegistryGateway.addOutboundReference(reference, true);

		try {
			return pigeonHole.get(timeout);
		}
		catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for Meem for the MeemPath", ex);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, meemRegistryClient);
		}
	}

	/**
	 * 
	 * @param meemPath
	 * @param callback
	 */
	private void resolveDefinitive(MeemPath meemPath, AsyncCallback<Meem> callback) {

		MeemRegistryClientCallback meemRegistryClient = new MeemRegistryClientCallback(callback);
		meemRegistryClient.resolve(meemPath);
	}
	
	/**
	 * 
	 * @param meemPath
	 * @return
	 */
	private Meem resolveHyperSpace(MeemPath meemPath) {
		PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();
		final MeemPathResolverClientImpl client = new MeemPathResolverClientImpl(pigeonHole);

		Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
		Facet proxy = GatewayManagerWedge.getTargetFor(client, MeemResolverClient.class);

		Reference reference = Reference.spi.create("meemResolverClient", proxy, true, new ExactMatchFilter(meemPath));

		resolverMeem.addOutboundReference(reference, true);

		try {
			return pigeonHole.get(timeout);
		}
		catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for Meem for the MeemPath", ex);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, client);
		}
	}
	
	private void resolveHyperSpace(MeemPath meemPath, AsyncCallback<Meem> callback) {

		MeemRegistryClientCallback meemRegistryClient = new MeemRegistryClientCallback(callback);
		meemRegistryClient.resolve(meemPath);
	}


	/**
	 * PigeonHole version of MeemRegistry revolver
	 *
	 */
	public class MeemRegistryClientImpl implements MeemRegistryClient, ContentClient {
		public MeemRegistryClientImpl(PigeonHole<Meem> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void meemRegistered(Meem meem) {
			this.meem = meem;
		}

		public void meemDeregistered(Meem meem) {
			this.meem = null;
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(meem);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}

		private PigeonHole<Meem> pigeonHole;
		private Meem meem = null;
	}

	/**
	 * Callback version of MeemRegistry resolver
	 *
	 */
	public class MeemRegistryClientCallback implements MeemRegistryClient, ContentClient {
		private AsyncCallback<Meem> callback;
		private Facet clientProxy;
		private MeemPath meemPath;
		private Meem meem = null;

		Runnable timeoutHandler = new Runnable() {
			public void run() {
				contentFailed("Timeout while resolving meem " + meemPath);
			}
		};
		
		public MeemRegistryClientCallback(AsyncCallback<Meem> callback) {
			this.callback = callback;
		}
		
		public void resolve(MeemPath meemPath) {
			this.meemPath = meemPath;
			clientProxy = GatewayManagerWedge.getTargetFor(this, MeemRegistryClient.class);
			Filter filter = new ExactMatchFilter(meemPath);
			Reference reference = Reference.spi.create("meemRegistryClient", clientProxy, true, filter);
			Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());
			meemRegistryGateway.addOutboundReference(reference, true);

			ThreadManager.spi.create().queue(timeoutHandler, timeout);
		}

		public void meemRegistered(Meem meem) {
			this.meem = meem;
		}

		public void meemDeregistered(Meem meem) {
			this.meem = null;
		}

		public void contentSent() {
			if (callback != null) {
				callback.result(meem);
				callback = null;
				revokeProxy();
			}
		}

		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new Exception(reason));
				callback = null;
				revokeProxy();
			}
		}
		
		private void revokeProxy() {
			if (clientProxy != null) {
				ThreadManager.spi.create().cancel(timeoutHandler);
				GatewayManagerWedge.revokeTarget(clientProxy, this);
				clientProxy = null;
			}
		}
	}
	
	/**
	 * 
	 * MeemPathResolverClientImpl
	 * 
	 * @author stormboy
	 */
	public class MeemPathResolverClientImpl implements MeemResolverClient, ContentClient {
		public MeemPathResolverClientImpl(PigeonHole<Meem> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void meemResolved(MeemPath meemPath, Meem meem) {
			this.meem = meem;
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(meem);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}

		private PigeonHole<Meem> pigeonHole;
		private Meem meem = null;
	}
	
	/**
	 * Callback version of MeemPath resolver.
	 *
	 */
	public class MeemPathClientCallback implements MeemResolverClient, ContentClient {
		private AsyncCallback<Meem> callback;
		private Facet clientProxy;
		private MeemPath meemPath;
		private Meem meem = null;
		
		Runnable timeoutHandler = new Runnable() {
			public void run() {
				contentFailed("Timeout resolving meem at " + meemPath);
			}
		};
		
		public MeemPathClientCallback(AsyncCallback<Meem> callback) {
			this.callback = callback;
		}
		
		public void resolve(MeemPath meemPath) {
			this.meemPath = meemPath;
			clientProxy = GatewayManagerWedge.getTargetFor(this, MeemResolverClient.class);
			Reference reference = Reference.spi.create("meemResolverClient", clientProxy, true, new ExactMatchFilter(meemPath));
			Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
			resolverMeem.addOutboundReference(reference, true);

			ThreadManager.spi.create().queue(timeoutHandler, timeout);
		}

		public void meemResolved(MeemPath meemPath, Meem meem) {
			this.meem = meem;
		}

		public void contentSent() {
			if (callback != null) {
				callback.result(meem);
				callback = null;
				revokeProxy();
			}
		}

		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new Exception(reason));
				callback = null;
				revokeProxy();
			}
		}
		
		private void revokeProxy() {
			if (clientProxy != null) {
				ThreadManager.spi.create().cancel(timeoutHandler);
				GatewayManagerWedge.revokeTarget(clientProxy, this);
				clientProxy = null;
			}
		}
	}

	private static final long timeout = 60000;

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();
}
