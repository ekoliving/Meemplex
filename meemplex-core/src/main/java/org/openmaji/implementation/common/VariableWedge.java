/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common;

import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A VariableWedge maintains a generic type of value, including Numbers and
 * Strings.
 * </p>
 * <p>
 * VariableWedge is intended to work in conjunction with another Wedge that
 * provides a specific implementation of some Variable thing.  Typically,
 * this other Wedge will implement a specific hardware protocol for
 * interacting with some Variable hardware device.
 * </p>
 * <p>
 * VariableWedge provides a standardized client side interface for Variable things.
 * Using inter-Wedge Conduits this Wedge can interoperate with other Variable
 * Wedges combined as part of the Meem.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not known
 * </p>
 * @author  Christos Kakris
 */

public class VariableWedge implements Variable, Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();

  /**
   * Variable client (out-bound Facet)
   */

  public Variable variableClient;
  public final ContentProvider<Variable> variableClientProvider = new ContentProvider<Variable>()
  {
      /**
       * Send content to a Variable client that has just had its Reference added.
       *
       * @param target           Reference to the target Meem
       * @param filter           No Filters are currently implemented
       */
      public void sendContent(Variable variable, Filter filter)
      {
        if ( DebugFlag.TRACE ) logger.log(Level.FINE, "sendContent() - invoked");
        Value initialContent = ( value == null ? new StringValue("") : value );
        variable.valueChanged(initialContent);
      }
  };


  /**
   * Value state maintained by this Variable Wedge and persisted by the Maji framework
   */

  public Value value = null;

  /**
   * The conduit through which incoming control message arrive from other Wedges in the Meem. 
   */

  public Variable variableControlConduit = null;

  /**
   * The conduit through which state changes are sent out to other Wedges in the Meem. 
   */

  public Variable variableStateConduit = new VariableStateConduit();


  /* ---------- Variable Facet method(s) --------------------------------------- */

  /**
   * Respond to a value change by simply passing the change on to any Wedges
   * that act as a variableControlConduit target.
   *
   * @param value Changed value
   */

  public void valueChanged(Value value)
  {
    if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on in-bound facet");
    variableControlConduit.valueChanged(value);
  }


  /* ---------- VariableStateConduit ------------------------------------------- */

  /**
   * This class handles incoming Variable state messages from other
   * Wedges in the Meem.
   * 
   * @author Chris Kakris
   */

  class VariableStateConduit implements Variable
  {
    /**
     * Respond to a value change by simply passing the change on to any Meems
     * depending upon the out-bound Variable Facet.
     *
     * @param newValue Changed value
     */

    public void valueChanged(Value newValue)
    {
      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on VariableStateConduit");
      value = newValue;
      variableClient.valueChanged(value);
    }
  }
}
