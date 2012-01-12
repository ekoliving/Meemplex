/*
 * @(#)MajiSPI.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.spi;

import java.security.*;


/**
 * Base class for a service provider that can be used to instance objects via the
 * "spi" pattern used in the standard API.
 * <p>
 * The basic idea of the use of the "spi" pattern is to provide a pluggable way of changing
 * the underlying implementations of system wedges and objects with out requiring any
 * changes to application code. Essentially the service provider is invoked when a method
 * on a nested spi class is called and it is responsible for the creation of the object requested.
 * </p>
 * For example, to locate the Meem with the MeemPath "hyperspace:/users":
 * <pre>
 *     Meem m = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/users"));
 * </pre>
 * @author  Andy Gelme
 * @version 1.0
 */

public abstract class MajiSPI implements MajiProvider {

  public static final String MAJI_PROVIDER_CLASSNAME_PROPERTY =
    "org.openmaji.spi.MajiProviderClassName";

/**
 * Default MajiProvider SPI implementation Class name.
 *
 * This MUST be the only reference of any kind in the Maji Component API
 * that isn't contained within the Maji Component API packages.  This is
 * safe, because it is only the default MajiProvider implementation.
 */

  public static final String DEFAULT_MAJI_PROVIDER_CLASSNAME =
    "org.openmaji.implementation.server.spi.MajiServerProvider";

  private static MajiProvider majiProvider = null;

  protected MajiSPI() {
    SecurityManager securityManager = System.getSecurityManager();

    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("majiProvider"));
    }
  }

  public static synchronized MajiProvider provider() {
    if (majiProvider == null) {
      AccessController.doPrivileged(
        new PrivilegedAction<Object>() {
          public Object run() {
            if (loadProviderFromProperty() == false) {
              loadProviderAsService();
            }

            return(null);
          }
        }
      );
    }

    if (majiProvider == null) {
      throw new RuntimeException("Couldn't create MajiProvider: ?");
    }

    return(majiProvider);
  }

  private static boolean loadProviderFromProperty() {
    String majiProviderClassName = System.getProperty(
      MAJI_PROVIDER_CLASSNAME_PROPERTY, DEFAULT_MAJI_PROVIDER_CLASSNAME
    );

    try {
      Class<?> majiProviderClass = Class.forName(majiProviderClassName, true, MajiSPI.class.getClassLoader());
      majiProvider = (MajiProvider) majiProviderClass.newInstance();

      return(true);
    }
    catch (Exception exception) {
      throw new RuntimeException(
        "Couldn't create MajiProvider: " + majiProviderClassName +
        ", Exception: " + exception
      );
    }
/*
    catch (ClassNotFoundException x) {
      throw new ServiceConfigurationError(x);
    }
    catch (IllegalAccessException x) {
      throw new ServiceConfigurationError(x);
    }
    catch (InstantiationException x) {
      throw new ServiceConfigurationError(x);
    }
    catch (SecurityException x) {
      throw new ServiceConfigurationError(x);
    }
 */
  }

  private static boolean loadProviderAsService() {
/*
    Iterator i = Service.providers(SelectorProvider.class,
                 ClassLoader.getSystemClassLoader());
    for (;;) {
      try {
        if (!i.hasNext()) return false;
        provider = (SelectorProvider)i.next();
        return true;
      } catch (ServiceConfigurationError sce) {
        if (sce.getCause() instanceof SecurityException) {
        // Ignore the security exception, try the next provider
          continue;
        }
        throw sce;
      }
    }
*/
    return(false);
  }

  /**
   * Create an object from an underlying implementation where the constructor requires
   * no arguments.
   * 
   * @param specification the class to instance the object from.
   */
  public Object create(
    Class<?> specification) {

    return(create(specification, null));
  }

  /**
   * Create an object from an underlying implementation where the constructor just takes
   * one argument.
   * 
   * @param specification the class to instance the object from.
   * @param arg the object to be passed as an argument.
   */
  public Object create(
    Class<?>  specification,
    Object arg) {

    Object[] args = { arg };

    return(create(specification, args));
  }

  /**
   * Create an object from an underlying implementation where the constructor takes
   * multiple arguments.
   * 
   * @param specification the class to instance the object from.
   * @param args the array of arguments to passed to the constructor.
   */
  public abstract Object create(
    Class<?>    specification,
    Object[] args);
}