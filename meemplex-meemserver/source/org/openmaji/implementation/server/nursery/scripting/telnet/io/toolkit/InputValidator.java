/*
 * @(#)InputValidator.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

/*
 * This code is based upon code from the TelnetD library.
 * http://sourceforge.net/projects/telnetd/
 * Used under the BSD license.
 */


package org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit;

/**
 * Interface that has to be implemented by any class
 * that should be registerable as input validator for
 * Editfield instances.
 *
 * @author Dieter Wimberger
 * @version 1.0 29/04/2000
 */
 public interface InputValidator {

	/**
	 * Method that is called by the Editfield the InputValidator instance
	 * has been registered with on the Users signal that
	 * he finished editing his/her input.
  	 * This gives the InputValidator the opportunity to 
	 * check if the users input is valid.
	 *
	 * @param str String representing the Editfields value.
	 * 
	 * @return boolean representing if input was valid or not (and user has to reedit).
	 */
	 public boolean validate(String str);

 }//interface InputValidator