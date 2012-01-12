package org.meemplex.meem;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 
 * @author stormboy
 *
 */
public interface MeemInstance {

	/**
	 * Unique identifier for this Meem instance
	 * 
	 * @return the universally nique identifier for the Meem
	 */
	UUID getId();

	/**
	 * Name of the Meem instance
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * A description for this instance
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * 
	 * @param facetPath
	 * @param dependency
	 */
	void addDependency(String facetPath, Dependency dependency);

	/**
	 * 
	 * @param facetPath
	 * @param dependency
	 */
	void removeDependency(String facetPath, Dependency dependency);
	
	/**
	 * Invoke a Method on an inbound Facet of the Meem.
	 * 
	 * @param facetPath
	 * @param methodName
	 * @param args
	 */
	void invoke(String facetPath, String methodName, Object... args);

	/**
	 * Invoke a Method on an inbound Facet of the Meem.
	 * 
	 * @param facetPath
	 * @param method
	 * @param args
	 */
	void invoke(String facetPath, Method method, Object... args);
	
	/**
	 * Add a listener object to messages from an outbound Facet.
	 * 
	 * The listener must implement the same Interface as the Facet
	 * 
	 * @param facetPath
	 * @param listener
	 * @param filter
	 */
	void addListener(String facetPath, Object listener, InvocationFilter filter);

	/**
	 * Remove a listener to messages from an outbound Facet.
	 * 
	 * @param facetPath
	 * @param listener
	 */
	void removeListener(String facetPath, Object listener);
	
//	/**
//	 * Add an XMPP listener to messages from an outbound Facet.
//	 * 
//	 * @param facetPath
//	 * @param listener
//	 * @param filter
//	 */
//	void addListener(String facetPath, JID listener, InvocationFilter filter);
//
//	/**
//	 * Remove an XMPP listener to messages from an outbound Facet
//	 * @param facetPath
//	 * @param listener
//	 */
//	void removeListener(String facetPath, JID listener);
}
