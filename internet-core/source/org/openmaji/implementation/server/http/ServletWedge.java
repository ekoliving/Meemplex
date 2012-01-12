package org.openmaji.implementation.server.http;

import java.util.Properties;

import org.meemplex.meem.Conduit;
import org.meemplex.meem.ConfigProperty;
import org.meemplex.meem.Wedge;
import org.openmaji.implementation.server.http.ServletConsumer;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * A wedge that provides a Servlet.
 * 
 * Listening on the servletConsumer Conduit will be a Web Server that will embed the servlet.
 * 
 * @author stormboy
 *
 */
@Wedge(name="ServletWedge")
public class ServletWedge implements org.openmaji.meem.Wedge {
	
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
	@Conduit(name="servletConsumer")
	public ServletConsumer servletConsumerConduit;
	
	
	/* ------------------ persisted properties -------------------- */
	
	/**
	 * The name of the servlet
	 */
	@ConfigProperty(name="name", description="servlet name")
	public String servletName = "";

	/**
	 * The class for the servlet
	 */
	@ConfigProperty(name="class", description="servlet class")
	public String servletClass = "";
	
	/**
	 * the path to the servlet when requesting a URL of the server. 
	 * this is appended to the context path
	 */
	@ConfigProperty(name="path", description="the path of the servlet")
	public String servletPath = "/myservlet/*";

	/** 
	 * comma separated list of params.
	 * i.e. name=value, name=value
	 */
	@ConfigProperty(name="params", description="servlet parameters")
	public String params = ""; 
	
	/**
	 * Debug the RPC Service
	 */
	@ConfigProperty(name="debug", description="whether to log the activity of this wedge")
	public boolean debug   = false;  
	

	/* ----------------- configuration specifications ------------------------- */

	public transient ConfigurationSpecification servletClassSpecification = new ConfigurationSpecification("servlet class");

	public transient ConfigurationSpecification servletNameSpecification = new ConfigurationSpecification("servlet name");

	public transient ConfigurationSpecification servletPathSpecification = new ConfigurationSpecification("servlet path");
	
	public transient ConfigurationSpecification paramsSpecification = new ConfigurationSpecification("servlet parameters");
	
	public transient ConfigurationSpecification debugSpecification = new ConfigurationSpecification("servlet path", Boolean.TYPE, LifeCycleState.LOADED);

	
	/* ---------------- getters and setters for configuration ----------------- */
	
	public void setParams(String params) {
		// TODO check params are valid
		String[] pa = params.split(",");
		
		// TODO set params
		
		this.params = params;
	}
	
	public String getParams() {
		return params;
	}

	public void setServletName(String servletName) {
	    this.servletName = servletName;
    }

	public String getServletName() {
	    return servletName;
    }

	public void setServletClass(String servletClass) {
	    this.servletClass = servletClass;
    }

	public String getServletClass() {
	    return servletClass;
    }

	public void setServletPath(String servletPath) {
	    this.servletPath = servletPath;
    }

	public String getServletPath() {
	    return servletPath;
    }

	public void setDebug(Boolean debug) {
	    this.debug = debug;
    }

	public Boolean isDebug() {
	    return debug;
    }


	/* ----------------- Lifecycle methods ------------------ */
	
	/**
	 * Send servlet details to the servletConsumerConduit
	 */
	public void commence() {
		Properties properties = new Properties();

		// set properties
		if (params != null) {
			String[] pa = params.split(",");
			for (String param : pa) {
				String[] entry = param.split("=");
				if (entry != null && entry.length > 1) {
					properties.setProperty(entry[0].trim(), entry[1].trim());
				}
			}
		}

		// send servlet details down servletConsumerConduit
		servletConsumerConduit.servlet(getServletName(), getServletPath(), getServletClass(), properties);
	}
	
	/**
	 * 
	 */
	public void conclude() {
	}

}
