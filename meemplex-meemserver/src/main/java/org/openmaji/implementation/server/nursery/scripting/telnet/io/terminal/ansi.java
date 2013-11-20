/*
 * @(#)ansi.java
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
 * Implements ansi terminal support, which
 * is defined as vt100 with graphics rendition
 * capabilities within this library.
 * 
 * @author Dieter Wimberger
 * @version 1.0 28/08/2000 
 */
 public class ansi extends BasicTerminal {
	
	public boolean supportsSGR(){
		return true;
    }//supportsSGR      

 	public boolean supportsScrolling(){
 		return true;
	}//supportsSoftScroll

}//class ansi
