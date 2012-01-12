/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmaji.diagnostic.Debug;
import org.openmaji.implementation.deployment.Descriptor;
import org.openmaji.implementation.deployment.MeemDeploymentDescriptor;
import org.openmaji.implementation.deployment.Progress;
import org.openmaji.implementation.deployment.ProgressConduit;
import org.openmaji.implementation.deployment.SimpleProgress;
import org.openmaji.implementation.deployment.SubsystemDescriptor;
import org.openmaji.implementation.deployment.TagRegistrator;
import org.openmaji.implementation.deployment.meem.DeploymentSubsystemMeem;
import org.openmaji.implementation.deployment.meem.TransientDeploymentSubsystemMeem;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.utility.MeemUtility;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * This wedge creates and deploys a list subsystems and meems which it receives
 * via the deploymentProcessorConduit from another wedge.
 *
 * @author Warren Bloomer
 */
public class DeploymentWedge implements Wedge, DeploymentProcessor, ContentClient, SubsystemFactoryClient {

	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LogFactory.getLogger();

	private static final boolean DEBUG = true;
	
	/**
	 * MeemCore
	 */
	public MeemCore meemCore;

	/* ------------------ outbound Facets ---------------- */

	/**
	 * Facet to the subsystem factory. Used to create and destroy Subsystems.
	 */
	public SubsystemFactory subsystemFactory;


	/* ------------------- Conduits --------------------- */

	/** for requesting references */
	public MeemClientConduit meemClientConduit;

	/** for receiving lifecycle change information */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

	/** a conduit for adding and removing dependencies */
	public DependencyHandler dependencyHandlerConduit;

	/** a conduit for receiving dependency status of this meem */
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	/** for receiving deployment descriptors */
	public DeploymentProcessor deploymentProcessorConduit = this;

	/** for receiving configuration data */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	/** for sending deployment progress */
	public ProgressConduit progressConduit;

	/** for sending tag information */
	public TagRegistrator tagRegistratorConduit;

	/** for receivign debug configuration */
	public Debug debugConduit = new MyDebugConduit();


	/* ----------------- private properties ----------------- */

	/** 
	 * used for determining the progress that has been made
	 */
	private final Progress progress = new SimpleProgress();

	/**
	 * the debug level. used for verbose logging
	 */
	private int debugLevel = 1;

	/** 
	 * a map of subsystem descriptor names to a collection of meem ids for the meems to
	 * be created in the subsystem.
	 */
	private Map<String,Collection<String>> subsystemDescriptors = new HashMap<String,Collection<String>>();

	/**
	 * a map of meem ids to meem deployment descriptors
	 */
	private Map<String,MeemDeploymentDescriptor> meemDescriptors = new HashMap<String,MeemDeploymentDescriptor>();

	/**
	 * a set of subsystem names that are to be made transient subsystems
	 */
	private final Set<String> transientSubsystems = new HashSet<String>();

	/** 
	 * a Map of subsystem names to the subsystem meem 
	 */
	private final Map<String,Meem> subsystems = new HashMap<String,Meem>();

	/** 
	 * a Map of subsystem names to a HashSet of meem ids that exist in the
	 * subsystem
	 */
	private Map<String,Collection<String>> subsystemMeems = new HashMap<String,Collection<String>>();

	/** 
	 * the DependencyAttribute for the SubsystemFactoryClient facet
	 */
	private DependencyAttribute subsystemFactoryClientDependencyAttribute;

	/** 
	 * the DependencyAttribute for the SubsystemFactory facet
	 */
	private DependencyAttribute subsystemFactoryDependencyAttribute;

	/**
	 *  whether the SubsystemFactory dependency has been connected
	 */
	private boolean subsystemFactoryConnected = false;

	/** 
	 * whether the SubsystemFactoryClient has been connected
	 */
	private boolean subsystemFactoryClientConnected = false;

	private final MeemUtility meemUtility = MeemUtility.spi.get();


	/* ------------------------ DeploymentProcessor -------------------------- */

	/**
	 * Provide this DeploymentProcessor with a collection of deployment descriptors.
	 */
	public synchronized void setDescriptors(Collection<Descriptor> descriptors) {
		if (DEBUG) {
			LogTools.info(logger, "setting deployment descriptors");
		}
		if (progress.getProgressPoint() < progress.getCompletionPoint()) {
			LogTools.info(logger, "deployment already in progress");
		}

		subsystemDescriptors.clear();
		meemDescriptors.clear();
		int numberOfMeemsInSubsystems = 0;

		// get subsystem descriptors
		for (Descriptor obj : descriptors) {
			if (obj instanceof SubsystemDescriptor) {
				if (DEBUG) {
					LogTools.info(logger, "got subsystem deployment descriptor: " + obj);
				}
				SubsystemDescriptor descriptor = (SubsystemDescriptor) obj;
				numberOfMeemsInSubsystems += descriptor.getMeemIds().size();
				subsystemDescriptors.put(descriptor.getName(), descriptor.getMeemIds());
				if (descriptor.isTransientSubsystem()) {
					transientSubsystems.add(descriptor.getName());
				}
			}
			else if (obj instanceof MeemDeploymentDescriptor) {
				if (DEBUG) {
					LogTools.info(logger, "got meem deployment descriptor: " + obj);
				}
				MeemDeploymentDescriptor deploymentDescriptor = (MeemDeploymentDescriptor) obj;
				meemDescriptors.put(deploymentDescriptor.getId(), deploymentDescriptor);
			}
			else {
				// ???
				LogTools.info(logger, "Unhandled descriptor type: " + obj);
			}
		}

		progressConduit.addCompletionPoints(numberOfMeemsInSubsystems);
	}
	
	public synchronized void addDescriptors(Collection<Descriptor> descriptors) {
		if (DEBUG) {
			LogTools.info(logger, "setting deployment descriptors");
		}

		//subsystemDescriptors.clear();
		//meemDescriptors.clear();
		int numberOfExtraMeemsInSubsystems = 0;

		// get subsystem descriptors
		for (Descriptor obj : descriptors) {
			if (obj instanceof SubsystemDescriptor) {
				SubsystemDescriptor descriptor = (SubsystemDescriptor) obj;
				
				if (subsystemDescriptors.get(descriptor.getName()) == null) {
					subsystemDescriptors.put(descriptor.getName(), descriptor.getMeemIds());
					if (descriptor.isTransientSubsystem()) {
						transientSubsystems.add(descriptor.getName());
					}
					numberOfExtraMeemsInSubsystems += descriptor.getMeemIds().size();
				}
				else {
					Collection<String> newMeemIds = new HashSet<String>(descriptor.getMeemIds());
					Collection<String> oldMeemIds = subsystemDescriptors.get(descriptor.getName());
					newMeemIds.removeAll(oldMeemIds);
					numberOfExtraMeemsInSubsystems += newMeemIds.size();
					oldMeemIds.addAll(newMeemIds);
				}
			}
			else if (obj instanceof MeemDeploymentDescriptor) {
				MeemDeploymentDescriptor deploymentDescriptor = (MeemDeploymentDescriptor) obj;
				if (meemDescriptors.get(deploymentDescriptor.getId()) == null) {
					meemDescriptors.put(deploymentDescriptor.getId(), deploymentDescriptor);
				}
			}
			else {
				// ???
			}
		}

		progressConduit.addCompletionPoints(numberOfExtraMeemsInSubsystems);
	}
	

	public synchronized void processDescriptors() {

		if (meemDescriptors.size() == 0) {
			if (debugLevel > 0) {
				LogTools.info(logger, "No descriptors provided");
			}
			progressConduit.addProgressPoints(0);
			return;
		}

		if (debugLevel > 0) {
			LogTools.info(logger, "Got " + meemDescriptors.size() + " descriptors");
		}

		// make sure subsystems are created. then connect to create meems within them
		for (String subsystemName : subsystemDescriptors.keySet()) {
			Meem subsystemMeem = subsystems.get(subsystemName);
			if (subsystemMeem != null) {
				// Subsystem already exists so go ahead and create the meems
				connectSubsystem(subsystemName, subsystemMeem);
			}
			else {
				// Subsystem doesn't exist
				if (subsystemFactoryConnected && subsystemFactoryClientConnected) {
					// SubsystemFactory is connected - good we can proceed and create the subsystem
					createSubsystem(subsystemName);
				}
				else {
					// SubsystemFactory is not connected - our desired subsystem will be created when the SubsystemFactory is connected
				}
			}
		}
	}
	
	/**
	 * TODO add this Deployment facet and implement
	 * 
	 * @param subsystem the name of the subsystem to deploy the meem into.
	 * @param meemDescriptor settings for the meem to be deployed
	 */
	public void deploy(String subsystem, MeemDeploymentDescriptor meemDescriptor) {

		// TODO possibly add subsystem descriptor
		if (subsystemDescriptors.containsKey(subsystem)) {
			// add meem id to subsystem descriptor
			subsystemDescriptors.get(subsystem).add(meemDescriptor.getId());
		}
		else {
			HashSet<String> meemIds = new HashSet<String>();
			meemIds.add(meemDescriptor.getId());
			subsystemDescriptors.put(subsystem, meemIds);
		}
		
		if (meemDescriptors.containsKey(meemDescriptor.getId())) {
			// already got descriptor for this
		}
		else {
			meemDescriptors.put(meemDescriptor.getId(), meemDescriptor);
			// TODO initiate creation of meem
		}
		
		// TODO handle progress
		// TODO initiate creation of subsystem and meem if required
	}

	/* ---------------- SubsystemFactoryClient Facet ---------------------- */

	public void definitionsAdded(MeemDefinition[] meemDefinitions) {
	}

	public void definitionsRemoved(MeemDefinition[] meemDefinitions) {
	}

	public void subsystemCreated(Meem meem, MeemDefinition meemDefinition) {
		if (meemDefinition == null) {
			LogTools.info(logger, "Subsystem created with null meemDefinition?");
			return;
		}

		String subsystemName = meemDefinition.getMeemAttribute().getIdentifier();
		if (debugLevel > 0) {
			LogTools.info(logger, "subsystem created: " + subsystemName);
		}

		if (subsystems.get(subsystemName) != null || subsystemMeems.get(subsystemName) != null) {
			LogTools.warn(logger, "Subsystem, " + subsystemName + " already received");
		}

		// store the subsystem meem in the sybsystems map
		subsystems.put(subsystemName, meem);

		// create an entry to place created meems within the subsystem
		subsystemMeems.put(subsystemName, new HashSet<String>());

		if (subsystemDescriptors.containsKey(subsystemName)) {
			// get configuraiton handler to change the debug property to match the debug property of this meem
			MeemClientCallback callback = new SubsystemConfigurationHandlerCallback(meem, subsystemName);
			meemClientConduit.provideReference(meem, "configurationHandler", ConfigurationHandler.class, callback);
		}
	}

	public void subsystemDestroyed(Meem meem) {
		if (debugLevel > 0) {
			LogTools.info(logger, "subsystem destroyed: " + meem);
		}
		
		// remove the subsystem from subsystemMeems and subsystems
		MeemDefinition meemDefinition = meemUtility.getMeemDefinition(meem);
		if (meemDefinition != null) {
			String subsystemId = meemDefinition.getMeemAttribute().getIdentifier();
			subsystemMeems.remove(subsystemId);
			subsystems.remove(subsystemId);
		}
	}

	/* ------------- LIFECYCLE METHODS ----------------------- */

	private void commence() {
		// register the subsystem tag
		tagRegistratorConduit.register("subsystem", SubsystemDescriptor.class);

		// add depdendencies on the subsystem factory.
		dependencyHandlerConduit.addDependency("subsystemFactory", getSubsystemFactoryDependencyAttribute(), LifeTime.TRANSIENT);
		dependencyHandlerConduit.addDependency("subsystemFactoryClient", getSubsystemFactoryClientDependencyAttribute(), LifeTime.TRANSIENT);

		//subsystemFactoryClientFacet = (SubsystemFactoryClient) meemCore.getTargetFor(new SubsystemFactoryClientImpl(), SubsystemFactoryClient.class);
		//dependencyHandlerConduit.addDependency(subsystemFactoryClientFacet, subsystemFactoryClientDependencyAttribute, LifeTime.TRANSIENT);
	}

	private void conclude() {
		dependencyHandlerConduit.removeDependency(getSubsystemFactoryDependencyAttribute());
		dependencyHandlerConduit.removeDependency(getSubsystemFactoryClientDependencyAttribute());
		//subsystemFactoryConnected = false;
		//subsystemFactoryClientConnected = false;
		subsystems.clear();
		progressConduit.reset();
	}

	/* ---------------------- private methods ------------------------- */

	private DependencyAttribute getSubsystemFactoryDependencyAttribute() {
		if (subsystemFactoryDependencyAttribute == null) {
			MeemPath subsystemFactoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_FACTORY);
			subsystemFactoryDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, subsystemFactoryMeemPath, "subsystemFactory");
		}
		return subsystemFactoryDependencyAttribute;
	}

	private DependencyAttribute getSubsystemFactoryClientDependencyAttribute() {
		if (subsystemFactoryClientDependencyAttribute == null) {
			MeemPath subsystemFactoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_FACTORY);
			subsystemFactoryClientDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, subsystemFactoryMeemPath,
					"subsystemFactoryClient");
		}
		return subsystemFactoryClientDependencyAttribute;
	}

	/**
	 * This will create subsystems for any required subsystem descriptors where a
	 * subsystem meem is not yet received.
	 */
	private void createSubsystems() {
		for (String subsystemName : subsystemDescriptors.keySet()) {
			if (subsystems.get(subsystemName) == null) {
				// subsystem does not yet exist
				if (debugLevel > 0) {
					LogTools.info(logger, "Requesting creation of " + subsystemName);
				}
				createSubsystem(subsystemName);
			}
		}
	}

	/**
	 * Create a subsystem of a particular name
	 * 
	 * @param name the name of the subsystem.
	 */
	private void createSubsystem(String name) {

		if (debugLevel > 0) {
			LogTools.info(logger, "In createSubsystem(" + name + ")");
		}
		
		MeemDefinition subsystemMeemDefinition;
		if (transientSubsystems.contains(name)) {
			subsystemMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(TransientDeploymentSubsystemMeem.class);
		}
		else {
			subsystemMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(DeploymentSubsystemMeem.class);
		}

		subsystemMeemDefinition.getMeemAttribute().setIdentifier(name);
		subsystemFactory.createSubsystem(subsystemMeemDefinition);
	}

	/**
	 * Create a CategoryClient to listen for meems managed by this subsystem.
	 * 
	 * @param subsystemName the name of the subsystem
	 * @param subsystemMeem the subsystem meem.
	 */
	private void connectSubsystem(String subsystemName, Meem subsystemMeem) {
		// setup subsystem dependencies
		DependencyAttribute lifeCycleManagerCategoryClientDependencyAttribute = 
			new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, subsystemMeem, "lifeCycleManagerCategoryClient");

		SubsystemLcmCategoryClient client = new SubsystemLcmCategoryClient(subsystemName, subsystemMeem);

		LifeCycleManagerCategoryClient lifeCycleManagerCategoryClientFacet = 
			(LifeCycleManagerCategoryClient) meemCore.getTargetFor(client, LifeCycleManagerCategoryClient.class);

		dependencyHandlerConduit.addDependency(lifeCycleManagerCategoryClientFacet, lifeCycleManagerCategoryClientDependencyAttribute, LifeTime.TRANSIENT);
	}

	private MeemDeploymentDescriptor getDeploymentDescriptor(String meemId) {
		return meemDescriptors.get(meemId);
	}

	public void contentSent() {
		LogTools.info(logger, "ContentSent");
	}

	public void contentFailed(String reason) {
		LogTools.info(logger, "ContentFailed: " + reason);
	}

	/* ------------------------------------------------------------------------ */

	/**
	 * Change the debug level of the subsystem
	 */
	private final class SubsystemConfigurationHandlerCallback implements MeemClientCallback, ContentClient {

		private Meem subsystemMeem;
		private String subsystemName;

		SubsystemConfigurationHandlerCallback(Meem subsystemMeem, String subsystemName) {
			this.subsystemMeem = subsystemMeem;
			this.subsystemName = subsystemName;
		}

		public void referenceProvided(Reference reference) {
			// set the debug level on the subsystem meem
			ConfigurationHandler handler = (ConfigurationHandler) reference.getTarget();
			ConfigurationIdentifier ci = new ConfigurationIdentifier("DebugWedge", "debugLevel");
			handler.valueChanged(ci, new Integer(debugLevel));

			connectSubsystem(subsystemName, subsystemMeem);
		}

		public void contentSent() {
			// OK
		}

		public void contentFailed(String reason) {
			LogTools.error(logger, "DeploymentSubsystemMeem is supposed to have a DebugWedge");
		}
	}

	/* ---------------------------------------------------------------------- */

	private final class DependencyClientConduit implements DependencyClient {

		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getSubsystemFactoryDependencyAttribute())) {
				subsystemFactoryConnected = true;
				if (subsystemFactoryClientConnected) {
					createSubsystems();
				}
			}
			else if (dependencyAttribute.equals(getSubsystemFactoryClientDependencyAttribute())) {
				subsystemFactoryClientConnected = true;
				if (subsystemFactoryConnected) {
					createSubsystems();
				}
			}
		}

		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getSubsystemFactoryDependencyAttribute())) {
				subsystemFactoryConnected = false;
			}
			else if (dependencyAttribute.equals(getSubsystemFactoryClientDependencyAttribute())) {
				subsystemFactoryClientConnected = false;

				// clear local cache of subsystems
				subsystems.clear();
				//subsystemMeems.clear();
			}
		}

	}

	/* -------- SubsystemFactoryClient Methods ------------------------------ */
	/*
	 private final class SubsystemFactoryClientImpl implements SubsystemFactoryClient {
	 
	 public void definitionsAdded(MeemDefinition[] arg0) {
	 // don't care
	 }
	 
	 public void definitionsRemoved(MeemDefinition[] arg0) {
	 // don't care
	 }
	 
	 public void subsystemCreated(Meem meem, MeemDefinition meemDefinition) {
	 if (meemDefinition == null) {
	 LogTools.info(logger, "Subsystem created with null meemDefinition?");
	 return;
	 }
	 
	 String subsystemName = meemDefinition.getMeemAttribute().getIdentifier();
	 if ( debugLevel > 0 ) {
	 LogTools.info(logger, "subsystem created "+subsystemName);
	 }
	 subsystemMeems.put(subsystemName, new HashSet());
	 subsystems.put(subsystemName, meem);
	 
	 if ( subsystemDescriptors.containsKey(subsystemName) ) {
	 MeemClientCallback callback = new ConfigurationHandlerMeemClientCallback(meem, subsystemName);
	 meemClientConduit.provideReference(meem, "configurationHandler", ConfigurationHandler.class, callback);
	 }
	 }
	 
	 public void subsystemDestroyed(Meem meem) {
	 // ignore
	 }
	 }
	 */

	/* ---------------------------------------------------------------------- */

	/**
	 * Client to the Subsystem LifeCycleManager Category. Monitors the meems within a subsystem.
	 */
	private final class SubsystemLcmCategoryClient implements LifeCycleManagerCategoryClient, ContentClient {

		/** the name of the subsystem */
		private String subsystemName;

		/** the subsystem meem */
		private Meem subsystemMeem;

		/** collection of meems to be created */
		private Collection<String> desiredMeemIds;

		/** collection of meems that have been created */
		private Collection<String> createdMeemIds;

		private boolean contentSent = false;

		private boolean finished = false;

		public SubsystemLcmCategoryClient(String name, Meem meem) {
			this.subsystemName = name;
			this.subsystemMeem = meem;
			this.createdMeemIds = subsystemMeems.get(subsystemName);
			this.desiredMeemIds = subsystemDescriptors.get(subsystemName);
		}

		public void entriesAdded(CategoryEntry[] categoryEntries) {

			if (finished) {
				//TODO using 'finished' is a hack until I can remove this instance as a dependee of the subsystem
				return;
			}

			if (contentSent) {
				// These entries are for meems that we asked to be created (created after initial list of category entries was received)
				for (int i = 0; i < categoryEntries.length; i++) {
					CategoryEntry entry = categoryEntries[i];
					MeemDefinition meemDefinition = meemUtility.getMeemDefinition(entry.getMeem());
					if (meemDefinition != null) {
						String identifier = meemDefinition.getMeemAttribute().getIdentifier();
						createdMeemIds.add(identifier);
						desiredMeemIds.remove(identifier);
						progressConduit.addProgressPoints(1);
					}
				}
				if (desiredMeemIds.size() == 0) {
					finished = true;
				}
			}
			else {
				// These entries are for meems that already existed in the subsystem - initial content
				for (int i = 0; i < categoryEntries.length; i++) {
					CategoryEntry entry = categoryEntries[i];
					MeemDefinition meemDefinition = meemUtility.getMeemDefinition(entry.getMeem());
					if (meemDefinition != null) {
						String identifier = meemDefinition.getMeemAttribute().getIdentifier();
						createdMeemIds.add(identifier);
						if (desiredMeemIds.contains(identifier)) {
							progressConduit.addProgressPoints(1);
						}
					}
				}
			}
		}

		public void entriesRemoved(CategoryEntry[] categoryEntries) {
		}

		public void entryRenamed(CategoryEntry oldCategoryEntry, CategoryEntry newCategoryEntry) {
		}

		/**
		 * Called when initial content has completed being sent. Now we can create remaining meems
		 */
		public void contentSent() {

			contentSent = true;

			// determine Meems not yet created in the subsystem
			for (String meemId : createdMeemIds) {
				desiredMeemIds.remove(meemId);
			}

			if (desiredMeemIds.size() > 0) {
				if (debugLevel > 0) {
					LogTools.info(logger, desiredMeemIds.size() + " Meems to be created for subsystem \"" + subsystemName + "\"");
				}
				
				// create uncreated, desired Meems.
				MeemClientCallback callback = new SubsystemMeemClientCallback(subsystemName, desiredMeemIds);
				meemClientConduit.provideReference(subsystemMeem, "subsystem", Subsystem.class, callback);
			}
		}

		public void contentFailed(String message) {
			LogTools.info(logger, "Problem when getting subsystem meems: " + message);
		}

	}

	/* ---------------------------------------------------------------------- */

	/**
	 * MeemClient to a Subsystem Meem. Receives a reference to the Subsystem facet.
	 * It then uses that facet to create desired Meems in the Subsystem.
	 */
	private final class SubsystemMeemClientCallback implements MeemClientCallback, ContentClient {

		private String subsystemName;

		/** collection of meems ids of meems to be created for the subsystem */
		private Collection<String> meemIds;

		SubsystemMeemClientCallback(String subsystemName, Collection<String> meemIds) {
			this.subsystemName = subsystemName;
			this.meemIds = meemIds;
		}

		/**
		 * Receives reference to the Subsystem facet of the SubsystemMeem
		 */
		public void referenceProvided(Reference reference) {
			Subsystem subsystem = (Subsystem) reference.getTarget();
			for (String meemId : meemIds) {
				// get descriptor for the meemId
				MeemDeploymentDescriptor descriptor = getDeploymentDescriptor(meemId);
				if (descriptor != null) {
					subsystem.createMeem(descriptor.getMeemDefinition(), descriptor);
				}
				else {
					LogTools.error(logger, "No deployment descriptor found for '" + meemId + "' for subsystem '" + subsystemName + "'");
				}
			}
		}

		public void contentSent() {
			// OK
		}

		public void contentFailed(String reason) {
			LogTools.error(logger, "Error receiving the ConfigurationHandler facet of a managed meem: " + reason);
		}
	}

	/* ---------------------------------------------------------------------- */

	/**
	 * LifecycleClient conduit
	 */
	private class LifeCycleClientHandler implements LifeCycleClient {

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.LOADED_PENDING)) {
				commence();
			}
			else if (transition.equals(LifeCycleTransition.READY_PENDING)) {
				conclude();
			}
			else if (transition.equals(LifeCycleTransition.READY_LOADED)) {
				conclude();
			}
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
		}
	}

	/* ---------------------------------------------------------------------- */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}	
}
