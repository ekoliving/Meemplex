/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.implementation.automation.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * The LoopbackVariableWedge Wedge is a simple example of a pluggable Variable
 * thing.
 * </p>
 * <p>
 * LoopbackVariableWedge is a variableControlConduit target that listens
 * for Variable method invocations and immediately passes on those method
 * invocations as a variableStateConduit source.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not known
 * </p>
 * @author  Christos Kakris
 */

public class LoopbackVariableWedge implements Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();

  /* ---------------------------------- conduits ----------------------------- */
  
  public Variable variableControlConduit = new VariableControlConduit();
  public Variable variableStateConduit = null;
  
  public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

  
  public transient ConfigurationSpecification positionSpecification = 
		new ConfigurationSpecification(
				"Position specified as 'value increment min max'",
				String.class, 
				LifeCycleState.READY
			);

  private Value cachedValue;

	/* ---------- configuration getters/setters ------------------------------- */

	public void setPosition(String valueString) throws ConfigurationRejectedException {
		cachedValue = new StringValue(valueString);
		variableStateConduit.valueChanged(cachedValue);
	}

	public String getPosition() {
		return cachedValue == null ? "" : cachedValue.toString();
	}

  /* ---------- VariableControlConduit ----------------------------------------- */

  private class VariableControlConduit implements Variable
  {
    public void valueChanged(Value value)
    {
      if ( DebugFlag.TRACE ) {
    	  logger.log(Level.FINE, "valueChanged() - invoked on VariableControlConduit");
      }      
      variableStateConduit.valueChanged(value);
    }
  }
}
