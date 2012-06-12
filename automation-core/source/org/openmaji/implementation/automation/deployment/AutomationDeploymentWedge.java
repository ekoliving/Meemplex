/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemState;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.utility.CategoryUtility;
import org.openmaji.system.utility.MeemUtility;

import org.openmaji.automation.address.AddressSpecification;
import org.openmaji.automation.device.DeviceDescription;
import org.openmaji.diagnostic.Debug;




import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.deployment.ConfigurationParameter;
import org.openmaji.implementation.deployment.Descriptor;
import org.openmaji.implementation.deployment.SimpleProgress;
import org.openmaji.implementation.deployment.Progress;
import org.openmaji.implementation.deployment.ProgressConduit;
import org.openmaji.implementation.deployment.TagRegistrator;
import org.openmaji.implementation.deployment.wedge.DeploymentProcessor;

/**
 * This Wedge will deploy meems that are described in a DeviceSubsystem descriptor.
 *
 */
public class AutomationDeploymentWedge implements Wedge, DeploymentProcessor {
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemCore meemCore;

	/* --------------------------- conduits --------------------------- */

	public MeemClientConduit meemClientConduit;

	public DependencyHandler dependencyHandlerConduit;

	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	public DeploymentProcessor deploymentProcessorConduit = this;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

	public TagRegistrator tagRegistratorConduit;

	public ProgressConduit progressConduit;

	/**
	 * For queueing tasks
	 */
	public ThreadManager threadManagerConduit;

	public Debug debugConduit = new MyDebugConduit();

	/* ------------------------ private members ------------------------- */

	private DependencyAttribute subsystemFactoryClientDependencyAttribute;

	/**
	 * A Map of meemId to Subsystem descriptor.
	 */
	private final Map<String, DeviceSubsystemDescriptor> descriptors = new HashMap<String, DeviceSubsystemDescriptor>();

	private final Map<DependencyAttribute, DeviceSubsystemDescriptor> dependencyAttributes = new HashMap<DependencyAttribute, DeviceSubsystemDescriptor>();

	/**
	 * A Map of subsystem names to the subsystem meems 
	 */
	private final Map<String, Meem> subsystemMeems = new HashMap<String, Meem>();
	
	/** 
	 * a set of deployment descriptors for a subsystem 
	 */
	private HashSet<MeemDefinition> subsystemMeemDefinitions = new HashSet<MeemDefinition>();

	/**
	 * A set of category names that have been created
	 */
	private final HashSet<String> createdCategories = new HashSet<String>();

	private HashSet<SubsystemClientImpl> subsystemClients = new HashSet<SubsystemClientImpl>();
	
	private Meem hyperspaceMeem;

	private int numberMeems = 0;

	private final Progress progress = new SimpleProgress();

	private boolean subsytemFactoryDependencyAdded = false;

	private int debugLevel;
	
	private boolean DEBUG = true;

	/* ------------------------------------------------------------------------ */

	private void commence() {
	}

	private void conclude() {
		hyperspaceMeem = null;
		for (SubsystemClientImpl client : subsystemClients) {
			client.stop();
		}
		subsystemClients.clear();
	}

	/* ------------------------------------------------------------------------ */

	public void setDescriptors(Collection<Descriptor> allDescriptors) {
		if (progress.getProgressPoint() < progress.getCompletionPoint()) {
			logger.log(Level.INFO, "deployemeny already in progress");
		}
		
		hyperspaceMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/"));
		descriptors.clear();
		dependencyAttributes.clear();
		subsystemMeems.clear();
		createdCategories.clear();
		numberMeems = 0;

		int numberSubsystems = 0;
		for (Descriptor item : allDescriptors) {
			if (item instanceof DeviceSubsystemDescriptor) {
				numberSubsystems++;
				DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) item;
				numberMeems += descriptor.getMeemDescriptions().size();

				// check for duplicate ids
				List<DeviceMeemDescription> descs = new ArrayList<DeviceMeemDescription>(descriptor.getMeemDescriptions());
				while (descs.size() > 1) {
					DeviceMeemDescription desc = descs.remove(0);
					for (DeviceMeemDescription desc2 : descs) {
						if (desc.getIdentifier().equals(desc2.getIdentifier())) {
							logger.log(Level.INFO, "Warning: non-unique identifier: " + desc.getIdentifier());
						}
					}
				}
				
				descriptors.put(descriptor.getIdentifier(), descriptor);
				
				// TODO why are categories created here instead of in "processDescriptors"
				String categoryName = descriptor.getDefaultCategory();
				if (!createdCategories.contains(categoryName)) {
					if (debugLevel > 0) {
						logger.log(Level.INFO, "path: " + categoryName);
					}
					MeemPath categoryPath = MeemPath.spi.create(Space.HYPERSPACE, categoryName);
					CategoryUtility.spi.get().getCategory(hyperspaceMeem, categoryPath); // Create the category
					createdCategories.add(categoryName);
				}
			}
		}
		
		if (DEBUG) {
			logger.log(Level.INFO, "Got " + numberMeems + " automation meem descriptors to deploy");
		}

		// add more points to completion
		progressConduit.addCompletionPoints(numberMeems);
	}

	public void addDescriptors(Collection<Descriptor> deploymentDescriptors) {
	}
	
	/**
	 * Process and deploy the subsystems and devices as specified in the descriptors.
	 */
	public void processDescriptors() {
		if (DEBUG) {
			logger.log(Level.INFO, "Processing " + numberMeems + " descriptors");
		}		

		if (numberMeems == 0) {
			if (debugLevel > 0) {
				logger.log(Level.INFO, "No automation descriptors found in deployment file");
			}
			progressConduit.addProgressPoints(0);
			return;
		}
		
		/*
		// create the categories for the subsyystem categories 
		Object[] subsystemDescriptors = descriptors.values().toArray();
		for (int i=0; i<subsystemDescriptors.length; i++) {
			DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) subsystemDescriptors[i];				
			String categoryName = descriptor.getDefaultCategory();
			if (!createdCategories.contains(categoryName)) {
				if (debugLevel > 0) {
					logger.log(Level.INFO, "path: " + categoryName);
				}
				MeemPath categoryPath = MeemPath.spi.create(Space.HYPERSPACE, categoryName);
				CategoryUtility.spi.get().getCategory(hyperspaceMeem, categoryPath); // Create the category
				createdCategories.add(categoryName);
			}
		}
		*/
		
		if (!subsytemFactoryDependencyAdded && descriptors.size() > 0) {
			setupSubsystemFactoryDependency();
		}
		// TODO force creation
	}

	/* ------------------------------------------------------------------------ */

	private void setupSubsystemFactoryDependency() {
		if (DEBUG) {
			logger.log(Level.INFO, "setupSubsystemFactoryDependency()");
		}		

		Facet facet = meemCore.getTargetFor(new SubsystemFactoryClientImpl(), SubsystemFactoryClient.class);
		dependencyHandlerConduit.addDependency(facet, getSubsystemFactoryClientDependencyAttribute(), LifeTime.TRANSIENT);
	}
	
	private DependencyAttribute getSubsystemFactoryClientDependencyAttribute() {
		if (subsystemFactoryClientDependencyAttribute == null) {
			MeemPath subsystemFactoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_FACTORY);
			subsystemFactoryClientDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, subsystemFactoryMeemPath, "subsystemFactoryClient");
		}

		return subsystemFactoryClientDependencyAttribute;
	}

	/**
	 * Sets up a dependency between meem's outbound subsystemClient facet and a
	 * local SubsystemClient object.
	 * 
	 * TODO this is only called after subsystem is created. Change so that it is also called when subsystem is located
	 * 
	 * @param meem The meem whose subsystemClient Facet will be depended on.  
	 * @param descriptor
	 */
	private void setupSubsystemDependency(Meem meem, DeviceSubsystemDescriptor descriptor) {
		if (DEBUG) {
			logger.log(Level.INFO, "setupSubsystemDependency()");
		}		

		SubsystemClientImpl subsystemClient = new SubsystemClientImpl(descriptor);
		subsystemClients.add(subsystemClient);
		
		DependencyAttribute dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "subsystemClient");
		Facet facet = meemCore.getTargetFor(subsystemClient, SubsystemClient.class);
		dependencyHandlerConduit.addDependency(facet, dependencyAttribute, LifeTime.TRANSIENT);
		dependencyAttributes.put(dependencyAttribute, descriptor);
	}

	private void createSubsystems() {
		if (DEBUG) {
			logger.log(Level.INFO, "createSubsystems(): " + descriptors.size());
		}		

		Object[] items = descriptors.values().toArray();
		for (int i=0; i<items.length; i++) {
			DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) items[i];
			createSubsystem(descriptor);
		}
	}

	private void createSubsystem(DeviceSubsystemDescriptor descriptor) {
		if (DEBUG) {
			logger.log(Level.INFO, "createSubsystem(): " + descriptor);
		}		

		// check subsystem types
		MeemDefinition[] definitions = (MeemDefinition[]) subsystemMeemDefinitions.toArray(new MeemDefinition[0]);
		for (int i = 0; i < definitions.length; i++) {
			MeemDefinition definition = definitions[i];
			if (definition.getMeemAttribute().getIdentifier().equals(descriptor.getType())) {
				MeemDefinition newDefinition = (MeemDefinition) definition.clone();

				newDefinition.getMeemAttribute().setIdentifier(descriptor.getIdentifier());
				MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_FACTORY);
				Meem meem = Meem.spi.get(meemPath);
				SubsystemFactory subsystemFactory = (SubsystemFactory) MeemUtility.spi.get().getTarget(meem, "subsystemFactory", SubsystemFactory.class);
				subsystemFactory.createSubsystem(newDefinition);
				return;
			}
		}

		logger.log(Level.WARNING, "No subsystem type '" + descriptor.getType() + "' found.  Is the meemkit that provides this subsystem type installed?");
	}

	private void configureSubsystem(Meem meem, String identifier) {
		if (DEBUG) {
			logger.log(Level.INFO, "configureSubsystem(): " + identifier);
		}		

		DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) descriptors.get(identifier);
		MeemClientCallback callback = new ConfigureSubsystemCallback(meem, descriptor);
		meemClientConduit.provideReference(meem, "configurationHandler", ConfigurationHandler.class, callback);
	}

	private void commissionSubsystem(Meem meem, DeviceSubsystemDescriptor descriptor) {
		if (DEBUG) {
			logger.log(Level.INFO, "commissionSubsystem(): " + descriptor);
		}		

		MeemClientCallback callback = new CommissionSubsystemCallback();
		meemClientConduit.provideReference(meem, "subsystem", Subsystem.class, callback);
	}

	/* ------------------------------------------------------------------------ */

	private class SubsystemClientImpl implements SubsystemClient {
		private final DeviceSubsystemDescriptor descriptor;

		private boolean commissioned = false;

		private boolean doNotUse = false;

		private MeemDefinition[] meemDefinitions;

		private MeemDescription[] meemDescriptions;

		private final Category category;

		private Hashtable<String, DeviceMeemDescription> meemDefinitionDescriptionIndex = new Hashtable<String, DeviceMeemDescription>();

		private Monitor monitor;

		public SubsystemClientImpl(DeviceSubsystemDescriptor descriptor) {
			this.descriptor = descriptor;

			if (descriptor.getDefaultCategory() == null) {
				category = null;
			}
			else {
				MeemPath meempath = MeemPath.spi.create(Space.HYPERSPACE, descriptor.getDefaultCategory());
				Meem meem = Meem.spi.get(meempath);
				category = CategoryUtility.spi.get().getCategory(meem);
			}
			
			// monitor to keep track of the outstanding meems
			monitor = new Monitor(meemDefinitionDescriptionIndex);
		}
		
		public void stop() {
			monitor.stop();
		}

		public void subsystemStateChanged(SubsystemState subsystemState) { /* ignore */
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemClient.subsystemStateChanged(): " + subsystemState);
			}		
		}

		public void commissionStateChanged(CommissionState commissionState) {
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemClient.commissionStateChanged(): " + commissionState);
			}
			synchronized (this) {
				if (commissionState.equals(CommissionState.COMMISSIONED)) {
					if (commissioned || doNotUse) {
						return;
					}
					Meem meem = subsystemMeems.get(descriptor.getIdentifier());

					// TODO get the subsystem facet asynchronously
					Subsystem subsystem = (Subsystem) MeemUtility.spi.get().getTarget(meem, "subsystem", Subsystem.class);
					Collection<DeviceMeemDescription> meemDescriptions = descriptor.getMeemDescriptions();
					for (Iterator<DeviceMeemDescription> iterator = meemDescriptions.iterator(); iterator.hasNext();) {
						DeviceMeemDescription dmd = iterator.next();
						createMeem(dmd, subsystem);
					}
					commissioned = true;

					// start the monitor
					monitor.start();
				}
			}
		}

		private void createMeem(DeviceMeemDescription dmd, Subsystem subsystem) {
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemClient.createMeem(): " + dmd);
			}		
			MeemDefinition meemDefinition = null;
			MeemDescription meemDescription = null;

			if (meemDefinitions == null) {
				logger.log(Level.INFO, "No meemDefinitions, so cannot create meem for " + dmd);
				return;
			}

			for (int i = 0; meemDefinitions != null && i < meemDefinitions.length; i++) {
				String type = dmd.getType();
				if (type.equals(meemDefinitions[i].getMeemAttribute().getIdentifier())) {
					meemDefinition = (MeemDefinition) meemDefinitions[i].clone();
					meemDefinition.getMeemAttribute().setIdentifier(dmd.getIdentifier());
					DeviceDescription deviceDescription = (DeviceDescription) meemDescriptions[i];
					DeviceDescription newDeviceDescription = null;
					try {
						newDeviceDescription = (DeviceDescription) deviceDescription.clone();
					}
					catch (CloneNotSupportedException ex) {
						logger.log(Level.WARNING, "Subsystem device description '" + deviceDescription + "' has not implemened clone()");
						return;
					}
					AddressSpecification addressSpecification = newDeviceDescription.getProtocol().getAddressSpecification();
					addressSpecification.setAddress(dmd.getAddress());
					newDeviceDescription.setAddress(addressSpecification.getAddress());
					newDeviceDescription.setDescription(dmd.getDescription());
					newDeviceDescription.setIdentifier(dmd.getIdentifier());
					meemDescription = newDeviceDescription;
				}
			}

			if (meemDefinition == null) {
				logger.log(Level.WARNING, "Subsystem " + descriptor.getIdentifier() + " does not have a meem of type '" + dmd.getType() + "'");
				return;
			}

			meemDefinitionDescriptionIndex.put(dmd.getIdentifier(), dmd);
			subsystem.createMeem(meemDefinition, meemDescription);
		}

		public void meemCreated(Meem meem, MeemDefinition meemDefinition) {
			String identifier = meemDefinition.getMeemAttribute().getIdentifier();
			logger.log(Level.INFO, "created: " + identifier);

			if (category != null) {
				category.addEntry(identifier, meem);
			}

			DeviceMeemDescription deviceMeemDescription = meemDefinitionDescriptionIndex.remove(identifier);

			if (deviceMeemDescription == null) {
				logger.log(Level.WARNING, "meemCreated(): Couldn't find associated DeviceMeemDescription for " + identifier);
			}
			else {
				Iterator<String> hyperSpacePaths = deviceMeemDescription.getHyperSpacePaths().iterator();

				while (hyperSpacePaths.hasNext()) {
					String location = hyperSpacePaths.next();

					int index = location.lastIndexOf("/");
					String categoryName = location.substring(0, index);
					String entryName = location.substring(index + 1);

					Category category = CategoryUtility.spi.get().getCategory(hyperspaceMeem, MeemPath.spi.create(Space.HYPERSPACE, categoryName));

					if (category == null) {
						logger.log(Level.WARNING, "No category facet on meem: " + categoryName);
					}
					else {
						category.addEntry(entryName, meem);
						if (debugLevel > 0) {
							StringBuffer message = new StringBuffer(80);
							message.append("hyperspace: ");
							message.append(identifier);
							message.append(" -> ");
							message.append(categoryName);
							logger.log(Level.INFO, message.toString());
						}
					}
				}
			}

			progressConduit.addProgressPoints(1);
		}

		public void meemsAvailable(MeemDefinition[] meemDefinitions, MeemDescription[] meemDescriptions) {
			if (DEBUG) {
				String num = (meemDefinitions == null) ? "null" : (""+ meemDefinitions.length);
				logger.log(Level.INFO, "SubsystemClient.meemsAvailable(): " + num);
			}		

			if (meemDefinitions == null) {
				logger.log(Level.WARNING, "null meemDefinitions have been provided as available");
				return;
			}
			for (int i = 0; i < meemDefinitions.length; i++) {
				if (meemDefinitions[i].getMeemAttribute().getIdentifier() == null) {
					logger.log(Level.WARNING, "The subsystem " + descriptor.getIdentifier() + " has not set the identifier in its device meems MeemDefinitions");
					doNotUse = true;
					return;
				}
			}
			this.meemDefinitions = meemDefinitions;
			this.meemDescriptions = meemDescriptions;
		}
	}

	/* ------------------------------------------------------------------------ */

	private class CommissionSubsystemCallback implements MeemClientCallback {
		public void referenceProvided(Reference reference) {
			if (reference == null) {
				logger.log(Level.WARNING, "referenceProvided() - Meem has no wedges that implement 'Subsystem' facet");
				return;
			}

			Subsystem subsystem = (Subsystem) reference.getTarget();
			subsystem.changeSubsystemState(SubsystemState.STARTED);
			subsystem.changeCommissionState(CommissionState.COMMISSIONED);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class ConfigureSubsystemCallback implements MeemClientCallback {
		private final Meem meem;

		private final DeviceSubsystemDescriptor descriptor;

		public ConfigureSubsystemCallback(Meem meem, DeviceSubsystemDescriptor descriptor) {
			this.meem = meem;
			this.descriptor = descriptor;
		}

		public void referenceProvided(Reference reference) {
			if (reference == null) {
				logger.log(Level.WARNING, "referenceProvided() - Meem has no wedges that implement 'ConfigurationHandler' facet");
				return;
			}

			// set configuration parameters
			ConfigurationHandler ch = (ConfigurationHandler) reference.getTarget();
			for (Iterator<ConfigurationParameter> iterator = descriptor.getConfigurationParameters().iterator(); iterator.hasNext();) {
				ConfigurationParameter cp = iterator.next();
				ch.valueChanged(cp.getConfigurationIdentifier(), cp.getValue());
			}

			commissionSubsystem(meem, descriptor);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class DependencyClientConduit implements DependencyClient {
		
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getSubsystemFactoryClientDependencyAttribute())) {
				subsytemFactoryDependencyAdded = true;
			}
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getSubsystemFactoryClientDependencyAttribute())) {
				subsytemFactoryDependencyAdded = false;
			}
		}
		
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getSubsystemFactoryClientDependencyAttribute())) {
				createSubsystems();
			}

			DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) dependencyAttributes.get(dependencyAttribute);
			if (descriptor != null) {
				String identifier = descriptor.getIdentifier();
				Meem meem = (Meem) subsystemMeems.get(identifier);
				configureSubsystem(meem, identifier);
			}
		}

		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
	}

	/* ------------------------------------------------------------------------ */

	private class SubsystemFactoryClientImpl implements SubsystemFactoryClient, ContentClient {
		
		public void definitionsAdded(MeemDefinition[] definitions) {
			if (DEBUG) {
				String num = (definitions == null) ? "null" : (""+definitions.length);
				logger.log(Level.INFO, "SubsystemFactoryClient.definitionsAdded(): " + num);
			}		

			if (definitions == null) {
				return;
			}
			for (int i=0; i<definitions.length; i++) {
				subsystemMeemDefinitions.add(definitions[i]);
			}
		}

		public void definitionsRemoved(MeemDefinition[] definitions) { /* ignore */
			if (DEBUG) {
				String num = (definitions == null) ? "null" : (""+definitions.length);
				logger.log(Level.INFO, "SubsystemFactoryClient.definitionsRemoved(): " + num);
			}
			
			if (definitions == null) {
				return;
			}
			for (int i=0; i<definitions.length; i++) {
				subsystemMeemDefinitions.remove(definitions[i]);
			}
		}

		public void subsystemCreated(Meem meem, MeemDefinition meemDefinition) {
			if (meemDefinition == null) {
				logger.log(Level.WARNING, "Received null MeemDefinition for subsystem meem: " + meem.getMeemPath());
				return;
			}
			
			String identifier = meemDefinition.getMeemAttribute().getIdentifier();
			
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemFactoryClient.subsystemCreated(): " + identifier);
			}
			
			DeviceSubsystemDescriptor descriptor = (DeviceSubsystemDescriptor) descriptors.get(identifier);
			if (descriptor != null) {
				subsystemMeems.put(identifier, meem);
				setupSubsystemDependency(meem, descriptor);
			}
		}

		public void subsystemDestroyed(Meem meem) { /* ignore */
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemFactoryClient.subsystemDestroyed()");
			}
		}

		public void contentSent() { /* ignore */
			if (DEBUG) {
				logger.log(Level.INFO, "SubsystemFactoryClient.contentSent()");
			}			
		}

		public void contentFailed(String errorMessage) {
			logger.log(Level.WARNING, "Unable to obtain SubsystemFactory details: " + errorMessage);
		}
	}

	/* ---------------------------------------------------------------------- */

	private class LifeCycleClientHandler implements LifeCycleClient {

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.LOADED_PENDING)) {
				tagRegistratorConduit.register("device-subsystem", DeviceSubsystemDescriptor.class);
			}
			if (transition.equals(LifeCycleTransition.PENDING_READY)) {
				commence();
			}
			if (transition.equals(LifeCycleTransition.READY_PENDING)) {
				conclude();
			}
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}
	
	private class Monitor implements Runnable {
		//private Thread currentThread;
		private boolean running = false;
		private Hashtable<String,?> table;
		private long interval = 10000;
		private int iterations = 0;
		
		public Monitor(Hashtable<String, ?> table) {
			this.table = table;
		}
		
		public void start() {
			synchronized(this) {
				if (running == false) {
					iterations = 0;
					running = true;
					threadManagerConduit.queue(this);
				}
				/*
				if (currentThread == null) {
					iterations = 0;
					currentThread = new Thread(this);
					currentThread.start();
				}
				*/
			}
		}
		
		public void stop() {
			synchronized(this) {
				//currentThread = null;
				running = false;
				this.notifyAll();
			}
		}
		
		public void run() {
			synchronized (this) {
				//while (table.size() > 0 && Thread.currentThread() == currentThread && iterations < 10) {
				if (table.size() > 0 && running && iterations < 10) {
					
					StringBuffer sb = new StringBuffer();
					sb.append(table.size());
					sb.append(". ");
					Iterator<String> keys = table.keySet().iterator();
					while (keys.hasNext()) {
						sb.append(keys.next());
						sb.append(", ");
					}
					logger.log(Level.INFO, "Remaining meems: " + sb);
					
					/*
					try {
						wait(interval);
					}
					catch (InterruptedException e) {
						//currentThread = null;
						running = false;
					}
					*/
					iterations++;
					threadManagerConduit.queue(this, System.currentTimeMillis()+interval);
				}
				else {
					running = false;
				}
			}
		}
	}
}
