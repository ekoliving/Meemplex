/*
 * @(#)MeemServerWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meemserver;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerMeem;
import org.openmaji.implementation.server.manager.lifecycle.subsystem.SubsystemFactoryMeem;
import org.openmaji.implementation.server.nursery.worksheet.WorksheetLifeCycleManagerMeem;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationProvider;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class MeemServerWedge implements Wedge, MeemServer {

	private String name = MeemServer.spi.getName();

	public MeemCore meemCore;

	public Category categoryConduit; // outbound conduit

	public MeemClientConduit meemClientConduit;

	public ConfigurationProvider configurationProviderConduit = new ConfigurationProviderConduit();

	public transient ConfigurationSpecification<String> nameSpecification = ConfigurationSpecification.create("The name of the MeemServer");

	public void setName(String name) {
		this.name = name;
	}

	public class ConfigurationProviderConduit implements ConfigurationProvider {

		public void provideConfiguration(ConfigurationClient client, Filter filter) {
			ConfigurationSpecification<?>[] specs = { nameSpecification };

			client.specificationChanged(null, specs);

			String meemServerName = (name == null ? "" : name);
			client.valueAccepted(nameSpecification.getIdentifier(), meemServerName);

		}
	}

	private boolean init = false;
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition arg0) {
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (!init && transition.getCurrentState().equals(LifeCycleState.READY)) {
				init = true;

				if (subsystemCategoryMeemPath == null) {
					if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
						logger.log(logLevel, "Creating the Subsystem Category");
					}

					MeemDefinition categoryMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
					categoryMeemDefinition.getMeemAttribute().setIdentifier(StandardHyperSpaceCategory.SUBSYSTEM);
					lifeCycleManagerConduit.createMeem(categoryMeemDefinition, LifeCycleState.READY);
				}

				if (worksheetLCMMeemPath == null) {
					if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
						logger.log(logLevel, "Creating the Worksheet LifeCycleManager");
					}

					MeemDefinition worksheetLCMMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(WorksheetLifeCycleManagerMeem.class);

					lifeCycleManagerConduit.createMeem(worksheetLCMMeemDefinition, LifeCycleState.READY);
				}
			}
		}
	};

	// MeemServer meems: SubsystemManager, WorkSheet LCM

	public MeemPath subsystemCategoryMeemPath = null;
	public MeemPath worksheetLCMMeemPath = null;

	private Category subsystemCategory = null;

	public LifeCycleManager lifeCycleManagerConduit;
	public LifeCycleManagerClient lifeCycleManagerClientConduit = new LifeCycleManagerClient() {
		public void meemCreated(Meem meem, String identifier) {
			String worksheetLCMIdentifier = WorksheetLifeCycleManagerMeem.IDENTIFIER;
			if (identifier.equals(worksheetLCMIdentifier)) {
				if (Common.TRACE_ENABLED && Common.TRACE_MEEMSERVER) {
					logger.log(logLevel, "Worksheet LifeCycleManager Meem created: " + meem.getMeemPath());
				}

				worksheetLCMMeemPath = meem.getMeemPath();

				categoryConduit.addEntry("worksheet", meem);
			}

			else if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM)) {
				subsystemCategoryMeemPath = meem.getMeemPath();

				categoryConduit.addEntry(StandardHyperSpaceCategory.SUBSYSTEM, meem);

				meemClientConduit.provideReference(meem, "category", Category.class, new CategoryMeemClientCallback());
			}

			else if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_LCM)) {
				subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_LCM, meem);
			}

			else if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_INSTALLED)) {
				subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_INSTALLED, meem);
			}

			else if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_MEEMS_PATH)) {
				subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_MEEMS_PATH, meem);
			}

			else if (identifier.equals(SubsystemFactory.spi.getIdentifier())) {
				if (Common.TRACE_ENABLED && (Common.TRACE_SUBSYSTEM || Common.TRACE_MEEMSERVER)) {
					logger.log(logLevel, "SubsystemFactory Meem created: " + meem.getMeemPath());
				}

				subsystemCategory.addEntry(SubsystemFactory.spi.getIdentifier(), meem);
			}
		}

		public void meemDestroyed(Meem meem) {
			logger.log(Level.WARNING, "The MeemServer meems should never be destroyed");
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			logger.log(Level.WARNING, "The MeemServer meems should never be transferred");
		}
	};

	private class CategoryMeemClientCallback implements MeemClientCallback<Category> {

		public void referenceProvided(Reference<Category> reference) {
			subsystemCategory = reference.getTarget();

			// create factory
			MeemDefinition subsystemFactoryMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(SubsystemFactoryMeem.class);
			lifeCycleManagerConduit.createMeem(subsystemFactoryMeemDefinition, LifeCycleState.READY);

			// create LCM
			MeemDefinition lcmMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(PersistingLifeCycleManagerMeem.class);
			lcmMeemDefinition.getMeemAttribute().setIdentifier(StandardHyperSpaceCategory.SUBSYSTEM_LCM);
			lifeCycleManagerConduit.createMeem(lcmMeemDefinition, LifeCycleState.READY);

			// create installed category
			MeemDefinition categoryMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
			categoryMeemDefinition.getMeemAttribute().setIdentifier(StandardHyperSpaceCategory.SUBSYSTEM_INSTALLED);
			lifeCycleManagerConduit.createMeem(categoryMeemDefinition, LifeCycleState.READY);

			// create subsystem meems category
			MeemDefinition categoryMeemDefinition2 = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
			categoryMeemDefinition2.getMeemAttribute().setIdentifier(StandardHyperSpaceCategory.SUBSYSTEM_MEEMS_PATH);
			lifeCycleManagerConduit.createMeem(categoryMeemDefinition2, LifeCycleState.READY);
		}

	}

	/* --------------------- Logging fields ----------------------------- */

	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final Level logLevel = Common.getLogLevel();
}
