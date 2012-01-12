/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * @author Warren Bloomer
 *
 */
public final class SingleForwardDependency implements FacetConnectable
{
	private static final Logger logger = LogFactory.getLogger();
	
	private final boolean DEBUG = false;
	
	private final DependencyHandlerWedge dependencyHandlerWedge;
	private final Meem targetMeem;
	private final String sourceFacetIdentifier;
	private final String targetFacetIdentifier;
	private final boolean isSystem;
	private final Class specification;
	private final Filter dependencyFilter;
	private final Connectable connectable;
	private final boolean initialContentRequired;
	private final LifeTime lifeTime;
	
	private MeemClientCallbackImpl callback = null;
	private Reference dependencyReference = null;
	private boolean connected = false;
	
	public SingleForwardDependency(
			DependencyHandlerWedge dependencyHandlerWedge, 
			Meem meem, 
			DependencyDescriptor dependencyDescriptor,
			boolean isSystem,
			Class specification, 
			Connectable connectable
		)
	{
		DependencyAttribute dependencyAttribute = dependencyDescriptor.getDependencyAttribute();
		
		this.dependencyHandlerWedge = dependencyHandlerWedge;
		this.targetMeem = meem;
		
		this.sourceFacetIdentifier = dependencyDescriptor.getFacetIdentifier();
		this.targetFacetIdentifier = dependencyAttribute.getFacetIdentifier();
		this.dependencyFilter = dependencyAttribute.getFilter();
		this.initialContentRequired = dependencyAttribute.isInitialContentRequired();
		this.lifeTime = dependencyDescriptor.getLifeTime();
		
		this.isSystem = isSystem;
		this.specification = specification;
		this.connectable = connectable;
	}

	public synchronized void connect() {
		if (DEBUG) {
			LogTools.info(logger, "connect(): " + targetMeem + " - " + targetFacetIdentifier);
		}
		
		if (!connected) {
			callback = new MeemClientCallbackImpl();
	
			dependencyHandlerWedge.meemClientConduit.provideReference(
					targetMeem, targetFacetIdentifier, specification, callback
				);
			connected = true;			
		}
	}

	public synchronized void disconnect()
	{
		if (DEBUG) {
			LogTools.info(logger, "disconnect(): " + targetMeem + " - " + targetFacetIdentifier);
		}
		
		if (connected) {
			if (dependencyReference != null) {
				dependencyHandlerWedge.meemConduit.removeOutboundReference(dependencyReference);
				dependencyReference = null;
	
				connectable.disconnect();
			}
			else if (callback != null) {
				callback.cancel();
				callback = null;
			}
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

	private class MeemClientCallbackImpl implements MeemClientCallback
	{
		private boolean cancelled = false;

		public void referenceProvided(Reference reference)
		{
			if (DEBUG) {
				LogTools.info(logger, "referenceProvided(): " + reference);
			}
			
			if (!cancelled) {
				cancelled = true;
				callback = null;

				if (reference == null) {
					// TODO[peter] How to handle this error?
				}
				else {
					dependencyReference = Reference.spi.create(
							sourceFacetIdentifier,
							reference.getTarget(), 
							reference.isContentRequired() && initialContentRequired, 
							dependencyFilter
						);

					if (DEBUG) {
						LogTools.info(logger, "referenceProvided(): adding reference: " + dependencyReference);
					}
					
					dependencyHandlerWedge.meemConduit.addOutboundReference(dependencyReference, false);

					connectable.connect();
				}
			}
		}
		
		public void cancel() {
		 this.cancelled = true;
		}
	}
}
