/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;


import org.meemplex.meem.Conduit;
import org.meemplex.meem.Facet;
import org.meemplex.meem.FacetContent;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.ResourceHandler;
import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.implementation.server.meemkit.Meemkit;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.implementation.server.utility.FileUtility;
import org.openmaji.implementation.server.utility.JarUtility;
import org.openmaji.implementation.server.utility.URLUtility;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemkit.core.MeemkitDependency;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemkit.core.MeemkitManager;
import org.openmaji.system.meemkit.core.MeemkitManagerClient;
import org.openmaji.system.meemkit.core.MeemkitVersion;
import org.openmaji.system.request.RequestContext;
import org.openmaji.system.request.RequestCreationException;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;


import java.util.logging.Level;
import java.util.logging.Logger;


public class MeemkitManagerWedge implements Wedge, MeemkitManager, Meemkit, JiniLookupClient, CategoryClient, LifeCycleManagerClient {

	@org.meemplex.meem.MeemContext
	public MeemContext meemContext;

	public MeemCore meemCore;

	public RequestContext requestContext;


	/* ---------------------- outbound facets --------------------------- */

	/**
	 * 
	 */
	@Facet(direction=org.meemplex.service.model.Direction.OUT)
	public LifeCycleManager lifeCycleManager;

	/**
	 * 
	 */
	@Facet(direction=org.meemplex.service.model.Direction.OUT)
	public MeemkitManagerClient meemkitManagerClientOutput;

	@FacetContent(facet="meemkitManagerClientOutput")
	public void sendMeemkitManagerClientContent(MeemkitManagerClient target, Filter filter) {
		MeemkitDescriptor[] meemDescs = new MeemkitDescriptor[descriptors.size()];
		descriptors.values().toArray(meemDescs);
		target.meemkitDescriptorsAdded(meemDescs);
		for (String s : installedMeemkits) {
			target.meemkitInstalled(s);
		}
	}

	public final ContentProvider meemkitManagerClientOutputProvider = new MeemkitManagerClientProvider();

	/**
	 * 
	 */
	@Facet(direction=org.meemplex.service.model.Direction.OUT)
	public MeemkitLifeCycleManager meemkitLifeCycleManagerOutput;

	@FacetContent(facet="meemkitLifeCycleManagerOutput")
	public void sendMeemkitLcmContent(MeemkitLifeCycleManager lcm, Filter filter) {
		String[] names = new String[installedMeemkits.size()];
		URL[] urls = new URL[installedMeemkits.size()];
		int index = 0;
		for (String meemkitName : installedMeemkits) {
			names[index] = meemkitName;
			urls[index] = descriptorURLs.get(meemkitName);
			index++;
		}
		lcm.detailsChanged(names, urls);
	}
	
	public final ContentProvider meemkitLifeCycleManagerOutputProvider = new MeemkitLifeCycleManagerProvider();

	/**
	 * Category for grouping LCM
	 */
	@Facet(direction=org.meemplex.service.model.Direction.OUT)
	public Category category;


	/* -------------------------- conduits -------------------------- */

	@Conduit
	public ErrorHandler errorHandlerConduit;

	@Conduit
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	@Conduit
	public JiniLookup jiniLookupConduit;

	@Conduit
	public JiniLookupClient jiniLookupClientConduit = this;

	@Conduit
	public Vote lifeCycleControlConduit = null;

	@Conduit
	public DependencyHandler dependencyHandlerConduit;

	@Conduit
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	@Conduit
	public MeemClientConduit meemClientConduit;

	@Conduit
	public MeemPatternControl meemPatternControlConduit;

	@Conduit
	public MeemPatternState meemPatternStateConduit = new MeemPatternStateConduit();

	@Conduit
	public MeemkitClassloaderMonitor meemkitClassloaderMonitorConduit;

	@Conduit
	public ThreadManager threadManagerConduit;


	/* ------------------------- private members ---------------------------- */

	private List<DependencyAttribute> categoryDependencies = new ArrayList<DependencyAttribute>();

	private String meemkitManagerDirectory;

	private String availableMeemkitsDirectory;

	private String installedMeemkitsDirectory;

	private final Map<String, URL> descriptorURLs = new HashMap<String, URL>();

	private final Map<String, MeemkitDescriptor> descriptors = new HashMap<String, MeemkitDescriptor>();

	/**
	 * Number of outstanding requests for pattern meems
	 */
	private final Map<String, Integer> outstandingRequests = new HashMap<String, Integer>();

	private final Map<String, JarDetails> jarsDetails = new Hashtable<String, JarDetails>();

	private final List<String> installedMeemkits = Collections.synchronizedList(new ArrayList<String>());

	private final List<String> availableMeemkits = Collections.synchronizedList(new ArrayList<String>());

	private final List<DependencyAttribute> dependencyAttributes = Collections.synchronizedList(new ArrayList<DependencyAttribute>());

	private DependencyAttribute dependencyAttributeLCM;

	private DependencyAttribute dependencyAttributeLCMClient;

	private final Random random = new Random();

	private Meem categoryMeem;

	private int numberMeemkitLifeCycleManagers = 0;

	private boolean patternMeemCategoryLocated = false;

	private boolean patternWedgeCategoryLocated = false;

	private DependencyAttribute resolverMeemDependencyAttribute;

	private DependencyAttribute resolverWedgeDependencyAttribute;

	private Server server;

	private String hostAddress = null;

	private int httpPort;

	/**
	 * The MeemKit Lifecycle Meem
	 */
	private Meem ownMeemkitLCMMeem;

	private boolean ownMeemkitLCMMeemStarted = false;

	private boolean meemkitLCMDiscoveryStarted = false;

	private int newMeemkitsPollTimeSeconds = -1;

	private final Set<String> entries = new HashSet<String>();

	/* ------------------ Meem functionality ---------------------------------- */

	public void commence() {
		try {
			getSystemProperties();
			startHTTPServer();
			determineInstalledMeemkits();
			determineAvailableMeemkits();
			startHyperSpaceResolver();
			createCategory();
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
		}

		// We don't indicate that we can go READY until all our dependencies have
		// been setup to the Category of MeemkitLCMs
		if (DEBUG) {
			logger.log(Level.INFO, "commence() MeemkitManagerWedge voting false");
		}
		lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
	}

	public void conclude() {
		jiniLookupConduit.stopLookup();
		meemkitLCMDiscoveryStarted = false;

		try {
			server.stop();
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
		}

		for (DependencyAttribute dependencyAttribute : dependencyAttributes) {
			dependencyHandlerConduit.removeDependency(dependencyAttribute);
		}

		dependencyHandlerConduit.removeDependency(dependencyAttributeLCM);
		dependencyHandlerConduit.removeDependency(dependencyAttributeLCMClient);
		descriptors.clear();
		descriptorURLs.clear();
		installedMeemkits.clear();
		availableMeemkits.clear();

		if (categoryMeem != null) {
			MeemClientCallback callback = new MyCallback();
			meemClientConduit.provideReference(categoryMeem, LifeCycle.spi.getIdentifier(), LifeCycle.class, callback);
		}
	}

	/* ------------------ MeemkitManager facet methods ------------------------ */

	public void installMeemkit(String meemkitName, URL meemkitURL) {
		if (DEBUG) {
			logger.log(Level.INFO, "installing meemkit: " + meemkitName);
		}

		//TODO the meemkitName argument should be removed from this Facet
		// because the meemkitName is extracted from the downloaded jar

		if (meemkitName == null && meemkitURL == null) {
			Exception ex = new MeemkitManagerException("A meemkitName or meemkitURL must be provided");
			errorHandlerConduit.thrown(ex);
			return;
		}

		try {
			doInstallMeemkit(meemkitURL);
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
		}
	}

	public void installMeemkit(String meemkitName) {
		if (DEBUG) {
			logger.log(Level.INFO, "installing meemkit: " + meemkitName);
		}

		if (meemkitName == null) {
			Exception ex = new MeemkitManagerException("A meemkitName must be provided");
			errorHandlerConduit.thrown(ex);
			return;
		}

		try {
			doInstallMeemkit(meemkitName);
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
		}
	}

	public void upgradeMeemkit(String meemkitName, URL meemkitURL) {
		//TODO the meemkitName argument should be removed from this Facet
		// because the meemkitName is extracted from the downloaded jar

		if (meemkitURL == null) {
			Exception ex = new MeemkitManagerException("MeemkitURL must not be null");
			errorHandlerConduit.thrown(ex);
			return;
		}

		try {
			doUpgradeMeemkit(meemkitURL);
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
		}
	}

	public void uninstallMeemkit(String meemkitName) {
		try {
			confirmCanUninstall(meemkitName);
		}
		catch (MeemkitManagerException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		installedMeemkits.remove(meemkitName);
		descriptorURLs.remove(meemkitName);

		MeemkitDescriptor descriptor = (MeemkitDescriptor) descriptors.get(meemkitName);
		meemPatternControlConduit.uninstallPatternMeems(descriptor);
	}

	/* ------------------ Inbound Meemkit facet methods ----------------------- */

	public void detailsChanged(String[] names, URL[] descriptorLocations) {
		if (DEBUG) {
			logger.log(Level.INFO, "detailsChanged()");
		}

		/*
		 * This method is invoked in one of the following situations:
		 *   a) from an invocation via inbound MeemkitManager facet to install/uninstall ONE meemkit
		 *   b) after a restart and this wedge noticed that one or more meemkits have previously been installed
		 */

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (DEBUG) {
				logger.log(Level.INFO, "detailsChanged() name: " + names[i]);
			}
			URL descriptorLocation = null;
			if (descriptorLocations != null) {
				descriptorLocation = descriptorLocations[i];
			}
			Integer outstanding = (Integer) outstandingRequests.get(name);
			if (outstanding == null) {
				// This happens when the MeemServer restarts after having previously
				// installed one or more meemkits. In this case we don't want to re-install
				// the pattern meems in the toolkit.
				meemkitClassloaderMonitorConduit.meemkitClassloaderStarted(name);
				meemkitManagerClientOutput.meemkitInstalled(name);
				MeemkitDescriptor[] array = new MeemkitDescriptor[] { descriptors.get(name) };
				meemkitManagerClientOutput.meemkitDescriptorsAdded(array);
				continue;
			}

			if (outstanding.intValue() == 1) {
				// This is the last response from all the MeemkitLifeCycleManagers after
				// installing a meemkit.
				if (descriptorLocation == null) {
					meemkitClassloaderMonitorConduit.meemkitClassloaderStopped(name);
				}
				else {
					meemkitClassloaderMonitorConduit.meemkitClassloaderStarted(name);
				}
				// Now we need to either install or uninstall the pattern meems.
				outstandingRequests.remove(name);
				if (patternMeemCategoryLocated && patternWedgeCategoryLocated) {
					MeemkitDescriptor descriptor = (MeemkitDescriptor) descriptors.get(name);
					if (descriptorLocation == null) {
						meemkitManagerClientOutput.meemkitUninstalled(name);
						String directoryName = installedMeemkitsDirectory + File.separator + name;
						try {
							FileUtility.deleteAllFilesRecursively(directoryName);
						}
						catch (IOException ex) {
							errorHandlerConduit.thrown(ex);
						}
					}
					else {
						meemPatternControlConduit.installPatternMeems(descriptor);
					}
				}
			}
			else {
				// We still need to wait for one or more MeemkitLifeCycleManagers to finish
				Integer remaining = new Integer(outstanding.intValue() - 1);
				outstandingRequests.put(name, remaining);
			}
		}
	}

	/* ------------------ JiniLookupClient facet methods ---------------------- */

	/**
	 * Add Meem to category this Meem is depending on.
	 */
	public void meemAdded(Meem meem) {
		if (meem.equals(ownMeemkitLCMMeem)) {
			if (ownMeemkitLCMMeemStarted) {
				return;
			}
			else {
				ownMeemkitLCMMeemStarted = true;
			}
		}
		logger.log(Level.INFO, "meemAdded() - " + meem);
		String entryName = meem.getMeemPath().getLocation();
		if (!entries.contains(entryName)) {
			entries.add(entryName);
			category.addEntry(entryName, meem);
		}
	}

	public void meemRemoved(Meem meem) {
		logger.log(Level.INFO, "meemRemoved() - " + meem);
		String entryName = meem.getMeemPath().getLocation();
		entries.remove(entryName);
		category.removeEntry(entryName);
	}

	/* --------- Inbound CategoryClient facet methods ------------------------- */

	public void entriesAdded(CategoryEntry[] newEntries) {
		numberMeemkitLifeCycleManagers = numberMeemkitLifeCycleManagers + newEntries.length;
	}

	public void entriesRemoved(CategoryEntry[] removedEntries) {
		numberMeemkitLifeCycleManagers = numberMeemkitLifeCycleManagers - removedEntries.length;
	}

	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		// This should never happen
	}

	/* --------- Inbound LifeCycleManagerClient facet methods ----------------- */

	public void meemCreated(Meem meem, String identifier) {
		if (DEBUG) {
			logger.log(Level.INFO, "meemCreated: " + identifier + " " + meem.getMeemPath());
		}

		Object context = requestContext.get();
		if (context == null) {
			return;
		}

		if (DEBUG) {
			logger.log(Level.INFO, "meemCreated (in context): " + identifier + " " + meem.getMeemPath());
		}

		DependencyAttribute attr;
		categoryMeem = meem;
		Integer id = (Integer) context;
		if (id.toString().equals(identifier)) {
			if (DEBUG) {
				logger.log(Level.INFO, "meemCreated (in context and right id): " + identifier + " " + meem.getMeemPath());
			}

			attr = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, meem, "category", null, true);
			dependencyHandlerConduit.addDependency("category", attr, LifeTime.TRANSIENT);
			dependencyAttributes.add(attr);
			categoryDependencies.add(attr);

			attr = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, meem, "categoryClient", null, true);
			dependencyHandlerConduit.addDependency("categoryClient", attr, LifeTime.TRANSIENT);
			dependencyAttributes.add(attr);
			categoryDependencies.add(attr);

			// depend on children of the category
			attr = new DependencyAttribute(DependencyType.WEAK_MANY, Scope.LOCAL, meem, "meemkitLifeCycleManager", null, true);
			dependencyHandlerConduit.addDependency("meemkitLifeCycleManagerOutput", attr, LifeTime.TRANSIENT);
			dependencyAttributes.add(attr);
			categoryDependencies.add(attr);

			attr = new DependencyAttribute(DependencyType.WEAK_MANY, Scope.LOCAL, meem, "meemkitOutput", null, true);
			dependencyHandlerConduit.addDependency("meemkit", attr, LifeTime.TRANSIENT);
			dependencyAttributes.add(attr);
			categoryDependencies.add(attr);
		}

		requestContext.end();
	}

	public void meemDestroyed(Meem meem) {
	}

	public void meemTransferred(Meem meem, LifeCycleManager lcm) {
	}

	/* ------------------ private helper methods ------------------------------ */

	private void startHyperSpaceResolver() {
		MeemResolverClient client = new MyMeemResolverClient();
		MeemResolverClient resolverClientFacet = meemCore.getTargetFor(client, MeemResolverClient.class);

		Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());

		MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM);
		resolverMeemDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, resolverMeem, "meemResolverClient", new ExactMatchFilter(meemPath), false);
		dependencyHandlerConduit.addDependency(resolverClientFacet, resolverMeemDependencyAttribute, LifeTime.TRANSIENT);

		meemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_WEDGE);
		resolverWedgeDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, resolverMeem, "meemResolverClient", new ExactMatchFilter(meemPath), false);
		dependencyHandlerConduit.addDependency(resolverClientFacet, resolverWedgeDependencyAttribute, LifeTime.TRANSIENT);
	}

	private JarDetails getJarDetails(String jarFilename) throws IOException, MeemkitManagerException {
		// Grab a local copy of the, possibly remote, meemkit jar. Then extract the
		// meemkit descriptor and determine the name of the meemkit

		File jarFile = new File(jarFilename);
		File tempFile = File.createTempFile("descriptor", ".xml", new File(availableMeemkitsDirectory));
		JarUtility.unjarEntryWithName(jarFilename, "meemkitDescriptor.xml", tempFile.getCanonicalPath());
		MeemkitDescriptor descriptor = MeemkitUtility.createMeemkitDescriptor(tempFile.getCanonicalPath());
		tempFile.delete();
		if (descriptor == null) {
			throw new MeemkitManagerException("Malformed meemkit descriptor in " + jarFilename);
		}

		// Rename the jar file and the meemkit descriptor

		JarDetails details = new JarDetails();
		details.meemkitName = descriptor.getHeader().getName();
		details.meemkitDescriptor = descriptor;
		details.jarFilename = jarFilename;
		details.jarLastModified = jarFile.lastModified();
		return details;
	}

	private void doUpgradeMeemkit(URL meemkitURL) throws IOException, MeemkitManagerException {
		String jarFilename = URLUtility.downloadToDirectory(meemkitURL, availableMeemkitsDirectory);
		JarDetails jarDetails = getJarDetails(jarFilename);
		String meemkitName = jarDetails.meemkitName;
		if (installedMeemkits.contains(meemkitName) == false) {
			throw new MeemkitManagerException("'" + meemkitName + "' is not installed. Can not upgrade");
		}

		File file = new File(jarFilename);
		String directoryEntryname = file.getName();
		jarsDetails.put(directoryEntryname, jarDetails);

		doUpgradeMeemkit(jarDetails);
	}

	private void doUpgradeMeemkit(JarDetails jarDetails) {
		try {
			confirmCanUpgrade(jarDetails.meemkitName);
		}
		catch (MeemkitManagerException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		String meemkitName = jarDetails.meemkitName;
		MeemkitManagerRequest request = new MeemkitManagerRequest(UPGRADE_REQUEST_ID, jarDetails);
		try {
			requestContext.begin(Integer.MAX_VALUE, request);
		}
		catch (RequestCreationException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		// Uninstall the pattern meems first. Once that is done we will tell all
		// the MeemkitLifeCycleManagers to recreate the appropriate Meemkit meem

		MeemkitDescriptor descriptor = (MeemkitDescriptor) descriptors.get(meemkitName);
		descriptors.remove(meemkitName);
		meemkitManagerClientOutput.meemkitDescriptorsRemoved(new MeemkitDescriptor[] { descriptor });
		meemPatternControlConduit.uninstallPatternMeems(descriptor);
	}

	private void confirmCanUpgrade(String dependeeName) throws MeemkitManagerException {
		MeemkitDescriptor dependeeDescriptor = (MeemkitDescriptor) descriptors.get(dependeeName);

		for (String meemkitName : installedMeemkits) {
			MeemkitDescriptor descriptor = descriptors.get(meemkitName);
			if (descriptor != null) {
				if (descriptor.dependsOn(dependeeDescriptor)) {
					// compare the version numbers
					throw new MeemkitManagerException("This meemkit is depended on by other meemkits - FIX THIS CHRISTOS!");
				}
			}
		}

		// If we get to this point then it means that none of the installed meemkits
		// is dependent on the meemkit selected to be upgraded.
	}

	private void confirmCanUninstall(String dependeeName) throws MeemkitManagerException {
		MeemkitDescriptor dependeeDescriptor = (MeemkitDescriptor) descriptors.get(dependeeName);
		if (dependeeDescriptor == null) {
			throw new MeemkitManagerException("Meemkit '" + dependeeName + "' is not installed");
		}

		for (String meemkitName : installedMeemkits) {
			MeemkitDescriptor descriptor = descriptors.get(meemkitName);
			if (descriptor != null) {
				if (descriptor.dependsOn(dependeeDescriptor)) {
					throw new MeemkitManagerException("This meemkit is depended on by other meemkits");
				}
			}
		}

		// If we get to this point then it means that none of the installed meemkits
		// is dependent on the meemkit selected to be uninstalled.
	}

	private void confirmCanInstall(String meemkitName) throws MeemkitManagerException {
		MeemkitDescriptor descriptor = descriptors.get(meemkitName);
		if (descriptor == null) {
			throw new MeemkitManagerException("Meemkit descriptor not available for that meemkit");
		}

		if (installedMeemkits.size() == 0) {
			return;
		}

		List<MeemkitVersion> installedVersions = new ArrayList<MeemkitVersion>();
		for (String installedMeemkitName : installedMeemkits) {
			MeemkitDescriptor meemkitDescriptor = descriptors.get(installedMeemkitName);
			MeemkitVersion version = meemkitDescriptor.getHeader().getMeemkitVersion();
			installedVersions.add(version);
		}

		if (descriptor.allDependenciesInstalled(installedVersions)) {
			return;
		}
		else {
			throw new MeemkitManagerException("Please install this Meemkit's dependencies first");
		}
	}

	private void doInstallMeemkit(String meemkitName) throws IOException, MeemkitManagerException {
		JarDetails details = null;
		for (JarDetails temp : jarsDetails.values()) {
			if (temp.meemkitName.equals(meemkitName)) {
				details = temp;
			}
		}

		if (details == null) {
			Exception ex = new Exception("The meemkit '" + meemkitName + "' is not known");
			errorHandlerConduit.thrown(ex);
			return;
		}

		doInstallMeemkit(details);
	}

	private void doInstallMeemkit(URL meemkitURL) throws IOException, MeemkitManagerException {
		logger.log(Level.INFO, "installing meemkit at: " + meemkitURL);
		
		String jarFilename = URLUtility.downloadToDirectory(meemkitURL, availableMeemkitsDirectory);
		JarDetails details = getJarDetails(jarFilename);

		String meemkitName = details.meemkitName;
		if (installedMeemkits.contains(meemkitName)) {
			throw new MeemkitManagerException("'" + meemkitName + "' is already installed");
		}

		File file = new File(jarFilename);
		String directoryEntryname = file.getName();
		jarsDetails.put(directoryEntryname, details);

		descriptors.put(meemkitName, details.meemkitDescriptor);
		meemkitManagerClientOutput.meemkitDescriptorsAdded(new MeemkitDescriptor[] { details.meemkitDescriptor });

		doInstallMeemkit(details);
	}

	private void doInstallMeemkit(JarDetails jarDetails) throws IOException, MeemkitManagerException {
		if (DEBUG) {
			logger.log(Level.INFO, "doInstallMeemkit(): " + jarDetails);
		}
		
		if (DISABLE) {
			throw new MeemkitManagerException("Installing meemkits via meemkit manager has been disabled");
		}

		confirmCanInstall(jarDetails.meemkitName);

		try {
			requestContext.begin(Integer.MAX_VALUE, new MeemkitManagerRequest(INSTALL_REQUEST_ID));
		}
		catch (RequestCreationException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		unpackMeemkit(jarDetails, true);
		createMeemkitMeem(jarDetails.meemkitName);
	}

	private void unpackMeemkit(JarDetails jarDetails, boolean installing) throws IOException, MeemkitManagerException {
		String meemkitName = jarDetails.meemkitName;
		String destinationDirectory = installedMeemkitsDirectory + File.separator + meemkitName;
		File destinationFile = new File(destinationDirectory);
		if (installing) {
			if (destinationFile.exists() == true) {
				throw new MeemkitManagerException("Meemkit '" + meemkitName + "' is already installed");
			}
		}
		else {
			if (destinationFile.exists() == false) {
				throw new MeemkitManagerException("That meemkit is not installed - can not upgrade");
			}
		}

		String jarFilename = jarDetails.jarFilename;
		JarUtility.unjar(jarFilename, destinationDirectory);

		String installedDescriptorName = installedMeemkitsDirectory + File.separator + meemkitName + File.separator + "meemkitDescriptor.xml";
		JarUtility.unjarEntryWithName(jarFilename, "meemkitDescriptor.xml", installedDescriptorName);

		// If we install a meemkit but are unable to create the pattern meems
		// because Hyperspace isn't available yet then we create a temporary lock file
		// to keep track of which meemkits require their patterns installed

		String patternLockName = determinePatternLockName(meemkitName);
		FileUtility.dumpToFile(patternLockName, "bum");
	}

	/**
	 * 
	 * @param meemkitName
	 * @throws MalformedURLException
	 */
	private void createMeemkitMeem(String meemkitName) throws MalformedURLException {
		String path = HTTP_SERVER_CONTEXT_PATH + "/" + meemkitName + "/meemkitDescriptor.xml";
		URL meemkitDescriptorURL = new URL("http", hostAddress, httpPort, path);

		logger.log(Level.INFO, "createMeemkitMeem(" + meemkitName + ") - " + meemkitDescriptorURL);

		descriptorURLs.put(meemkitName, meemkitDescriptorURL);

		outstandingRequests.put(meemkitName, new Integer(numberMeemkitLifeCycleManagers));
		meemkitLifeCycleManagerOutput.detailsChanged(new String[] { meemkitName }, new URL[] { meemkitDescriptorURL });
	}

	/**
	 * 
	 * @param meemkitName
	 * @return
	 */
	private String determinePatternLockName(String meemkitName) {
		return installedMeemkitsDirectory + File.separator + INSTALL_PATTERNS_PREFIX + meemkitName;
	}

	private String[] getPatternLockNames() {
		String[] names = null;

		File file = new File(installedMeemkitsDirectory);
		String[] entries = file.list();
		int count = 0;
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].startsWith(INSTALL_PATTERNS_PREFIX))
				count++;
		}
		if (count == 0)
			return null;
		names = new String[count];
		int index = 0;
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].startsWith(INSTALL_PATTERNS_PREFIX)) {
				names[index++] = entries[i].substring(INSTALL_PATTERNS_PREFIX.length());
			}
		}
		return names;
	}

	/**
	 * Connect this Meem with the transient lifecycle manager in order to create a category Meem
	 */
	private void createCategory() {
		Meem lcmMeem = EssentialMeemHelper.getEssentialMeem(TransientLifeCycleManagerMeem.spi.getIdentifier());
		dependencyAttributeLCM = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, lcmMeem, "lifeCycleManager", null, true);
		dependencyHandlerConduit.addDependency("lifeCycleManager", dependencyAttributeLCM, LifeTime.TRANSIENT);

		logger.log(Level.INFO, "created LCM dependencyAttributes: " + dependencyAttributeLCM.getKey());

		dependencyAttributeLCMClient = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, lcmMeem, "lifeCycleManagerClient", null, false);
		dependencyHandlerConduit.addDependency("lifeCycleManagerClient", dependencyAttributeLCMClient, LifeTime.TRANSIENT);
	}

	private void getSystemProperties() throws Exception {
		String temp = System.getProperty(PROPERTY_MEEMKIT_MANAGER_POLL_NEW_MEEMKITS);
		if (temp != null) {
			try {
				newMeemkitsPollTimeSeconds = Integer.parseInt(temp);
			}
			catch (NumberFormatException ex) {
				logger.log(Level.WARNING, "Unable to parse property '" + PROPERTY_MEEMKIT_MANAGER_POLL_NEW_MEEMKITS + "'. Ignoring it.");
			}
			if (newMeemkitsPollTimeSeconds < 1) {
				logger.log(Level.WARNING, "'" + PROPERTY_MEEMKIT_MANAGER_POLL_NEW_MEEMKITS + "' must be greater than 0");
				newMeemkitsPollTimeSeconds = -1;
			}
		}

		temp = System.getProperty(PROPERTY_MEEMKIT_MANAGER_HTTP_PORT);
		if (temp == null || temp.length() == 0)
			throw new Exception("System property '" + PROPERTY_MEEMKIT_MANAGER_HTTP_PORT + "' not set");

		try {
			httpPort = Integer.parseInt(temp);
		}
		catch (NumberFormatException ex) {
			throw new Exception("System property '" + PROPERTY_MEEMKIT_MANAGER_HTTP_PORT + "' is not an integer");
		}

		meemkitManagerDirectory = System.getProperty(PROPERTY_MEEMKIT_MANAGER_DIRECTORY);
		if (meemkitManagerDirectory == null || meemkitManagerDirectory.length() == 0)
			throw new Exception("System property '" + PROPERTY_MEEMKIT_MANAGER_DIRECTORY + "' not set");

		File file = new File(meemkitManagerDirectory);
		if (file.exists() && file.isFile()) {
			throw new Exception(file.getPath() + " is not a directory");
		}

		if (file.exists() == false)
			file.mkdirs();

		availableMeemkitsDirectory = meemkitManagerDirectory + AVAILABLE_MEEMKITS_DIRECTORY;
		File availableFile = new File(availableMeemkitsDirectory);
		if (availableFile.exists() == false)
			availableFile.mkdir();

		installedMeemkitsDirectory = meemkitManagerDirectory + INSTALLED_MEEMKITS_DIRECTORY;
		File installedFile = new File(installedMeemkitsDirectory);
		if (installedFile.exists() == false)
			installedFile.mkdir();
	}

	private void determineInstalledMeemkits() throws Exception {
		File file = new File(installedMeemkitsDirectory);
		File[] entries = file.listFiles();
		if (entries == null)
			return;

		for (int i = 0; i < entries.length; i++) {
			File entry = entries[i];
			if (entry.isDirectory()) {
				String entryName = entry.getName();
				installedMeemkits.add(entryName);
				String path = HTTP_SERVER_CONTEXT_PATH + "/" + entryName + "/meemkitDescriptor.xml";
				URL meemkitDescriptorURL = new URL("http", hostAddress, httpPort, path);
				logger.log(Level.INFO, "determineInstalledMeemkits() - " + meemkitDescriptorURL);
				descriptorURLs.put(entryName, meemkitDescriptorURL);
			}
		}
	}

	private void determineAvailableMeemkits() throws IOException, MeemkitManagerException {
		File availableDirectoryFile = new File(availableMeemkitsDirectory);
		File[] entries = availableDirectoryFile.listFiles();
		for (int i = 0; i < entries.length; i++) {
			String entryName = entries[i].getName();
			int index = entryName.indexOf(MeemkitDescriptor.ARCHIVE_SUFFIX);
			if (index != -1) {
				String filename = entries[i].getCanonicalPath();
				JarDetails jarDetails = getJarDetails(filename);
				descriptors.put(jarDetails.meemkitName, jarDetails.meemkitDescriptor);
				jarsDetails.put(entryName, jarDetails);
			}
		}

		if (newMeemkitsPollTimeSeconds != -1) {
			Runnable runnable = new AvailableDirectoryWatcher();
			threadManagerConduit.queue(runnable, System.currentTimeMillis() + 60 * 1000);
		}
	}

	private boolean portAlreadyBound(String hostAddress, int port) {
		try {
			Socket socket = new Socket(hostAddress, port);
			socket.close();
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	private void startHTTPServer() throws Exception {
		// First determine what IP address the Jetty server should use
		
		String nicName = System.getProperty(PROPERTY_MEEMKIT_MANAGER_NIC);
		hostAddress = System.getProperty(PROPERTY_MEEMKIT_MANAGER_ADDRESS);

		InetAddress address = null;
		
		if (hostAddress != null) {
			try {
				address = InetAddress.getByName(hostAddress);
			}
			catch (UnknownHostException ex) {
				throw new Exception("validateHostAndPort() - Unknown host " + hostAddress);
			}
		}
		else if (nicName != null) {
			try {
				NetworkInterface ni = NetworkInterface.getByName(nicName);
				if (ni == null) {
					throw new Exception("no nic with name: " + nicName);
				}
				Enumeration<InetAddress> addressEnum = ni.getInetAddresses();
				if (addressEnum.hasMoreElements()) {
					address = addressEnum.nextElement();
				}
			}
			catch (SocketException ex) {
				throw new Exception("problem getting nic " + nicName);
			}
		}

		if (address == null) {
			address = InetAddress.getLocalHost();
			if (address.isLoopbackAddress()) {
				address = null;
			}
		}

		if (address == null) {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (address == null && networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = networkInterfaces.nextElement();
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while (address == null && addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					if ( !addr.isLoopbackAddress() && addr instanceof Inet4Address) {
						address = addr;
					}
				}
			}
		}
		
		if (address.isLoopbackAddress()) {
			throw new Exception("Your hostname is set to localhost. Can't proceed");
		}

		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
		if (networkInterface == null) {
			throw new Exception("No network interface with address " + address);
		}

		hostAddress = address.getHostAddress();
		if (portAlreadyBound(hostAddress, httpPort)) {
			throw new Exception("Something is already bound to " + hostAddress + ":" + httpPort);
		}
		
		System.err.println("Meemkit server: Using Address: " + hostAddress);

		// Now start the Jetty server
		// http://jetty.mortbay.org/jetty/tut/HttpServer.html

		server = new Server();
		SocketConnector connector = new SocketConnector();
		// bind to all nics
		//connector.setHost(hostAddress);
		connector.setPort(httpPort);

		server.addConnector(connector);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(installedMeemkitsDirectory);

		ContextHandler contextHandler = new ContextHandler(HTTP_SERVER_CONTEXT_PATH);
		contextHandler.addHandler(resourceHandler);

		//server.addHandler(resourceHandler);
		//server.addHandler(new DefaultHandler());

		//Context context = new Context(server, "/");
		//context.setContextPath(HTTP_SERVER_CONTEXT_PATH + "/*");
		//context.setResourceBase(installedMeemkitsDirectory);

		server.addHandler(contextHandler);
		server.addHandler(new DefaultHandler());

		server.start();
	}

	private void startMeemkitLCMDiscovery() {
		if (!meemkitLCMDiscoveryStarted) {
			meemkitLCMDiscoveryStarted = true;
			FacetItem facetItem = new FacetItem(MeemkitLifeCycleManager.spi.getIdentifier(), MeemkitLifeCycleManager.class.getName(), Direction.INBOUND);
			jiniLookupConduit.startLookup(facetItem, false);
			logger.log(Level.INFO, "startMeemkitLCMDiscovery() - MeemkitLCM Jini lookup initiated ...");
		}
	}

	/* ------------------------------------------------------------------------ */

	/*
	 * This inner class is used to resolve the Hyperspace meem. Once the Hyperspace
	 * meem has been resolved it is safe to create the pattern meems.
	 */
	private class MyMeemResolverClient implements MeemResolverClient {
		public void meemResolved(MeemPath meemPath, Meem meem) {
			if (Common.TRACE_ENABLED && Common.TRACE_MEEMKIT) {
				logger.log(Common.getLogLevel(), "meemResolved() meemPath=[" + meemPath.getLocation() + "] meem=[" + meem + "]");
			}

			if (meem == null)
				return;

			if (meemPath.equals(MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM))) {
				patternMeemCategoryLocated = true;
				dependencyHandlerConduit.removeDependency(resolverMeemDependencyAttribute);
			}

			if (meemPath.equals(MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_WEDGE))) {
				patternWedgeCategoryLocated = true;
				dependencyHandlerConduit.removeDependency(resolverWedgeDependencyAttribute);
			}

			if (patternMeemCategoryLocated && patternWedgeCategoryLocated) {
				String[] meemkitNames = getPatternLockNames();
				if (meemkitNames == null)
					return;

				try {
					startPatternMeemCreation(meemkitNames);
				}
				catch (Exception ex) {
					errorHandlerConduit.thrown(ex);
					return;
				}
			}
		}

		private void startPatternMeemCreation(String[] meemkitNames) throws RequestCreationException, MeemkitManagerException {
			// We need to install meemkits in the correct order so that their dependencies
			// are met before attempting to install them. To do this we repeatedly scan through
			// the list of meemkits installing those whose dependencies have been resolved.

			List<MeemkitVersion> started = new ArrayList<MeemkitVersion>();
			List<String> pending = new ArrayList<String>();

			for (int i = 0; i < meemkitNames.length; i++) {
				String meemkitName = meemkitNames[i];
				pending.add(meemkitName);
			}

			for (String meemkitName : installedMeemkits) {
				MeemkitDescriptor descriptor = descriptors.get(meemkitName);
				MeemkitVersion version = descriptor.getHeader().getMeemkitVersion();
				started.add(version);
			}

			while (pending.size() > 0) {
				boolean atLeastOneStarted = false;
				for (String meemkitName : pending) {
					MeemkitDescriptor descriptor = descriptors.get(meemkitName);
					MeemkitDependency[] dependencies = descriptor.getDependencies();
					if (dependencies == null || dependencies.length == 0) {
						startOneMeemkit(descriptor, started, pending);
						atLeastOneStarted = true;
						break;
					}
					if (descriptor.allDependenciesInstalled(started)) {
						startOneMeemkit(descriptor, started, pending);
						atLeastOneStarted = true;
						break;
					}
				}
				if (atLeastOneStarted == false) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("Unable to resolve dependencies for these meemkits:");
					for (String meemkitName : pending) {
						buffer.append(' ');
						buffer.append(meemkitName);
					}
					throw new MeemkitManagerException(buffer.toString());
				}
			}
		}

		private void startOneMeemkit(MeemkitDescriptor descriptor, List<MeemkitVersion> started, List<String> pending) throws RequestCreationException {
			requestContext.begin(Integer.MAX_VALUE, new MeemkitManagerRequest(INSTALL_REQUEST_ID));
			meemPatternControlConduit.installPatternMeems(descriptor);
			String meemkitName = descriptor.getHeader().getName();
			MeemkitVersion version = descriptor.getHeader().getMeemkitVersion();
			started.add(version);
			pending.remove(meemkitName);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemPatternStateConduit implements MeemPatternState {
		public void patternMeemsInstalled(String meemkitName) {
			Object context = requestContext.get();
			if (context == null)
				return;

			installedMeemkits.add(meemkitName);

			String patternLockName = determinePatternLockName(meemkitName);
			File file = new File(patternLockName);
			if (file.exists())
				file.delete();

			if (context instanceof MeemkitManagerRequest) {
				MeemkitManagerRequest request = (MeemkitManagerRequest) context;
				if (request.id == INSTALL_REQUEST_ID) {
					meemkitManagerClientOutput.meemkitInstalled(meemkitName);
				}
				else {
					meemkitManagerClientOutput.meemkitUpgraded(meemkitName);
				}
	
				requestContext.end();
			}
		}

		public void patternMeemsUninstalled(String meemkitName) {
			Object context = requestContext.get();
			if (context == null) {
				// Since there is no request context then this must originally have been
				// an UNINSTALL request rather than an UPGRADE
				outstandingRequests.put(meemkitName, new Integer(numberMeemkitLifeCycleManagers));
				meemkitLifeCycleManagerOutput.detailsChanged(new String[] { meemkitName }, null);
				return;
			}

			MeemkitManagerRequest request = (MeemkitManagerRequest) context;
			if (request.id == UPGRADE_REQUEST_ID) {
				// Ok, we have removed all of the pattern meems. Now we need to tell
				// the MeemkitLifeCycleManagers to restart their appropriate Meemkit meem
				JarDetails jarDetails = request.jarDetails;
				MeemkitDescriptor descriptor = jarDetails.meemkitDescriptor;
				descriptors.put(meemkitName, descriptor);
				meemkitManagerClientOutput.meemkitDescriptorsAdded(new MeemkitDescriptor[] { descriptor });
				try {
					unpackMeemkit(jarDetails, false);
					createMeemkitMeem(meemkitName);
				}
				catch (Exception ex) {
					errorHandlerConduit.thrown(ex);
				}
			}
			else {
				Exception ex = new Exception("Unexpected request ID of " + request.id + " received");
				errorHandlerConduit.thrown(ex);
			}

			// We intentionally DO NOT end the request context because we want it done
			// after the pattern meems have been recreated
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyCallback implements MeemClientCallback {
		public void referenceProvided(Reference reference) {
			if (reference != null) {
				LifeCycle lifeCycle = (LifeCycle) reference.getTarget();
				lifeCycle.changeLifeCycleState(LifeCycleState.ABSENT);
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitManagerClientProvider implements ContentProvider {
		public void sendContent(Object target, Filter filter) {
			MeemkitManagerClient client = (MeemkitManagerClient) target;
			MeemkitDescriptor[] array = new MeemkitDescriptor[descriptors.size()];
			descriptors.values().toArray(array);
			client.meemkitDescriptorsAdded(array);

			for (int i = 0; i < installedMeemkits.size(); i++) {
				client.meemkitInstalled((String) installedMeemkits.get(i));
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitLifeCycleManagerProvider implements ContentProvider {
		public void sendContent(Object target, Filter filter) {
			MeemkitLifeCycleManager lcm = (MeemkitLifeCycleManager) target;
			String[] names = new String[installedMeemkits.size()];
			URL[] urls = new URL[installedMeemkits.size()];
			int index = 0;
			for (String meemkitName : installedMeemkits) {
				names[index] = meemkitName;
				urls[index] = descriptorURLs.get(meemkitName);
				index++;
			}
			lcm.detailsChanged(names, urls);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitManagerRequest {
		protected final int id;

		protected final JarDetails jarDetails;

		public MeemkitManagerRequest(int requestID) {
			this.id = requestID;
			this.jarDetails = null;
		}

		public MeemkitManagerRequest(int requestID, JarDetails jarDetails) {
			this.id = requestID;
			this.jarDetails = jarDetails;
		}
	}

	/* ------------------------------------------------------------------------ */

	private class JarDetails {
		protected String meemkitName;

		protected MeemkitDescriptor meemkitDescriptor;

		protected String jarFilename;

		protected long jarLastModified;
	}

	/* ------------------------------------------------------------------------ */

	private class AvailableDirectoryWatcher implements Runnable {
		public void run() {
			try {
				lookForChanges();
			}
			catch (Exception ex) {
				errorHandlerConduit.thrown(ex);
			}

			// Now reschedule this task to run again

			long delayMillis = System.currentTimeMillis() + newMeemkitsPollTimeSeconds * 1000;
			threadManagerConduit.queue(this, delayMillis);
		}

		public void lookForChanges() throws IOException, MeemkitManagerException {
			// Scan the directory of available meemkits and if we find one that
			// has already been installed then we start an upgrade request

			File availableDirectoryFile = new File(availableMeemkitsDirectory);
			File[] entries = availableDirectoryFile.listFiles();
			for (int i = 0; i < entries.length; i++) {
				String entryName = entries[i].getName();
				int index = entryName.indexOf(MeemkitDescriptor.ARCHIVE_SUFFIX);
				if (index != -1) {
					String filename = entries[i].getPath();
					JarDetails previousDetails = (JarDetails) jarsDetails.get(entryName);
					if (previousDetails == null) {
						JarDetails newDetails = getJarDetails(filename);
						jarsDetails.put(entryName, newDetails);
						descriptors.put(newDetails.meemkitName, newDetails.meemkitDescriptor);
						meemkitManagerClientOutput.meemkitDescriptorsAdded(new MeemkitDescriptor[] { newDetails.meemkitDescriptor });
						logger.log(Level.INFO, "lookForChanges() - Found new meemkit '" + newDetails.meemkitName + "'");
					}
					else {
						File file = new File(filename);
						if (previousDetails.jarLastModified != file.lastModified()) {
							JarDetails newDetails = getJarDetails(filename);
							String meemkitName = newDetails.meemkitName;
							if (installedMeemkits.contains(meemkitName) == false) {
								logger.log(Level.WARNING, "lookForChanges() - Can not upgrade meemkit '" + meemkitName + "' because it is not installed");
							}
							else {
								logger.log(Level.INFO, "lookForChanges() - About to upgrade meemkit '" + meemkitName + "'");
								previousDetails.jarLastModified = newDetails.jarLastModified;
								doUpgradeMeemkit(newDetails);
							}
						}
					}
				}
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class DependencyClientConduit implements DependencyClient {
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyConnected(): " + dependencyAttribute);
			}

			if (dependencyAttribute.equals(dependencyAttributeLCM)) {	// lifecycle manager connected
				Integer id = new Integer(random.nextInt());
				try {
					requestContext.begin(Integer.MAX_VALUE, id);
				}
				catch (RequestCreationException ex) {
					errorHandlerConduit.thrown(ex);
					return;
				}
				if (DEBUG) {
					logger.log(Level.INFO, "dependencyConnected(): create a new category meem: " + id);
				}
				MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(CategoryWedge.class);
				meemDefinition.getMeemAttribute().setIdentifier(id.toString());
				lifeCycleManager.createMeem(meemDefinition, LifeCycleState.READY);
			}
			else {
				if (categoryDependencies.remove(dependencyAttribute)) {
					// if both category dependencies are connected, get the MeemkitLifecycleManager meem
					if ( categoryDependencies.isEmpty() ) {
						ownMeemkitLCMMeem = EssentialMeemHelper.getEssentialMeem(MeemkitLifeCycleManager.spi.getIdentifier());
						meemAdded(ownMeemkitLCMMeem);
					}
				}
			}

			// why was it checking for DependencyType.WEAK? Because it's waiting for a member of the depended-on category
			if (dependencyAttribute.getDependencyType().equals(DependencyType.WEAK)
					&& dependencyAttribute.getFacetIdentifier().equals("meemkitLifeCycleManager")) {
				//if (dependencyAttribute.getFacetIdentifier().equals("meemkitLifeCycleManager")) {
				if (DEBUG) {
					logger.log(Level.INFO, "dependencyConnected() voting true");
				}

				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), true);
				startMeemkitLCMDiscovery();
			}
		}

		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
			// A dependency has been added, but is not yet connected.
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyAdded(): " + facetId + " - " + dependencyAttribute);
			}
		}

		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
	}

	
	private static final String HTTP_SERVER_CONTEXT_PATH = "/meemkits";

	private static final int INSTALL_REQUEST_ID = 1;

	private static final int UPGRADE_REQUEST_ID = 2;

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;
	
	private static final boolean DISABLE = true;
	

	/** the amount of time in seconds to poll for new meemkits */
	public static final String PROPERTY_MEEMKIT_MANAGER_POLL_NEW_MEEMKITS = "org.openmaji.meemkit.manager.newmeemkits.poll.seconds";

	/** address to bind to */
	public static final String PROPERTY_MEEMKIT_MANAGER_ADDRESS = "org.openmaji.meemkit.manager.adress";
	
	/** if address is not set, the network interface to bind to */
	public static final String PROPERTY_MEEMKIT_MANAGER_NIC = "org.openmaji.meemkit.manager.nic";

	/** port to bind to */
	public static final String PROPERTY_MEEMKIT_MANAGER_HTTP_PORT = "org.openmaji.meemkit.manager.port";

	public static final String INSTALL_PATTERNS_PREFIX = "install-patterns-flag-";


}
