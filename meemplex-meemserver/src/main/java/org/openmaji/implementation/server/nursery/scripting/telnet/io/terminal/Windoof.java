/*
 * @(#)Windoof.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
 * Implements a special variant which is common on
 * windows plattforms (i.e. the telnet application thats
 * coming with all of those OSes).
 *
 * @author Dieter Wimberger
 * @version 1.0 28/08/2000 
 */
 public class Windoof extends BasicTerminal {

	 public boolean supportsSGR(){
		return false;
	 }//supportsSGR   
	 
	 public boolean supportsScrolling(){
 		return true;
	 }//supportsScrolling
 
}//class Windoof
