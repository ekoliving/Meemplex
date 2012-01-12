/*
 * @(#)MeemServerWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class MeemServerWedge implements Wedge, MeemServer {

	private String name = MeemServer.spi.getName();
	
	public MeemCore meemCore;
	
	public Category categoryConduit; // outbound
	
	public MeemClientConduit meemClientConduit;
	
	public ConfigurationProvider configurationProviderConduit = new ConfigurationProviderConduit();

	public transient ConfigurationSpecification nameSpecification =	new ConfigurationSpecification("The name of the MeemServer");

	public void setName(String name) {
		this.name = name;
	}

	public class ConfigurationProviderConduit implements ConfigurationProvider {

		public void provideConfiguration(ConfigurationClient client, Filter filter) {
			ConfigurationSpecification[] specs = { nameSpecification };

			client.specificationChanged(null, specs);

			String meemServerName = (name == null ? "" : name);
			client.valueAccepted(nameSpecification.getIdentifier(), meemServerName);
		
		}
	}

	private boolean init = false;
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition arg0) {}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (!init && transition.getCurrentState().equals(LifeCycleState.READY)) {
				init = true;

				if (subsystemCategoryMeemPath == null) {
					if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
						LogTools.trace(logger, logLevel, "Creating the Subsystem Category");
					}
	
					MeemDefinition categoryMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
					categoryMeemDefinition.getMeemAttribute().setIdentifier(StandardHyperSpaceCategory.SUBSYSTEM);
					lifeCycleManagerConduit.createMeem(categoryMeemDefinition, LifeCycleState.READY);
				} 

				if (worksheetLCMMeemPath == null) {
					if (Common.TRACE_ENABLED && Common.TRACE_SUBSYSTEM) {
						LogTools.trace(logger, logLevel, "Creating the Worksheet LifeCycleManager");
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
		public void meemCreated(Meem meem, String identifier)
		{
			String worksheetLCMIdentifier = WorksheetLifeCycleManagerMeem.IDENTIFIER;
			if (identifier.equals(worksheetLCMIdentifier))
			{
				if (Common.TRACE_ENABLED && Common.TRACE_MEEMSERVER)
				{
					LogTools.trace(logger, logLevel, "Worksheet LifeCycleManager Meem created: " + meem.getMeemPath());
				}

				worksheetLCMMeemPath = meem.getMeemPath();
				
				categoryConduit.addEntry("worksheet", meem);
			}
			
			if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM))
			{
				subsystemCategoryMeemPath = meem.getMeemPath();
				
				categoryConduit.addEntry(StandardHyperSpaceCategory.SUBSYSTEM, meem);
				
				meemClientConduit.provideReference(meem, "category", Category.class, new CategoryMeemClientCallback());
			}
			
			if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_LCM))
			{
				subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_LCM, meem);
			}
			
			if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_INSTALLED))
			{
				subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_INSTALLED, meem);
			}
			
      if (identifier.equals(StandardHyperSpaceCategory.SUBSYSTEM_MEEMS_PATH))
      {
        subsystemCategory.addEntry(StandardHyperSpaceCategory.SUBSYSTEM_MEEMS_PATH, meem);
      }
      
			String subsystemFactoryIdentifier = SubsystemFactory.spi.getIdentifier(); 
			if (identifier.equals(subsystemFactoryIdentifier))
			{
				if (Common.TRACE_ENABLED && (Common.TRACE_SUBSYSTEM || Common.TRACE_MEEMSERVER))
				{
					LogTools.trace(logger, logLevel, "SubsystemFactory Meem created: " + meem.getMeemPath());
				}
				
				subsystemCategory.addEntry(subsystemFactoryIdentifier, meem);
			}
		}

		public void meemDestroyed(Meem meem)
		{
			LogTools.error(logger, "The MeemServer meems should never be destroyed");
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager)
		{
			LogTools.error(logger, "The MeemServer meems should never be transferred");
		}
	};
	
	class CategoryMeemClientCallback implements MeemClientCallback {
		
		public void referenceProvided(Reference reference) {
			subsystemCategory = (Category) reference.getTarget();
			
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

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final int logLevel = Common.getLogLevel();
}
