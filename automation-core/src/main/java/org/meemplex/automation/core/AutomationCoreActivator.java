package org.meemplex.automation.core;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meemplex.system.MeemkitService;
import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitUtility;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class AutomationCoreActivator implements BundleActivator {
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
		if (Common.TRACE_ENABLED) {
			logger.log(Level.INFO, "Starting " + bc.getBundle().getSymbolicName());
		}
		this.bundleContext = bc;
		this.meemkitRegistration = bc.registerService(MeemkitService.class, meemkitService, null);		// register meemkit service
	}

	/**
	 * 
	 */
	public void stop(BundleContext bc) throws Exception {
		if (meemkitRegistration != null) {
			meemkitRegistration.unregister();
			meemkitRegistration = null;
		}
	}

}
