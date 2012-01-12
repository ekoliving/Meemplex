package org.openmaji.implementation.server.classloader;

public interface MeemkitClassLoaderListener {

	void classloaderAdded(String meemkitName);
	
	void classloaderRemoved(String meemkitName);
}
