/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import org.openmaji.implementation.server.meem.ReferenceFilter;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;



/**
 * @author Warren Bloomer
 *
 */
public final class SingleReverseDependency implements FacetConnectable, MeemClient
{
	private final MeemCore meemCore;
	private final Meem targetMeem;
	private final String sourceFacetIdentifier;
	private final String targetFacetIdentifier;
	private final boolean dependencyContentRequired;
	private final Filter dependencyFilter;
	private final Connectable connectable;
	private final boolean isSystem;
	private final LifeTime lifeTime;

	private boolean referenceConnected = false;
	private Reference dependencyReference = null;
	private Reference disconnectedDependencyReference = null;
	private Reference meemClientReference = null;
	private boolean connected = false;

	public SingleReverseDependency(
			DependencyHandlerWedge dependencyHandlerWedge,
			Meem meem, 
			DependencyDescriptor dependencyDescriptor,
			boolean isSystem,
			Connectable connectable)
	{
		DependencyAttribute dependencyAttribute = dependencyDescriptor.getDependencyAttribute();
		
		this.meemCore = dependencyHandlerWedge.meemCore;
		this.targetMeem = meem;
		
		this.sourceFacetIdentifier = dependencyDescriptor.getFacetIdentifier();
		this.targetFacetIdentifier = dependencyAttribute.getFacetIdentifier();
		this.dependencyContentRequired = dependencyAttribute.isInitialContentRequired();
		this.dependencyFilter = dependencyAttribute.getFilter();
		this.lifeTime = dependencyDescriptor.getLifeTime();
		
		this.isSystem = isSystem;
		this.connectable = connectable;
	}

	public synchronized void connect()
	{
		if (!connected) {
//			FacetFilter facetFilter = new FacetFilter(targetFacetIdentifier, null, Direction.OUTBOUND);

			dependencyReference = Reference.spi.create(
					targetFacetIdentifier,
					meemCore.getTarget(sourceFacetIdentifier), 
					dependencyContentRequired, 
					dependencyFilter
				);
	
			ReferenceFilter referenceFilter = new ReferenceFilter(dependencyReference);

			// TODO[peter] Ideally we would just use a snapshot here
			meemClientReference = Reference.spi.create(
					"meemClientFacet",
					meemCore.getLimitedTargetFor(this, MeemClient.class), 
					false, 
					referenceFilter
				);
	
			targetMeem.addOutboundReference(meemClientReference, false);
			targetMeem.addOutboundReference(dependencyReference, false);
			
			connected = true;
		}
	}

	public synchronized void disconnect()
	{
		if (connected) {
			if (targetMeem == null || dependencyReference == null) {
				// we have already disconnected
				return;
			}
			targetMeem.removeOutboundReference(dependencyReference);
	
			disconnectedDependencyReference = dependencyReference;
			dependencyReference = null;
	
			if (referenceConnected)
			{
				referenceConnected = false;
				connectable.disconnect();
			}
			cleanup();
			connected = false;
		}
	}

	public Meem getMeem() {
		return targetMeem;
	}
	
	public boolean isSystemFacet() {
		return isSystem;
	}

	public String getLocalFacetId() {
		return sourceFacetIdentifier;
	}
	
	public LifeTime getLifeTime() {
		return lifeTime;
	}


	public synchronized void referenceAdded(Reference reference)
	{
		if (!referenceConnected && reference != null && reference.equals(dependencyReference))
		{				
			referenceConnected = true;
			connectable.connect();
		}
	}

	public synchronized void referenceRemoved(Reference reference)
	{
		if (reference != null && reference.equals(disconnectedDependencyReference) && meemClientReference != null) {
			((MeemCoreImpl) meemCore).revokeTargetProxy(meemClientReference.getTarget(), this);
			meemClientReference = null;
			disconnectedDependencyReference = null;
		}
		
		if (reference != null && reference.equals(dependencyReference))
		{
			disconnect();
		}
	}

	private void cleanup()
	{
		if (meemClientReference != null && targetMeem != null) {
			targetMeem.removeOutboundReference(meemClientReference);
		}
	}
}
