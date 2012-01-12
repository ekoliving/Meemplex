/* Copyright 2005 by Majitek Limited.  All Rights Reserved.
*
* This software is the proprietary information of Majitek Limited.
* Use is subject to license terms.
*/
package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.common.Multistate;
import org.openmaji.common.State;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * The LoopbackMultistateWedge Wedge is a multistateControlConduit target
 * that listens for Multistate method invocations and immediately passes
 * on those method invocations as a multistateSourceConduit source.
 * 
 * @author  Diana Huang
 */

public class LoopbackMultistateWedge implements Wedge{
	private static Logger logger = LogFactory.getLogger();
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
	      if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"stateChanged() - invoked on MultistateControlConduit");
	      multistateStateConduit.stateChanged(state);
	    }
	  }
	}