/*
 * Created on 29/06/2005
 */
package org.openmaji.implementation.deployment;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;

/**
 * @author Warren Bloomer
 *
 */
public class DependencyDescriptor {
	
	/** name of the facet on the dependent Meem */
	private String              dependentFacetName;
	
	/** 
	 * The attribute describing the dependency details on
	 * the remote Meem.
	 */
	private DependencyAttribute dependencyAttribute;
	
	/**
	 * The lifetime of the dependency.
	 */
	private LifeTime            lifeTime;
	
	/**
	 * Create a new DependencyDescriptor
	 * 
	 * @param localFacetId The facet id of the meem that the dependency is to be added to
	 * @param dependencyAttribute the dependency artribute
	 * @param lifeTime the lifetime of the dependency.
	 */
	public DependencyDescriptor(
			String localFacetId, 
			DependencyAttribute dependencyAttribute, 
			LifeTime lifeTime) 
	{
		this.dependentFacetName = localFacetId;
		this.dependencyAttribute = dependencyAttribute;
		this.lifeTime = lifeTime;
	}
	
	public DependencyAttribute getDependencyAttribute() {
		return dependencyAttribute;
	}
	
	public String getFacetName() {
		return dependentFacetName;
	}
	
	public LifeTime getLifetime() {
		return lifeTime;
	}
}
