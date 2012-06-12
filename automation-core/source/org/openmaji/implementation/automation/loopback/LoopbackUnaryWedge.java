package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.common.Unary;



import java.util.logging.Level;
import java.util.logging.Logger;

public class LoopbackUnaryWedge implements Wedge{
	private static Logger logger = Logger.getAnonymousLogger();
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
	      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on UnaryControlConduit");
	      unaryStateConduit.valueChanged();
	    }
	  }

}
