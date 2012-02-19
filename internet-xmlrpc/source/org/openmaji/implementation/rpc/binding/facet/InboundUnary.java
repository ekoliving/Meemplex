/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import org.openmaji.common.Unary;
import org.openmaji.rpc.binding.InboundBinding;

public class InboundUnary extends InboundBinding
{
  public InboundUnary()
  {
    setFacetClass(Unary.class);
  }

  public void addUnaryFacet(Unary listener)
  {
    addListener(listener);
  }

  public void removeUnaryFacet(Unary listener)
  {
    removeListener(listener);
  }

  protected void invoke(String method, Object[] params)
  {
    if ( "valueChanged".equals(method) )
    {
      ( (Unary) proxy ).valueChanged();
    }
  }
}