/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;

import org.openmaji.common.Multistate;
import org.openmaji.common.State;
import org.openmaji.rpc.binding.OutboundBinding;

public class OutboundMultistate extends OutboundBinding implements Multistate
{
  public OutboundMultistate()
  {
    setFacetClass(Multistate.class);
  }

  public void stateChanged(State state)
  {
    if ( state == null )
    {
      return;
    }

    ArrayList<String> vector = new ArrayList<String>();
    vector.add(state.getState());
    String[] availableStates = state.getAvailableStates();
    for ( int i=0; i<availableStates.length; i++ )
    {
      vector.add(availableStates[i]);
    }

    send("valueChanged", new Serializable [] { vector });
  }
}
