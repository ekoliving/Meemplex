/*
 * @(#)MeemSystemWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.meem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryClient;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationSource;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.filter.*;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.MeemPath
 */

public class MeemSystemWedge implements Meem, Wedge, FilterChecker {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	/**
	 * Internal reference to MeemCore
	 */
	public MeemCore meemCore;

	/* ------------------------- outbound facets -------------------------- */
	
    /**
     * FacetClient (out-bound Facet)
     */
    public FacetClient facetClientFacet;
    
    public final ContentProvider<FacetClient> facetClientFacetProvider = new ContentProvider<FacetClient>() {
        public void sendContent(FacetClient client, Filter filter) {
            List<FacetItem> facetItems = new ArrayList<FacetItem>();
            
			FacetFilter facetFilter = filter instanceof FacetFilter ? (FacetFilter) filter : null;

			for (WedgeImpl wedgeImpl : ((MeemCoreImpl)meemCore).getWedgeImpls()) {
				
                for (FacetImpl<?> facetImpl : wedgeImpl.getFacets()) {
					String identifier = facetImpl.getIdentifier();
					Class<? extends Facet> specification = facetImpl.getSpecification();
					Direction direction = facetImpl.getDirection();

					if (facetFilter != null && !facetFilter.match(identifier, specification, direction)) {
							continue;
					}

					//facetClient.hasA(identifier, specification, direction);
					FacetItem item = new FacetItem(identifier, specification.getName(), direction);
					facetItems.add(item);
                }
            }
			client.facetsAdded(facetItems.toArray(new FacetItem[]{}));
        }
    };

    /**
     * MeemClient (out-bound Facet)
    */
	public MeemClient meemClientFacet;
	
	// TODO provide a separate outbound facet for providing invocation target references for the meem versus references from this meem to others
	public final ContentProvider<MeemClient> meemClientFacetProvider = new ContentProvider<MeemClient>() {
		public void sendContent(MeemClient client, Filter filter) throws ContentException {
			if (filter == null || !(filter instanceof FacetDescriptor)) {
				throw new ContentException("Filter not supported: " + filter);
			}

			FacetDescriptor facetDescriptor = (FacetDescriptor) filter;

			InboundFacetImpl<?> inboundFacetImpl = ((MeemCoreImpl) meemCore).getInboundFacetImpl(facetDescriptor.facetIdentifier);

			if (inboundFacetImpl != null && facetDescriptor.specification.isAssignableFrom(inboundFacetImpl.getSpecification()))
			{
				Reference<?> reference = Reference.spi.create(
						inboundFacetImpl.getIdentifier(),
						meemCore.getTarget(inboundFacetImpl.getIdentifier()),
						inboundFacetImpl.isContentRequired()
					);

				client.referenceAdded(reference);
			}
		}
	};


	/* ------------------------- conduits -------------------------- */

	public Meem meemConduit = this;

	public MeemClient meemReferenceClientConduit;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.READY_LOADED)
				|| transition.equals(LifeCycleTransition.PENDING_LOADED)) {

				// TODO[peter] Separate handling for application facets
				((MeemCoreImpl) meemCore).revokeFacetProxies();
			}
			else if (transition.equals(LifeCycleTransition.LOADED_DORMANT)) {

				((MeemCoreImpl) meemCore).revokeAllProxies();

				// TODO[peter] Schedule cleanup of outbound proxies
			}
			else if (transition.equals(LifeCycleTransition.DORMANT_LOADED)) {

				// set the meemIdentifier to be the same as the one in MeemAttribute
				if (meemIdentifier.equals("Meem")) {
					meemIdentifier = meemCore.getMeemStructure().getMeemAttribute().getIdentifier();
				}
			}
		}
	};

	public DependencyHandler dependencyHandlerConduit;


	/* ------------------------ persisted properties ------------------------- */
	
	/** persisted property */
	public String meemIdentifier = "Meem";

	// Configuration
	public transient ConfigurationSpecification<String> meemIdentifierSpecification	= ConfigurationSpecification.create(
		"The identifier for this Meem", String.class, LifeCycleState.READY);

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public void setMeemIdentifier(String meemIdentifier) {
		this.meemIdentifier = meemIdentifier;
	}


	public MeemPath getMeemPath() {
		return meemCore.getMeemPath();
	}


	/* ---------- Methods that only work when the Meem is bound ---------------- */

	public void addDependency(Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		dependencyHandlerConduit.addDependency(facet, dependencyAttribute, lifeTime);
	}
	
	public void addDependency(String facetIdentifier, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		dependencyHandlerConduit.addDependency(facetIdentifier, dependencyAttribute, lifeTime);
	}
	
	public void removeDependency(DependencyAttribute dependencyAttribute) {
		dependencyHandlerConduit.removeDependency(dependencyAttribute);
	}
	
	public void updateDependency(DependencyAttribute dependencyAttribute) {
		dependencyHandlerConduit.updateDependency(dependencyAttribute);
	}
	
	@Override
	public <T extends Facet> void addOutboundReference(final Reference<T> reference, final boolean automaticRemove) {
		final Facet target = reference.getTarget();

		//if (Common.TRACE_ENABLED) {
//			if ("meemRegistry".equals(reference.getFacetIdentifier())) {
//				logger.log(Level.INFO, "--- adding outbound reference on " + getMeemPath() + " to " + reference + "\n\t" + reference.getFacetIdentifier());
//			}
		//}

		ContentClient contentClient = getContentClientFromTarget(target);

		try {
			final OutboundFacetImpl<T> outboundFacetImpl = getOutboundFacetImpl(reference);
			final Class<?> specification = outboundFacetImpl.getSpecification();

			if (!specification.isInstance(target))
			{
				throw new ContentException("Reference does not match type of facet: " + outboundFacetImpl.getIdentifier());
			}

			final MeemInvocationSource<T> meemInvocationSource = outboundFacetImpl.getMeemInvocationSource();

			if (reference.isContentRequired())
			{
				if (meemInvocationSource.asyncContentProvider != null)
				{

					if (!automaticRemove) {
						final ContentClient completionContentClient = contentClient;
						contentClient = new ContentClient() {
							public synchronized void contentSent() {
								if (!done) {
									done = true;
									completionContentClient.contentSent();
									meemClientFacet.referenceAdded(reference);
									meemInvocationSource.addReference(reference);
									meemReferenceClientConduit.referenceAdded(reference);
								}
							}

							public synchronized void contentFailed(String reason) {
								if (!done) {
									done = true;
									completionContentClient.contentFailed(reason);
								}
							}

							private boolean done = false;
						};
					}
					
//					logger.log(Level.INFO, "--- asyncContentProvider... " + meemInvocationSource.getFacetAttribute().getIdentifier() + " : " + reference.getTarget().getClass().getInterfaces()[0]);
//					meemInvocationSource.asyncContentProvider.asyncSendContent(
//							(T)LoggerProxyFactory.createProxy(meemInvocationSource.getFacetAttribute().getIdentifier(), reference.getTarget().getClass()),
//							reference.getFilter(),
//							contentClient);

					meemInvocationSource.asyncContentProvider.asyncSendContent(
						reference.getTarget(), reference.getFilter(), contentClient);

					return;
				}

				if (meemInvocationSource.contentProvider != null)
				{
//					logger.log(Level.INFO, "--- contentProvider... " + meemInvocationSource.getFacetAttribute().getIdentifier() + " : " + reference.getTarget().getClass().getInterfaces()[0]);
//					meemInvocationSource.contentProvider.sendContent(
//							(T)LoggerProxyFactory.createProxy(meemInvocationSource.getFacetAttribute().getIdentifier(), reference.getTarget().getClass()),
//							reference.getFilter());

					meemInvocationSource.contentProvider.sendContent(
						reference.getTarget(), reference.getFilter());
				}
			}

			contentClient.contentSent();

			if (!automaticRemove)
			{
				meemClientFacet.referenceAdded(reference);

				meemInvocationSource.addReference(reference);

				meemReferenceClientConduit.referenceAdded(reference);
			}
		}
		catch (ContentException e) {
			
			logger.log(Level.INFO, "ContentException on " + meemCore.getMeemPath() + " : " + e.getMessage());
			contentClient.contentFailed(e.getMessage());
		}
	}

	public <T extends Facet> void removeOutboundReference(Reference<T> reference){

    if ( reference == null )
    {
      logger.log(Level.WARNING, "removeOutboundReference() invoked with a null reference");
      return;
    }

    try {
			MeemInvocationSource<T> meemInvocationSource = getOutboundFacetImpl(reference).getMeemInvocationSource();

			boolean removed = meemInvocationSource.removeReference(reference);

			if (removed) {
				meemClientFacet.referenceRemoved(reference);
				meemReferenceClientConduit.referenceRemoved(reference);
			}
		}
		catch (ContentException e) {
			logger.log(Level.INFO, "ContentException: " + e.getMessage());
		}
	}

	private <T extends Facet> OutboundFacetImpl<T> getOutboundFacetImpl(Reference<T> reference) throws ContentException {
		
		String facetIdentifier = reference.getFacetIdentifier();

		OutboundFacetImpl<?> outboundFacetImpl = ((MeemCoreImpl) meemCore).getOutboundFacetImpl(facetIdentifier);

		if (outboundFacetImpl == null) {
			String errorMessage = "Reference(" + reference + ") specifies a facet identifier(" + facetIdentifier + ") that doesn't exist";
			throw new ContentException(errorMessage);
		}
		
		if ( outboundFacetImpl.getSpecification().isAssignableFrom(reference.getTarget().getClass()) == false ) {
			String errorMessage = "Reference(" + reference + ") has a Facet type that does not match the type of facet (" + facetIdentifier + ")";
			throw new ContentException(errorMessage);
		}

		return (OutboundFacetImpl<T>) outboundFacetImpl;
	}

	@Override
	public boolean invokeMethodCheck(Filter filter, String facetName, String methodName, Object[] args)
		throws IllegalFilterException {

		if ((filter instanceof FacetFilter)) {
			if (methodName.equals("referenceAdded") || methodName.equals("referenceRemoved")) {
				FacetFilter facetFilter = (FacetFilter) filter;
				Reference<?> reference = (Reference<?>) args[0];
	
				return facetFilter.match(reference.getFacetIdentifier(), reference.getTarget().getClass());
			}
		}
		else if (filter instanceof ReferenceFilter) {
			if (methodName.equals("referenceAdded") || methodName.equals("referenceRemoved")) {
				ReferenceFilter referenceFilter = (ReferenceFilter) filter;
				Reference<?> reference = (Reference<?>) args[0];
				
				return referenceFilter.matches(reference);
			}			
		}
		else {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		return false;
	}


	/* ---------- Object methods ----------------------------------------------- */

	  /**
	   * Compares Meem to the specified object.
	   * The result is true, if and only if the MeemPaths are equals.
	   *
	   * @return true, if Meems are equal
	   */

	  public boolean equals(
		Object object) {

		if (object == this) return(true);

		if ((object instanceof Meem) == false) return(false);

		Meem thatMeem = (Meem) object;

		return(meemCore.getMeemPath().equals(thatMeem.getMeemPath()));
	  }

	  /**
	   * Provides the Object hashCode.
	   * Must follow the Object.hashCode() and Object.equals() contract.
	   *
	   * @return Meem hashCode
	   */

	  public int hashCode() {
		return(meemCore.getMeemPath() == null  ?  0  :  meemCore.getMeemPath().hashCode());
	  }

	  /**
	   * Provides a String representation of MeemBase.
	   *
	   * @return String representation of MeemBase
	   */

	  public synchronized String toString() {
		return(
		  getClass().getName()  + "[" +
		  "meemPath=" + meemCore.getMeemPath()   +
		  ", bound]"
		);
	  }

	  


	public static ContentClient getContentClientFromTarget(Facet target) {
		return (target instanceof ContentClient)
			?	(ContentClient) target
			:	(target instanceof JiniMeemRegistryClient)
			?	new JiniContentClientProxy((JiniMeemRegistryClient) target)
			:	NULL_CONTENT_CLIENT;
	}

	private static final ContentClient NULL_CONTENT_CLIENT = new ContentClient() {
		public void contentSent() {}
		public void contentFailed(String reason) {}
	};




}
