package org.openmaji.implementation.rpc.server;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.openmaji.rpc.binding.NoSessionException;

/**
 * An interface for communications between clients and meems within a meemserver.
 * 
 * @author Warren Bloomer
 *
 */
public interface RpcMessageHandler {

	/**
	 * Start a new client session.
	 * 
	 * A client may send a sessionId.  If the sessionId exists, that session is
	 * used and its sessionId is returned.
	 * 
	 * @return the sessionId to use
	 */
	String beginSession(String sessionId);

	/**
	 * Terminate a client session.
	 * 
	 * The session handler is cleaned up and destroyed.
	 * 
	 * @param sessionId
	 */
	String endSession(String sessionId);

	/**
	 * Send a message to a Facet
	 * 
	 * @return Session identifier
	 */
	String send (
			String sessionId,
			String meemPath, 
			String facetId,
			String classname,
			String methodName,
			Object[] args
		);

	/**
	 * To receive the message intended for the session.
	 * These messages will be things like Facet events or health events.
	 */
	Map<String, Object> receive (String sessionId)
		throws NoSessionException;

	/**
	 * Receive all the events in the queue outbound from Maji.
	 * 
	 * @param sessionId
	 * @return a vector of hashtables representing Maji events.
	 */
	List<Map<String, Object>> receiveMany(String sessionId);

	
	/**
	 * Register interest in an inbound facet on a Meem
	 */
	String register (
			String sessionId, 		
			String meemPath, 
			String facetId,
			String classname
		);
	
	/**
	 * 
	 * @param sessionId
	 * @param meemPath
	 * @param facetId
	 * @param classname
	 * @return
	 */
	String registerOutbound (
			String sessionId, 		
			String meemPath, 
			String facetId,
			String classname
		);
	
	/**
	 * De-register interest in a Facet
	 */
	String deregister (
			String sessionId, 		
			String meemPath, 
			String facetId
		);

	/**
	 * Interrupt a blocking receive call.
	 * 
	 * @param sessionId
	 */
	String interrupt(String sessionId) 
		throws NoSessionException;
	
 }
