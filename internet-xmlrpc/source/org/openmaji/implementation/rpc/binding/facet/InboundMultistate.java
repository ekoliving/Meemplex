/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.Vector;

import org.openmaji.common.AbstractState;
import org.openmaji.common.Multistate;
import org.openmaji.common.State;
import org.openmaji.rpc.binding.InboundBinding;

public class InboundMultistate extends InboundBinding
{
  public InboundMultistate()
  {
    setFacetClass(Multistate.class);
  }

  public void addMultistateFacet(Multistate listener)
  {
    addListener(listener);
  }

  public void removeMultistateFacet(Multistate listener)
  {
    removeListener(listener);
  }

  protected void invoke(String method, Object[] params)
  {
    if ( "valueChanged".equals(method) )
    {
      Vector vector = (Vector) params[0];
      int numberOfStates = vector.size() - 1;
      String[] availableStates = new String[numberOfStates];
      for ( int i=0; i<availableStates.length; i++ )
      {
        availableStates[i] = (String) vector.get(i+1);
      }
      String initialState = (String) vector.get(0);
      State state = new SimpleState(initialState,availableStates);
      ( (Multistate) proxy ).stateChanged(state);
    }
  }

  /* ------------------------------------------------------------------------ */

  private class SimpleState extends AbstractState implements Serializable
  {
		private static final long serialVersionUID = 0L;
    public SimpleState(String state, String[] availableStates)
    {
      super.availableStates = availableStates;
      setState(state);
    }

    public void setState(String state)
    {
      super.state = state;
    }
  }
}
