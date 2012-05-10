/* Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
*
* This software is the proprietary information of EkoLiving Pty Ltd.
* Use is subject to license terms.
*/
package org.openmaji.implementation.common;

import org.openmaji.common.Unary;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * UnaryWedge does not maintain any status and is like a command to trigger
 * a task without any condition
 * 
 * @author Diana Huang
 * @see org.openmaji.common.Unary
 */

public class UnaryWedge implements Unary,Wedge{
	private static Logger logger = LogFactory.getLogger();
	/*
	 * Unary outbound Facet
	 */
	public Unary unaryClient;
	public final ContentProvider unaryClientProvider = new ContentProvider(){
		/**
	     * Send content to a Unary client that has just had its Reference added.
	     *
	     * @param target           Reference to the target Meem
	     * @param filter           No Filters are currently implemented
	     */
	    public synchronized void sendContent(Object target, Filter filter){
			if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"sendContent() - invoked");
	        ((Unary) target).valueChanged();
	    }
	};
	
	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to trigger Unary.  
	 */
	public Unary unaryControlConduit = null;
	/**
	 * The conduit through which Unary change is received other Wedges in the Meem. 
	 */
	 public Unary unaryStateConduit = new UnaryConduit();

	/* ---------- Unary Facet method(s) --------------------------------------- */
	 public synchronized void valueChanged(){
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on inbound facet");
	    unaryControlConduit.valueChanged();
	 }

	/**
	 * This class handles incoming Unary invocations from other
	 * Wedges in the Meem.
	 */
	 class UnaryConduit implements Unary{
		 public synchronized void valueChanged(){
			 if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on UnaryStateConduit");
			  
			 unaryClient.valueChanged();
	    }
	  }
}
