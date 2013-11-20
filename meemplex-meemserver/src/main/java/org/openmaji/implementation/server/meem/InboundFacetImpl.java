/*
 * Created on Mar 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.openmaji.implementation.server.meem;

import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetInboundAttribute;

/**
 * @author Chris Kakris
 */
public class InboundFacetImpl<T extends Facet> extends FacetImpl<T> {
	/**
	 * Dynamic Proxy Object that provides a reference to the facet implementation
	 */
	private T proxy = null;

	private MeemCoreImpl meemCoreImpl;

	public InboundFacetImpl(MeemCoreImpl meemCoreImpl, WedgeImpl wedgeImpl, FacetInboundAttribute facetInboundAttribute) throws ClassNotFoundException {
		super(wedgeImpl, (Class<T>) ObjectUtility.getClass(Facet.class, facetInboundAttribute.getInterfaceName()));

		this.meemCoreImpl = meemCoreImpl;
		this.facetInboundAttribute = facetInboundAttribute;
	}

	/**
	 * Provides the FacetAttribute.
	 * 
	 * @return Attribute for this Facet implementation
	 */
	public FacetAttribute getFacetAttribute() {
		return facetInboundAttribute;
	}

	/**
	 * Provides the Facet Direction.
	 * 
	 * @return Direction for this Facet instance
	 */
	public Direction getDirection() {
		return Direction.INBOUND;
	}

	/**
	 * Indicates whether an in-bound Facet requires Content upon resolving a Dependency. This is only applicable for in-bound Facets.
	 * 
	 * @return True if content is required
	 */
	public boolean isContentRequired() {
		return facetInboundAttribute.isContentRequired();
	}

	/**
	 * Return the proxy for inbound facets.
	 * 
	 * @return Object
	 */
	public T makeProxy() {
		if (proxy == null) {
			T implementation = (T) this.getWedgeImpl().getImplementation();

			// proxy = meemCoreImpl.getTargetFor(implementation, getSpecification());
			proxy = meemCoreImpl.proxyTargetFor(implementation, getSpecification());
		}

		return proxy;
	}

	private final FacetInboundAttribute facetInboundAttribute;
}
