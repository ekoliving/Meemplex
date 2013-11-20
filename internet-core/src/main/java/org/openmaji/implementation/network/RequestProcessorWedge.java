/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

import org.openmaji.automation.controller.RequestProcessor;
import org.openmaji.automation.device.Command;
import org.openmaji.common.StringValue;
import org.openmaji.common.Variable;
import org.openmaji.meem.Wedge;

/**
 * The RequestProcessorWedge is a generic component that accepts
 * commands on its RequestProcessor inbound Facet and send them out
 * its Variable outbound Facet.
 *
 * @author  Christos Kakris
 */

public class RequestProcessorWedge implements Wedge, RequestProcessor
{
  public Variable request;

  /* --------- RequestProcessor interface ------------- */

  public void process(Command command)
  {
    String commandString = command.getCommand();
    StringValue stringValue = new StringValue(commandString);
    request.valueChanged(stringValue);
  }
}
