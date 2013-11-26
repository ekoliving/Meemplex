/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.diagnostic;

import org.openmaji.diagnostic.Debug;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.definition.WedgeDefinitionUtility;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

public class DebugWedge implements Debug, Wedge, WedgeDefinitionProvider {
	public Debug debugOutput;
	public final ContentProvider<Debug> debugOutputProvider = new MyContentProvider();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	public Debug debugConduit;

	public int debugLevel = 1; // If you add this wedge to your meem it's assumed you want debugging
	public transient ConfigurationSpecification<Integer> debugLevelSpecification = ConfigurationSpecification.create("Debug mode 0=off", Integer.class, LifeCycleState.READY);

	public void setDebugLevel(Integer value) {
		debugLevel = value.intValue();
		debugConduit.debugLevelChanged(debugLevel);
		debugOutput.debugLevelChanged(debugLevel);
	}

	public void debugLevelChanged(int level) {
		debugConduit.debugLevelChanged(level);
		debugOutput.debugLevelChanged(debugLevel);
	}

	public void commence() {
		debugConduit.debugLevelChanged(debugLevel);
	}

	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition wedgeDefinition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "debug", "debugIput");
		return wedgeDefinition;
	}

	/* ------------------------------------------------------------------------ */

	private class LifeCycleClientHandler implements LifeCycleClient {
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.LOADED_PENDING)) {
				commence();
			}
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}
	}

	private class MyContentProvider implements ContentProvider<Debug> {
		public synchronized void sendContent(Debug debugTarget, Filter filter) {
			debugTarget.debugLevelChanged(debugLevel);
		}
	}
}
