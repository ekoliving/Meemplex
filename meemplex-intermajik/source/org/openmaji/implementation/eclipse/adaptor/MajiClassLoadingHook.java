package org.openmaji.implementation.eclipse.adaptor;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.loader.BaseClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.internal.baseadaptor.BaseClassLoadingHook;

/**
 * Creates a MajiEclipseClassLoader for Maji Eclipse plugins.
 * Maji plugins are determined from examining the "location" of the plugin.  Should
 * contain the work "maji"
 * 
 * @author stormboy
 */
public class MajiClassLoadingHook extends BaseClassLoadingHook {

	private static final String INTERMAJIK_TERM = "intermajik";
	private static final String OPENMAJI_TERM = "openmaji";
	
	public BaseClassLoader createClassLoader(
			ClassLoader parent, 
			ClassLoaderDelegate delegate, 
			BundleProtectionDomain domain,
			BaseData data, 
			String[] bundleclasspath) 
	{
		String location = data.getLocation();
		if ( location.indexOf(INTERMAJIK_TERM) >= 0 || location.indexOf(OPENMAJI_TERM) >= 0 ) {

			// print some info.
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<bundleclasspath.length; i++) {
				sb.append(bundleclasspath[i]);
				sb.append(",");
			}			
			System.out.println("--- Creating MajiEclipseClassloader for location: " + data.getLocation());
			System.out.println("--- bundle classpath: " + sb);
			
			return new MajiEclipseClassLoader(parent, delegate, domain, data, bundleclasspath);
		}
		else {
			return null;
		}
	}
}
