/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.emulator;

import java.util.Properties;

/**
 * <p>A TelnetServerEmulator is used to emulate the functionality of a real
 * telnet-like server. A telnet-like server listens to incoming socket
 * connections on a specific port and establishes a new Socket instance
 * to process that connection. It typically uses a human-readable protocol
 * and follows a request/response type of interaction.</p>
 * 
 * <p>Implementations of this interface should be able to be repeatedly
 * stopped and started and configured. You may write the emulator to accept
 * configuration changes either when the emulator is in a stopped state or
 * while it is currently active.</p>
 *
 * @author Chris Kakris
 */
public interface TelnetServerEmulator
{
  /**
   * Invoked to start the TelnetServer listening on its port(s) and accept
   * incoming connections.
   * 
   * @throws EmulatorException  If the emulator was unable start normally
   */
  public void startup() throws EmulatorException;

  /**
   * Call this method to change any of the configuration properties for the
   * emulator.
   * You should call the configure() method before attempting to start the
   * emulator but that's really up to the author of the emulator.
   * 
   * @param properties  A Properties instance containing configuration changes
   * @throws EmulatorException  Thrown if the emulator was unable to process a configuration property
   */
  public void configure(Properties properties) throws EmulatorException;

  /**
   * Process a request and return an optional Response. Note that in most
   * cases the emulator will be communicated with by establishing a socket
   * connection to it and using that emulator's protocol.
   * 
   * @param request  The request for the emulator
   * @return  An optional Response that resulted from processing the request
   * @throws EmulatorException  If there was a problem processing the request
   */
  public Response processRequest(Request request) throws EmulatorException;

  /**
   * Shutdown the emulator so that it stops processing new incoming connections
   * and closes all existing ones.
   */
  public void shutdown();
}
