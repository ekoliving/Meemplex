package org.meemplex.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.eclipse.core.runtime.FileLocator;
import org.meemplex.system.MeemkitService;
import org.openmaji.implementation.server.manager.lifecycle.hyperspace.HyperSpaceMeem;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemPatternControl;
import org.openmaji.implementation.server.nursery.scripting.telnet.util.TelnetSessionLog;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.server.helper.MeemPathResolverHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * An OSGI Bundle activator for Meemplex
 * 
 * @author stormboy
 *
 */
public class MeemplexActivator implements BundleActivator {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean INSTALL_PATTERNS = true;
	
	private BundleContext bundleContext = null;
	
	private String meemplexPath = "/Users/stormboy/Projects/majiklab/openmaji-meemserver/";
	
	/**
	 * Start the Bundle
	 */
	public void start(BundleContext bc) throws Exception {
		logger.info("Starting " + bc.getBundle().getSymbolicName());
		
		this.bundleContext = bc;
		
		//start the meemplex engine.
		startMeemEngine();

		if (HyperSpaceHelper.getInstance().isHyperSpaceSet()) {
			Meem systemMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM));
			logger.info("got hyperspace: " + (systemMeem !=null) );
		}
		else {
			logger.info("hyperspace not yet set");
		}
		
		// get hyperspace before handling meemkits
		Runnable meemkitRunnable = new Runnable() {
			public void run() {
				// blocks until hyperspace located
				Meem hyperSpaceMeem = HyperSpaceHelper.getInstance().getHyperSpaceMeem();
				if (hyperSpaceMeem != null) {
					try {
						Thread.sleep(6000);	// make sure hyperspace categories are created before proceeding
						listenForMeemkits(bundleContext);
					}
					catch (Exception e) {
						logger.log(Level.INFO, "Problem listening for meemkits", e);
					}
				}
			}
		};
		new Thread(meemkitRunnable).start();
		

	}

	/**
	 * Stop the bundle
	 */
	public void stop(BundleContext bc) throws Exception {
		try {
			MeemEngineLauncher.instance().shutdown();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Problem shutting down meemplex", e);
		}
	}
	
	private static final String PROP_HOME = "org.openmaji.directory";
	
	
	/**
	 * 
	 */
	private void startMeemEngine() {

		try {
			URL entry = bundleContext.getBundle().getEntry("/");
			String path = FileLocator.toFileURL(entry).getPath(); 
			logger.info("URL to MeemServer bundle is: " + path);
			meemplexPath = path;
		}
		catch (IOException e) {
			logger.log(Level.INFO, "Problem getting meemserver bundle path", e);
		}
		
		// find meemplex home path
//		ServiceReference<URLConverter> ref = bundleContext.getServiceReference(URLConverter.class);
//		URLConverter urlConverter = (URLConverter) bc.getService(ref);
//		meemplexPath = urlConverter.toFileURL(meemplexEntry).getPath();
		
		logger.info("launching meemplex. base directory: " + meemplexPath);

		// set up system properties
		if (System.getProperty(PROP_HOME) == null) {
			System.setProperty(PROP_HOME, meemplexPath);
		}
		System.setProperty("java.security.policy", meemplexPath + "/conf/security/all.policy");
		System.setProperty("org.openmaji.properties", "conf/meemServer_01.properties");

		MeemEngineLauncher.instance().launch();
	}

	/**
	 * The listener for MeemKit OSGI services
	 */
	private ServiceListener meemkitListener = new ServiceListener() {
		@Override
		public void serviceChanged(ServiceEvent se) {
			
			//logger.info("MeemKitListener: serviceChanged " + se.getType());
			
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

	private void listenForMeemkits(BundleContext bc) throws Exception {

		String filter = "(" + Constants.OBJECTCLASS + "=" + MeemkitService.class.getCanonicalName() + ")";
		
		logger.info("service filter = " + filter);
		
		bc.addServiceListener(meemkitListener, filter);
		
		Collection<ServiceReference<MeemkitService>> serviceRefs = bc.getServiceReferences(MeemkitService.class, null);
		for (ServiceReference<MeemkitService> serviceRef : serviceRefs) {
			MeemkitService meemkitService = bc.getService(serviceRef);
			if (meemkitService != null) {
				meemkitListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceRef));
			}
		}
	}
	
	/**
	 * 
	 */
	private void loadMeemkit(String name, MeemkitService meemkit) {
		logger.info("loading meemkit: " + name);
		final MeemkitDescriptor meemkitDescriptor = meemkit.getDescriptor();
		
//		if (meemkitDescriptor != null) {
//			MeemkitHeader header = meemkitDescriptor.getHeader();
//			logger.info("meemkit descriptor: " + header);
//		}
		
		/* */
		// TODO load pattern meems
		// TODO load pattern wedges
		// TODO load singletons

		if (INSTALL_PATTERNS) {
			//logger.info("installing pattern meems...");
	
			try {
				ServerGateway gateway = ServerGateway.spi.create(MeemCoreRootAuthority.getSubject());
				
				// install pattern meems
				MeemPath meemkitManagerPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitManager");
				Meem meemkitManagerMeem = gateway.getMeem(meemkitManagerPath);
			    final MeemPatternControl meemPatternControl = ReferenceHelper.getTarget(meemkitManagerMeem, "meemPatternControl", MeemPatternControl.class);

			    Runnable runnable = new Runnable() {
					public void run() {
					    Subject.doAs(MeemCoreRootAuthority.getSubject(), new PrivilegedAction<Void>() {
					    	public Void run() {
					    	    meemPatternControl.installPatternMeems(meemkitDescriptor);
					    	    return null;
					    	}
						});
					}
				};
				new Thread(runnable).start();
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Problem installing pattern meems", e);
			}
		}
	}
	
	/**
	 * 
	 */
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
		
		/*
		LifeCycleManagerHelper.createMeem(
				HyperSpaceMeem.getMeemDefinition(), 
				EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier())
			);
			
		  HyperSpaceHelper hsh = HyperSpaceHelper.getInstance();
		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM);
		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_INSTALLATION); 
		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_LIBRARY);  
		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN); 
		  hsh.createPath(StandardHyperSpaceCategory.MAJI_SYSTEM_CONFIGURATION);
		  */
	}
	
	/**
	 * 
	 */
	private void unloadMeemkit(String name) {
		logger.info("unloading meemkit: " + name);

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
