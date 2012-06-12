package org.openmaji.implementation.server.utility;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitManagerWedge;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;

/**
 * <p>
 * PropertiesLoader is used by Maji and InterMajik during their launch process to load required properties.
 * The behavior of PropertiesLoader (in order) is as follows:
 * </p>
 * <ol>
 * <li>The utility UserProperties is used to load the user's global personal preferences and make them System properties</li>
 * <li>PropertiesLoader will expect the System property PropertiesLoader.PROPERTY_KEY to be defined. If it is not defined an error message will be logged and the launch sequence aborted.</li>
 * <li>The System property PropertiesLoader.PROPERTY_KEY will be expected to be set to a filename using an absolute path. If that file can not be read an error message will be logged and the launch sequence will be aborted.</li>
 * <li>Properties in that file will be merged into the System properties. They will _not_ override pre-existing System properties</li>
 * <li>That file may recursively include other properties files using one of the 'org.openmaji.includeFileXXX' keys</li>
 * <li>All of the properties declared in these recursively loaded files will be added to the System properties and will _not_ override pre-existing System properties</li>
 * </ol>
 *  
 * @author Chris Kakris
 */

public class PropertiesLoader
{
	private static final Logger logger = Logger.getAnonymousLogger();
	
  public static final String PROPERTY_KEY = "org.openmaji.properties";

  private static final String PROPERTY_LOGIN_CONFIG = "java.security.auth.login.config";

  private static final String DEFAULT_PROPERTIES = "conf/maji.properties";

  /**
   * Load the properties from the file specified by the PropertiesLoader.PROPERTY_KEY
   * System property and make all those properties System properties.
   * 
	 * @throws IOException When either PropertiesLoader.PROPERTY_KEY isn't set or the file it specifies doesn't exist
	 */

	public static void load() throws IOException
  {
    Properties userProperties = UserProperties.getInstance().getProperties();
    PropertyUtility.mergeSystemProperties(userProperties);

    String propertiesFilename = System.getProperty(PROPERTY_KEY);
    if ( propertiesFilename == null || propertiesFilename.length() == 0 )
    {
    	System.setProperty(PROPERTY_KEY, DEFAULT_PROPERTIES);
    	propertiesFilename = DEFAULT_PROPERTIES;
    }

    // make sure the properties filename is absolute. prepend maji.home is necessary
    File propertiesFile = new File(propertiesFilename);
    if (!propertiesFile.isAbsolute()) {
    	// make the properties file absolute
    	String majiHome = getMajiHome();
    	if (majiHome != null) {
    		propertiesFilename = makeAbsolute(propertiesFilename);
    		propertiesFile = new File(propertiesFilename);
    	}
    }
    
    Properties properties = PropertyUtility.loadRecursively(propertiesFilename);
    PropertyUtility.mergeSystemProperties(properties);
    
    // make login config absolute
    String loginConfigUrl = System.getProperty(PROPERTY_LOGIN_CONFIG);
    try {
	    URI uri = new URI(loginConfigUrl);
	    File configFile = new File(uri.getSchemeSpecificPart());
	    if (configFile.isAbsolute() == false) {
	    	uri = new URI(uri.getScheme(), uri.getHost(), makeAbsolute(uri.getSchemeSpecificPart()), uri.getFragment());
	    	System.setProperty(PROPERTY_LOGIN_CONFIG, uri.toString());
	    }
    }
    catch (URISyntaxException e) {
    	logger.log(Level.INFO, "Login config property. " + loginConfigUrl + ", is not a valid URI");
    }
   
    // make meemstore location absolute
    String meemStoreLocation = System.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
    File meemStoreFile = new File(meemStoreLocation);
    if (meemStoreFile.isAbsolute() == false) {
    	System.setProperty(MeemStoreWedge.MEEMSTORE_LOCATION, makeAbsolute(meemStoreFile.getPath()));
    }
    
    // make meemkit location absolute
    String meemkitsLocation = System.getProperty(MeemkitManagerWedge.PROPERTY_MEEMKIT_MANAGER_DIRECTORY);
    File meemkitsFile = new File(meemkitsLocation);
    if (meemkitsFile.isAbsolute() == false) {
    	System.setProperty(MeemkitManagerWedge.PROPERTY_MEEMKIT_MANAGER_DIRECTORY, makeAbsolute(meemkitsFile.getPath()));
    }
  }
	
	private static String makeAbsolute(String path) {
		return getMajiHome() + System.getProperty("file.separator") + path;
	}
	
	private static String getMajiHome() {
	    String majiHome = System.getProperty(Common.PROPERTY_MAJI_HOME);
	    if (majiHome != null) {
	    	// make sure majiHome is an absolute path
		    File majiHomeFile = new File(majiHome);
		    if (majiHomeFile.isAbsolute() == false) {
		    	try {
		    		majiHome = majiHomeFile.getCanonicalPath();
		    	}
		    	catch (IOException e) {
		    		majiHome = majiHomeFile.getAbsolutePath();
		    	}
		    	System.setProperty(Common.PROPERTY_MAJI_HOME, majiHome);
		    }
	    }
	    return majiHome;
	}
}
