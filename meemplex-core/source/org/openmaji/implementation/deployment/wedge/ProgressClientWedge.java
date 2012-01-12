/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import org.openmaji.diagnostic.Debug;
import org.openmaji.implementation.deployment.Progress;
import org.openmaji.implementation.deployment.ProgressClient;
import org.openmaji.implementation.deployment.ProgressConduit;
import org.openmaji.implementation.deployment.SimpleProgress;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

public class ProgressClientWedge implements Wedge {
	private static final Logger logger = LogFactory.getLogger();

	public ProgressClient progressClient;

	public final ContentProvider progressClientProvider = new MyContentProvider();

	public ProgressConduit progressConduit = new MyProgressConduit();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

	public Debug debugConduit = new MyDebugConduit();

	private volatile int completion;

	private volatile int current;

	private int debugLevel;

	private void sendOverallProgress(ProgressClient progressClient) {
		Progress overallProgress = new SimpleProgress(current, completion);
		progressClient.updateProgress(overallProgress);
	}

	private void resetAllFields() {
		completion = 0;
		current = 0;
	}

	/* ---------------------------------------------------------------------- */

	private class MyProgressConduit implements ProgressConduit {
		public void reset() {
			resetAllFields();
		}

		public void addCompletionPoints(int points) {
			completion = completion + points;
			if (debugLevel > 0) {
				LogTools.info(logger, "completion point is " + completion);
			}
			sendOverallProgress(progressClient);
		}

		public void addProgressPoints(int points) {
			current = current + points;
			if (debugLevel > 0) {
				LogTools.info(logger, "progress=[" + current + "/" + completion + "]");
			}
			sendOverallProgress(progressClient);
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MyContentProvider implements ContentProvider {
		public void sendContent(Object target, Filter filter) throws ContentException {
			ProgressClient client = (ProgressClient) target;
			sendOverallProgress(client);
		}
	}

	/* ---------------------------------------------------------------------- */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}

	/* ---------------------------------------------------------------------- */

	private class LifeCycleClientHandler implements LifeCycleClient {
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.PENDING_READY)) {
				resetAllFields();
			}
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}
	}
}
