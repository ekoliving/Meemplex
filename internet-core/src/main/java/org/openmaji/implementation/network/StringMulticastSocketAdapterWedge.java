/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Implement Variable inbound Facet for transmission of String values.
 * - Make MAXIMUM_BUFFER_SIZE and MULTICAST_SOCKET_TIMEOUT configurable.
 * - Consider reworking to use Variable byte[] rather than a String.
 * - Include voting to correctly maintain the Meem's LifeCycleState.
 * - Persist the MeemContent whenever the SocketConfiguration changes.
 * - Replace "ErrorHandler reference" hack with a proper Conduit.
 * - Consider throwing RuntimeException instead of logger.log(Level.WARNING,),
 *   once thread decoupling is correctly utilized everywhere.
 * - conclude() effectively blocks for MULTICAST_SOCKET_TIMEOUT seconds.
 */

package org.openmaji.implementation.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.StringValue;
import org.openmaji.common.Variable;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.IntegerConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.meem.wedge.error.ErrorHandler;

/**
 * <p>
 * The StringMulticastSocketAdapterWedge effectively adapts a String Variable
 * into a multicast UDP socket.
 * </p>
 * <p>
 * This implementation allows for the dynamic reconfiguration of the multicast
 * socket, via the SocketConfiguration inbound Facet.  Configuration changes
 * are send via the SocketConfiguration outbound Facet.
 * </p>
 * <p>
 * Due to the implementation approach, this Meem can take up to
 * MULTICAST_SOCKET_TIMEOUT milliseconds, e.g 1 second to conclude
 * its operations, when take from the READY state to the LOADED state.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not consider yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Wedge
 */

public class StringMulticastSocketAdapterWedge
  implements Wedge, Runnable {

  private static Logger logger = Logger.getAnonymousLogger();

  private static final int MAXIMUM_BUFFER_SIZE = 32; // bytes

  private static final int MULTICAST_SOCKET_TIMEOUT = 1000; // milliseconds

  /**
   * Reference to Meem internal structures (initialized by Maji framework)
   */

  public MeemContext meemContext;

  public String host = "239.6.20.71";

  public int port = 4378;

  public Vote lifeCycleControlConduit = null;

  public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

  /**
   * The conduit through which we are alerted to life cycle changes
   */
  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
  
  private MulticastSocket multicastSocket = null;

  private volatile boolean terminated = false;

  public Variable variableStateConduit = null;

  /**
   * Long-term thread for listening to incoming multicast packets
   */

  private Thread workerThread = null;

  /**
   * ErrorHandlerConduit for reporting errors to ErrorHandler Wedge
   */

  public ErrorHandler errorHandlerConduit;

  /* ---------- ConfigurationChangeHandler listener ------------------------------- */

  public transient ConfigurationSpecification hostSpecification = ConfigurationSpecification.create("The multicast address to connect to");
  public transient ConfigurationSpecification portSpecification = new IntegerConfigurationSpecification("The port number on the remote host to connect to",0);

  public void setHost(String value) {
    this.host = value;
  }

  public void setPort(Integer value) {
    this.port = value.intValue();
  }

  /* ---------- Runnable method(s) ------------------------------------------- */

  /**
   *
   */

  public void run() {

    while (terminated == false) {
      try {
        byte[] buffer = new byte[MAXIMUM_BUFFER_SIZE];

        DatagramPacket datagramPacket =
          new DatagramPacket(buffer, buffer.length);

        multicastSocket.receive(datagramPacket);

        variableStateConduit.valueChanged(
          new StringValue(
            new String(datagramPacket.getData(), 0, datagramPacket.getLength())
          )
        );
      }
      catch (SocketTimeoutException socketTimeoutException) {
      // This catch block intentionally left blank.
      // Periodic wake-up call, just to see if "terminated".
      }
      catch (IOException ioException) {
        errorHandlerConduit.thrown(ioException);
        lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(),false);
        terminated = true;
      }
    }

    logger.log(Level.INFO,  "run() - Multicast receiver thread concluded on UDP port: " + port);
  }

  /**
   * Allocate resources, so that this Meem can become READY.
   * This involves initializing a new MulticastSocket and worker thread.
   */
  public synchronized void commence() {

    try {
      // -----------------------------------------------
      // Set-up MulticastSocket to listen to X-10 events

      multicastSocket = new MulticastSocket(port);
      multicastSocket.joinGroup(InetAddress.getByName(host));
      multicastSocket.setSoTimeout(MULTICAST_SOCKET_TIMEOUT);
      
      // ----------------------------------------------------------
      // Run the "worker" as a background thread, so we don't block
      
      workerThread = new Thread(this, "StringMulticastSocketAdapterWorker");
      workerThread.setDaemon(true);
      workerThread.start();
      lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(),true);
      logger.log(Level.INFO,  "commence() - Multicast receiver started on UDP port: " + port);
    }
    catch (IOException ioException) {
      errorHandlerConduit.thrown(ioException);
      lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(),false);
    }
  }

  /**
   * Deallocate resources, so that this Meem can become LOADED.
   * This involves terminating the worker thread.
   */

  public synchronized void conclude() {
    if (workerThread != null) {
      terminated = true;

      // -------------------------------------------------------------------
      // Wait for worker Thread, takes MULTICAST_SOCKET_TIMEOUT milliseconds

      try {
        workerThread.join(MULTICAST_SOCKET_TIMEOUT * 2);
      }
      catch (InterruptedException interruptedException) {}

      if (workerThread.isAlive()) {
    	  logger.log(Level.INFO, "conclude() - Unable to terminate Multicast worker Thread");
      }
      else {
        try {
          multicastSocket.leaveGroup(InetAddress.getByName(host));
          multicastSocket.close();
        }
        catch (IOException ioException) {}
        finally {
          multicastSocket = null;
          workerThread = null;
        }
      }
    }
  }

}
