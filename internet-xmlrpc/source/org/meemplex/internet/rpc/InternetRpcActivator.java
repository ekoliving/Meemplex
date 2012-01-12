package org.meemplex.internet.rpc;

import java.net.URL;
import java.util.logging.Logger;

import org.meemplex.system.MeemkitService;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitUtility;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class InternetRpcActivator implements BundleActivator {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private BundleContext bundleContext;
	
	private ServiceRegistration<MeemkitService> serviceReg = null;
	
	/**
	 * The Meemkit
	 */
	private MeemkitService meemkitService = new MeemkitService() {
		public URL getDescriptorUrl() {
			return bundleContext.getBundle().getEntry("/meemkitDescriptor.xml");
		}
		public MeemkitDescriptor getDescriptor() {
			URL meemkitUrl = getDescriptorUrl();
			//logger.info("got meemkit resource: " + meemkitUrl);
			
			MeemkitDescriptor descriptor = MeemkitUtility.createMeemkitDescriptor(meemkitUrl);
		    return descriptor;
		}
	};

	/**
	 * 
	 */
	public void start(BundleContext bc) throws Exception {
		logger.info("Starting " + bc.getBundle().getSymbolicName());
		
		this.bundleContext = bc;
		
		serviceReg = bc.registerService(MeemkitService.class, meemkitService, null);
	}
	
	/**
	 * 
	 */
	public void stop(BundleContext bc) throws Exception {
		if (serviceReg != null) {
			serviceReg.unregister();
			serviceReg = null;
		}
	}
}
