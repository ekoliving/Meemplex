package org.openmaji.implementation.rpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openmaji.implementation.server.http.ServletConsumer;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;


public class MajiRpcServletWedge implements Wedge {
	

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
	public ServletConsumer servletConsumerConduit;
	
	
	/* ------------------ persisted properties -------------------- */
	
	/** the name of the servlet */
	public String servletName = "XmlRpcServlet";

	/**
	 * the path to the servlet when requesting a URL of the server. 
	 * this is appended to the context path
	 */
	public String servletPath = "/rpc/*";
	
	/** session timeout in seconds */
	public long    sessionTimeout   = 120;  

	
	/* ----------------- configuration specifications ------------------------- */

	public transient ConfigurationSpecification sessionTimeoutSpecification = ConfigurationSpecification.create("Session timout in seconds");

	
	/* ---------------- getters and setters for configuration ----------------- */
	
	public void setSessionTimeout(String seconds) {
		sessionTimeout = Long.parseLong(seconds);
	}
	
	public String getSessionTimeout() {
		return Long.toString(sessionTimeout);
	}
	

	/* ----------------- Lifecycle methods ------------------ */
	
	/**
	 * 
	 */
	public void commence() {
		String classname = MajiRpcServlet.class.getName();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(MajiRpcServlet.PARAM_SESSION_TIMEOUT, Long.toString(sessionTimeout*1000));

		servletConsumerConduit.servlet(servletName, servletPath, classname, properties);
	}
	
	/**
	 * 
	 */
	public void conclude() {
	}
}
