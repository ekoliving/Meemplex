/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.util.PositionHelper;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * <p>
 * The LoopbackLinearWedge Wedge is a simple example of a pluggable Linear thing.
 * </p>
 * <p>
 * LoopbackLinearWedge is a linearControlConduit target that listens for
 * Linear method invocations and immediately passes on those method invocations
 * as a linearStateConduit source.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-10-07)
 * </p>
 * @author  Andy Gelme
 * @author  Christos Kakris
 * @version 1.0
 * @see org.openmaji.meem.Wedge
 * @see org.openmaji.common.Linear
 */

public class LoopbackLinearWedge implements Wedge {
	public Linear linearControlConduit = new LinearControlConduit();

	public Linear linearStateConduit = null;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public transient ConfigurationSpecification positionSpecification = 
		ConfigurationSpecification.create(
				"Position specified as 'value increment min max'",
				String.class, 
				LifeCycleState.READY
			);

	private Position cachedPosition;

	/* ---------- configuration getters/setters ------------------------------- */

	public void setPosition(String positionString) throws ConfigurationRejectedException {
		cachedPosition = PositionHelper.ParseFloatPosition(positionString);
		linearStateConduit.valueChanged(cachedPosition);
	}

	public String getPosition() {
		return cachedPosition == null ? "" : cachedPosition.toParseableString();
	}

	/* ---------- LinearControlConduit ---------------------------------------- */

	class LinearControlConduit implements Linear {
		public synchronized void valueChanged(Position newPosition) {
			linearStateConduit.valueChanged(newPosition);
		}
	}
}
