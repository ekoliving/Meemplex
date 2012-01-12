package org.openmaji.implementation.server.http;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;

public class WebAppWedge implements Wedge {
	
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
	public MapConsumer webappMapConduit;
	
	
	/* ------------------ persisted properties -------------------- */

	/**
	 *
	 */
	public String contextPath = "/eko-gui";

	/**
	 * 
	 */
	public String resourcePath = System.getProperty(MEEMKIT_MANAGER_DIR) + "/installed/eko-gui/jars/lib/openlaszlo.war";

	
	/* ----------------- configuration specifications ------------------------- */

	public transient ConfigurationSpecification contextPathSpecification = new ConfigurationSpecification("The context path");

	public transient ConfigurationSpecification resourcePathSpecification = new ConfigurationSpecification("The path of the resources on the filesystem");

	
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
		webappMapConduit.add(contextPath, resourcePath);
	}
	
	/**
	 * 
	 */
	public void conclude() {
	}
}
