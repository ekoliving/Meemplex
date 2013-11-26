/*
 * Created on 19/08/2004
 *
 */
package org.openmaji.implementation.server.http;

import java.net.BindException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;

import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.*;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.utility.TestHarness;
import org.meemplex.meem.Conduit;
import org.meemplex.meem.ConfigProperty;
import org.meemplex.meem.Content;
import org.meemplex.meem.Wedge;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

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
	@Conduit
	public ConfigurationClient  configurationClientConduit = new ConfigurationClientAdapter(this);

	/**
	 * The conduit through which this Wedge will signal whether or not is able to go READY
	 */
	@Conduit
	public Vote              lifeCycleControlConduit;

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	@Conduit
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	/**
	 * Conduit on which to receive Servlets
	 */
	@Conduit
	public ServletConsumer servletConsumerConduit = new ServletConsumer() {
		public void servlet(String name, String path, String classname, Map<String,String> properties) {
			ServletSpec spec = new ServletSpec(name, path, classname, properties);
			addServlet(spec);
		};
	};
	
	/**
	 * for adding resource handler
	 */
	@Conduit
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
	@Conduit
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

	@Content
	public String servletContextString = "/maji";
	
	@Content
	public String meemkitContextString = "/meemkits";
	
	// configurable properties
	@ConfigProperty(description="The port for this web server to listener on")
	public int     port             = 8000;
	
	@ConfigProperty
	public boolean useSSL           = false;
	
	@ConfigProperty(description="whether client authentication is required")
	public boolean needClientAuth   = false;		// whether to use client authentication
	
	@ConfigProperty
	public String  sslKeystore      = "";
	
	@ConfigProperty
	public String  sslPassword      = "";
	
	@ConfigProperty
	public String  sslKeyPassword   = "";
	
	@ConfigProperty(description="Number of seconds before a user session times out")
	public long    sessionTimeout   = 120;  // in seconds


	/* -------------------- configuration specifications -------------------- */
	
	public transient ConfigurationSpecification<Integer> portSpecification           = ConfigurationSpecification.create("The port for this web server to listener on", Integer.class);
	public transient ConfigurationSpecification<String> useSSLSpecification          = ConfigurationSpecification.create("Whether the server should use SSL");
	public transient ConfigurationSpecification<String> needClientAuthSpecification  = ConfigurationSpecification.create("Whether to require client authentication");
	public transient ConfigurationSpecification<String> sslKeystoreSpecification     = ConfigurationSpecification.create("Path to the keystore (for SSL)");
	public transient ConfigurationSpecification<String> sslPasswordSpecification     = ConfigurationSpecification.create("Password for the keystore (for SSL)");
	public transient ConfigurationSpecification<String> sslKeyPasswordSpecification  = ConfigurationSpecification.create("Password for the key (for SSL)");


	/*--------------------------- private properties ------------------------- */
	
	private Server server;
	
	private HandlerCollection handlerCollection;

	private ServletContextHandler servletContext;

	private HashSet<ServletSpec> servletSpecs = new HashSet<ServletSpec>();
	
	private HashMap<String, String> resourceSpecs = new HashMap<String, String>();

	private HashMap<String, String> webappSpecs = new HashMap<String, String>();

	/* ---------------- getters and setters for configuration ---------------- */
	
	/**
	 * @param port
	 */
	public void setPort (Integer port) {
		logger.info("!!!! port being set to: " + port);
		this.port = port.intValue();
	}
	
	public void setUseSSL(String useSSL) {
		this.useSSL = new Boolean(useSSL).booleanValue();
	}
	
	public String getUseSSL() {
		return Boolean.toString(this.useSSL);
	}
	
	public void setNeedClientAuth(String clientAuth) {
		this.needClientAuth = new Boolean(clientAuth).booleanValue();		
	}
	
	public String getNeedClientAuth() {
		return Boolean.toString(this.needClientAuth);
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
		logger.info("STARTING SERVER");
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
			Log.setLog(null);
			System.setProperty("DEBUG", "false");
			System.setProperty("VERBOSE", "false");
			 
			server = new Server();
		}

		Connector connector = null;
		if (useSSL) {
			SslContextFactory contextFactory = new SslContextFactory();
			contextFactory.setKeyStore(sslKeystore);
			contextFactory.setKeyStorePassword(sslPassword);
			contextFactory.setKeyManagerPassword(sslKeyPassword);
			contextFactory.setNeedClientAuth(needClientAuth);
			connector = new SslSelectChannelConnector(contextFactory);
			
			// Setup JSSE keystore and set parameters here correctly
//			connector = new SslSocketConnector();
//			((SslSocketConnector)connector).setKeystore(sslKeystore);
//			((SslSocketConnector)connector).setPassword(sslPassword);
//			((SslSocketConnector)connector).setKeyPassword(sslKeyPassword);
//			((SslSocketConnector)connector).setNeedClientAuth(needClientAuth);
			// uses an entry in the keystore called "jetty"
		}
		else { 
			//connector = new SocketConnector();
			connector = new SelectChannelConnector();
		}
		connector.setPort(port);
		server.addConnector(connector);
		
		// set the Server's HandlerCollection. Other handlers will be added to the HandlerCollection
		handlerCollection = new ContextHandlerCollection();
		server.setHandler(handlerCollection);
		

		// create servlet context
		servletContext = new ServletContextHandler(handlerCollection, servletContextString, ServletContextHandler.SESSIONS);
//		servletContext = new ServletContextHandler(handlerCollection, servletContextString, ServletContextHandler.SESSIONS);

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
	
	private void addServletsToContext(ServletContextHandler context) 
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

	private void addServletToContext(ServletContextHandler context, ServletSpec spec) 
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
		servletHolder.setInitParameters(spec.getProperties());

//		Class<Servlet> servletClass = ObjectUtility.getClass(Servlet.class, spec.getClassname());
//		Dynamic servletRegistration = context.addServlet(servletClass, spec.getPath());
//		servletRegistration.setInitParameters(spec.getProperties());
		
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
			contextHandler.setHandler(resourceHandler);
			
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
		private final Map<String, String> properties;
		
		public ServletSpec(String name, String path, String classname, Map<String,String> properties) {
			this.name = name;
			this.path = path;
			this.classname = classname;

			if (properties == null) {
				properties = new HashMap<String, String>();
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
		
		public Map<String, String> getProperties() {
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