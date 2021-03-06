/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.subsystem;

import java.util.HashSet;
import java.util.Hashtable;

import org.openmaji.automation.device.Device;
import org.openmaji.automation.device.DeviceDescription;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionControl;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemConfiguration;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemMeemControl;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.utility.MeemUtility;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a wedge for a subsystem that commissions its own meems
 *
 */
public class SubsystemCommissioningWedge implements Wedge {
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemContext meemContext;
	public MeemCore meemCore;


	/* --------------------------- conduits ------------------------ */
	
	public ErrorHandler errorHandlerConduit;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public SubsystemClient subsystemClientConduit;

	public SubsystemMeemControl meemControlConduit = new MeemControlConduit();

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new MyLifeCycleManagerClient();

	public SubsystemCommissionControl commissionControlConduit = new CommissionControlConduit();

	public SubsystemCommissionState commissionStateConduit = new CommissionStateConduit();

	public SubsystemConfiguration subsystemConfigurationConduit = new SubsystemConfigurationHandler();
	
	public DependencyHandler dependencyHandlerConduit;


	/* ----------------------- persisted properties ------------------------------- */
	
	public CommissionState commissionState = CommissionState.NOT_COMMISSIONED;

	public String myHyperSpacePath;

	
	/* ---------------------- private and protected members ----------------------- */
	
	protected MeemDescription[] meemDescriptions;

	protected MeemDefinition[] meemDefinitions;

	protected String controllerName;

	protected String[] commissionNames;

	protected Class[] commissionClasses;

	protected Meem[] commissionMeems = null;

	protected final Hashtable indexTable = new Hashtable(); //TODO get rid of this

	private final Hashtable descriptions = new Hashtable();
	
	private final HashSet meemIdentifiers = new HashSet();

	private boolean DEBUG = false;
	
	private DependencyAttribute categoryDependencyAttribute;
	
	/* ---------------------------------- methods -------------------------------------- */
	
	public SubsystemCommissioningWedge() {
	}

	public SubsystemCommissioningWedge(MeemDescription[] descriptions, MeemDefinition[] definitions, String[] commissionNames, Class[] commissionClasses, String controllerName) {
		this();
		this.meemDefinitions = definitions;
		this.meemDescriptions = descriptions;
		this.commissionNames = commissionNames;
		this.commissionClasses = commissionClasses;
		this.controllerName = controllerName;
	}

	private void performCommission() throws Exception {
		if (commissionNames == null) {
			return;
		}

		commissionMeems = new Meem[commissionNames.length];

		for (int i = 0; i < commissionMeems.length; i++) {
			Object meemInstance = commissionClasses[i].newInstance();
			MeemDefinitionProvider mdp = (MeemDefinitionProvider) meemInstance;
			MeemDefinition meemDefinition = mdp.getMeemDefinition();
			String name = commissionNames[i];
			meemDefinition.getMeemAttribute().setIdentifier(name);
			indexTable.put(name, new Integer(i));
			lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.LOADED);
		}
	}

	/**
	 * Set up a dependency between a meem and a hyperspace-referenced meem.
	 * 
	 * @param meem The dependent meem.
	 * @param path The path to the meem to depend on.
	 * @param target the target facet
	 * @param source The source facet
	 */
	protected void setDependency(Meem meem, String path, String target, String source) {
		
		MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, myHyperSpacePath + "/" + path);
		
		// TODO make an AddressFilter
		Filter filter = null;
		
		DependencyAttribute attr = new DependencyAttribute(
				DependencyType.STRONG, 
				Scope.DISTRIBUTED, 
				meemPath, 
				target, 
				filter, 
				true
			);

		if (DEBUG) {
			logger.log(Level.INFO, "Adding dependency: " + meem + "." + source + " -> " + path + "." + target);
		}

		// XXX
		//DependencyHandler dependencyHandler = (DependencyHandler) MeemUtility.spi.get().getTarget(meem, "dependencyHandler", DependencyHandler.class);
		//dependencyHandler.addDependency(source, attr, LifeTime.PERMANENT);
		
		meem.addDependency(source, attr, LifeTime.PERMANENT);
	}

	protected void configureCommissionMeem(Meem meem, String identifier, int index) {
		// This method optionally over-riden by subclass
	}

	/* ----------------- Meem functionality ----------------------------------- */

	public void commence() {
		subsystemClientConduit.meemsAvailable(meemDefinitions, meemDescriptions);
		setupCategoryClient();
	}

	public void conclude() {
		removeCategoryClient();
	}

	
	/* ------------------------------------------------------------------------ */
	
	protected String getHyperspacePath() {
		return myHyperSpacePath;
	}
	
	private void setupCategoryClient() {
		if (categoryDependencyAttribute != null) {
			dependencyHandlerConduit.removeDependency(categoryDependencyAttribute);
		}
		if (myHyperSpacePath == null) {
			return;
		}
		
		MeemPath categoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, myHyperSpacePath);
		categoryDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, categoryMeemPath, "categoryClient");

		logger.log(Level.INFO, "Setting up categoryClient dependency: " + categoryDependencyAttribute);
		
		Facet facet = meemCore.getTargetFor(new CategoryClientImpl(), CategoryClient.class);
		dependencyHandlerConduit.addDependency(facet, categoryDependencyAttribute, LifeTime.TRANSIENT);
	}
	
	private void removeCategoryClient() {
		dependencyHandlerConduit.removeDependency(categoryDependencyAttribute);
	}


	/* ------------------------------------------------------------------------ */

	private class SubsystemConfigurationHandler implements SubsystemConfiguration {
		public void setHyperSpacePath(String newHyperSpacePath) {
			myHyperSpacePath = newHyperSpacePath;
			
			// set up dependency on hyperspace category
			removeCategoryClient();
			MeemPath categoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, myHyperSpacePath);
			categoryDependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, categoryMeemPath, "categoryClient");
			setupCategoryClient();
		}
	}

	/* ------------------------------------------------------------------------ */

	private class CommissionControlConduit implements SubsystemCommissionControl {
		public void changeCommissionState(CommissionState newState) {
			if (newState.equals(commissionState)) {
				logger.log(Level.WARNING, "changeCommissionState() - Subsystem already in that state");
				return;
			}

			if (myHyperSpacePath == null || myHyperSpacePath.length() == 0) {
				logger.log(Level.WARNING, "changeCommissionState() - HyperSpace path has not been configured");
				return;
			}

			try {
				if (newState.equals(CommissionState.COMMISSIONED)) {
					performCommission();
				}
				else {
					commissionMeems = null;
				}
			}
			catch (Exception ex) {
				errorHandlerConduit.thrown(ex);
				return;
			}

			subsystemClientConduit.commissionStateChanged(newState);
			commissionStateConduit.commissionStateChanged(newState);
			commissionState = newState;
		}
	}

	/* ------------------------------------------------------------------------ */

	private class CommissionStateConduit implements SubsystemCommissionState {
		public void commissionStateChanged(CommissionState newState) {
			commissionState = newState;
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemControlConduit implements SubsystemMeemControl {
		public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription) {
			if (commissionState.equals(CommissionState.NOT_COMMISSIONED)) {
				logger.log(Level.WARNING, "createMeem() - can not create Meems until subsystem commissioned");
				return;
			}

			if (myHyperSpacePath == null || myHyperSpacePath.length() == 0) {
				logger.log(Level.WARNING, "createMeem() - HyperSpace path has not been configured");
				return;
			}

			String identifier = meemDefinition.getMeemAttribute().getIdentifier();
			if (identifier == null || identifier.length() == 0) {
				logger.log(Level.WARNING, "createMeem() - identifier not set in MeemDefinition");
				return;
			}
			
			if (meemIdentifiers.contains(identifier)) {
				if (DEBUG) {
					logger.log(Level.INFO, "createMeem() - meem with identifier, \"" + identifier + "\", is already created in the subsystem");
				}

				// TODO check if the existing description != new description, destroy old meem and create new one
				return;
			}

			// add to set of pending meem creations 
			descriptions.put(identifier, meemDescription);
			lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.READY);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyLifeCycleManagerClient implements LifeCycleManagerClient {

		// NOTE we do not get initial content from the LCM wedge, so "meemIdentifiers" will not be populated
		public void meemCreated(Meem meem, String identifier) {
			// TODO Use request/response to match up created meem with request

			if (DEBUG) {
				logger.log(Level.INFO, "meemCreated(): " + identifier);
			}
			meemIdentifiers.add(identifier);	// add identifier to set of ids for meems in subsystem

			// remove from set of pending meem creations
			MeemDescription meemDescription = (MeemDescription) descriptions.remove(identifier);
			if (meemDescription != null) {
				handleMeemCreation(meem, identifier, meemDescription);
				return;
			}

			// check if the created meem is a commissioned meem
			Integer index = (Integer) indexTable.remove(identifier);
			if (index != null) {
				handleCommissionMeemCreation(meem, identifier, index.intValue());
				return;
			}

			/** Diana---commented the error logger because sometimes the required Meem descriptors are 
			 * passed via XML deployment file rather than SubsystemWedge 
			 **/
			//logger.log(Level.WARNING, "meemCreated() - unexpected meem, identifier=["+identifier+"]");
		}

		public void meemDestroyed(Meem meem) {
			/** Diana---commented the error logger to avoid error confusion **/
			//logger.log(Level.WARNING, "meemDestroyed() - TODO: finish this ?");  

			// TODO get identifier from meem
			//meem.getMeemPath();
			//meemIdentifiers.remove(identifier);	// add identifier to set of ids for meems in subsystem

		}

		public void meemTransferred(Meem meem, LifeCycleManager lcm) {
			logger.log(Level.WARNING, "meemTransferred() - TODO: finish this ?");
		}

		private void handleMeemCreation(Meem meem, String identifier, MeemDescription meemDescription) {
			// add meem to hyperspace category
			Meem categoryMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, myHyperSpacePath));
			Category category = (Category) MeemUtility.spi.get().getTarget(categoryMeem, "category", Category.class);
			category.addEntry(identifier, meem);

			// setup dependencies between the meem and controller meem
			if (controllerName != null) {
				// set up dependency between the meem and the controller meem
				setDependency(meem, controllerName, "deviceControllerInput", "deviceControllerOutput");
				setDependency(meem, controllerName, "deviceControllerOutput", "deviceControllerInput");
			}

			if (meemDescription instanceof DeviceDescription) {
				DeviceDescription description = (DeviceDescription) meemDescription;
				Device device = (Device) MeemUtility.spi.get().getTarget(meem, "deviceInput", Device.class);
				device.descriptionChanged(description);
			}

			MeemDefinition meemDefinition = MeemUtility.spi.get().getMeemDefinition(meem);
			meemDefinition.getMeemAttribute().setIdentifier(identifier);
			subsystemClientConduit.meemCreated(meem, meemDefinition);
		}

		private void handleCommissionMeemCreation(Meem meem, String identifier, int index) {
			commissionMeems[index] = meem;

			Meem categoryMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, myHyperSpacePath));
			Category category = (Category) MeemUtility.spi.get().getTarget(categoryMeem, "category", Category.class);
			category.addEntry(identifier, meem);

			configureCommissionMeem(meem, identifier, index);

			LifeCycleLimit lifeCycleLimit = (LifeCycleLimit) MeemUtility.spi.get().getTarget(meem, "lifeCycleLimit", LifeCycleLimit.class);
			lifeCycleLimit.limitLifeCycleState(LifeCycleState.READY);

			LifeCycle lifeCycle = (LifeCycle) MeemUtility.spi.get().getTarget(meem, "lifeCycle", LifeCycle.class);
			lifeCycle.changeLifeCycleState(LifeCycleState.READY);
		}

	}

	
	private final class CategoryClientImpl implements CategoryClient {
		public void entriesAdded(CategoryEntry[] entries) {
			for (int i=0; i<entries.length; i++) {
				if (DEBUG) {
					logger.log(Level.INFO, "adding identifier to known ids: " + entries[i].getName());
				}
				meemIdentifiers.add(entries[i].getName());
			}
		};
		
		public void entriesRemoved(CategoryEntry[] entries) {
			for (int i=0; i<entries.length; i++) {
				if (DEBUG) {
					logger.log(Level.INFO, "removing identifier from known ids: " + entries[i].getName());
				}
				meemIdentifiers.remove(entries[i].getName());
			}
		};
		
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			meemIdentifiers.remove(oldEntry.getName());			
			meemIdentifiers.add(newEntry.getName());
		};
		
		public void contentSent() {
		};

		public void contentFailed(String reason) {
		};	
	}
}
