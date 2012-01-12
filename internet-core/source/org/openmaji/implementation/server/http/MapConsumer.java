package org.openmaji.implementation.server.http;

import org.openmaji.meem.Facet;

/**
 * 
 * @author stormboy
 *
 */
public interface MapConsumer extends Facet {

	void add(String key, String value);
	
	void remove(String key);
}
