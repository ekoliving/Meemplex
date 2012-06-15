/*
 * @(#)MeemResolverWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 */
package org.openmaji.implementation.server.space.resolver;

import java.util.*;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.*;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.FacetClientCallback;
import org.openmaji.system.meem.FacetClientConduit;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.resolver.MeemResolverClient;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */

public class MeemResolverWedge implements Wedge, MeemDefinitionProvider
{
	private static final Logger logger = Logger.getAnonymousLogger();
	public static final Level LOG_LEVEL = Common.getLogLevelVerbose();
	private static boolean DEBUG = false;

	public MeemCore meemCore;

	/* --------- outbound facets -------------- */
	
	public MeemClient meemReferenceClientConduit = new MeemClient()
	{
		public void referenceAdded(Reference reference)
		{
			Facet target = reference.getTarget();
			//if ("meemResolverClient".equals(reference.getFacetIdentifier()))
			if (target instanceof MeemResolverClient)
			{
				MeemResolverClient client = (MeemResolverClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();

				resolveContinuous(meemPath, client);
			}
		}

		public void referenceRemoved(Reference reference)
		{
			Facet target = reference.getTarget();
			if (target instanceof MeemResolverClient)
			{
				MeemResolverClient client = (MeemResolverClient) target;
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) reference.getFilter()).getTemplate();

				terminateResolveMeem(meemPath, client);
			}
		}
	};

	public MeemResolverClient meemResolverClient;
	
	// TODO why is this here when there is a continuous monitor created on referenceAdded for the MeemResolverClient facet?
	public final AsyncContentProvider meemResolverClientProvider = new AsyncContentProvider()
	{
		public void asyncSendContent(Object target, Filter filter, ContentClient contentClient)
		{
			MeemResolverClient client = (MeemResolverClient) target;

			if (filter instanceof ExactMatchFilter)
			{
				MeemPath meemPath = (MeemPath) ((ExactMatchFilter) filter).getTemplate();

				if (meemPath == null)
				{
					contentClient.contentFailed("MeemPath ExactMatchFilter cannot be null");
				}
				else
				{
					resolveMeem(meemPath, client, contentClient);
				}
			}
			else
			{
				contentClient.contentSent();
			}
		}
	};


	/* ---------- conduits -------------- */

	public FacetClientConduit facetClientConduit;

	
	/* ------------- private members ---------- */
	
	/**
	 *  meempath=clients map for success notification
	 */
	private Map<MeemPath, Set<MeemResolverClient>> clientsForMeemPaths = new HashMap<MeemPath, Set<MeemResolverClient>>();

	private Map<MeemPath, List<MonitorDetail>> monitorDetails = new HashMap<MeemPath, List<MonitorDetail>>();
	
	private Map<MeemPath, ResolvedDetail> resolvedDetails = new HashMap<MeemPath, ResolvedDetail>();
	
	private Map<MeemPath, Meem> resolvedMeemsCache = new HashMap<MeemPath, Meem>();

	private CategoryMonitorTree categoryMonitorTree = new CategoryMonitorTree();

	private RegistryMonitor registryMonitor = null;
	
	
	/* ----------- -------------- */
	
	private RegistryMonitor getRegistryMonitor()
	{
		if (registryMonitor == null)
		{
			// TODO[peter] Why does this have to be a separate object?
			registryMonitor = new RegistryMonitor(this, meemCore);
			HyperSpaceHelper.getInstance().getHyperSpaceMeem(new AsyncCallback<Meem>() {
				public void result(Meem meem) {
					registryMonitor.meemRegistered(meem);
				}
				public void exception(Exception arg0) {
				}
			});
		}

		return registryMonitor;
	}

	/**
	 * @see org.openmaji.space.resolver.MeemResolver#resolveMeemPath(org.openmaji.meem.MeemPath, org.openmaji.space.resolver.MeemResolverClient)
	 */
	private void resolveMeem(MeemPath meemPath, final MeemResolverClient client, final ContentClient contentClient)
	{
		if (meemPath.isDefinitive())
		{
//			client.meemResolved(meemPath, Meem.spi.get(meemPath));
//			contentClient.contentSent();

			new RegistryLookup(meemPath, client, contentClient);
		}
		else if (meemPath.getSpace().equals(Space.HYPERSPACE))
		{
			// check for leading slash and insert one if necessary
			if (!meemPath.getLocation().startsWith("/"))
			{
				meemPath = MeemPath.spi.create(meemPath.getSpace(), "/" + meemPath.getLocation());
			}

			final MeemPath hyperSpaceMeemPath = meemPath;
			HyperSpaceHelper.getInstance().getHyperSpaceMeem(new AsyncCallback<Meem>() {
				public void result(Meem hyperSpacMeem) {
					handleHyperSpaceToResolveMeem(hyperSpacMeem, hyperSpaceMeemPath, client, contentClient);
				}
				public void exception(Exception e) {
					contentClient.contentFailed("MeemPathResolver cannot find HyperSpace while trying to resolve " + hyperSpaceMeemPath);
				}
			});
			
//			Meem meem = HyperSpaceHelper.getInstance().getHyperSpaceMeem();
//			if (meem == null) {
//				contentClient.contentFailed("MeemPathResolver cannot find HyperSpace while trying to resolve " + meemPath);
//			}
//			else {
//				new ResolvedMeemPathCategoryCallback(meemPath, client, contentClient).proceed(meem);
//			}
		}
		else 
		{
			// if it isn't a HyperSpace path, we can't handle it.
			contentClient.contentFailed("MeemPath of unknown type unresolvable: " + meemPath);
		}
	}

	/**
	 * When hyperspace meem is resolved for the purposes of resolving a meempath
	 * @param hyperSpacMeem
	 * @param meemPath
	 * @param client
	 * @param contentClient
	 */
	private void handleHyperSpaceToResolveMeem(Meem hyperSpacMeem, MeemPath meemPath, MeemResolverClient client, ContentClient contentClient) {
		if (hyperSpacMeem == null) {
			contentClient.contentFailed("MeemPathResolver cannot find HyperSpace while trying to resolve " + meemPath);
		}
		else {
			new ResolvedMeemPathCategoryCallback(meemPath, client, contentClient).proceed(hyperSpacMeem);
		}
	}


	private synchronized void resolveContinuous(MeemPath meemPath, final MeemResolverClient client)
	{
		if (meemPath == null)
		{
			throw new IllegalArgumentException("MeemResolver.resolveContinuous(): MeemPath is null");
		}

		if (meemPath.isDefinitive())
		{
			Meem cachedMeem = (Meem) resolvedMeemsCache.get(meemPath);
			if (cachedMeem != null)
			{
				client.meemResolved(meemPath, cachedMeem);
			}
		}
		else if (!meemPath.getSpace().equals(Space.HYPERSPACE))
		{
			// We can't handle it!
			client.meemResolved(meemPath, null);
			return;
		}

		boolean shouldStart = false;
		Set<MeemResolverClient> clients = clientsForMeemPaths.get(meemPath);

		if (clients == null)
		{
			clients = new HashSet<MeemResolverClient>();
			clientsForMeemPaths.put(meemPath, clients);

			shouldStart = true;
		}

		clients.add(client);

		if (shouldStart)
		{
			startMonitoring(meemPath);
		}
	}

	/**
	 */
	public synchronized void terminateResolveMeem(MeemPath meemPath, MeemResolverClient client)
	{
		Set<MeemResolverClient> clients = clientsForMeemPaths.get(meemPath);

		if (clients != null)
		{
			clients.remove(client);

			if (clients.size() == 0)
			{
				clientsForMeemPaths.remove(meemPath);
				stopMonitoring(meemPath);
			}
		}
	}

	private synchronized void startMonitoring(MeemPath meemPath)
	{
		if (meemPath.isDefinitive())
		{
			ResolvedDetail resolvedDetail = new ResolvedDetail(meemPath, meemPath);
			resolvedDetails.put(meemPath, resolvedDetail);

			getRegistryMonitor().addPathToWatch(meemPath);
		}
		else
		{
			// check for leading slash and insert one if necessary
			if (!meemPath.getLocation().startsWith("/"))
			{
				meemPath = MeemPath.spi.create(meemPath.getSpace(), "/" + meemPath.getLocation());
			}

			// -mg- this should use the space type, not just assume hyperspace
			final MeemPath hyperSpacePath = meemPath;
			HyperSpaceHelper.getInstance().getHyperSpaceMeem(new AsyncCallback<Meem>() {
				public void result(Meem hyperSpaceMeem) {
					handleHyperSpaceMeemForMonitoring(hyperSpaceMeem, hyperSpacePath);
				}
				public void exception(Exception e) {
				}
			});

//			Meem meem = HyperSpaceHelper.getInstance().getHyperSpaceMeem();
//
//			String location = meemPath.getLocation();
//			StringTokenizer tok = new StringTokenizer(location, "/");
//
//			if (tok.hasMoreTokens()) {
//				MeemPath currentMeemPath = MeemPath.spi.create(meemPath.getSpace(), "");
//				String firstPath = tok.nextToken();
//
//				createCategoryMonitor(meem, currentMeemPath, firstPath, meemPath);
//			}
//			else {
//				// No location part of the MeemPath, so the request is for the
//				// root of the space
//				pathResolved(meemPath, meem.getMeemPath());
//			}
		}
	}
	
	private void handleHyperSpaceMeemForMonitoring(Meem hyperSpaceMeem, MeemPath meemPath) {
		String location = meemPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");

		if (tok.hasMoreTokens()) {
			MeemPath currentMeemPath = MeemPath.spi.create(meemPath.getSpace(), "");
			String firstPath = tok.nextToken();

			createCategoryMonitor(hyperSpaceMeem, currentMeemPath, firstPath, meemPath);
		}
		else {
			// No location part of the MeemPath, so the request is for the root
			// of the space
			pathResolved(meemPath, hyperSpaceMeem.getMeemPath());
		}

	}

	private synchronized void stopMonitoring(MeemPath meemPath)
	{
		if (meemPath.isDefinitive())
		{
			resolvedMeemsCache.remove(meemPath);
			getRegistryMonitor().stopWatchingPath(meemPath);
			resolvedDetails.remove(meemPath);
		}
		else if (meemPath.getSpace().equals(Space.HYPERSPACE))
		{
			categoryMonitorTree.removeMeemPath(meemPath);
		}
	}

	public synchronized void pathResolved(MeemPath searchMeemPath, MeemPath resolvedMeemPath)
	{
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
		{
			logger.log(LOG_LEVEL,
				"pathResolved() searchMeemPath: " + searchMeemPath + " resolvedMeemPath: " + resolvedMeemPath);
		}

		if (resolvedMeemPath != null)
		{
			Set<MeemResolverClient> clients = clientsForMeemPaths.get(searchMeemPath);

			if (clients != null)
			{
				ResolvedDetail resolvedDetail = new ResolvedDetail(searchMeemPath, resolvedMeemPath);
				resolvedDetails.put(resolvedMeemPath, resolvedDetail);

				getRegistryMonitor().addPathToWatch(resolvedMeemPath);
			}
		}
	}

	private synchronized void pathResolvedMeemRegistered(Meem meem, MeemPath searchMeemPath, MeemPath resolvedMeemPath)
	{		
		resolvedMeemsCache.put(searchMeemPath, meem);

		Set<MeemResolverClient> clients = clientsForMeemPaths.get(searchMeemPath);

		if (clients != null)
		{
			for (MeemResolverClient client : clients) {
				client.meemResolved(searchMeemPath, meem);
			}
		}
	}

	/**
	 * @param newEntryPath non-storage path of resolved entry (eg: hyperspace:/a/b/c);
	 * @param path storage path of resolved entry (eg: meemstore:/uid)
	 * @param meemPathsVector Vector of MeemPaths that are to be watched
	 */
	public synchronized void handleEntryResolved(MeemPath newEntryPath, MeemPath path, Vector<MeemPath> meemPathsVector)
	{
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
		{
			logger.log(LOG_LEVEL,
				"handleEntryResolved() newEntryPath: " + newEntryPath + " MeemPath: " + path);
		}

		for (MeemPath meemPath : meemPathsVector) 
		{
			if (meemPath.equals(newEntryPath))
			{
				// we've found what we are looking for
				pathResolved(meemPath, path);
			}
			else
			{
				// we need to keep looking
				// grab the next bit of path 
				String nextEntry = null;

				String relativePath = meemPath.toString().substring(newEntryPath.toString().length());
				StringTokenizer tok = new StringTokenizer(relativePath, "/");
				if (tok.hasMoreTokens())
				{
					nextEntry = tok.nextToken();
				}

				if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
				{
					logger.log(LOG_LEVEL, "handleEntryResolved() locateMeem():" + path);
				}

				// start watching for it
				
				synchronized(monitorDetails) {
					
					List<MonitorDetail> monitors = monitorDetails.get(path);
					if (monitors == null) {
						monitors = new Vector<MonitorDetail>();
						monitorDetails.put(path, monitors);
					}
					
					MonitorDetail monitorDetail = new MonitorDetail(newEntryPath, nextEntry, meemPath);
					monitors.add(monitorDetail);
				}
				
				getRegistryMonitor().addPathToWatch(path);
			}
		}

	}

	public synchronized void handleMeemRegistered(MeemPath meemPath, Meem meem)
	{
		if (meem == null)
		{
			logger.log(Level.WARNING, "handleMeemRegistered() Got null meem for " + meemPath);
			return;
		}
		List<MonitorDetail> monitors = null;
		
		synchronized(monitorDetails) {
			monitors = monitorDetails.remove(meemPath);
		}
		if (monitors != null)
		{
			facetClientConduit.hasA(meem, "categoryClient",	CategoryClient.class, Direction.OUTBOUND,
				new MeemRegisteredCategoryCallback(meem, monitors));
		}
		else
		{
			ResolvedDetail resolvedDetail = (ResolvedDetail) resolvedDetails.get(meemPath);
			if (resolvedDetail != null)
			{
				pathResolvedMeemRegistered(meem, resolvedDetail.getSearchMeemPath(),
					resolvedDetail.getResolvedMeemPath());
			}
		}
	}

	public synchronized void handleMeemDeregistered(MeemPath meemPath)
	{
		ResolvedDetail resolvedDetail = (ResolvedDetail) resolvedDetails.get(meemPath);
		if (resolvedDetail != null)
		{
			pathResolvedMeemRegistered(null, resolvedDetail.getSearchMeemPath(), resolvedDetail.getResolvedMeemPath());
		}
	}

	/**
	 * @param removedEntryPath non-storage path of resolved entry (eg: hyperspace:/a/b/c);
	 * @param meemPaths Vector of MeemPaths that are to be watched
	 */
	public synchronized void handleEntryRemoved(final MeemPath removedEntryPath, List<MeemPath> meemPaths)
	{
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
		{
			logger.log(MeemResolverWedge.LOG_LEVEL, "entryRemoved: " + removedEntryPath);
		}

		for (MeemPath meemPath : meemPaths) 
		{
			if (meemPath.equals(removedEntryPath))
			{
				// notify this client with a null path to notify that the meempath is no longer valid
				pathResolved(meemPath, null);
				break;
			}
		}

		// we need to trash all the CategoryWatchers with meempaths that start with this meempath
		// get the parent CategoryMonitorMapPair and remove this entry from it
		LinkedList<String> paths = new LinkedList<String>();

		String location = removedEntryPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");
		while (tok.hasMoreTokens())
		{
			paths.add(tok.nextToken());
		}

		StringBuffer pathBuffer = new StringBuffer();
		for (int i = 0; i < paths.size() - 1; i++)
		{
			pathBuffer.append("/");
			pathBuffer.append((String) paths.get(i));
		}

		MeemPath parentMeemPath = MeemPath.spi.create(removedEntryPath.getSpace(), pathBuffer.toString());

		CategoryMonitorMapPair pair = categoryMonitorTree.getCategoryMonitorMapPair(parentMeemPath);

		String pathName = (String) paths.get(paths.size() - 1);

		pair.removeChild(pathName);

	}

	/**
	 * 
	 * @param category Category Meem to watch
	 * @param categoryPath Non-storage path to the category
	 * @param entryName entry name to watch for
	 * @param fullMeemPath full meempath of the meem that is ultimately being watched for
	 */
	private void createCategoryMonitor(
		Meem categoryMeem,
		MeemPath categoryPath,
		String entryName,
		MeemPath fullMeemPath)
	{

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
		{
			logger.log(LOG_LEVEL, "createCategoryMonitor(): " + entryName + " : " + fullMeemPath);
		}

		CategoryMonitorMapPair pair = categoryMonitorTree.getCategoryMonitorMapPair(categoryPath);

		CategoryMonitor categoryMonitor;
		if (pair == null)
		{
			// this path isn't being watched 
			categoryMonitor = new CategoryMonitor(this, categoryMeem, categoryPath);

			CategoryClient categoryClient =
				(CategoryClient) meemCore.getTargetFor(categoryMonitor, CategoryClient.class);

			Reference reference = Reference.spi.create("categoryClient", categoryClient, true);
			categoryMonitor.setReference(reference);

			// add to category watcher tree
			categoryMonitorTree.addCategoryMonitor(categoryPath, categoryMonitor);
		}
		else
		{
			categoryMonitor = pair.getCategoryMonitor();
		}

		categoryMonitor.addEntryToWatchFor(entryName, fullMeemPath);
	}
	

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition()
	{
		if (meemDefinition == null)
		{
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass()});
		}

		return (meemDefinition);
	}

	
	/* -------- inner classes ----------- */
	
	/**
	 * this class must be declared public.
	 */
	public final class ResolvedMeemPathCategoryCallback implements CategoryClient, ContentClient
	{
		private final StringTokenizer tok;
		private final MeemPath meemPath;
		private final MeemResolverClient client;
		private final ContentClient contentClient;
		private Facet categoryClientTarget;
		private CategoryEntry[] entries = null;

		public ResolvedMeemPathCategoryCallback(MeemPath meemPath, MeemResolverClient client, ContentClient contentClient)
		{
			String location = meemPath.getLocation();
			this.tok = new StringTokenizer(location, "/");

			this.meemPath = meemPath;
			this.client = client;
			this.contentClient = contentClient;
			this.categoryClientTarget = null;
		}

		/**
		 * If the entry name exists assign meem.
		 * 
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(CategoryEntry[] newEntries)
		{
			if (DEBUG) {
				logger.log(Level.INFO, "entriesAdded: " + newEntries);
			}
			this.entries = newEntries;
		}

		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries)
		{
			throw new IllegalStateException("entriesRemoved called!");
		}

		/**
		 * @see org.openmaji.system.space.CategoryClient#entryRenamed(org.openmaji.system.space.CategoryEntry, org.openmaji.system.space.CategoryEntry)
		 */
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry)
		{
			throw new IllegalStateException("entryRenamed called!");
		}

		/**
		 * We've either been given a meem or not...
		 * 
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent()
		{
			complete();

			Meem entry = null;
			if (this.entries != null)
			{
				CategoryEntry categoryEntry = this.entries[0];
				entry = categoryEntry.getMeem();
			}

			if (entry == null)
			{
				client.meemResolved(meemPath, null);
				contentClient.contentSent();
			}
			else
			{
				proceed(entry);
			}
		}

		/**
		 * Failure - we'll abandon ship.
		 * 
		 */
		public void contentFailed(String reason)
		{
			complete();

			// TODO[peter] Should we propagate the contentFailed instead?
			client.meemResolved(meemPath, null);
			//contentClient.contentSent();
			contentClient.contentFailed(reason);
		}

		public void proceed(Meem meem)
		{
			if (DEBUG) {
				logger.log(Level.INFO, "proceeding with meem: " + meem + " remaining: " + tok.countTokens());
			}
			
			if (tok.hasMoreTokens())
			{
				String entryName = tok.nextToken();
				Filter filter = new ExactMatchFilter(entryName);

				if (DEBUG) {
					logger.log(Level.INFO, "getting entry, " + entryName + ", of " + meem);
				}
				
				this.categoryClientTarget = meemCore.getLimitedTargetFor(this, CategoryClient.class);
				Reference categoryClientReference = Reference.spi.create("categoryClient", categoryClientTarget, true, filter);

				this.entries = null;
				meem.addOutboundReference(categoryClientReference, true);
			}
			else
			{
				client.meemResolved(meemPath, meem);
				contentClient.contentSent();
			}
		}

		private void complete()
		{
			((MeemCoreImpl) meemCore).revokeTargetProxy(this.categoryClientTarget, this);
			categoryClientTarget = null;
		}
	}
	

	private final class RegistryLookup implements MeemRegistryClient, ContentClient
	{
		private final MeemPath meemPath;
		private final MeemResolverClient meemResolverClient;
		private final ContentClient contentClient;
		private Meem resolvedMeem = null;
		
		public RegistryLookup(MeemPath meemPath, MeemResolverClient meemResolverClient, ContentClient contentClient)
		{
			this.meemPath = meemPath;
			this.meemResolverClient = meemResolverClient;
			this.contentClient = contentClient;

			Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(
				MeemRegistryGateway.spi.getIdentifier());

			Reference reference = Reference.spi.create(
				"meemRegistryClient",
				meemCore.getLimitedTargetFor(this, MeemRegistryClient.class),
				true,
				new ExactMatchFilter(meemPath));

			meemRegistryGateway.addOutboundReference(reference, true);
		}

		public void meemRegistered(Meem meem)
		{
//			System.err.println("RegistryLookup.meemRegistered: " + meem);
			this.resolvedMeem = meem;
		}

		public void meemDeregistered(Meem arg0)
		{
		}

		public void contentSent()
		{
//			System.err.println("RegistryLookup.contentSent()");
			meemResolverClient.meemResolved(meemPath, resolvedMeem);
			contentClient.contentSent();
		}

		public void contentFailed(String reason)
		{
//			System.err.println("RegistryLookup.contentFailed(): " + reason);
			contentClient.contentFailed(reason);
		}
	}
	
	private final class MeemRegisteredCategoryCallback implements FacetClientCallback
	{
		private final Meem meem;
		private final List<MonitorDetail> monitors;

		public MeemRegisteredCategoryCallback(Meem meem, List<MonitorDetail> monitors)
		{
			this.meem = meem;
			this.monitors = monitors;
		}

		/* (non-Javadoc)
		 * @see org.openmaji.meem.FacetClientCallback#returnIsAvailable(boolean)
		 */
		public void facetExists(boolean available)
		{
			if (!available)
			{
				// not a category. bad.
				throw new RuntimeException(
					"MeemResolver expecting a Category got something else: " + meem.getMeemPath());
			}

			if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER)
			{
				logger.log(LOG_LEVEL,
					"handleMeemRegistered() " + meem.getMeemPath() + " : monitors: " + monitors);
			}
			
			for (MonitorDetail monitorDetail : monitors) {				
				createCategoryMonitor(
						meem, monitorDetail.getCategoryPath(),
						monitorDetail.getEntryName(), monitorDetail.getFullMeemPath()
					);
			}			
		}
	}
	
	private final class MonitorDetail
	{
		private MeemPath categoryPath;
		private String entryName;
		private MeemPath fullMeemPath;

		public MonitorDetail(MeemPath categoryPath, String entryName, MeemPath fullMeemPath)
		{
			this.categoryPath = categoryPath;
			this.entryName = entryName;
			this.fullMeemPath = fullMeemPath;
		}

		public MeemPath getCategoryPath()
		{
			return categoryPath;
		}

		public String getEntryName()
		{
			return entryName;
		}

		public MeemPath getFullMeemPath()
		{
			return fullMeemPath;
		}
	}

	private final class ResolvedDetail
	{
		private MeemPath searchMeemPath;
		private MeemPath resolvedMeemPath;

		public ResolvedDetail(MeemPath searchMeemPath, MeemPath resolvedMeemPath)
		{
			this.searchMeemPath = searchMeemPath;
			this.resolvedMeemPath = resolvedMeemPath;
		}

		public MeemPath getResolvedMeemPath()
		{
			return resolvedMeemPath;
		}

		public MeemPath getSearchMeemPath()
		{
			return searchMeemPath;
		}
	}

}