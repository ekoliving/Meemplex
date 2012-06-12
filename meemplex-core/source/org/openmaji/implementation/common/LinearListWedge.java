/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common;

import org.openmaji.common.LinearList;
import org.openmaji.common.Position;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A LinearListWedge maintains a list of Positions.
 * </p>
 * <p>
 * LinearListWedge is intended to work in conjunction with another Wedge that
 * provides a specific implementation of some Linear thing.  Typically,
 * this other Wedge will implement a specific hardware protocol for
 * interacting with some Variable hardware device.
 * </p>
 * <p>
 * LinearListWedge provides a standardized client side interface for Linear things.
 * Using inter-Wedge Conduits this Wedge can interoperate with other Variable
 * Wedges combined as part of the Meem.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not known
 * </p>
 * @author  Warren Bloomer
 */

public class LinearListWedge implements LinearList, Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();

  /**
   * Variable client (out-bound Facet)
   */

  public LinearList linearListClient;
  
  public final ContentProvider linearListClientProvider = new ContentProvider()
  {
      /**
       * Send content to a Variable client that has just had its Reference added.
       *
       * @param target           Reference to the target Meem
       * @param filter           No Filters are currently implemented
       */
      public void sendContent(Object target, Filter filter)
      {
        if ( DebugFlag.TRACE ) logger.log(Level.FINE, "sendContent() - invoked");
        LinearList linearList = (LinearList) target;
        Position[] initialContent = ( valueList == null ? new Position[0] : valueList );
        linearList.valueChanged(initialContent);
      }
  };


  /**
   * Value state maintained by this Variable Wedge and persisted by the Maji framework
   */

  public Position[] valueList = new Position[0];

  /**
   * The conduit through which incoming control message arrive from other Wedges in the Meem. 
   */

  public LinearList linearListControlConduit = null;

  /**
   * The conduit through which state changes are sent out to other Wedges in the Meem. 
   */

  public LinearList linearListStateConduit = new VariableListStateConduit();


  /* ---------- Variable Facet method(s) --------------------------------------- */

  /**
   * Respond to a value change by simply passing the change on to any Wedges
   * that act as a variableControlConduit target.
   *
   * @param valueList Changed value list
   */

  public void valueChanged(Position[] valueList)
  {
    if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on in-bound facet");
    linearListControlConduit.valueChanged(valueList);
  }


  /* ---------- VariableStateConduit ------------------------------------------- */

  /**
   * This class handles incoming Variable state messages from other
   * Wedges in the Meem.
   * 
   */

  class VariableListStateConduit implements LinearList
  {
    /**
     * Respond to a value change by simply passing the change on to any Meems
     * depending upon the out-bound Variable Facet.
     */

    public void valueChanged(Position[] newLinearList) {
      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on LinearListStateConduit");
      valueList = newLinearList;
      linearListClient.valueChanged(valueList);
    }
  }
}
