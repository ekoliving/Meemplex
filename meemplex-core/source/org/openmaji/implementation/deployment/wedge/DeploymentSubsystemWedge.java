/*
 * @(#)DeploymentSubsystem.java
 *
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.deployment.wedge;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.openmaji.diagnostic.Debug;
import org.openmaji.implementation.deployment.ConfigurationParameter;
import org.openmaji.implementation.deployment.DependencyDescriptor;
import org.openmaji.implementation.deployment.MeemDeploymentDescriptor;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.*;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.Category;
import org.openmaji.system.utility.CategoryUtility;
import org.openmaji.system.utility.MeemUtility;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * @author mg
 */
public class DeploymentSubsystemWedge implements Wedge {

	/** logger */
	private static final Logger logger = LogFactory.getLogger();

	/** Meem context */
	public MeemContext meemContext;

	/* ----------------- outbound facets ------------------ */

	/* ------------------- conduits ----------------------- */

	public SubsystemClient subsystemClientConduit;

	public SubsystemCommissionControl commissionControlConduit = new CommissionControlConduit();

	public SubsystemMeemControl meemControlConduit = new MeemControlConduit();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new LifeCycleManagerClientConduit();

	public SubsystemCommissionState commissionStateConduit;

	public Debug debugConduit = new MyDebugConduit();

	/**
	 * Conduit on which to request dynamic facets.
	 */
	public MeemClientConduit meemClientConduit;

	/* --------------------- private fields ------------------- */

	private Hashtable<String, MeemDeploymentDescriptor> descriptions = new Hashtable<String, MeemDeploymentDescriptor>();

	private Hashtable<MeemPath, MeemDefinition> definitions = new Hashtable<MeemPath, MeemDefinition>();

	private int debugLevel;

	private Meem hyperspaceMeem;

	public void commence() {
		hyperspaceMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/"));

		commissionStateConduit.commissionStateChanged(CommissionState.COMMISSIONED);
	}

	private void configureMeem(Meem meem, MeemDeploymentDescriptor meemDescription) {
		MeemClientCallback callback = new ConfigurationHandlerMeemClientCallback(meem, meemDescription);
		meemClientConduit.provideReference(meem, "configurationHandler", ConfigurationHandler.class, callback);
	}

	private void setupDependencies(Meem meem, MeemDeploymentDescriptor deploymentDescriptor) {

		Collection<DependencyDescriptor> dependencies = deploymentDescriptor.getDependencies();

		if (dependencies.size() > 0) {
			//DependencyHandler dependencyHandler = (DependencyHandler) MeemUtility.spi.get().getTarget(meem, "dependencyHandler", DependencyHandler.class);

			String identifier = deploymentDescriptor.getId();

			// Iterate through each of the declared dependencies for this meem and use MetaMeem to create them
			Iterator<DependencyDescriptor> iterator = dependencies.iterator();
			while (iterator.hasNext()) {
				DependencyDescriptor dependencyDescriptor = (DependencyDescriptor) iterator.next();
				DependencyAttribute dependencyAttribute = dependencyDescriptor.getDependencyAttribute();

				meem.addDependency(dependencyDescriptor.getFacetName(), dependencyAttribute, LifeTime.PERMANENT);

				if (debugLevel > 0) {
					StringBuffer message = new StringBuffer(80);
					message.append("dependency: ");
					message.append(identifier);
					message.append(".");
					message.append(dependencyDescriptor.getFacetName());
					message.append(" -> ");
					message.append(dependencyAttribute.getMeemPath());
					message.append(".");
					message.append(dependencyAttribute.getFacetIdentifier());
					LogTools.info(logger, message.toString());
				}
			}
		}
	}

	private void addToHyperspace(Meem meem, MeemDeploymentDescriptor deploymentDescriptor) {
		Collection<MeemPath> paths = deploymentDescriptor.getHyperSpacePaths();
		Iterator<MeemPath> it = paths.iterator();
		while (it.hasNext()) {
			MeemPath meemPath = it.next();

			if (Space.HYPERSPACE.equals(meemPath.getSpace())) {

				String location = meemPath.getLocation();

				int i = location.lastIndexOf("/");
				String categoryName = location.substring(0, i);
				String entryName = location.substring(i + 1);

				Category category = CategoryUtility.spi.get().getCategory(hyperspaceMeem, MeemPath.spi.create(Space.HYPERSPACE, categoryName));
				if (category == null) {
					LogTools.error(logger, "No category facet on meem: " + categoryName);
				}
				else {
					category.addEntry(entryName, meem);
					if (debugLevel > 0) {
						StringBuffer message = new StringBuffer(80);
						message.append("hyperspace: ");
						message.append(deploymentDescriptor.getId());
						message.append(" -> ");
						message.append(categoryName);
						LogTools.info(logger, message.toString());
					}
				}
			}
		}
		updateMaxLifeCycleState(meem);
	}

	private void updateMaxLifeCycleState(Meem meem) {
		MeemClientCallback callback = new LifeCycleLimitMeemClientCallback(meem);
		meemClientConduit.provideReference(meem, "lifeCycleLimit", LifeCycleLimit.class, callback);
	}

	private void updateLifeCycleState(Meem meem) {
		MeemClientCallback callback = new LifeCycleMeemClientCallback(meem);
		meemClientConduit.provideReference(meem, "lifeCycle", LifeCycle.class, callback);
	}

	private void notifyClients(Meem meem) {
		MeemDefinition meemDefinition = definitions.get(meem.getMeemPath());
		subsystemClientConduit.meemCreated(meem, meemDefinition);
	}

	/* ------------------------------------------------------------------------ */

	private final class CommissionControlConduit implements SubsystemCommissionControl {

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionControl#changeCommissionState(org.openmaji.system.manager.lifecycle.subsystem.CommissionState)
		 */
		public void changeCommissionState(CommissionState commissionState) {
			commissionStateConduit.commissionStateChanged(commissionState);
		}
	}

	/* ------------------------------------------------------------------------ */

	private final class MeemControlConduit implements SubsystemMeemControl {

		public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription) {
			String identifier = meemDefinition.getMeemAttribute().getIdentifier();
			if (identifier == null || identifier.length() == 0) {
				LogTools.error(logger, "createMeem() - identifier not set in MeemDefinition");
				return;
			}
			if (!(meemDescription instanceof MeemDeploymentDescriptor)) {
				LogTools.error(logger, "createMeem() - meemDescription not MeemDeploymentDescriptor");
				return;
			}

			descriptions.put(identifier, (MeemDeploymentDescriptor) meemDescription);

			if (debugLevel > 0) {
				LogTools.info(logger, "creating meem: " + identifier);
			}
			lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.LOADED);
		}
	}

	/* ------------------------------------------------------------------------ */

	private final class LifeCycleManagerClientConduit implements LifeCycleManagerClient {

		public void meemCreated(Meem meem, String identifier) {
			MeemDeploymentDescriptor meemDescription = descriptions.remove(identifier);

			if (debugLevel > 0) {
				StringBuffer message = new StringBuffer(20);
				message.append("created: ");
				message.append(identifier);
				LogTools.info(logger, message.toString());
			}

			if (meemDescription != null) {
				MeemDefinition meemDefinition = MeemUtility.spi.get().getMeemDefinition(meem);
				meemDefinition.getMeemAttribute().setIdentifier(identifier);
				definitions.put(meem.getMeemPath(), meemDefinition);
				// configure meem 
				configureMeem(meem, meemDescription);
			}

		}

		public void meemDestroyed(Meem arg0) {
			//			LogTools.error(logger, "meemDestroyed() - TODO: finish this ?");
		}

		public void meemTransferred(Meem arg0, LifeCycleManager arg1) {
			// don't care
		}
	}

	/* ------------------------------------------------------------------------ */

	private final class ConfigurationHandlerMeemClientCallback implements MeemClientCallback, ContentClient {

		private Meem meem;

		private MeemDeploymentDescriptor descriptor;

		ConfigurationHandlerMeemClientCallback(Meem meem, MeemDeploymentDescriptor descriptor) {
			this.meem = meem;
			this.descriptor = descriptor;
		}

		public void referenceProvided(Reference reference) {
			ConfigurationHandler target = (ConfigurationHandler) reference.getTarget();

			Collection<ConfigurationParameter> parameters = descriptor.getConfigProperties();
			for (ConfigurationParameter parameter : parameters) {
				if (debugLevel > 0) {
					StringBuffer message = new StringBuffer(80);
					message.append("config: [");
					message.append(parameter.getConfigurationIdentifier());
					message.append("] := ");
					message.append(parameter.getValue());
					LogTools.info(logger, message.toString());
				}

				target.valueChanged(parameter.getConfigurationIdentifier(), parameter.getValue());
			}

			setupDependencies(meem, descriptor);
			addToHyperspace(meem, descriptor);
		}

		public void contentSent() {
			// OK
		}

		public void contentFailed(String reason) {
			LogTools.error(logger, "Error receiving the ConfigurationHandler facet of a managed meem: " + reason);
		}
	}

	/* ---------------------------------------------------------------------- */

	private final class LifeCycleLimitMeemClientCallback implements MeemClientCallback, ContentClient {

		private Meem meem;

		public LifeCycleLimitMeemClientCallback(Meem meem) {
			this.meem = meem;
		}

		public void referenceProvided(Reference reference) {
			LifeCycleLimit target = (LifeCycleLimit) reference.getTarget();

			target.limitLifeCycleState(LifeCycleState.READY);

			updateLifeCycleState(meem);
		}

		public void contentSent() {
			// OK
		}

		public void contentFailed(String reason) {
			LogTools.error(logger, "Error receiving the LifeCycleLimit facet of managed meem: " + reason);
		}
	}

	/* ------------------------------------------------------------------------ */

	private final class LifeCycleMeemClientCallback implements MeemClientCallback, ContentClient {

		private Meem meem;

		public LifeCycleMeemClientCallback(Meem meem) {
			this.meem = meem;
		}

		public void referenceProvided(Reference reference) {
			LifeCycle target = (LifeCycle) reference.getTarget();

			target.changeLifeCycleState(LifeCycleState.READY);

			notifyClients(meem);
		}

		public void contentSent() {
			// OK
		}

		public void contentFailed(String reason) {
			LogTools.error(logger, "Error receiving the LifeCycle facet of managed meem: " + reason);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}
}