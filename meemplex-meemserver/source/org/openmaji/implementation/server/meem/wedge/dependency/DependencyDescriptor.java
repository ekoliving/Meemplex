/*
 * Created on 20/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.openmaji.implementation.server.meem.invocation.MeemInvocationSource;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationTarget;
import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;



/**
 * @author Warren Bloomer
 *
 */
public class DependencyDescriptor  implements Serializable {
	private static final long serialVersionUID = 959492302390L;
	
	private final String facetIdentifier;
	private final DependencyAttribute dependencyAttribute;
	private final LifeTime lifeTime;
	
	private int hash = 0;
	private boolean calculatedHash = false;
	
	public DependencyDescriptor(
			String facetIdentifier,	
			DependencyAttribute dependencyAttribute,
			LifeTime lifeTime) 
	{
		if (facetIdentifier == null) {
			throw new RuntimeException("A null facetIdentifier was provided for dependency descriptor");				
		}
		if (dependencyAttribute == null) {
			throw new RuntimeException("A null dependencyAttribute was provided for dependency descriptor");				
		}
		if (lifeTime == null) {
			throw new RuntimeException("A null lifeTime was provided for dependency descriptor");				
		}
		
		this.facetIdentifier = facetIdentifier;
		this.dependencyAttribute = dependencyAttribute;
		this.lifeTime = lifeTime;
	}
	
	public DependencyDescriptor(
			Facet facet,	
			DependencyAttribute dependencyAttribute,
			LifeTime lifeTime) 
	{
		if (facet == null) {
			throw new RuntimeException("A null facet was provided for dependency descriptor");				
		}
		if (dependencyAttribute == null) {
			throw new RuntimeException("A null dependencyAttribute was provided for dependency descriptor");				
		}
		if (lifeTime == null) {
			throw new RuntimeException("A null lifeTime was provided for dependency descriptor");				
		}

		if (Proxy.isProxyClass(facet.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler(facet);

			if (ih == null) {
				throw new RuntimeException("Could not determine facet identifier from facet");					
			}
			else if (ih instanceof MeemInvocationTarget) {
				this.facetIdentifier = ((MeemInvocationTarget) ih).getFacetIdentifier();
			}
			else if (ih instanceof MeemInvocationSource) {
				this.facetIdentifier = ((MeemInvocationSource) ih).getFacetAttribute().getIdentifier();
			}
			else {
				throw new RuntimeException("Could not determine facet identifier from facet");
			}
		}
		else {
			throw new RuntimeException("Could not determine facet identifier from facet. Facet is not a proxy.");
		}
		
		this.dependencyAttribute = dependencyAttribute;
		this.lifeTime = lifeTime;
	}
	
	public String getFacetIdentifier() {
		return facetIdentifier;
	}
	
	public DependencyAttribute getDependencyAttribute() {
		return dependencyAttribute;
	}
	
	public LifeTime getLifeTime() {
		return lifeTime;
	}
	
	public int hashCode() {
		if (!calculatedHash) {
			hash = facetIdentifier.hashCode() ^ dependencyAttribute.hashCode() ^ lifeTime.hashCode();
			calculatedHash = true;
		}
		return hash;
	}

	public boolean equals(Object other) {
		if (!(other instanceof DependencyDescriptor)) {
			return false;
		}
		DependencyDescriptor descriptor = (DependencyDescriptor) other;			
		return 
			facetIdentifier.equals(descriptor.facetIdentifier) &&
			dependencyAttribute.equals(descriptor.dependencyAttribute) &&
			lifeTime.equals(descriptor.lifeTime);
	}
}
