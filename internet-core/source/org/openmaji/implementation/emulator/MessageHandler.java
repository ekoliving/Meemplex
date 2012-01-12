/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.emulator;

/**
 * This interface is used by message handlers that are registered
 * by emulators. Their role is to process incoming messages and
 * generate sresponse according to the protocol being emulated.
 *
 * @author Chris Kakris
 */
public interface MessageHandler
{
  /**
   * Handle an incoming message from a client to a telnet-like server emulator
   * and return the corresponding response.
   * 
   * @param message  The request message from a client
   * @return  The response to be sent back to the client
   */
  public String handleMessage(String message);
}
