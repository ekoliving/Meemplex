/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.emulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple thread that creates a ServerSocket to listen for incoming socket
 * connections. Whenever a new connection is made a new Socket is created
 * and the processSocket() method is invoked allowing subclasses to perform
 * protocol specific processing. This class is designed to be extended to make
 * it easy to write servers to accept and process incoming socket connections.
 *
 * @author Chris Kakris
 */
public class SimpleServerThread extends Thread
{
  private static Logger logger = Logger.getAnonymousLogger();

  protected volatile boolean finished = false;
  protected volatile ServerSocket serverSocket = null;
  protected final Set<Socket> sockets = Collections.synchronizedSet(new HashSet<Socket>());

  /**
   * Constructs an instance of a thread with the specified name
   * and creates a new ServerSocket instance listening on the specified port.
   * 
   * @param name  The name of the thread
   * @param port  The port the ServerSocket should listen on for incoming connections
   * @throws EmulatorException If an exception occurs opening the socket
   */
  public SimpleServerThread(String name, int port) throws EmulatorException
  {
    super(name);
    try
    {
      serverSocket = new ServerSocket(port);
    }
    catch ( IOException ex )
    {
      throw new EmulatorException(ex.getMessage());
    }
  }

  /**
   * Waits for incoming socket connections and process them. Each new Socket
   * instance is kept track of so that when the shutdown() method is invoked
   * all the created sockets can be closed and discarded. Once a new connection
   * is accepted and a new Socket is created this method calls processSocket().
   */
  public void run()
  {
    while ( ! finished )
    {
      try
      {
        Socket socket = serverSocket.accept();
        sockets.add(socket);
        processSocket(socket);
      }
      catch ( SocketException ex )
      {
        // Caused by a call to close() so ignore it and fall out of while loop
      }
      catch ( IOException ioException )
      {
        logger.log(Level.INFO, "Couldn't accept Socket connection: " + ioException.getMessage());
      }
    }
  }

  /**
   * Override this method and it will be invoked each time a new
   * socket connection is made. Note that a reference to the Socket
   * is kept by this class so if you have finished with the socket
   * you should invoke discardSocket().
   * 
   * @param socket  The newly created socket
   */
  public void processSocket(Socket socket)
  {
  }

  /**
   * Call this method once you have finished processing a Socket. This method
   * will perform the close() operation for you and dispose of it.
   * 
   * @param socket  The Socket to discard
   */
  public void discardSocket(Socket socket)
  {
    if ( serverSocket != null )
    {
      try
      {
        serverSocket.close();
      }
      catch ( IOException ex ) { /* Ignore */ }
    }

    sockets.remove(socket);
  }

  /**
   * Close the ServerSocket instance so that no new connections will be accepted
   * and then close all the open sockets.
   */
  public void shutdown()
  {
    finished = true;

    if ( serverSocket != null )
    {
      try
      {
        serverSocket.close();
        serverSocket = null;
      }
      catch ( IOException ex ) { /* Ignore */ }
    }
    
    for ( Iterator<Socket> iteration = sockets.iterator(); iteration.hasNext(); )
    {
      Socket socket = iteration.next();
      try
      {
        socket.close();
      }
      catch ( IOException ex ) { /* Ignore it */ }
    }
  }
}
