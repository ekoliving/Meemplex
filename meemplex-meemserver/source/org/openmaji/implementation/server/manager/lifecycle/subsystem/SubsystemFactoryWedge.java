/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.subsystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MetaMeem;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.utility.CategoryUtility;
import org.openmaji.system.utility.MeemUtility;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This SubsystemFactory watches the Subsystem category in the toolkit portion
 * of HyperSpace to keep track of what subsystems can be created. Each entry in
 * that category is assumed to be a subsystem and its MeemDefinition is retrieved
 * and stored for any clients that need to know what subsystems can be created.
 *
 * @author Chris Kakris
 */
public class SubsystemFactoryWedge implements Wedge, SubsystemFactory, CategoryClient {
	private static final Logger logger = Logger.getAnonymousLogger();

	public static boolean DEBUG = true;

	public MeemCore meemCore;

	/* ---------------- outbound facets ------------------ */

	public SubsystemFactoryClient subsystemFactoryClient;

	public final ContentProvider subsystemFactoryClientProvider = new SubsystemFactoryContentProvider();

	/* --------------------- conduits -------------------- */

	/**
	 * for adding and removing dependencies
	 */
	public DependencyHandler dependencyHandlerConduit;

	/** a conduit for receiving dependency status of this meem */
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new LifeCycleManagerClientConduit();

	public ActivationClient activationClientConduit = new MyActivationClient();

	/* ----------------- private members ----------------- */

	private DependencyAttribute installedCategoryDependencyAttribute;

	private DependencyAttribute patternCategoryDependencyAttribute;

	private final Hashtable<String, MeemDefinition> meemDefinitions = new Hashtable<String, MeemDefinition>();

	private final Hashtable<String, Meem> meems = new Hashtable<String, Meem>();

	private final MeemUtility meemUtility = MeemUtility.spi.get();

	private final MeemPath installedCategoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_INSTALLED);

	private final MeemPath patternCategoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM + "/automation/subsystem");

	private Category installedCategory = null;

	private boolean installedCategoryConnected = false;

	/* -------------------- lifecycle functionality ----------------------- */

	public void commence() {
		Facet proxy = (Facet) meemCore.getTargetFor(new PatternCategoryClient(), CategoryClient.class);
		dependencyHandlerConduit.addDependency(proxy, getPatternCategoryDependencyAttribute(), LifeTime.TRANSIENT);

		// create dependency on installed category meem
		dependencyHandlerConduit.addDependency("categoryClient", getInstalledCategoryDependencyAttribute(), LifeTime.TRANSIENT);

	}

	public void conclude() {
		meemDefinitions.clear();

		dependencyHandlerConduit.removeDependency(getInstalledCategoryDependencyAttribute());
		dependencyHandlerConduit.removeDependency(getPatternCategoryDependencyAttribute());
	}

	/* --------------- SubsystemFactory interface methods --------------------- */

	public void addSubsystemDefinition(MeemDefinition meemDefinition) {
		String identifier = meemDefinition.getMeemAttribute().getIdentifier();
		if (identifier == null || identifier.length() == 0) {
			logger.log(Level.WARNING, "addSubsystemDefinition() - identifier not set in meemDefinition: " + meemDefinition);
			return;
		}

		meemDefinitions.put(identifier, meemDefinition);

		MeemDefinition[] definitions = new MeemDefinition[] { meemDefinition };
		subsystemFactoryClient.definitionsAdded(definitions);
	}

	public void removeSubsystemDefinition(MeemDefinition meemDefinition) {
		String identifier = meemDefinition.getMeemAttribute().getIdentifier();
		if (identifier == null || identifier.length() == 0) {
			logger.log(Level.WARNING, "removeSubsystemDefinition() - identifier not set in meemDefinition: " + meemDefinition);
			return;
		}

		meemDefinitions.remove(meemDefinition);

		MeemDefinition[] definitions = new MeemDefinition[] { meemDefinition };
		subsystemFactoryClient.definitionsRemoved(definitions);
	}

	/**
	 * Create a subsystem
	 */
	public void createSubsystem(MeemDefinition meemDefinition) {
		String identifier = meemDefinition.getMeemAttribute().getIdentifier();

		if (identifier == null) {
			logger.log(Level.INFO, "Can not create a subsystem will a null identifier");
			return;
		}

		if (DEBUG) {
			logger.log(Level.INFO, "creating a subsystem: " + identifier + " - " + meemDefinition);
		}

		if (!installedCategoryConnected) {
			logger.log(Level.INFO, "Installed subsystem category not connected.  Not creating subsystem, " + identifier);
			// TODO queue up meem definition for creation
			return;
		}

		if (meems.get(identifier) != null) {
			logger.log(Level.INFO, "Could not create sub-system \"" + identifier + "\" because it already exists");
		}
		else {
			lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.READY);
		}
	}

	public void destroySubsystem(Meem meem) {
		if (meems.size() == 0) {
			return;
		}

		Enumeration enumeration = meems.keys();
		while (enumeration.hasMoreElements()) {
			String subsystemName = (String) enumeration.nextElement();
			Meem knownMeem = (Meem) meems.get(subsystemName);
			if (meem.equals(knownMeem)) {
				getInstalledCategory().removeEntry(subsystemName);
				lifeCycleManagerConduit.destroyMeem(meem);
				return;
			}
		}

		logger.log(Level.WARNING, "destroyMeem() - Unknown Meem: " + meem.getMeemPath());
	}

	/* ------------------- private methods ------------------------------------ */

	/**
	 * This method should be removed once MeemUtility.getMeemDefinition has
	 * been fixed - all it does is remove the system Wedges from the meem
	 * definintion so that it can be used by a LifeCycleManager to create a
	 * new Meem.
	 * 
	 * @param meemDefinition
	 */
	private MeemDefinition removeSystemWedges(MeemDefinition meemDefinition) {
		if (meemDefinition == null) {
			return null;
		}

		Iterator iterator = meemDefinition.getWedgeDefinitions().iterator();
		ArrayList<WedgeDefinition> list = new ArrayList<WedgeDefinition>();
		while (iterator.hasNext()) {
			WedgeDefinition def = (WedgeDefinition) iterator.next();
			if (def.getWedgeAttribute().isSystemWedge())
				list.add(def);
		}

		MeemDefinition newMeemDefinition = (MeemDefinition) meemDefinition.clone();
		for (int i = 0; i < list.size(); i++) {
			WedgeDefinition def = (WedgeDefinition) list.get(i);
			newMeemDefinition.removeWedgeDefinition(def);
		}

		return newMeemDefinition;
	}

	private Category getInstalledCategory() {
		if (installedCategory == null) {
			installedCategory = CategoryUtility.spi.get().getCategory(Meem.spi.get(installedCategoryMeemPath));
		}
		return installedCategory;
	}

	private DependencyAttribute getPatternCategoryDependencyAttribute() {
		if (patternCategoryDependencyAttribute == null) {
			patternCategoryDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, patternCategoryMeemPath, "categoryClient", null, true);
		}
		return patternCategoryDependencyAttribute;
	}

	private DependencyAttribute getInstalledCategoryDependencyAttribute() {
		if (installedCategoryDependencyAttribute == null) {
			installedCategoryDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, installedCategoryMeemPath, "categoryClient");
		}
		return installedCategoryDependencyAttribute;
	}

	/* ------------------------------------------------------------------------ */

	private class LifeCycleManagerClientConduit implements LifeCycleManagerClient {
		public void meemCreated(Meem meem, String identifier) {
			if (DEBUG) {
				logger.log(Level.INFO, "meemCreated: " + identifier + " : " + meem);
			}

			// added by Warren 11/10/2006 to expediate entry into meems list
			meems.put(identifier, meem);

			if (identifier.equals("unidentified")) {
				// TODO Work out why this is happening and fix it
				logger.log(Level.WARNING, "meemCreated() - why is identifier=[" + identifier + "] for meem=[" + meem.getMeemPath().getLocation() + "]");
				return;
			}

			String path = StandardHyperSpaceCategory.MAJI_SUBSYSTEM_MEEMS + "/" + identifier;
			ConfigurationHandler ch = (ConfigurationHandler) meemUtility.getTarget(meem, "configurationHandler", ConfigurationHandler.class);
			ch.valueChanged(new ConfigurationIdentifier("SubsystemWedge", "hyperSpacePath"), path);

			getInstalledCategory().addEntry(identifier, meem);

			final String meemIdentifier = identifier;

			// send the meemDefinition to the subsystemFactoryclient
			new MetaMeemClient(meem, new MeemDefinitionRunnable() {
				public void run(Meem meem, MeemDefinition meemDefinition) {
					meemDefinition.getMeemAttribute().setIdentifier(meemIdentifier);
					subsystemFactoryClient.subsystemCreated(meem, meemDefinition);
				}
			});

		}

		public void meemDestroyed(Meem meem) {
			if (DEBUG) {
				logger.log(Level.INFO, "meemDestroyed: " + meem);
			}
			removeSubsystemFromInstalledCategory(meem);
			subsystemFactoryClient.subsystemDestroyed(meem);
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			if (DEBUG) {
				logger.log(Level.INFO, "meemTransferred: " + meem);
			}
			Meem lcmMeem = (Meem) targetLifeCycleManager;

			if (lcmMeem.getMeemPath().equals(meemCore.getMeemPath())) {

				if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
					logger.log(Common.getLogLevelVerbose(), "meemTransferred (Added) " + meem);
				}

				// send meemDefinition to subsystemFactoryClient
				new MetaMeemClient(meem, new MeemDefinitionRunnable() {
					public void run(Meem meem, MeemDefinition meemDefinition) {
						String identifier = meemDefinition.getMeemAttribute().getIdentifier();

						// transferred to us - create entry
						getInstalledCategory().addEntry(identifier, meem);
						subsystemFactoryClient.subsystemCreated(meem, meemDefinition);
					}
				});
			}
			else {
				if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
					logger.log(Common.getLogLevelVerbose(), "meemTransferred (Removed) " + meem);
				}
				removeSubsystemFromInstalledCategory(meem);
			}

		}

		private void removeSubsystemFromInstalledCategory(Meem meem) {

			// remove the entry from installed category
			new MetaMeemClient(meem, new MeemDefinitionRunnable() {
				public void run(Meem meem, MeemDefinition meemDefinition) {
					String identifier = meemDefinition.getMeemAttribute().getIdentifier();

					if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
						logger.log(Common.getLogLevelVerbose(), "removeSubsystemFromInstalledCategory " + meem + " identifier = " + identifier);
					}

					getInstalledCategory().removeEntry(identifier);
				}
			});

		}
	}

	/* ------------------------------------------------------------------------ */

	/**
	 * The ActivationClient conduit is used to notify clients when previously
	 * created subsystems have been started. Notification of their existence is
	 * sent to all SubsystemFactoryClients. The subsystems are not cached here,
	 * that is done in the InstalledCategoryClient
	 * 
	 * @author mg
	 */
	private class MyActivationClient implements ActivationClient {
		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient#activated(org.openmaji.meem.MeemPath, org.openmaji.meem.Meem, org.openmaji.meem.definition.MeemDefinition)
		 */
		public void activated(MeemPath meemPath, Meem meem, MeemDefinition meemDefinition) {
			meemDefinition = removeSystemWedges(meemDefinition);
			subsystemFactoryClient.subsystemCreated(meem, meemDefinition);
		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient#activationFailed(org.openmaji.meem.MeemPath)
		 */
		public void activationFailed(MeemPath meemPath) {
		}
	}

	/* ------------------------------------------------------------------------ */

	/**
	 * This CategoryClient watches the subsystems category in the toolkit pattern
	 * portion of HyperSpace. The entries it finds are used to keep track of the
	 * different possible subsystems that can be created. All SubsystemFactoryClients
	 * are notified of these.
	 *
	 * Note this must be public, otherwise IllegalAccessExceptions are thrown.
	 * 
	 * @author Chris Kakris
	 */
	public class PatternCategoryClient implements CategoryClient {
		public void entriesAdded(CategoryEntry[] entries) {
			MeemDefinition[] definitionsAdded = new MeemDefinition[entries.length];

			for (int i = 0; i < entries.length; i++) {
				CategoryEntry entry = entries[i];
				MeemDefinition definition = meemUtility.getMeemDefinition(entry.getMeem());
				if (definition == null) {
					logger.log(Level.WARNING, "entriesAdded() - why does MeemUtility return null as a definition for meem " + entry.getName() + " : " + entry.getMeem());
				}
				else {
					//        TODO Remove this next line once MeemUtility has been fixed
					definition = removeSystemWedges(definition);
					String identifier = definition.getMeemAttribute().getIdentifier();
					if (identifier == null || identifier.length() == 0) {
						definition.getMeemAttribute().setIdentifier(entry.getName());
					}
					meemDefinitions.put(entry.getName(), definition);
					definitionsAdded[i] = definition;
				}
			}

			subsystemFactoryClient.definitionsAdded(definitionsAdded);
		}

		public void entriesRemoved(CategoryEntry[] entries) {
			MeemDefinition[] definitionsRemoved = new MeemDefinition[entries.length];

			for (int i = 0; i < entries.length; i++) {
				CategoryEntry entry = entries[i];
				meemDefinitions.remove(entry.getName());
				definitionsRemoved[i] = meemUtility.getMeemDefinition(entry.getMeem());
			}

			subsystemFactoryClient.definitionsRemoved(definitionsRemoved);
		}

		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			MeemDefinition oldDefinition = meemUtility.getMeemDefinition(oldEntry.getMeem());
			meemDefinitions.remove(oldEntry.getName());
			subsystemFactoryClient.definitionsRemoved(new MeemDefinition[] { oldDefinition });

			MeemDefinition newDefinition = meemUtility.getMeemDefinition(newEntry.getMeem());
			String identifier = newDefinition.getMeemAttribute().getIdentifier();
			if (identifier == null || identifier.length() == 0) {
				newDefinition.getMeemAttribute().setIdentifier(newEntry.getName());
			}
			meemDefinitions.put(newEntry.getName(), newDefinition);
			subsystemFactoryClient.definitionsRemoved(new MeemDefinition[] { newDefinition });
		}
	}

	/* ------------------------------------------------------------------------ */

	/**
	 * This CategoryClient watches the category of installed Subsystems. The Meems
	 * for those subsystems are cached. 
	 *
	 * @author Chris Kakris
	 */
	public void entriesAdded(CategoryEntry[] entries) {
		for (int i = 0; i < entries.length; i++) {
			CategoryEntry entry = entries[i];
			if (DEBUG) {
				logger.log(Level.INFO, "Adding subsystem category entry: " + entry.getName());
			}
			meems.put(entry.getName(), entry.getMeem());

			if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
				logger.log(Common.getLogLevelVerbose(), entry.getName() + " added to subsystem category");
			}

		}
	}

	public void entriesRemoved(CategoryEntry[] entries) {
		for (int i = 0; i < entries.length; i++) {
			CategoryEntry entry = entries[i];
			Meem meem = entry.getMeem();
			meems.remove(entry.getName());

			if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
				logger.log(Common.getLogLevelVerbose(), entry.getName() + " removed from subsystem category");
			}

			subsystemFactoryClient.subsystemDestroyed(meem);
		}
	}

	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
			logger.log(Common.getLogLevelVerbose(), oldEntry.getName() + " renamed to " + newEntry.getName() + " in subsystem category");
		}

		meems.remove(oldEntry.getName());
		subsystemFactoryClient.subsystemDestroyed(oldEntry.getMeem());

		meems.put(newEntry.getName(), newEntry.getMeem());
		MeemDefinition newDefinition = meemUtility.getMeemDefinition(newEntry.getMeem());
		subsystemFactoryClient.subsystemCreated(newEntry.getMeem(), newDefinition);
	}

	/* --------------- ContentProvider ---------------------------------------- */

	private class SubsystemFactoryContentProvider implements ContentProvider {
		public synchronized void sendContent(Object target, Filter filter) {
			SubsystemFactoryClient client = (SubsystemFactoryClient) target;

			if (meemDefinitions.size() != 0) {
				MeemDefinition[] definitions = new MeemDefinition[meemDefinitions.size()];
				Enumeration enumeration = meemDefinitions.elements();
				int index = 0;
				while (enumeration.hasMoreElements()) {
					definitions[index++] = (MeemDefinition) enumeration.nextElement();
				}
				client.definitionsAdded(definitions);
			}

			if (meems.size() != 0) {
				Enumeration enumeration = meems.elements();
				while (enumeration.hasMoreElements()) {
					Meem meem = (Meem) enumeration.nextElement();
					MeemDefinition meemDefinition = meemUtility.getMeemDefinition(meem);
					client.subsystemCreated(meem, meemDefinition);
				}
			}
		}
	}

	/* --------------- MetaMeem client ------------------------- */

	private interface MeemDefinitionRunnable {
		public void run(final Meem meem, final MeemDefinition meemDefinition);
	}

	private class MetaMeemClient implements MetaMeem, ContentClient {

		final Meem meem;

		final MeemDefinitionRunnable meemDefinitionRunnable;

		private MeemDefinition meemDefinition;

		private WedgeDefinition lastWedgeDefinition = null;

		private FacetDefinition lastFacetDefinition = null;

		public MetaMeemClient(Meem meem, MeemDefinitionRunnable meemDefinitionRunnable) {
			this.meem = meem;
			this.meemDefinitionRunnable = meemDefinitionRunnable;

			meemDefinition = new MeemDefinition();

			Reference metaMeemClientReference = Reference.spi.create("metaMeemClient", meemCore.getTargetFor(this, MetaMeem.class), true);
			meem.addOutboundReference(metaMeemClientReference, true);
		}

		public void updateMeemAttribute(MeemAttribute meemAttribute) {
			meemDefinition.setMeemAttribute(meemAttribute);
		}

		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute) {
		}

		public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute) {
			lastFacetDefinition = new FacetDefinition(facetAttribute);
			lastWedgeDefinition.addFacetDefinition(lastFacetDefinition);
		}

		public void addWedgeAttribute(WedgeAttribute wedgeAttribute) {
			lastWedgeDefinition = new WedgeDefinition(wedgeAttribute);
			meemDefinition.addWedgeDefinition(lastWedgeDefinition);
		}

		public void removeDependencyAttribute(Serializable dependencyKey) {
			// don't care
		}

		public void removeFacetAttribute(String facetKey) {
			// don't care
		}

		public void removeWedgeAttribute(Serializable wedgeKey) {
			// don't care
		}

		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {
			// don't care
		}

		public void updateFacetAttribute(FacetAttribute facetAttribute) {
			// don't care
		}

		public void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {
			// don't care
		}

		public void contentFailed(String reason) {
		}

		public void contentSent() {
			meemDefinitionRunnable.run(meem, meemDefinition);
		}

	}

	private final class DependencyClientConduit implements DependencyClient {

		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}

		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getInstalledCategoryDependencyAttribute())) {
				installedCategoryConnected = true;
				//if (subsystemFactoryClientConnected) {
				//	createSubsystems();
				//}
			}
		}

		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttribute.equals(getInstalledCategoryDependencyAttribute())) {
				installedCategoryConnected = false;
			}
		}

	}

}
