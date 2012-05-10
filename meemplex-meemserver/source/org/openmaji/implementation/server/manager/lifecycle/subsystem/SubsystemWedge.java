/*
 * @(#)SubsystemWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.lifecycle.subsystem;

import java.util.HashSet;
import java.util.Set;

import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.StringConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionControl;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemConfiguration;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemMeemControl;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemState;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.utility.CategoryUtility;
import org.openmaji.system.utility.MeemUtility;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * The SubsystemWedge is a standard component that can be used by every subsystem meem to stop and start a subsystem. The subsystem developer is expected to write a domain specific
 * Wedge that extends SubsystemCommissioningWedge and uses conduits to interact with this wedge.
 * 
 * @author Chris Kakris
 */
public class SubsystemWedge implements Wedge, Subsystem {
	private static final Logger logger = LogFactory.getLogger();

	public MeemContext meemContext;

	public MeemCore meemCore;

	/* ------------------------ outbound facets ------------------------ */

	public SubsystemClient subsystemClient;

	public final ContentProvider subsystemClientProvider = new SubsystemClientContentProvider();

	/* ---------------------------- conduits --------------------------- */

	public SubsystemClient subsystemClientConduit = new SubsystemClientConduit();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new MyLifeCycleManagerClient();

	public LifeCycleAdapter lifeCycleAdapterConduit;

	public ActivationClient activationClientConduit = new MyActivationClient();

	public SubsystemCommissionControl commissionControlConduit;

	public SubsystemCommissionState commissionStateConduit = new CommissionStateConduit();

	public SubsystemConfiguration subsystemConfigurationConduit;

	public SubsystemMeemControl meemControlConduit;

	public ConfigurationHandler configurationHandlerConduit;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	/* -------------------------- persisted properties ------------------------- */

	public SubsystemState subsystemState = SubsystemState.STOPPED;

	public CommissionState commissionState = CommissionState.NOT_COMMISSIONED;

	public String hyperSpacePath;

	public transient ConfigurationSpecification hyperSpacePathSpecification = new StringConfigurationSpecification("Location in HyperSpace to create subsystem meems", LifeCycleState.READY);

	/* ------------------------------ private members ----------------------- */

	private Set<Meem> meems = new HashSet<Meem>();

	private Set<String> categoryEntries = new HashSet<String>();

	private LifeCycleManager lifeCycleManager = null;

	private MeemDefinition[] meemDefinitions;

	private MeemDescription[] meemDescriptions;

	private final MeemUtility meemUtility = MeemUtility.spi.get();

	/* --------------- Meem functionality ------------------------------------- */

	public void commence() {
		LogTools.info(logger, "Subsystem started: " + meemCore.getMeemPath() + " : " + meemCore.getMeemStructure().getMeemAttribute().getIdentifier());

		lifeCycleManager = (LifeCycleManager) meemContext.getTarget("lifeCycleManager");

		if (hyperSpacePath == null) {
			String newHyperSpacePath = StandardHyperSpaceCategory.MAJI_SUBSYSTEM_MEEMS + "/" + meemCore.getMeemStructure().getMeemAttribute().getIdentifier();

			configurationHandlerConduit.valueChanged(new ConfigurationIdentifier("SubsystemWedge", "hyperSpacePath"), newHyperSpacePath);
		}
	}

	public void conclude() {
		LogTools.info(logger, "Subsystem stopped: " + meemCore.getMeemPath() + " : " + meemCore.getMeemStructure().getMeemAttribute().getIdentifier());
	}

	/* ---------- ConfigurationChangeHandler listener ------------------------------- */

	public void setHyperSpacePath(String value) throws ConfigurationRejectedException {
		if (commissionState.equals(CommissionState.COMMISSIONED)) {
			// Can not change HyperSpacePath once commissioned
			return;
		}

		hyperSpacePath = value;
		subsystemConfigurationConduit.setHyperSpacePath(value);
	}

	/* ---------------------- Subsystem facet methods ------------------------- */

	public void changeSubsystemState(SubsystemState newState) {
		if (newState.equals(SubsystemState.STARTING))
			return;
		if (newState.equals(SubsystemState.STOPPING))
			return;

		if (newState.equals(SubsystemState.STARTED)) {
			setMySubsystemState(SubsystemState.STARTING);
			changeMeemsState(LifeCycleState.READY);
			setMySubsystemState(SubsystemState.STARTED);
		}
		else {
			setMySubsystemState(SubsystemState.STOPPING);
			changeMeemsState(LifeCycleState.LOADED);
			setMySubsystemState(SubsystemState.STOPPED);
		}
	}

	public void changeCommissionState(CommissionState newState) {
		if (newState.equals(CommissionState.NOT_COMMISSIONED)) {
			if (commissionState.equals(CommissionState.COMMISSIONED)) {
				blowAwayCategoryEntries();
				blowAwayMeems();
				commissionControlConduit.changeCommissionState(CommissionState.NOT_COMMISSIONED);
			}
		}
		else {
			if (commissionState.equals(CommissionState.NOT_COMMISSIONED)) {
				createSubsystemCategory();
				commissionControlConduit.changeCommissionState(CommissionState.COMMISSIONED);
			}
		}
	}

	public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription) {
		meemControlConduit.createMeem(meemDefinition, meemDescription);
	}

	/* ------------- private helper methods ----------------------------------- */

	private void createSubsystemCategory() {

		if (hyperSpacePath == null) {
			LogTools.error(logger, "Subsystem HyperSpace Category Path not initialized");
			return;
		}

		CategoryUtility.spi.get().getCategory(Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/")), MeemPath.spi.create(Space.HYPERSPACE, hyperSpacePath));
	}

	private void blowAwayCategoryEntries() {
		if (hyperSpacePath == null) {
			// This happens when a GenericSubsystem is decommissioned
			return;
		}

		// First remove all the entries from the category

		Meem categoryMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, hyperSpacePath));
		Category category = (Category) meemUtility.getTarget(categoryMeem, "category", Category.class);
		if (category != null) {
			for (String entryName : categoryEntries) {
				category.removeEntry(entryName);
			}
			categoryEntries.clear();
		}

		// Remove the category itself from its parent category

		int index = hyperSpacePath.lastIndexOf('/');
		String parentCategoryName = hyperSpacePath.substring(0, index);
		String entryName = hyperSpacePath.substring(index + 1);
		MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, parentCategoryName);
		category = CategoryUtility.spi.get().getCategory(Meem.spi.get(meemPath));
		category.removeEntry(entryName);

		// Finally destroy the category itself

		meemPath = MeemPath.spi.create(Space.HYPERSPACE, hyperSpacePath);
		Meem meem = Meem.spi.get(meemPath);
		LifeCycle lifeCycle = (LifeCycle) meemUtility.getTarget(meem, "lifeCycle", LifeCycle.class);
		if (lifeCycle != null) {
			lifeCycle.changeLifeCycleState(LifeCycleState.ABSENT);
		}
	}

	private void blowAwayMeems() {
		Set<Meem> meemsCopy = new HashSet<Meem>(meems);
		for (Meem meem : meemsCopy) {
			lifeCycleManagerConduit.destroyMeem(meem);
		}
	}

	private void setMySubsystemState(SubsystemState newState) {
		subsystemState = newState;
		subsystemClient.subsystemStateChanged(newState);
	}

	private void changeMeemsState(LifeCycleState lifeCycleState) {
		for (Meem meem : meems) {
			lifeCycleAdapterConduit.changeLifeCycleState(meem, lifeCycleState);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class SubsystemClientConduit implements SubsystemClient {
		public void subsystemStateChanged(SubsystemState subsystemState) {
			subsystemClient.subsystemStateChanged(subsystemState);
		}

		public void commissionStateChanged(CommissionState commissionState) {
			subsystemClient.commissionStateChanged(commissionState);
		}

		public void meemCreated(Meem meem, MeemDefinition meemDefinition) {
			subsystemClient.meemCreated(meem, meemDefinition);
		}

		public void meemsAvailable(MeemDefinition[] definitions, MeemDescription[] descriptions) {
			meemDefinitions = definitions;
			meemDescriptions = descriptions;
			subsystemClient.meemsAvailable(definitions, descriptions);
		}

	}

	/* ------------------------------------------------------------------------ */

	private class MyLifeCycleManagerClient implements LifeCycleManagerClient {
		public void meemCreated(Meem meem, String identifier) {
			meems.add(meem);
			categoryEntries.add(identifier);
		}

		public void meemDestroyed(Meem meem) {
			meems.remove(meem);
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			if (lifeCycleManager.equals(targetLifeCycleManager)) {
				// We don't need to do anything here, because we will see an activation message
			}
			else {
				meems.remove(meem);
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyActivationClient implements ActivationClient {
		public void activated(MeemPath meemPath, Meem meem, MeemDefinition meemDefinition) {
			meems.add(meem);
		}

		public void activationFailed(MeemPath meemPath) {
			LogTools.error(logger, "activationFailed() - Meem in subsystem failed to activate: " + meemPath);
		}
	};

	/* ------------------------------------------------------------------------ */

	private class CommissionStateConduit implements SubsystemCommissionState {
		public void commissionStateChanged(CommissionState newState) {
			commissionState = newState;
			subsystemClient.commissionStateChanged(newState);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class SubsystemClientContentProvider implements ContentProvider {
		public void sendContent(Object target, Filter filter) throws ContentException {
			SubsystemClient subsystemClient = (SubsystemClient) target;

			// send meemdefs and descriptions first.
			subsystemClient.meemsAvailable(meemDefinitions, meemDescriptions);
			subsystemClient.commissionStateChanged(commissionState);
			subsystemClient.subsystemStateChanged(subsystemState);
		}
	}
}
