/*
 * Created on 29/06/2005
 */
package org.openmaji.implementation.deployment;

import java.util.Collection;
import java.util.Vector;

import org.jdom.Element;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;

/**
 * @author Warren Bloomer
 *
 */
public class MeemDeploymentDescriptor implements MeemDescription, Descriptor {

	private static final long serialVersionUID = -1120106707921571684L;
	
	/**
	 * An identifier for this Meem, unique to the
	 * application.
	 */
	final private String id;

	/**
	 * The MeemDefinition for the Meem.
	 */
	final private MeemDefinition meemDefinition;

	/**
	 * A collection of configuration properties for the Meem.
	 * 
	 * The collection is of ConfigurationParameters.
	 */
	final private Collection<ConfigurationParameter> configProperties = new Vector<ConfigurationParameter>();

	/**
	 * Some dependenciew may be on MeemPaths that are relative to the
	 * deployed application.  These will have ${application} in them.
	 * 
	 * The collection is of DependencyDescriptions.
	 */
	final private Collection<DependencyDescriptor> dependencies = new Vector<DependencyDescriptor>();
	
	/** 
	 * A collection of hyperspace paths to add the Meem to. 
	 * This will be unique to this Meem.  May be relative 
	 * to the deployed application, in which case ${application} 
	 * will be at the start of the path.
	 */
	final private Collection<MeemPath> hypserspacePaths = new Vector<MeemPath>();

	/**
	 * Constructor.
	 * 
	 * @param id An identifier unique to a deployment
	 * @param meemDefinition definition for the structure of the Meem
	 * @param configProperties A Collection of ConfigurationParameters.
	 * @param dependencies Collection of DependencyDescriptors.
	 * @param hyperspacePaths A Collection of MeemPaths - hyperspace paths.
	 */
	public MeemDeploymentDescriptor(
			String id,
			MeemDefinition meemDefinition,
			Collection<ConfigurationParameter> configProperties,
			Collection<DependencyDescriptor> dependencies,
			Collection<MeemPath> hyperspacePaths
		) 
	{
		this.id = id;
		this.meemDefinition = meemDefinition;
		this.configProperties.addAll(configProperties);
		this.dependencies.addAll(dependencies);
		this.hypserspacePaths.addAll(hyperspacePaths);
	}
	
	public String getId() {
		return id;
	}
	
	public MeemDefinition getMeemDefinition() {
		return meemDefinition;
	}
	
	public Collection<ConfigurationParameter> getConfigProperties() {
		return configProperties;
	}
	
	public Collection<DependencyDescriptor> getDependencies() {
		return dependencies;
	}
	
	public Collection<MeemPath> getHyperSpacePaths() {
		return hypserspacePaths;
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.MeemDescription#getDescription()
	 */
	public String getDescription() {
		return "Deployment Meem Description";
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.MeemDescription#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		// do nothing
	}
	
	public void processElement(Element element) {
		// TODO Auto-generated method stub
	}
}
