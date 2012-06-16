package org.meemplex.server;

import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.eclipse.core.runtime.FileLocator;
import org.meemplex.system.MeemkitService;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemPatternControl;
import org.openmaji.implementation.server.nursery.scripting.telnet.util.TelnetSessionLog;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemserver.MeemServer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * An OSGI Bundle activator for Meemplex
 * 
 * @author stormboy
 *
 */
public class MeemplexActivator implements BundleActivator {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean TRACE = true;
	
	private static final String PROP_HOME = "org.openmaji.directory";
	
	private static final boolean INSTALL_PATTERNS = true;
	
	private BundleContext bundleContext = null;
	
	private String meemplexPath = "/Users/stormboy/Projects/Meemplex/source/meemplex-meemserver/";
	
	/**
	 * Start the Bundle
	 */
	public void start(BundleContext bc) throws Exception {
		if (TRACE) {
			logger.log(Level.INFO, "Starting " + bc.getBundle().getSymbolicName());
		}

		this.bundleContext = bc;
		
		//start the meemplex engine.
		startMeemEngine();

		// get hyperspace before handling meemkits
		HyperSpaceHelper.getInstance().getHyperSpaceMeem(new AsyncCallback<Meem>() {
			public void result(Meem hyperSpaceMeem) {
				handleHyperSpace(hyperSpaceMeem);
			}
			public void exception(Exception e) {
				logger.log(Level.INFO, "Could not get Hyperspace Meem", e);
			}
		});
	}

	/**
	 * Stop this bundle.  Stop the meemplex engine.
	 */
	public void stop(BundleContext bc) throws Exception {
		try {
			MeemEngineLauncher.instance().shutdown();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Problem shutting down meemplex", e);
		}
	}
	
	/**
	 * Handle hyperspace meem.
	 * 
	 * @param hyperSpaceMeem
	 */
	private void handleHyperSpace(Meem hyperSpaceMeem) {
		if (hyperSpaceMeem == null) {
			logger.log(Level.INFO, "hyperspace is null");
		}
		else {

			logger.log(Level.INFO, "got hyperspace: " + hyperSpaceMeem );
			try {
				Thread.sleep(6000);	// make sure hyperspace categories are created before proceeding
									// TODO remove this wait hack - listen for meemkitmanager becoming ready
				listenForMeemkits(bundleContext);
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Problem listening for meemkits", e);
			}
		}
	}
	
	/**
	 * 
	 */
	private void startMeemEngine() {

		// use Eclipse FileLocator to get the root of this bundle.
		try {
			URL entry = bundleContext.getBundle().getEntry("/");
			String path = FileLocator.toFileURL(entry).getPath(); 
			if (TRACE) {
				logger.log(Level.INFO, "URL to MeemServer bundle is: " + path);
			}
			meemplexPath = path;
		}
		catch (IOException e) {
			logger.log(Level.INFO, "Problem getting meemserver bundle path", e);
		}
		
		// find meemplex home path
//		ServiceReference<URLConverter> ref = bundleContext.getServiceReference(URLConverter.class);
//		URLConverter urlConverter = (URLConverter) bc.getService(ref);
//		meemplexPath = urlConverter.toFileURL(meemplexEntry).getPath();

		if (TRACE) {
			logger.log(Level.INFO, "launching meemplex. base directory: " + meemplexPath);
		}

		// set up system properties
		if (System.getProperty(PROP_HOME) == null) {
			System.setProperty(PROP_HOME, meemplexPath);
		}
		System.setProperty("java.security.policy", meemplexPath + "/conf/security/all.policy");
		
		/*
		System.setProperty("java.rmi.server.RMIClassLoaderSpi", "net.jini.loader.pref.PreferredClassProvider");
		System.setSecurityManager(new RMISecurityManager());
		*/
		
		if (System.getProperty("org.openmaji.properties") == null) {
			System.setProperty("org.openmaji.properties", "conf/meemServer_01.properties");
		}

		MeemEngineLauncher.instance().launch();
	}

	/**
	 * Filter for Meemkit OSGI services.
	 */
	private static final String MEEMKIT_FILTER = "(" + Constants.OBJECTCLASS + "=" + MeemkitService.class.getCanonicalName() + ")";
	
	/**
	 * Start listening for OSGI Meemkit services
	 * 
	 * @param bc
	 * @throws Exception
	 */
	private void listenForMeemkits(BundleContext bc) throws Exception {
		// start listening for services
		bc.addServiceListener(meemkitListener, MEEMKIT_FILTER);
		
		// get services that may already be registered in the bundle context
		Collection<ServiceReference<MeemkitService>> serviceRefs = bc.getServiceReferences(MeemkitService.class, null);
		for (ServiceReference<MeemkitService> serviceRef : serviceRefs) {
			MeemkitService meemkitService = bc.getService(serviceRef);
			if (meemkitService != null) {
				meemkitListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceRef));
			}
		}
	}
	
	/**
	 * The listener for MeemKit OSGI services
	 */
	private ServiceListener meemkitListener = new ServiceListener() {
		public void serviceChanged(ServiceEvent se) {
			@SuppressWarnings("unchecked")
			ServiceReference<MeemkitService> sr = (ServiceReference<MeemkitService>) se.getServiceReference();
			Bundle bundle = sr.getBundle();
			String name = bundle.getSymbolicName();
			MeemkitService meemkit = (MeemkitService) bundleContext.getService(sr);
			switch(se.getType()) {
			case ServiceEvent.REGISTERED:
				loadMeemkit(name, meemkit);
				break;
			case ServiceEvent.UNREGISTERING:
				unloadMeemkit(name);
				break;
			case ServiceEvent.MODIFIED:
				unloadMeemkit(name);
				loadMeemkit(name, meemkit);
				break;
			}
		}
	};

	/**
	 * 
	 */
	private void loadMeemkit(String name, MeemkitService meemkit) {
		if (TRACE) {
			logger.log(Level.INFO, "loading meemkit: " + name);
		}
		
		final MeemkitDescriptor meemkitDescriptor = meemkit.getDescriptor();
		
		if (INSTALL_PATTERNS) {		// load pattern meems and wedges
			try {
				ServerGateway gateway = ServerGateway.spi.create(MeemCoreRootAuthority.getSubject());
				
				// install Meemkit pattern meems using the meemPatternControl facet of the MeemkitManagerMeem
				MeemPath meemkitManagerPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitManager");
				Meem meemkitManagerMeem = gateway.getMeem(meemkitManagerPath);

				ReferenceHelper.getTarget(meemkitManagerMeem, "meemPatternControl", MeemPatternControl.class, new AsyncCallback<MeemPatternControl>() {
					public void result(MeemPatternControl meemPatternControl) {
						installMeemkitPatternMeems(meemPatternControl, meemkitDescriptor);
					};
					public void exception(Exception e) {
						if (TRACE) {
							logger.log(Level.INFO, "Could not get meemPatternControl facet from MeemkitManagerMeem", e);
						}
					}
				});
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Problem installing pattern meems", e);
			}
		}
		
		// TODO load singletons/instances for the meemkit
	}

	/**
	 * Install meemkit patterns when the meemPatternControl facet of the MeemkitManager Meem has been resolved.
	 * 
	 * @param meemPatternControl
	 * 	The meemPatternControl facet
	 * @param meemkitDescriptor
	 * 	The meemkit descriptor that describes the meemkit patterns to install
	 */
	private void installMeemkitPatternMeems(final MeemPatternControl meemPatternControl, final MeemkitDescriptor meemkitDescriptor) {
	    Runnable runnable = new Runnable() {
			public void run() {
			    Subject.doAs(MeemCoreRootAuthority.getSubject(), new PrivilegedAction<Void>() {
			    	public Void run() {
						if (TRACE) {
							logger.log(Level.INFO, "Installing pattern meems for meemkit " + meemkitDescriptor.getHeader().getName());
						}
			    	    meemPatternControl.installPatternMeems(meemkitDescriptor);
			    	    return null;
			    	}
				});
			}
		};
		ThreadManager.spi.create().queue(runnable);
	}

	/**
	 * TODO automatic creation of hyperspace 
	 */
	/*
	private void createHyperspace() throws EvalError, FileNotFoundException, IOException  {
//		ServerGateway gateway =  ServerGateway.spi.create();
//		MeemPath essentialLcmPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitLifeCycleManager");
//		Meem meem = gateway.getMeem(path);
		
		String beanShellDirectory = meemplexPath + "scripts/beanshell";
		String beanShellScript = "initialize.bsh";
		
		Interpreter interpreter = new Interpreter();
		interpreter.set("log", new Log());
		interpreter.eval("cd(\"" + beanShellDirectory + "\");");
		interpreter.eval("source(\"" + beanShellScript + "\");");

		//interpreter.set("bsh.cwd", beanShellDirectory);
		//interpreter.source("initialize.bsh");
		//interpreter.source("meemSpace.bsh");

		interpreter.eval("createMeemSpace();");
		
//		LifeCycleManagerHelper.createMeem(
//				HyperSpaceMeem.getMeemDefinition(), 
//				EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier())
//			);
//			
//		  HyperSpaceHelper hsh = HyperSpaceHelper.getInstance();
//		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM);
//		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_INSTALLATION); 
//		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_LIBRARY);  
//		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN); 
//		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_CONFIGURATION);
	}
*/
	
	/**
	 * 
	 */
	private void unloadMeemkit(String name) {
		logger.log(Level.INFO, "unloading meemkit: " + name);

		// TODO remove singletons
		// TODO remove pattern meems
		// TODO remove pattern wedge
	}
	
	private class Log implements TelnetSessionLog {
		@Override
		public void error(String message) {
			logger.log(Level.SEVERE, message);
		}
		@Override
		public void info(String message) {
			logger.log(Level.INFO, message);
		}
		@Override
		public void trace(String message) {
			logger.log(Level.FINE, message);
		}
		@Override
		public void verbose(String message) {
			logger.log(Level.INFO, message);
		}
		@Override
		public void warn(String message) {
			logger.log(Level.WARNING, message);
		}
	}
}
