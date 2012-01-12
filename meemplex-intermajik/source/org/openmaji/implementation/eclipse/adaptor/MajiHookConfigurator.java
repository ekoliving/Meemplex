package org.openmaji.implementation.eclipse.adaptor;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.internal.baseadaptor.BaseHookConfigurator;

public class MajiHookConfigurator extends BaseHookConfigurator implements HookConfigurator {

	public void addHooks(HookRegistry hookRegistry) {
		//System.out.println("addHooks... " + hookRegistry);
		
		// commented out by Warren Bloomer 26/10/2011
		//hookRegistry.addClassLoadingHook(new MajiClassLoadingHook());
	}

}
