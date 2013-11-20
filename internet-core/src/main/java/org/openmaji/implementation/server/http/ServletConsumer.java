package org.openmaji.implementation.server.http;

import java.util.Map;
import org.openmaji.meem.Facet;

public interface ServletConsumer extends Facet {

	/**
	 * Send servlet details.
	 * 
	 * @param name a name for the servlet.
	 * @param path the path withing the servlet context to place the servlet.
	 * @param classname the class of the servlet
	 * @param properties configuration properties for the servlet.
	 */
	void servlet(String name, String path, String classname, Map<String,String> properties);
	

}
