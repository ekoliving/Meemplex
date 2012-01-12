package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.common.Unary;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

public class LoopbackUnaryWedge implements Wedge{
	private static Logger logger = LogFactory.getLogger();
	/*
	 * inbound conduit
	 */
	public Unary unaryControlConduit = new UnaryControlConduit();
	/*
	 * outbound facet
	 */
	public Unary unaryStateConduit = null;
	  
	/* ---------- MultistateControlConduit ----------------------------------------- */

	class UnaryControlConduit implements Unary{
		
	    /**
	     * Respond to a value change by simply passing the change back to any
	     * Wedges that act as a unaryStateConduit target.
	     * 
	     */

	    public synchronized void valueChanged()
	    {
	      if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on UnaryControlConduit");
	      unaryStateConduit.valueChanged();
	    }
	  }

}
