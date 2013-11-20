package org.openmaji.implementation.server.revelation;

import java.io.IOException;

/**
 * The interface ControlMeemServer is implemented by classes that can send control
 * messages and query the status of a running MeemServer. One of those control messages
 * is a shutdown command to cleanly terminate a running MeemServer. If a
 * MeemServer is terminated abruptly there is a danger of its MeemSpace
 * becoming incorrectly or incompletely persisted.
 * 
 * @author Chris Kakris
 */

public interface ControlMeemServer
{
  public static final int UNKNOWN = 0;
  public static final int STARTING = 1;
  public static final int RUNNING = 2;
  public static final int TERMINATING = 3;
  public static final int NOT_RUNNING = 4;

  /**
   * Terminate the MeemServer but first allow it to persist its
   * MeemStore.
   * 
   * @throws IOException If an error occurs while communicating with the MeemServer
   */

  public void terminate() throws IOException;

  /**
   * Return the status of the MeemServer.
   * 
   * @throws IOException If an error occurs while communicating with the MeemServer
   */

  public int getStatus() throws IOException;
}