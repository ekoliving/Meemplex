/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.controller;

import org.openmaji.automation.device.Command;
import org.openmaji.meem.Facet;

/**
 * <p>
 * The RequestProcessor facet is implemented by Wedges that can accept
 * commands to control hardware devices.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface RequestProcessor extends Facet
{
  /**
   * Process this request as appropriate for the network this RequestProcessor
   * is responsible for.
   * 
   * @param command     The command to process.
   */

  public void process(Command command);
}

