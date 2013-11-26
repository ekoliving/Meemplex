package org.openmaji.implementation.server.http;

import org.meemplex.meem.ConfigProperty;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;

public class FileResourceWedge implements Wedge {
	
	private static final String MEEMKIT_MANAGER_DIR = "org.openmaji.meemkit.manager.dir";


	/* --------------------- conduits -------------------- */

	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient  configurationClientConduit = new ConfigurationClientAdapter(this);

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/**
	 * Conduit on which to send Servlet specifications.
	 */
	public MapConsumer resourceMapConduit;
	
	
	/* ------------------ persisted properties -------------------- */
	
	/**
	 *
	 */
	@ConfigProperty(description="The context path")
	public String contextPath = "/meemkits";

	/**
	 * 
	 */
	@ConfigProperty(description="The path of the resources on the filesystem")
	public String resourcePath = System.getProperty(MEEMKIT_MANAGER_DIR) + "/installed";

	
	/* ----------------- configuration specifications ------------------------- */

	public transient ConfigurationSpecification<String> contextPathSpecification = ConfigurationSpecification.create("The context path");

	public transient ConfigurationSpecification<String> resourcePathSpecification = ConfigurationSpecification.create("The path of the resources on the filesystem");

	
	/* ---------------- getters and setters for configuration ----------------- */
	
	public void setContextPath(String path) {
		this.contextPath = path;
	}
	
	public String getContextPath() {
		return contextPath;
	}
	
	public void setResourcePath(String path) {
		this.resourcePath = path;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	

	/* ----------------- Lifecycle methods ------------------ */
	
	/**
	 * 
	 */
	public void commence() {
		resourceMapConduit.add(contextPath, resourcePath);
	}
	
	/**
	 * 
	 */
	public void conclude() {
	}
}
