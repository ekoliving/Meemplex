package org.meemplex.internet.core;

import java.net.URL;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.meemplex.system.MeemkitService;
import org.openmaji.implementation.security.auth.LoginHelper;
import org.openmaji.implementation.server.http.EmbeddedJettyWedge;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitUtility;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class InternetCoreActivator implements BundleActivator {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private ServiceRegistration<MeemkitService> meemkitRegistration = null;
	
	private BundleContext bundleContext;
	
	/**
	 * The meemkit
	 */
	private MeemkitService meemkitService = new MeemkitService() {
		public URL getDescriptorUrl() {
			return bundleContext.getBundle().getEntry("/meemkitDescriptor.xml");
		}
		public MeemkitDescriptor getDescriptor() {
			MeemkitDescriptor descriptor = MeemkitUtility.createMeemkitDescriptor(getDescriptorUrl());
		    return descriptor;
		}
	};

	/**
	 * 
	 */
	public void start(BundleContext bc) throws Exception {
		logger.log(Level.INFO, "Starting " + bc.getBundle().getSymbolicName());
		
		this.bundleContext = bc;
		
		// register meemkit service
		meemkitRegistration = bc.registerService(MeemkitService.class, meemkitService, null);

		// start web server
		runPrivileged(new Runnable() {
			public void run() {
				createWebServer();
			}
		});
	}

	public void stop(BundleContext bc) throws Exception {
		if (meemkitRegistration != null) {
			meemkitRegistration.unregister();
			meemkitRegistration = null;
		}
	}

	
	private void createWebServer() {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(EmbeddedJettyWedge.class);
		LifeCycleManagerHelper.createTransientMeem(meemDefinition, LifeCycleState.LOADED, new AsyncCallback<Meem>() {
			public void result(Meem webServerMeem) {
				logger.info("created web server meem");
				configureWebServer(webServerMeem);			// configure the web server
			}

			public void exception(Exception e) {
				logger.log(Level.INFO, "problem creating web server meem", e);
			}
		});
	}
	
	private void configureWebServer(final Meem webServerMeem) {
		ReferenceHelper.getTarget(webServerMeem, "configurationHandler", ConfigurationHandler.class, new AsyncCallback<ConfigurationHandler>() {
			public void result(ConfigurationHandler configurationHandler) {
				logger.info("!!!! got configuration handler");
				ConfigurationIdentifier id = new ConfigurationIdentifier("embeddedJettyWedge", "port");
				configurationHandler.valueChanged(id, Integer.valueOf(8888));
				makeReady(webServerMeem);
			}
			public void exception(Exception e) {
				logger.log(Level.INFO, "problem getting configuration handler", e);
			}
		});
	}
	
	private void makeReady(Meem webServerMeem) {
		ReferenceHelper.getTarget(webServerMeem, "lifeCycle", LifeCycle.class, new AsyncCallback<LifeCycle>() {
			public void result(LifeCycle result) {
				logger.info("!!!! got lifecycle facet for web server meem: " + result);
				result.changeLifeCycleState(LifeCycleState.READY);
			}
			public void exception(Exception e) {
				logger.log(Level.INFO, "problem getting lifecycle facet", e);
			}
		});
	}

	private void runPrivileged(final Runnable runnable) {
		try {
			LoginContext loginContext = LoginHelper.login("guest", "guest99");
			Subject subject = loginContext.getSubject();
			PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
				public Void run() {
					runnable.run();
					return null;
				}
			};
			Subject.doAsPrivileged(subject, privilegedAction, null);
		}
		catch (LoginException e) {
			
		}

	}
}
