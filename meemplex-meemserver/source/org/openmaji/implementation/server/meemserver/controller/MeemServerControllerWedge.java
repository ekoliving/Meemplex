/*
 * @(#)MeemServerControllerWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meemserver.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meemserver.MeemServerMeem;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.common.VariableMap;
import org.openmaji.server.helper.*;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.hook.security.AccessControl;
import org.openmaji.system.meem.hook.security.AccessLevel;
import org.openmaji.system.meem.hook.security.Principals;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.meemserver.controller.MeemServerController;
import org.openmaji.system.presentation.MeemIconicPresentation;
import org.openmaji.system.presentation.ResourceExporter;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;
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
public class MeemServerControllerWedge implements MeemServerController, Wedge, LifeCycleManagerClient, MeemResolverClient {

	public MeemClientConduit meemClientConduit; // outbound

	public MeemCore meemCore;
	private Map<DependencyAttribute, String> meemServers = new HashMap<DependencyAttribute, String>();
	private Map<Serializable, DependencyAttribute> dependencyAttributes = new HashMap<Serializable, DependencyAttribute>();
	
	public DependencyHandler dependencyHandlerConduit;
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	public Category categoryConduit;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	private String localMeemServerName = MeemServer.spi.getName();

	/**
	 * 
	 */
	public void commence() {
		// can we resolve our own MeemServer meem in hyperspace?

		MeemPath meemServerPath =
			MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.DEPLOYMENT + "/" + localMeemServerName);

		Meem meemResolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());

		Reference reference =
			Reference.spi.create(
				"meemResolverClient",
				(MeemResolverClient) meemCore.getTarget("meemResolverClient"),
				true,
				new ExactMatchFilter(meemServerPath));

		meemResolverMeem.addOutboundReference(reference, true);
	}

	/**
	 * @see org.openmaji.system.space.resolver.MeemResolverClient#meemResolved(org.openmaji.meem.MeemPath, org.openmaji.meem.Meem)
	 */
	public void meemResolved(MeemPath meemPath, Meem meem) {
		if (meem == null) {
			// create a new MeemServerMeem
			createMeemServerMeem(localMeemServerName, EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier()));
		} else {
			// add it to our category
			categoryConduit.addEntry(localMeemServerName, meem);
		}
	}

	/**
	 */
	public void createMeemServerMeem(String meemServerName, Meem parentLifeCycleManagerMeem) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSERVER) {
			LogTools.trace(logger, logLevel, "Creating MeemServer Meem: " + meemServerName);
		}

		String identifier = MeemServer.spi.getIdentifier() + " " + meemServerName;

		// add dependency
		
		DependencyAttribute dependencyAttribute = new DependencyAttribute(
				DependencyType.WEAK,
				Scope.LOCAL,
				parentLifeCycleManagerMeem,
				"lifeCycleManagerClient",
				new ExactMatchFilter(identifier),
				false);
		
		dependencyAttributes.put(identifier, dependencyAttribute);

		dependencyHandlerConduit.addDependency("lifeCycleManagerClient", dependencyAttribute, LifeTime.TRANSIENT);

		meemServers.put(dependencyAttribute, meemServerName);
	}

	private final class CreateMeemTask implements MeemClientCallback {
		//private Meem parentLifeCycleManagerMeem;
		private String meemServerName;

		public CreateMeemTask(String meemServerName, Meem parentLifeCycleManagerMeem) {
			//this.parentLifeCycleManagerMeem = parentLifeCycleManagerMeem;
			this.meemServerName = meemServerName;

			meemClientConduit.provideReference(parentLifeCycleManagerMeem, "lifeCycleManager", LifeCycleManager.class, this);
		}

		/**
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
				LogTools.info(logger, "change - no lifeCycleManager facet.");
				return;
			}

			LifeCycleManager lifeCycleManager = (LifeCycleManager) reference.getTarget();
			MeemDefinition meemServerMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(MeemServerMeem.class);

			String identifier = MeemServer.spi.getIdentifier() + " " + meemServerName;

			MeemAttribute meemAttribute = new MeemAttribute(identifier);

			meemServerMeemDefinition.setMeemAttribute(meemAttribute);

			lifeCycleManager.createMeem(meemServerMeemDefinition, LifeCycleState.READY);
		}
	}

	/**
	 * Set access for other to READ only.
	 */
	private final class SetAccessTask implements MeemClientCallback {
		//private Meem meem;

		public SetAccessTask(Meem meem) {
			//this.meem = meem;

			meemClientConduit.provideReference(meem, "accessControl", AccessControl.class, this);
		}

		/**
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
				LogTools.info(logger, "change - no lifeCycleManager facet.");
				return;
			}

			AccessControl accessControl = (AccessControl) reference.getTarget();

			accessControl.addAccess(Principals.OTHER, AccessLevel.READ);
		}
	}

	/**
	  * Add the passed in entry to the passed in category.
	  */
	private final class AddEntryTask implements MeemClientCallback {
		//private Meem category;
		private String entryName;
		private Meem entryMeem;

		public AddEntryTask(Meem category, String entryName, Meem entryMeem) {
			//this.category = category;
			this.entryName = entryName;
			this.entryMeem = entryMeem;

			meemClientConduit.provideReference(category, "category", Category.class, this);
		}

		/**
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
				LogTools.info(logger, "change - no lifeCycleManager facet.");
				return;
			}

			Category cat = (Category) reference.getTarget();

			cat.addEntry(entryName, entryMeem);
		}
	}
	/* --------------- LifeCycleManagerClient methods ------------- */

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemCreated(org.openmaji.meem.Meem, java.lang.String)
	 */
	public void meemCreated(Meem meem, String identifier) {
		if (dependencyAttributes.containsKey(identifier)) {

			String meemServerName = identifier.substring(MeemServer.spi.getIdentifier().length() + 1);

			if (Common.TRACE_ENABLED && Common.TRACE_MEEMSERVER) {
				LogTools.trace(logger, logLevel, "MeemServer Meem created: " + meemServerName);
			}

			// remove dependency
			DependencyAttribute dependencyAttribute = (DependencyAttribute) dependencyAttributes.remove(identifier);

			dependencyHandlerConduit.removeDependency(dependencyAttribute);
			
			// add it to our category
			categoryConduit.addEntry(meemServerName, meem);

			// add the new MeemServer Meem to the deployment category in hyperspace

			Meem deploymentMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.DEPLOYMENT));

			if (deploymentMeem == null) {
				deploymentMeem = LifeCycleManagerHelper.assembleMeem(
					new Class[] {
						org.openmaji.implementation.common.VariableMapWedge.class,
						org.openmaji.implementation.server.space.CategoryWedge.class,
						org.openmaji.server.presentation.PatternGroupWedge.class },
					LifeCycleState.LOADED,
					LifeCycleState.READY,
					StandardHyperSpaceCategory.DEPLOYMENT);
					
					
				ResourceExporter re = new ResourceExporter(org.openmaji.server.presentation.hyperspace.images.Images.class);
				MeemIconicPresentation icons = new MeemIconicPresentation();
				icons.setSmallIcon(re.extract("meemserver_16.gif"));
				icons.setLargeIcon(re.extract(MeemIconicPresentation.getLargeIconName("meemserver_16.gif")));
	
				VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(deploymentMeem, "variableMap", VariableMap.class);
				variableMap.update(org.openmaji.system.presentation.InterMajik.ICONIC_PRESENTATION_KEY, icons);
	
				
				ConfigurationHandler ch =
					(ConfigurationHandler) ReferenceHelper.getTarget(deploymentMeem, "configurationHandler", ConfigurationHandler.class);
				ConfigurationIdentifier ci = new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
				ch.valueChanged(ci, "deployment");

			}
			//
			// add the meem server to the deployment category.
			//
			new AddEntryTask(deploymentMeem, meemServerName, meem);

			//
			// take read_write for other off.
			//
			new SetAccessTask(deploymentMeem);

		}

	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemDestroyed(org.openmaji.meem.Meem)
	 */
	public void meemDestroyed(Meem meem) {
		// don't care	
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemTransferred(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
		// don't care	
	}

	private final class DependencyClientConduit implements DependencyClient {

		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (meemServers.containsKey(dependencyAttribute)) {
				String meemServerName = (String) meemServers.remove(dependencyAttribute);
				new CreateMeemTask(meemServerName, dependencyAttribute.getMeem());
			}
		}
		
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
	}
	
	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final int logLevel = Common.getLogLevelVerbose();

}
