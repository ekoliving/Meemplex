/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import java.util.HashSet;

import org.openmaji.implementation.server.meem.ReferenceFilter;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;



/**
 * Monitors whether the Meem of the Facet is in appropriate life-cycle state.
 * 
 * @author Warren Bloomer
 */
public class MeemLifeCycleStateMonitor implements Connectable, LifeCycleClient, MeemClient
{
	private final MeemCore meemCore;
	private final Meem targetMeem;

	private Reference lifeCycleClientReference = null;
	private Reference meemClientReference = null; 
	private boolean connected = false;

	/** the LC state of the Meem */
	private LifeCycleState lifeCycleState = LifeCycleState.ABSENT;

	HashSet connectables = new HashSet();

	public MeemLifeCycleStateMonitor(MeemCore meemCore, Meem meem) {
		this.meemCore = meemCore;
		this.targetMeem = meem;
	}
	
	public void addConnectable(FacetConnectable connectable) {
		
		synchronized (connectables) {
			connectables.add(connectable);
		}
		doUpdate(connectable);
	}

	public void removeConnectable(FacetConnectable connectable) {
		synchronized (connectables) {
			connectables.remove(connectable);
		}
		connectable.disconnect();
	}
	
	public int connectables() {
		synchronized (connectables) {
			return connectables.size();
		}
	}
	
	
	/* --------------------- Connectable interface --------------------- */
	
	public void connect() {
		this.lifeCycleClientReference = Reference.spi.create("lifeCycleClient", meemCore.getLimitedTargetFor(this, LifeCycleClient.class), true, null);

		ReferenceFilter referenceFilter = new ReferenceFilter(lifeCycleClientReference);
		this.meemClientReference = Reference.spi.create("meemClientFacet", meemCore.getLimitedTargetFor(this, MeemClient.class), false, referenceFilter);

		targetMeem.addOutboundReference(lifeCycleClientReference, false);
		targetMeem.addOutboundReference(meemClientReference, false);
		
		connected = true;
	}

	public void disconnect() {
		targetMeem.removeOutboundReference(lifeCycleClientReference);
		if (connected) {
			connected = false;
			doDisconnect();
		}
	}

	
	/* --------------- LifeCycleClient interface ----------------- */
	
	public void lifeCycleStateChanged(LifeCycleTransition transition) {
		this.lifeCycleState = transition.getCurrentState();

		doUpdate();
	}		

	public void lifeCycleStateChanging(LifeCycleTransition transition) {}

	
	/* -------------------- MeemClient interface -------------------- */
	
	public void referenceAdded(Reference reference) {
	}

	public void referenceRemoved(Reference reference) {
		if (reference.equals(lifeCycleClientReference)) {
			((MeemCoreImpl) meemCore).revokeTargetProxy(lifeCycleClientReference.getTarget(), this);
			targetMeem.removeOutboundReference(meemClientReference);
		}
	}

	
	/* --------------------- private methods ----------------------- */
	
	private void doDisconnect() {
		Object[] arr; 
		synchronized (connectables) {
			arr = connectables.toArray();
		}
		for (int i=0; i<arr.length; i++) {
			((FacetConnectable)arr[i]).disconnect();
		}
	}
	
	/*
	private void doConnect() {
		Object[] arr;
		synchronized (connectables) {
			arr = connectables.toArray();
		}
		for (int i=0; i<arr.length; i++) {
			((FacetConnectable)arr[i]).connect();
		}
	}
	*/
	
	private void doUpdate() {
		Object[] arr;
		synchronized (connectables) {
			arr = connectables.toArray();
		}
		for (int i=0; i<arr.length; i++) {
			doUpdate((FacetConnectable)arr[i]);
		}
	}

	private void doUpdate(FacetConnectable connectable) {
		boolean shouldBeConnected = 
			lifeCycleState.equals(LifeCycleState.READY) || 
			( connectable.isSystemFacet() && ( lifeCycleState.equals(LifeCycleState.LOADED) || lifeCycleState.equals(LifeCycleState.PENDING) ) );

			if (shouldBeConnected) {
				connectable.connect();
			}
			else {
				connectable.disconnect();
			}
	}
}
