/* Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
*
* This software is the proprietary information of EkoLiving Pty Ltd.
* Use is subject to license terms.
*/
package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.common.Multistate;
import org.openmaji.common.State;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LoopbackMultistateWedge Wedge is a multistateControlConduit target
 * that listens for Multistate method invocations and immediately passes
 * on those method invocations as a multistateSourceConduit source.
 * 
 * @author  Diana Huang
 */

public class LoopbackMultistateWedge implements Wedge{
	private static Logger logger = Logger.getAnonymousLogger();
	/*
	 * inbound conduit
	 */
	public Multistate multistateControlConduit = new MultistateControlConduit();
	/*
	 * outbound facet
	 */
	public Multistate multistateStateConduit = null;
	  
	/* ---------- MultistateControlConduit ----------------------------------------- */

	class MultistateControlConduit implements Multistate{
		
	    /**
	     * Respond to a value change by simply passing the change back to any
	     * Wedges that act as a multistateStateConduit target.
	     *
	     * @param state Changed state value
	     */

	    public synchronized void stateChanged(State state)
	    {
	      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "stateChanged() - invoked on MultistateControlConduit");
	      multistateStateConduit.stateChanged(state);
	    }
	  }
	}