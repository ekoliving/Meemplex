/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider providing support for some simple Binary Filters, e.g only
 *   output "false to true" value changes transitions or vice-versa.
 *
 * - Consider generating an error, if the sendContent() target is not a Binary.
 *   Another approach is that the framework should pre-verify the target type.
 *
 * - Consider creating an optimized version, that only informs the Binary
 *   Meem clients, if the value really changed.  Is it possible to do this
 *   as a Filter (which would be much more flexible) ?
 */

package org.openmaji.implementation.common.group;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meemplex.meem.Conduit;
import org.meemplex.meem.Facet;
import org.meemplex.service.model.Direction;
import org.openmaji.common.Binary;
import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.implementation.server.meem.invocation.InvocationContext;
import org.openmaji.implementation.server.meem.invocation.InvocationContextTracker;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * Interfaces to a group of meems.
 * 
 * keeps track of those meems that are
 * 
 * Binary inbound facet is for receiving binary events from multiple meems.
 * 
 * @author stormboy
 *
 */
public class BinaryLinearGroupWedge implements Linear, Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	@org.meemplex.meem.MeemContext
	public MeemContext meemContext;
	
	public MeemCore meemCore;
	
	/* ------------------ facets -------------------------- */
	
	/**
	 * Binary out-bound Facet for sending control binary value to multiple meems.
	 */
	public Linear linearOut;


	@Facet(name="depdendencyCount", direction=Direction.OUT)
	public Linear depdendencyCount;
	
	@Facet(name="readyCount", direction=Direction.OUT)
	public Linear readyCount;
	
	@Facet(name="onCount", direction=Direction.OUT)
	public Linear onCount;
	public final ContentProvider onCountProvider = new ContentProvider() {
		public synchronized void sendContent(Object target, Filter filter) {
			((Linear) target).valueChanged(new IntegerPosition(on.size()));
		}
	};
	
	@Facet(name="offCount", direction=Direction.OUT)
	public Linear offCount;
	public final ContentProvider offCountProvider = new ContentProvider() {
		public synchronized void sendContent(Object target, Filter filter) {
			((Linear) target).valueChanged(new IntegerPosition(off.size()));
		}
	};
	
	/* ---------------- conduits ----------------------- */
	
	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to change the value of the binary thing.  
	 */
	@Conduit(name="binaryControl")
	public Binary binaryControlConduit = new BinaryControlConduit();

	/**
	 * The conduit through which state changes are received other Wedges in the Meem. 
	 */
//	@Conduit(name="binaryState")
//	public Binary binaryStateConduit;
	
	/**
	 * LifeCycleClient conduit.
	 */
	@Conduit(name="lifeCycleClient")
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	@Conduit(name="dependencyClient")
	public DependencyClient dependencyClientConduit = new DependencyClientImpl();
	
	/* ----------------------------- */
	
	private boolean debug = false;
	
	/**
	 * Desired Boolean value
	 */
	private Integer value = null;

	/**
	 * Number of depdencendies added
	 */
	private int numAdded = 0;
	
	/**
	 * Number connected
	 */
	private int numConnected = 0;


	/**
	 * 
	 */
	private Map<DependencyAttribute, Boolean> dependenciesConnected = new HashMap<DependencyAttribute, Boolean>();

	/**
	 * The meems that have sent on
	 */
	private Set<MeemPath> on = new HashSet<MeemPath>();
	
	/**
	 * The meems that have sent off
	 */
	private Set<MeemPath> off = new HashSet<MeemPath>();

	/* ---------- Binary Facet method(s) --------------------------------------- */

	/**
	 * Respond to a value change by simply passing the change on to any Wedges
	 * that act as a binaryControlConduit target.
	 *
	 * @param value Changed boolean value
	 */

	public void valueChanged(Position value) {

		InvocationContext invocationContext = InvocationContextTracker.getInvocationContext();
		MeemPath callingMeemPath = (MeemPath) invocationContext.get(InvocationContext.CALLING_MEEM_PATH);
		
		if (debug) {
			logger.log(Level.INFO, "valueChanged() - invoked on group binary in-bound facet from " + callingMeemPath);
		}

		if (value.intValue() != 0) {
			off.remove(callingMeemPath);
			on.add(callingMeemPath);
		}
		else {
			on.remove(callingMeemPath);
			off.add(callingMeemPath);
		}
		
		onCount.valueChanged(new IntegerPosition(on.size()));
		offCount.valueChanged(new IntegerPosition(off.size()));
		
//		binaryStateConduit.valueChanged(value);
	}

	/* ---------- BinaryStateConduit ------------------------------------------- */

	/**
	 * This class handles incoming Binary state messages from other
	 * Wedges in the Meem.
	 */

	private final class BinaryControlConduit implements Binary {

		/**
		 * Respond to a value change by simply passing the change on to any Meems
		 * depending upon the out-bound Binary Facet.
		 *
		 * @param newValue Changed boolean value
		 */

		public void valueChanged(boolean newValue) {

			if (debug) {
				logger.log(Level.INFO, "valueChanged() - invoked on BinaryControlConduit");
			}
			BinaryLinearGroupWedge.this.value = newValue ? 100 : 0;
			
			// send value to outbound facet
			linearOut.valueChanged(new IntegerPosition(value, 10, 0, 100));
		}
	}

	/* ---------- LifeCycle methds -------------------------------------------- */
	
	/**
	 * Called when the meem LifeCycle state goes from 'loaded' to 'pending'
	 */
	protected void commence() {
		dependenciesConnected.clear();
		numAdded = 0;
		numConnected = 0;
		off.clear();
		on.clear();
	}

	/**
	 * Called when the meem LifeCycle state goes from 'pending' to 'loaded'
	 */
	protected void conclude() {
		this.value = null;
	}

	/**
	 * 
	 */
	private class DependencyClientImpl implements DependencyClient {
		
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
			
			if (facetId.equals(BinaryLinearGroupMeem.FACETID_GROUP_INPUT) && !dependenciesConnected.containsKey(dependencyAttribute)) {
//				if (debug) {
//					logger.log(Level.INFO, "dependencyAdded: " + facetId + " <-> " + dependencyAttribute);
//				}
				dependenciesConnected.put(dependencyAttribute, false);
				numAdded++;
				depdendencyCount.valueChanged(new IntegerPosition(numAdded));
			}
		};
		
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
			if (dependenciesConnected.containsKey(dependencyAttribute)) {
//				if (debug) {
//					logger.log(Level.INFO, "dependencyRemoved: " + dependencyAttribute);
//				}
				dependenciesConnected.remove(dependencyAttribute);
				numAdded--;
				depdendencyCount.valueChanged(new IntegerPosition(numAdded));
			}
		};
		
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (dependenciesConnected.containsKey(dependencyAttribute)) {
//				if (debug) {
//					logger.log(Level.INFO, "dependencyConnected: " + dependencyAttribute);
//				}
				dependenciesConnected.put(dependencyAttribute, true);
				numConnected++;
				readyCount.valueChanged(new IntegerPosition(numConnected));
			}
		}
		
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
			if (dependenciesConnected.containsKey(dependencyAttribute)) {
//				if (debug) {
//					logger.log(Level.INFO, "dependencyConnected: " + dependencyAttribute);
//				}
				dependenciesConnected.put(dependencyAttribute, false);
				numConnected--;
				readyCount.valueChanged(new IntegerPosition(numConnected));
			}
		};
		
		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		};
		
	}
}
