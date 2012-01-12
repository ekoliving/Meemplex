/*
 * @(#)vt100.java
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

package org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal;

/**
 * Implements VT 100 terminal support, as original,
 * without graphics rendition capabilities.
 *
 * @author Dieter Wimberger
 * @version 1.0 28/08/2000 
 */
 public class vt100 extends BasicTerminal {	
 
 	 public boolean supportsSGR(){
        return false;
	 }//supportsSGR      

	 public boolean supportsScrolling(){
 		return true;
 	 }//supportsSoftScroll
 	 
}//class vt100
