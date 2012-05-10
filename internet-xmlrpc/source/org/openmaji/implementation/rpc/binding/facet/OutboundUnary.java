/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.common.Unary;
import org.openmaji.rpc.binding.OutboundBinding;

public class OutboundUnary extends OutboundBinding implements Unary
{
  public OutboundUnary()
  {
    setFacetClass(Unary.class);
  }

  public void valueChanged()
  {
    send("valueChanged",new Serializable[] { new Boolean(true) });  // I think you have to send something
  }
}