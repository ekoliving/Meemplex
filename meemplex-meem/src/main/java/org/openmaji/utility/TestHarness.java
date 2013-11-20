/*
 * @(#)MeemUtility.java
 *
 * Copyright 2008 (C) by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.utility;

import org.openmaji.meem.*;
import org.openmaji.meem.wedge.lifecycle.*;

/**
 * <p>
 * Utility class for testing Meems outside of a MeemServer.
 * </p>
 * 
 * <pre>
 * public static void main(String[] args) {
 * 
 * 	try {
 * 		testWedge testWedge = new TestWedge();
 * 		testWedge.meemContext = MeemContextUtility.getMeemContext();
 * 		testWedge.lifeCycleControlConduit = MeemContextUtility.getVote();
 * 
 * 		testWedge.commence();
 * 		testWedge.valueChanged();
 * 	} catch (Exception exception) {
 * 		logger.log(Level.WARNING, &quot;TestWedge error: &quot;, exception);
 * 	}
 * }
 * </pre>
 */

public class TestHarness {

	public static MeemContext getMeemContext() {
		MeemContext meemContext = new MeemContext() {
			public Object getImmutableAttribute(Object key)
					throws IllegalArgumentException {

				return (null);
			}

			public <T extends Facet> T getLimitedTargetFor(T facet,
					java.lang.Class<T> specification) {
				return null;
			};

			public <T extends Facet> T getNonBlockingTargetFor(T facet,
					Class<T> specification) {

				return (null);
			}

			public Meem getSelf() {
				return (null);
			}

			public Facet getTarget(String facetIdentifier) {

				return (null);
			}

			public <T extends Facet> T getTargetFor(T facet, Class<T> specification) {

				return (null);
			}

			public String getWedgeIdentifier() {
				return (null);
			}
		};

		return (meemContext);
	}

	public static Vote getVote() {
		Vote vote = new Vote() {
			public void vote(String voterIdentification, boolean goodToGo) {
			}

			public void vote(String voterIdentification,
					LifeCycleTransition lifeCycleTransition, boolean goodToGo) {
			}
		};

		return (vote);
	}
}