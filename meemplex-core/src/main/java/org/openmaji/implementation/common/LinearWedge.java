/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common;

import org.meemplex.meem.Conduit;
import org.meemplex.meem.Content;
import org.meemplex.meem.Facet;
import org.meemplex.meem.FacetContent;
import org.meemplex.service.model.Direction;
import org.openmaji.common.Linear;
import org.openmaji.common.PercentagePosition;
import org.openmaji.common.Position;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;








import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Implement inter-Wedge value updating, as soon as the inter-Wedge listener
//      pattern has been completed within the MeemBuilder. 

// TODO Consider generating an error, if the sendContent() target is not a Linear.

// TODO Consider creating an optimized version, that only informs the Linear Meem
//      clients, if the value really changed.

/**
 * <p>
 * The LinearWedge Wedge is used to maintain a value that falls within a bounded linearly increasing range.
 * For example, a light whose intensity can be dimmed.
 * </p>
 * <p>
 * LinearWedge is intended to work in conjunction with another Wedge that
 * provides a specific implementation of some Linear thing.  Typically,
 * this other Wedge will implement a specific hardware protocol for
 * interacting with some Linear hardware device.
 * </p>
 * <p>
 * LinearWedge provides a standardized client side interface for Linear things.
 * Using inter-Wedge Conduits this Wedge can interoperate with other Linear
 * Wedges combined as part of the Meem.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-08-06)
 * </p>
 * @author  Christos Kakris
 * @version 1.0
 */

@org.meemplex.meem.Wedge(name="LinearWedge")

public class LinearWedge implements Linear, Wedge
{
  private static final Logger logger = Logger.getAnonymousLogger();

  /**
   * Linear client (out-bound Facet)
   */

  @Facet(direction=Direction.OUT)
  public Linear linearClient;
  
  @FacetContent(facet="linearOutput")
  public final ContentProvider<Linear> linearClientProvider = new ContentProvider<Linear>()
  {
      /**
       * Send content to a Linear client that has just had its Reference added.
       *
       * @param target           Reference to the target Meem
       * @param filter           No Filters are currently implemented
       */
      public synchronized void sendContent(Linear target, Filter filter)
      {
        if ( DebugFlag.TRACE ) logger.log(Level.FINE, "sendContent() - invoked");
        target.valueChanged(position);
      }
  };


  /**
   * Position state maintained by this Linear Wedge and persisted by the Maji framework
   */
  @Content
  public Position position = new PercentagePosition();

  /**
   * The conduit through which incoming control message arrive from other Wedges in the Meem. 
   */

  @Conduit(name="linearControl")
  public Linear linearControlConduit = null;

  /**
   * The conduit through which state changes are sent out to other Wedges in the Meem. 
   */

  @Conduit(name="linearState")
  public Linear linearStateConduit = new LinearStateConduit();


  /* ---------- Linear Facet method(s) --------------------------------------- */

  /**
   * Respond to a value change by simply passing the change on to any Wedges
   * that act as a linearControlConduit target.
   *
   * @param position Changed Position value
   */

  public synchronized void valueChanged(Position position)
  {
    if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on in-bound facet");
    linearControlConduit.valueChanged(position);
  }


  /* ---------- LinearStateConduit ------------------------------------------- */

  /**
   * This class handles incoming Linear state messages from other
   * Wedges in the Meem.
   * 
   * @author Chris Kakris
   */

  private class LinearStateConduit implements Linear
  {
    /**
     * Respond to a value change by simply passing the change on to any Meems
     * depending upon the out-bound Linear Facet.
     *
     * @param newPosition Changed Position value
     */

    public synchronized void valueChanged(Position newPosition)
    {
      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "valueChanged() - invoked on LinearStateConduit");
      position = newPosition;
      linearClient.valueChanged(position);
    }
  }
}
