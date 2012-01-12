package org.openmaji.implementation.util.wedge;

import java.util.logging.Logger;

import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.Category;

/**
 * This Wedge represents a device meem that has a name and a lease period.
 * It updates it's Meems entry in a parentCategory based on the friendly name of the device.
 * It keeps track of the time passed since the device has last been heard from.  If it exceeds
 * the lease time, this wedge destroys its meem.
 * 
 * @author stormboy
 *
 */
public class DynamicDeviceWedge implements Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean DEBUG = false;
	
	public MeemContext meemContext;
	
	/* ----------------- outbound Facets ------------------- */

	public Category parentCategory;
	public final ContentProvider parentCategoryProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws ContentException {
			// send addEntry
			if (deviceName != null) {
				((Category)target).addEntry(deviceName, meemContext.getSelf());
			}
		};
	};
	
	/* --------------- conduits ------------------ */
	
	/** for lifecycle control */
	public LifeCycle lifeCycleConduit;

	/** for responding to lifecycle state changes */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientConduit();

	/**
	 * Conduit to send a name to.
	 */
	public Variable nameConduit = new Variable() {
		public void valueChanged(Value value) {
			setName(""+value);
		};
	};

	/**
	 * Conduit to allow this wedge to be configured. 
	 */
    public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
    

	/* --------------------- persisted properties ------------------ */
	
	/** the friendly name of the device */
	public String deviceName;
	
	/** lease time in milliseconds */
	public long leaseTime = 10 * 60000;

	/** the last time the device was heard from */
	public long timeLastHeard = Long.MAX_VALUE;
	
	/* --------------------- private members ----------------------- */

	/** an object that checks wether lease has expired */
	private LeaseChecker leaseChecker;

	
	/* ----------------- configuration specification ----------------- */

	public transient ConfigurationSpecification deviceNameSpecification  = 
		new ConfigurationSpecification("The friendly name for the device", String.class, LifeCycleState.LOADED);

	public transient ConfigurationSpecification leaseTimeSpecification  = 
		new ConfigurationSpecification("The lease time in minutes", String.class, LifeCycleState.LOADED);

	/**
	 * 
	 * @param timeString
	 */
	public void setLeaseTime(String timeString) {
		long time = Long.parseLong(timeString);
		this.leaseTime = time * 60000;
	}
	
	public String getLeaseTime() {
		return Long.toString(this.leaseTime / 60000);
	}
	

	/* ------------- lifecycle methods ----------------- */
	
	protected void commence() {
		if (DEBUG) {
			logger.info("commencing...");
		}
		// check lease time
		if (leaseChecker == null) {
			leaseChecker = new LeaseChecker();
		}
		//leaseChecker.start();
	}
	
	protected void conclude() {
		if (DEBUG) {
			logger.info("concluding...");
		}
		//leaseChecker.stop();
	}


	/* --------------- private methods ---------------- */
	
	private void setName(String name) {
		if (DEBUG) {
			logger.info("setting name: parentCategory=" + parentCategory + ", deviceName=" + deviceName);
		}
		if (deviceName != null) {
			parentCategory.removeEntry(deviceName);
		}
		this.deviceName = name;
		if (deviceName != null) {
			parentCategory.addEntry(deviceName, meemContext.getSelf());
		}
	}
	
	private void checkLease() {
		long timeSpan = System.currentTimeMillis() - timeLastHeard;
		if (timeSpan > leaseTime) {
			if (deviceName != null) {	
				parentCategory.removeEntry(deviceName);
			}
			lifeCycleConduit.changeLifeCycleState(LifeCycleState.ABSENT);
		}
	}
	
	private void touch() {
		timeLastHeard = System.currentTimeMillis();
	}
	
	private class LifeCycleClientConduit implements LifeCycleClient {
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			
			if ( LifeCycleTransition.LOADED_PENDING.equals(transition) ) {
				commence();
			}
			else if ( LifeCycleTransition.PENDING_LOADED.equals(transition) ) {
				conclude();
			}
			else if ( LifeCycleTransition.PENDING_READY.equals(transition) ) {
				touch();
			}
			else if ( LifeCycleTransition.READY_PENDING.equals(transition) ) {
				touch();
			}
			else if ( LifeCycleTransition.READY_LOADED.equals(transition) ) {
				touch();
			}
		}
		
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}
	}
	
	private class LeaseChecker implements Runnable {
		private boolean running = false;
		private long interval = 60000;
		
		public void start() {
			if (!running) {
				running = true;
				ThreadManager.spi.create().queue(this);
			}
		}
		
		public void stop() {
			running = false;
		}
		
		public void run() {
			if (running) {
				checkLease();
				ThreadManager.spi.create().queue(this, interval);
			}			
		}
	}
	
}
