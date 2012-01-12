package org.openmaji.implementation.server.revelation;

// TODO [christos] This class should not have the username/password of the MeemServer hard-coded

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Properties;

import org.openmaji.implementation.server.nursery.scripting.telnet.TelnetServerWedge;
import org.openmaji.implementation.server.utility.PropertyUtility;


/**
 * An implementation of ControlMeemServer that uses the BSFMeem running
 * in a MeemServer to control that MeemServer.
 * 
 * @author Chris Kakris
 */

public class BSHControlMeemServer implements ControlMeemServer
{
  private String hostname;
  private int port;
  private Socket socket = null;
  private OutputStream out = null;
  private InputStream in = null;

  /**
   * An instance of this class can be used to connect to a MeemServer
   * using its beanShell facility and send various control and query messages.
   * 
   * @param hostname The host on which the MeemServer is runnning
   * @param port The portnumber that the MeemServer's beanShell facility is listening on
   */

  public BSHControlMeemServer(String hostname, String port)
  {
    this.hostname = hostname;
    this.port = Integer.parseInt(port);
  }

  /**
   * An instance of this class can be used to connect to a MeemServer
   * using its beanShell facility and send various control and query messages.
   * 
   * @param hostname The host on which the MeemServer is runnning
   * @param port The portnumber that the MeemServer's beanShell facility is listening on
   */

  public BSHControlMeemServer(String hostname, int port)
  {
    this.hostname = hostname;
    this.port = port;
  }

  /**
   * Terminates the MeemServer by logging into the BSFMeem and
   * invoking the 'exit()' function.
   * 
   * @throws IOException If an error occurs while communicating with the MeemServer
   */

  public void terminate() throws IOException
  {
    connectToServer();

    try
    {
      sendMessage("system\nsystem99\n");
      // Sleep for a couple of seconds to make sure our login has completed
      try { Thread.sleep(1000); } catch (InterruptedException e) { /* Ignore */ }
      sendMessage("help();"); // Don't ask me why....
      sendMessage("shutdown();");
    }
    finally
    {
      disconnectFromServer();
    }
  }

  /**
   * This method returns the current status of the MeemServer.
   * 
   * @throws IOException If an error occurs while communicating with the MeemServer
   */

  public int getStatus() throws IOException
  {
    System.err.println("Not implemented yet");
    System.exit(1);
    
    return ControlMeemServer.UNKNOWN;
  }
  
  /**
   * Establish a connection to the MeemServer using a socket connection
   * and open an input and an output stream over which to communicate.
   * 
   * @throws IOException If an error occurs while establishing a connection
   */

  private void connectToServer() throws IOException
  {
    socket = new Socket(hostname, port);
    out = socket.getOutputStream();
    in = socket.getInputStream();
  }
  
  /**
   * Sends the specified message to the MeemServer.
   * 
   * @param message The message to send to the server
   * @return A String containing the response made by the MeemServer
   * @throws IOException If an error occurs while communicating with the MeemServer
   */

  private String sendMessage(String message) throws IOException
  {
    out.write(message.getBytes());
    out.flush();
    byte[] bytesRead = new byte[8888]; 
    in.read(bytesRead);
    return new String(bytesRead);
  }

  /**
   * Close the socket and the input and output streams to the MeemServer.
   */

  private void disconnectFromServer()
  {
    try
    {
      in.close();
      out.close();
      socket.close();
    }
    catch ( Exception ex )
    {
      // Ignore any exceptions while we're closing down the socket
    }
  }

  /**
   * <p>Used to terminate MeemServers that are running on localhost
   * using the Beanshell interface from the command line. For example
   * you can invoke it like this:</p>
   * 
   * <pre>
   *   java org.openmaji.implementation.server.revelation.BSHControlMeemServer /home/christos/.maji-01.properties
   * </pre>
   * 
   * @param args The list of MeemServer properties files
   */

  public static void main(String[] args)
  {
    if ( args.length == 0 )
    {
      System.err.println("No properties file specified");
      System.exit(1);
    }
    
    for ( int i=0; i<args.length; i++ )
    {
      try
      {
        Properties properties = PropertyUtility.loadRecursively(args[i]);
        String port = getBeanshellPort(properties);
        BSHControlMeemServer cms = new BSHControlMeemServer("localhost",port);
        cms.terminate();
      }
      catch ( IOException ex )
      {
        System.err.println(ex.getMessage());
        System.exit(1);
      }
    }
  }

  private static String getBeanshellPort(Properties properties)
  {
    Iterator iterator = properties.keySet().iterator();
    while ( iterator.hasNext() )
    {
      String keyName = (String) iterator.next();
      if ( keyName.startsWith(TelnetServerWedge.TELNET_SERVER_SESSION_PROPERTIES) )
      {
        int index = keyName.lastIndexOf(".");
        return keyName.substring(index+1);
      }
    }
    
    System.err.println("Could not find beanshell port number");
    System.exit(1);
    return null; // stupid compiler
  }
}
