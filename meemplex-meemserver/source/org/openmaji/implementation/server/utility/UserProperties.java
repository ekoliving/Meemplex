package org.openmaji.implementation.server.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * Certain Maji properties can be stored in the user's home directory. The filename
 * that these properties are stored in is specified by the system property 'org.openmaji.userProperties'.
 * If this system property isn't set then the filename used is '.maji' or 'maji'.
 * The methods in this class can be used to return those properties and test if specific
 * properties are set.
 * </p>
 * 
 * <p>
 * To use the default properties file use the following code:
 * </p>
 * 
 * <pre>
 * Properties p = UserProperties.getInstance().getProperties();
 * </pre>
 * 
 * <p>
 * To use an alternative properties file:
 * </p>
 * 
 * <pre>
 * Properties p = UserProperties.getInstance("christos.properties").getProperties();
 * </pre>
 * 
 * @author Chris Kakris
 */

public class UserProperties
{
  private static final Logger logger = LogFactory.getLogger();

  public static final String USER_PROPERTIES_FILE = "maji";

  private File file;

  private UserProperties(File file)
  {
    this.file = file;
  }

  /**
   * Creates an instance of this class that represents the default properties file.
   * 
   * @return An instance of this class
   */

  public static UserProperties getInstance()
  {
    String userPropertiesFilename = System.getProperty("org.openmaji.userProperties");
    if ( userPropertiesFilename != null )
    {
      File file = new File(userPropertiesFilename);
      if ( file.exists() == false )
      {
        LogTools.error(logger,"getInstance() - property 'org.openmaji.userProperties' set to non-existent '"+file+"'");
      }
      return new UserProperties(file);
    }

    String userHome = System.getProperty("user.home");
    String fileSeparator = System.getProperty("file.separator");

    // First we try and to see if there is a properties file with a "." prefix
    // which will in most cases be for Unix users

    String defaultFilename = userHome + fileSeparator + "." + USER_PROPERTIES_FILE;
    File file = new File(defaultFilename);
    if ( file.exists() ) return new UserProperties(file);

    // Now try a properties file without the "." prefix for Windows users
    // who don't like .dot files

    defaultFilename = userHome + fileSeparator + USER_PROPERTIES_FILE;
    file = new File(defaultFilename);
    return new UserProperties(file);
  }

  /**
   * Creates an instance of this class with an alternative properties file.
   * 
   * @param file An alternative name for the properties file
   * @return An instance of this class
   */

  public static UserProperties getInstance(File file)
  {
    return new UserProperties(file);
  }
  
  /**
   * This method will return all of those properties in a Properties
   * instance. If the file does not exist or if an error occurs while reading
   * the file this method will return a null.
   * 
   * @return The properties defined in the user's properties file or a null
   */

  public Properties getProperties()
  {
    FileInputStream inputStream;

    try
    {
      inputStream = new FileInputStream(file);
    }
    catch ( FileNotFoundException ex )
    {
      return null;
    }

    Properties properties = new Properties();
    try
    {
      properties.load(inputStream);
    }
    catch ( IOException ex )
    {
      LogTools.error(logger,"getProperties() - unable to read "+file);
      return null;
    }
    finally
    {
      try { inputStream.close(); } catch ( IOException ex ) { /* ignore it */ }
    }
    return properties;
  }
  
  /**
   * This method will return the value of the specified property in
   * the user's property file. If the property is not set, or if the file does
   * not exist, or if an error occurs while reading
   * the file this method will return a null.
   * 
   * @param property The name of the property
   * @return The value of the property or null
   */

  public String getProperty(String property)
  {
    Properties properties = getProperties();
    if ( properties == null ) return null;
    
    String value = properties.getProperty(property);
    return value;
  }
  
  /**
   * This method determines whether the value of the specified property in
   * the user's property file is equal to an expected value. If the property is not set,
   * or if the file does not exist, or if an error occurs while reading
   * the file this method will return false.
   * 
   * @param property The name of the property
   * @param expectedValue The value the property is expected to have
   * @return Whether or not the value of the property is equal to the expected value
   */

  public boolean confirmPropertySetTo(String property, String expectedValue)
  {
    String value = getProperty(property);
    if ( value == null ) return false;
    return value.equals(expectedValue);
  }

  /**
   * Determine if the specified user property is set to true. By 'true' we mean if the
   * value of the property is set to 'yes', 'on', 'true' or '1'. The comparison is made
   * by ignoring the case of the value.
   * 
   * @param property The name of the property
   * @return Whether or not the value of the property is true
   */

  public boolean confirmPropertyTrue(String property)
  {
    String value = getProperty(property);
    if ( value == null ) return false;
    if ( value.equalsIgnoreCase("true") ) return true;
    if ( value.equalsIgnoreCase("yes") ) return true;
    if ( value.equalsIgnoreCase("on") ) return true;
    if ( value.equals("1") ) return true;
    return false;
  }
}
