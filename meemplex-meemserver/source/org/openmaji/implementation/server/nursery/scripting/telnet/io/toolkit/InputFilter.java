/*
 * @(#)InputFilter.java
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
 * that should be registerable as input filter for
 * Editfield instances.
 *
 * @author Dieter Wimberger
 * @version 1.0 09/03/2000
 */
public interface InputFilter {
   
   /**
    * Method that is called by the Editfield this InputFilter instance
    * has been registered with on bytes read from the Stream.
    * This will give the InputFilter the opportunity to 
    *
    * @param key Integer representing a byte or a constant defining
    *			 a special key.
    * 
    * @return int Representing the byte to be used by the Editfield.
    */
    public int filterInput(int key);
	
	 public final int INPUT_HANDLED=-2000;
	 public final int INPUT_INVALID=-2001;

}//Interface InputFilter

