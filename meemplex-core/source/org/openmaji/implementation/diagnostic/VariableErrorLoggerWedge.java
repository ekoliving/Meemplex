/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.diagnostic;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.Wedge;

/**
 * This diagnostic Wedge logs the value placed on it's inbound
 * variable facet as an ERROR level log message.
 * 
 * @author Ben Stringer
 */

public class VariableErrorLoggerWedge implements Variable, Wedge
{
  private static Logger logger = Logger.getAnonymousLogger();


  public void valueChanged(Value value)
  {
	  logger.log(Level.WARNING, value.toString());
  } 
}
