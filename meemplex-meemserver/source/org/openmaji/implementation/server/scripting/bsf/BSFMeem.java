/*
 * @(#)BSFMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider implementing ...
 *     BSFMeem.spi.create(
 *       String beanShellDirectory,
 *       String beanShellScriptName,
 *       int    beanShellPort
 *     );
 */

package org.openmaji.implementation.server.scripting.bsf;


import org.apache.bsf.BSFException;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * BSFMeem provides an interactive means of scripting the Maji system.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface BSFMeem {

  /**
   * Default BeanShell directory path (relative)
   */

  public final static String DEFAULT_BEANSHELL_DIRECTORY = "../../beanshell";

  /**
   * Property for specifying the BeanShell directory path (relative)
   */

  public final static String PROPERTY_BEANSHELL_DIRECTORY =
    "org.openmaji.scripting.bsf.beanshell.directory";

  /**
   * Default BeanShell initialization script name
   */

  public final static String DEFAULT_BEANSHELL_SCRIPT = "initialize.bsh";

  /**
   * Property for specifying the BeanShell initialization script
   */

  public final static String PROPERTY_BEANSHELL_SCRIPT =
    "org.openmaji.scripting.bsf.beanshell.scriptName";

  /**
   * Default BeanShell TCP/IP listen port
   */

  public final static int DEFAULT_BEANSHELL_PORT = 6969;

  /**
   * Export an internal Maji object instance.
   *
   * @param name Object exported using this name
   * @param value Object instance to export
   * @param specification Object type
   * @exception BSFException BeanShell couldn't evaluate the variable value
   */

  public void export(
    String name,
    Object value,
    Class  specification)
    throws BSFException;
    
  /**
   * 
   * @param scriptFilename
   * @exception BSFException
   */
  public void source(String scriptFilename)     
  throws BSFException;

/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static BSFMeem create() {
      return((BSFMeem) MajiSPI.provider().create(BSFMeem.class));
    }

    public static String getIdentifier() {
      return("bsfMeem");
    };
  }

/* ---------- Factory specification ---------------------------------------- */

  /**
   * Left for org.openmaji.implementation.test.util.Tester's get find the BSFMeem
   * via the EssentialMeemHelper.  Remove A.S.A.P.
   */

  public final static String IDENTIFIER = "bsfMeem";
}