/*
 * @(#)FlightRecorderHookWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Allow the FlightRecorderHookWedge to be turned on or off on-the-fly.
 * - Filter the FlightRecorderHookWedge for certain Meem UIDs or types.
 * - Filter the FlightRecorderHookWedge for certain FacetIdentifiers.
 * - Filter the FlightRecorderHookWedge for system versus application Wedges.
 */

package org.openmaji.implementation.server.meem.hook.flightrecorder;


import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.meem.hook.HookProcessor;
import org.openmaji.system.meem.hook.flightrecorder.FlightRecorderHook;
import org.openmaji.system.meem.hook.invoke.Invocation;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * FlightRecorderHookWedge records Meem Facet invocations as they occur.
 * This record is intended to assist in problem diagnosis.
 * </p>
 * <p>
 * This hook does not suspend processing of the InvocationList.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class FlightRecorderHookWedge implements FlightRecorderHook, Wedge {

  public ConfigurationClient		configurationClientConduit = new ConfigurationClientAdapter(this);
  
  public transient ConfigurationSpecification flightRecorderModeSpecification = new FlightRecorderModeSpecification();
  
  public boolean detailed = false;
  public boolean on = false;
  public String flightRecorderMode = "off";
  
  public void setFlightRecorderMode(
  	String	newFlightRecorderMode)
  {
      if (flightRecorderMode.equals("on"))
      {
      	this.detailed = false;
      	this.on = true;
      }
      else if (flightRecorderMode.equals("verbose"))
      {
      	this.detailed = true;
      	this.on = true;
      }
      else
      {
        newFlightRecorderMode = "off";
      	this.detailed = false;
      	this.on = false;
      }
      this.flightRecorderMode = newFlightRecorderMode;
  }
  
  /**
   * Record the specified invocation method and args.
   *
   * @param invocation Contains method invocation information, etc
   * @return true, continue InvocationList processing
   */

  public boolean process(Invocation invocation, HookProcessor hookProcessor) throws Throwable {

    if (on)
    {
		String description = invocation.getDescription(detailed);
	
	    LogTools.info(logger, description);
    }

    return true;
  }

  public static class FlightRecorderModeSpecification
      extends ConfigurationSpecification
   {
	  private static final long serialVersionUID = 5630101894243L;
	  
   	    FlightRecorderModeSpecification()
   	    {
   	    	super("flightRecorderMode (off, on, verbose)", String.class, LifeCycleState.READY);
   	    }
   	    
   	    public String validate(
   	    	String	value)
   	    {
   	    	if (!value.equals("on") && !value.equals("off") && !value.equals("verbose"))
   	    	{
   	    		return "Flightrecorder flightRecorderMode must be one of: off, on, or verbose.";
   	    	}
   	    	
   	    	return null;
   	    }
   }
/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = LogFactory.getLogger();
}
