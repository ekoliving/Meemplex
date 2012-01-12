package org.openmaji.implementation.rpc.server;

import java.io.Serializable;
import java.util.List;

import org.openmaji.rpc.binding.MeemEvent;
import org.openmaji.rpc.binding.NoSessionException;

/**
 * An interface for communications between clients and meems within a meemserver.
 * 
 * @author Warren Bloomer
 *
 */
public interface SessionMessageHandler {


	/**
	 * Send a message to a Facet
	 * 
	 * @return Session identifier
	 */
	void send (
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
	MeemEvent receive ()
		throws NoSessionException;

	
	/**
	 * Receive all the events in the queue outbound from Maji.
	 * 
	 * @return a vector of hashtables representing Maji events.
	 */
	List<MeemEvent> receiveMany();

	
	/**
	 * Register interest in an outbound facet
	 */
	void register (
			String meemPath, 
			String facetId,
			String classname
		);
	
	/**
	 * De-register interest in a Facet
	 */
	void deregister (
			String meemPath, 
			String facetId
		);

	/**
	 * 
	 * @param sessionId
	 * @param meemPath
	 * @param facetId
	 * @param classname
	 * @return
	 */
	void registerOutbound (
			String meemPath, 
			String facetId,
			String classname
		);
	
	/**
	 * Interrupt a blocking receive call.
	 * 
	 * @param sessionId
	 */
	void interrupt();
	
 }
