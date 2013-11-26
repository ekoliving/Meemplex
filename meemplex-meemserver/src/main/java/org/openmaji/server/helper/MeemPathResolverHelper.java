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
import org.openmaji.system.manager.thread.Task;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;
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
		if (DEBUG) {
			logger.info("resolving meempath: " + meemPath);
		}
		return meemPath.isDefinitive() ? resolveDefinitive(meemPath) : resolveHyperSpace(meemPath);
	}

	/**
	 * Asynchronous version
	 * 
	 * @param meemPath
	 * @param callback
	 */
	public void resolveMeemPath(final MeemPath meemPath, AsyncCallback<Meem> callback) {
		if (DEBUG) {
			logger.info("resolving meempath async: " + meemPath);
		}
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
	private Meem resolveDefinitive(final MeemPath meemPath) {
		final PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();

		new MeemRegistryResolverTask(meemPath, new AsyncCallback<Meem>() {
			public void result(Meem target) {
				pigeonHole.put(target);
			};

			public void exception(Exception e) {
				pigeonHole.exception(e);
			}
		});

		try {
			Meem meem = pigeonHole.get();
			return meem;
		}
		catch (ContentException | TimeoutException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param meemPath
	 * @param callback
	 */
	private void resolveDefinitive(MeemPath meemPath, AsyncCallback<Meem> callback) {
		new MeemRegistryResolverTask(meemPath, callback);
	}

	/**
	 * 
	 * @param meemPath
	 * @return
	 */
	private Meem resolveHyperSpace(final MeemPath meemPath) {
		final PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();

		new MeemPathResolverTask(meemPath, new AsyncCallback<Meem>() {
			public void result(Meem target) {
				if (DEBUG) {
					logger.log(Level.INFO, "got meem for " + meemPath + " : " + meemPath);
				}
				pigeonHole.put(target);
			};

			public void exception(Exception e) {
				if (DEBUG) {
					logger.log(Level.INFO, "Problem resolving hyperspace path: " + meemPath, e);
				}
				pigeonHole.exception(e);
			}
		});

		try {
			return pigeonHole.get();
		}
		catch (ContentException e) {
			return null;
		}
		catch (TimeoutException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param meemPath
	 * @param callback
	 */
	private void resolveHyperSpace(MeemPath meemPath, AsyncCallback<Meem> callback) {
		new MeemPathResolverTask(meemPath, callback);
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
	 * 
	 * MeemPathResolverClientImpl
	 * 
	 * @author stormboy
	 */
	public class MeemPathResolverClientImpl implements MeemResolverClient, ContentClient {
		
		public MeemPathResolverClientImpl(PigeonHole<Meem> pigeonHole, AsyncCallback<Meem> callback) {
			this.pigeonHole = pigeonHole;
			if (callback != null) {
				pigeonHole.get(callback);
			}
		}
	
		public MeemPathResolverClientImpl(PigeonHole<Meem> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void meemResolved(MeemPath meemPath, Meem meem) {
			if (DEBUG) {
				logger.info("Meem resolved from meempath: " + meemPath + " : " + meem);
			}
			this.meem = meem;
		}

		public void contentSent() {
			if (DEBUG) {
				logger.info("content sent for meempath: " + meem);
			}
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
		AsyncCallback<Meem> callback = null;
	}

	/**
	 * Callback version of MeemRegistry resolver
	 * 
	 */
	public class MeemRegistryResolverTask implements MeemRegistryClient, ContentClient {
		private MeemPath meemPath;
		private MeemRegistryClient clientProxy;
		private Meem meem = null;
		private AsyncCallback<Meem> callback;

		private Task timeoutTask;
		private Runnable timeoutHandler = new Runnable() {
			public void run() {
				timeout();
			}
		};

		public MeemRegistryResolverTask(MeemPath meemPath, AsyncCallback<Meem> callback) {
			this.meemPath = meemPath;
			this.callback = callback;
			this.clientProxy = GatewayManagerWedge.getTargetFor(this, MeemRegistryClient.class);
			
			Filter filter = ExactMatchFilter.create(meemPath);
			Reference<MeemRegistryClient> reference = Reference.spi.create("meemRegistryClient", clientProxy, true, filter);
			
			Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());
			meemRegistryGateway.addOutboundReference(reference, true);
			
			this.timeoutTask = ThreadManager.spi.create().queue(timeoutHandler, System.currentTimeMillis() + timeout);
		}

		public void meemRegistered(Meem meem) {
			if (DEBUG) {
				logger.info("MeemRegistryResolverTask: meemRegistered: " + meem);
			}
			this.meem = meem;
			callback.result(meem);
		}

		public void meemDeregistered(Meem meem) {
			if (DEBUG) {
				logger.info("MeemRegistryResolverTask: meemDeregistered: " + meem);
			}
			this.meem = null;
		}

		public void contentSent() {
			if (DEBUG) {
				logger.info("MeemRegistryResolverTask: contentSent: " + meem);
			}
			if (callback != null) {
				callback = null;
				cleanup();
			}
		}

		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new ContentException(reason));
				callback = null;
				cleanup();
			}
		}

		private void timeout() {
			if (callback != null) {
				callback.exception(new TimeoutException("Timeout while resolving meem " + meemPath));
				callback = null;
				cleanup();
			}
		}

		private void cleanup() {
			if (timeoutTask != null) {
				timeoutTask.cancel();
			}
			if (clientProxy != null) {
				GatewayManagerWedge.revokeTarget(clientProxy, this);
				clientProxy = null;
			}
		}
	}

	/**
	 * Callback version of MeemPath resolver.
	 * 
	 */
	public class MeemPathResolverTask implements MeemResolverClient, ContentClient {
		private AsyncCallback<Meem> callback;
		private MeemResolverClient clientProxy;
		private MeemPath meemPath;
		private Meem meem = null;
		private Task timeoutTask;
		private Runnable timeoutHandler = new Runnable() {
			public void run() {
				timeout();
			}
		};

		public MeemPathResolverTask(MeemPath meemPath, AsyncCallback<Meem> callback) {
			this.callback = callback;
			this.meemPath = meemPath;
			this.clientProxy = GatewayManagerWedge.getTargetFor(this, MeemResolverClient.class);
			Reference<MeemResolverClient> reference = Reference.spi.create("meemResolverClient", clientProxy, true, ExactMatchFilter.create(meemPath));
			Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
			this.timeoutTask = ThreadManager.spi.create().queue(timeoutHandler, System.currentTimeMillis() + timeout);
			resolverMeem.addOutboundReference(reference, true);
		}

		public void meemResolved(MeemPath meemPath, Meem meem) {
			if (DEBUG) {
				logger.info("Meem resolved from meempath: " + meemPath + " : " + meem);
			}
			this.meem = meem;
		}

		public void contentSent() {
			if (callback != null) {
				callback.result(meem);
				callback = null;
				cleanup();
			}
		}

		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new ContentException(reason));
				callback = null;
				cleanup();
			}
		}

		public void timeout() {
			if (callback != null) {
				callback.exception(new TimeoutException("Timeout while resolving meem " + meemPath));
				callback = null;
				cleanup();
			}
		}

		private void cleanup() {
			if (timeoutTask != null) {
				timeoutTask.cancel();
			}
			if (clientProxy != null) {
				GatewayManagerWedge.revokeTarget(clientProxy, this);
				clientProxy = null;
			}
		}

	}

	private static final long timeout = Long.parseLong(System.getProperty(PigeonHole.PROPERTY_TIMEOUT, "60000"));

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;
}
