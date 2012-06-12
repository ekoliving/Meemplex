/*
 * Created on 19/08/2004
 *
 */
package org.openmaji.implementation.server.http;

import java.net.BindException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;

import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.*;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.utility.TestHarness;

import org.meemplex.meem.Wedge;
import org.mortbay.jetty.*;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.*;
import org.mortbay.jetty.security.*;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.MultiException;

/**
 * @author Warren Bloomer
 *
 */
@Wedge(name="EmbeddedJettyWedge")
public class EmbeddedJettyWedge implements org.openmaji.meem.Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemContext meemContext;


	/* ------------------------- conduits ---------------------------- */
	
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient  configurationClientConduit = new ConfigurationClientAdapter(this);

	/**
	 * The conduit through which this Wedge will signal whether or not is able to go READY
	 */
	public Vote              lifeCycleControlConduit;

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	/**
	 * Conduit on which to receive Servlets
	 */
	public ServletConsumer servletConsumerConduit = new ServletConsumer() {
		public void servlet(String name, String path, String classname, java.util.Properties properties) {
			ServletSpec spec = new ServletSpec(name, path, classname, properties);
			addServlet(spec);
		};
	};
	
	/**
	 * for adding resource handler
	 */
	public MapConsumer resourceMapConduit = new MapConsumer() {
		public void add(String contextPath, String resourcePath) {
			resourceSpecs.put(contextPath, resourcePath);
			try {
				if (server != null) {
					addResourceHandler(contextPath, resourcePath);
				}
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Problem while adding resource to HTTP server", e);
			}
		};
		
		public void remove(String contextPath) {
			resourceSpecs.remove(contextPath);
		};
	};

	/**
	 * For receiving webapp spec (context path and path to war file)
	 */
	public MapConsumer webappMapConduit = new MapConsumer() {
		public void add(String contextPath, String resourcePath) {
			webappSpecs.put(contextPath, resourcePath);
			if (server != null) {
				try {
					addWebApp(contextPath, resourcePath);
				}
				catch (Exception e) {
					logger.log(Level.INFO, "Problem while adding web app to HTTP server", e);
				}
			}
		};
		
		public void remove(String contextPath) {
			webappSpecs.remove(contextPath);
		};
	};


	/* ------------------------ persisted properties -------------------- */

	public String servletContextString = "/maji";
	public String meemkitContextString = "/meemkits";
	
	// configurable properties
	public int     port             = 8000;
	public boolean useSSL           = false;
	public boolean needClientAuth   = false;		// whether to use client authentication
	public String  sslKeystore      = "";
	public String  sslPassword      = "";
	public String  sslKeyPassword   = "";
	
	public long    sessionTimeout   = 120;  // in seconds


	/* -------------------- configuration specifications -------------------- */
	
	public transient ConfigurationSpecification portSpecification           = new ConfigurationSpecification("The port for this web server to listener on", Integer.class);
	public transient ConfigurationSpecification useSSLSpecification         = new ConfigurationSpecification("Whether the server should use SSL");
	public transient ConfigurationSpecification needClientAuthSpecification = new ConfigurationSpecification("Whether to require client authentication");
	public transient ConfigurationSpecification sslKeystoreSpecification    = new ConfigurationSpecification("Path to the keystore (for SSL)");
	public transient ConfigurationSpecification sslPasswordSpecification    = new ConfigurationSpecification("Password for the keystore (for SSL)");
	public transient ConfigurationSpecification sslKeyPasswordSpecification = new ConfigurationSpecification("Password for the key (for SSL)");


	/*--------------------------- private properties ------------------------- */
	
	private Server server;
	
	private HandlerCollection handlerCollection;

	private Context servletContext;
	//private Context fileContext;
	//private Context webAppContext;

	private HashSet<ServletSpec> servletSpecs = new HashSet<ServletSpec>();
	
	private HashMap<String, String> resourceSpecs = new HashMap<String, String>();

	private HashMap<String, String> webappSpecs = new HashMap<String, String>();

	/* ---------------- getters and setters for configuration ---------------- */
	
	/**
	 * @param port
	 */
	public void setPort (Integer port) {
		this.port = port.intValue();
	}
	
	public void setUseSSL(String useSSL) {
		this.useSSL = new Boolean(useSSL).booleanValue();
	}
	
	public void setNeedClientAuth(String clientAuth) {
		this.needClientAuth = new Boolean(clientAuth).booleanValue();		
	}
	
	public void setSslKeystore(String path) {
		sslKeystore    = path;
	}
	
	public void setSslPassword(String password) {
		sslPassword    = password;
	}
	
	public void setSslKeyPassword(String password) {
		sslKeyPassword    = password;
	}


	/* ------------ Meem Functionality ---------------------- */
	
	/**
	 * 
	 */
	public void validate() throws WedgeValidationException {
		logger.log(Level.FINE, "validate() - invoked");
		
		if (useSSL) {
			if (sslKeystore == null) {
				throw new WedgeValidationException("unable to find property for key store name.");
			}
	
			if (sslPassword == null) {
				throw new WedgeValidationException("unable to find property for key store password.");
			} 
		}
	}

	/**
	 * TODO catch exceptions and send "vote"
	 *
	 */
	public void commence() {
		logger.log(Level.FINE, "commence() - invoked");
		startServer();
	}
	
	/**
	 * 
	 */
	public void conclude() {
		logger.log(Level.FINE, "conclude() - invoked");
		stopServer();
	}

	/**
	 * 
	 *
	 */
	protected void startServer() {
		
		if (server == null) {
			// stop excessive logging
			org.mortbay.log.Log.setLog(null);
			System.setProperty("DEBUG", "false");
			System.setProperty("VERBOSE", "false");
			 
			server = new Server();
		}

		Connector connector = null;
		if (useSSL) {
			// Setup JSSE keystore and set parameters here correctly
			connector = new SslSocketConnector();
			((SslSocketConnector)connector).setKeystore(sslKeystore);
			((SslSocketConnector)connector).setPassword(sslPassword);
			((SslSocketConnector)connector).setKeyPassword(sslKeyPassword);
			((SslSocketConnector)connector).setNeedClientAuth(needClientAuth);
			// uses an entry in the keystore called "jetty"
		}
		else { 
			connector = new SocketConnector();
		}
		connector.setPort(port);
		server.addConnector(connector);
		
		// set the Server's HandlerCollection. Other handlers will be added to the HandlerCollection
		handlerCollection = new ContextHandlerCollection();
		server.setHandler(handlerCollection);
		

		// create servlet context
		servletContext = new Context(handlerCollection, servletContextString, Context.SESSIONS);

		// create web app context
		//webAppContext = new Context(handlerCollection, webAppContextString, Context.SESSIONS);
		
		try {

			// add ResourceHandlers
			addResourceHandlers();

			// add servlets to the servlet context
			//servletContext.addHandler(new SecurityHandler());
			addServletsToContext(servletContext);

			//
			addWebApps();

			// add default handler to the server
			handlerCollection.addHandler(new DefaultHandler());

			//start a Jetty
			server.start();
		}
		catch (BindException ex) {
		  logger.log(Level.INFO, "Could not start web server on port " + port + ": " + ex.getMessage(), ex);
		  lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
		  return;
		}
		catch (InstantiationException ex) {
			logger.log(Level.INFO, "Could not add servlet: ", ex);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		catch (IllegalAccessException ex) {
			logger.log(Level.INFO, "Could not add servlet: ", ex);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		catch (ClassNotFoundException ex) {
			logger.log(Level.INFO, "Could not add servlet: ", ex);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		catch (MultiException ex) {
			logger.log(Level.INFO, "Problem while starting the web server: ", ex);				
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		catch (Exception ex) {
			logger.log(Level.INFO, "Problem while starting the web server: ", ex);				
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}

		lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), true);

	}
	
	/**
	 * 
	 *
	 */
	protected void stopServer() {
		if (server != null) {
			try {
				server.stop();
				server = null;
			}
			catch (Exception ex) {
				logger.log(Level.INFO, "A problem occurred when stopping the server", ex);				
			}
		}
		handlerCollection = null;
		servletContext = null;
		
		servletSpecs.clear();
		resourceSpecs.clear();
	}

	private void addServlet(ServletSpec spec) {
		synchronized (servletSpecs) {
			servletSpecs.add(spec);
			try {
				// TODO make sure context has appropriate settings
				//servletContext.getSessionHandler().getSessionManager().getSessionCookieConfig().setName(name)

				addServletToContext(servletContext, spec);
			}
			catch (Exception ex) {
				logger.log(Level.INFO, "Could not add servlet", ex);
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
				return;
			}
		}
	}
	
	private void addServletsToContext(Context context) 
		throws InstantiationException, IllegalAccessException, ClassNotFoundException, Exception
	{
		synchronized (servletSpecs) {
			Iterator<ServletSpec> servletsIter = servletSpecs.iterator();
			while (servletsIter.hasNext()) {
				ServletSpec spec = servletsIter.next();
				addServletToContext(context, spec);
			}
		}
	}

	private void addServletToContext(Context context, ServletSpec spec) 
		throws InstantiationException, IllegalAccessException, ClassNotFoundException, Exception
	{
		if (context == null) {
			logger.log(Level.INFO, "Can not add servlet to null context: " + spec.getName());
			return;
		}
		
//		boolean restartContext = context.isRunning();
//		if (restartContext) {
//			context.stop();
//		}
		
		logger.log(Level.INFO, "Adding servlet to context: " + spec.getName() + ". context: " + context.getContextPath() + " spec: " + spec.getPath());

		ServletHolder servletHolder = new ServletHolder( ObjectUtility.getClass(Servlet.class, spec.getClassname()) );
		context.addServlet(servletHolder, spec.getPath());

		Properties properties = spec.getProperties();
		servletHolder.setInitParameters(properties);

//		if (restartContext) {
//			context.start();
//		}
	}
	
	private void addResourceHandlers() throws Exception {
		// TODO the following should be in another wedge
		// set a static resource base
		//String meemkitDir = System.getProperty(MEEMKIT_MANAGER_DIR);
		//addResourceHandler(meemkitContextString, meemkitDir + "/installed");

		// add ResourceHandlers for other contexts
		Iterator<String> keyIter = resourceSpecs.keySet().iterator();
		while (keyIter.hasNext()) {
			String contextPath = keyIter.next();
			String filePath = (String) resourceSpecs.get(contextPath);
			addResourceHandler(contextPath, filePath);
		}
	}

	private void addResourceHandler(String contextPath, String filePath) throws Exception {
		if (handlerCollection != null) {
			logger.log(Level.INFO, "Adding resource : " + contextPath + "=>" + filePath);
			
			ResourceHandler resourceHandler = new ResourceHandler();
			resourceHandler.setResourceBase(filePath);
			
			logger.log(Level.INFO, "serving: " + resourceHandler.getBaseResource());
			
			ContextHandler contextHandler = new ContextHandler(contextPath);
			contextHandler.addHandler(resourceHandler);
			
			handlerCollection.addHandler(contextHandler);
			
			try {
				resourceHandler.start();
				contextHandler.start();
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Could not start resource context", e);
			}
		}
	}

	/**
	 * Add web apps.
	 */
	private void addWebApps() throws Exception {
		Iterator<String> keyIter = webappSpecs.keySet().iterator();
		while (keyIter.hasNext()) {
			String contextPath = keyIter.next();
			String warPath = (String) webappSpecs.get(contextPath);
			addWebApp(contextPath, warPath);
		}
	}
	
	private void addWebApp(String path, String warPath) throws Exception {
		if (handlerCollection != null) {
//			boolean extractWar = warPath.endsWith(".war");
//			boolean parentLoaderPriority = true;
			
			// add it to the server
//			boolean restartHandler = handlerCollection.isRunning();
//			if (restartHandler) {
//				handlerCollection.stop();
//			}
			
			logger.log(Level.INFO, "Adding web app: " + warPath + " to path " + path);
			
			// create a webapp
			WebAppContext wah = new WebAppContext(handlerCollection, warPath, path);

			// configure it
//			wah.setExtractWAR(extractWar);
//			wah.setParentLoaderPriority(parentLoaderPriority);
			
			wah.setClassLoader(ObjectUtility.class.getClassLoader());	// set MeemServer classloader
			
			handlerCollection.addHandler(wah);
			wah.start();

//			if (restartHandler) {
//				handlerCollection.start();
//			}
		}
	}

	private static class ServletSpec { 
		private final String name;
		private final String path;
		private final String classname;
		private final Properties properties;
		
		public ServletSpec(String name, String path, String classname, Properties properties) {
			this.name = name;
			this.path = path;
			this.classname = classname;

			if (properties == null) {
				properties = new Properties();
			}			
			this.properties = properties;
		}
		
		public String getName() {
			return name;
		}
		
		public String getPath() {
			return path;
		}
		
		public String getClassname() {
			return classname;
		}
		
		public Properties getProperties() {
			return properties;
		}
	}
	
	/**
	 * To test the Wedge outside of Maji.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EmbeddedJettyWedge embeddedJettyWedge = new EmbeddedJettyWedge();
			embeddedJettyWedge.meemContext = TestHarness.getMeemContext();
			embeddedJettyWedge.lifeCycleControlConduit = TestHarness.getVote();			
			embeddedJettyWedge.startServer();
		}
		catch (Exception ex) {
			logger.log(Level.INFO, "Problem while running test", ex);
		}
	}
}