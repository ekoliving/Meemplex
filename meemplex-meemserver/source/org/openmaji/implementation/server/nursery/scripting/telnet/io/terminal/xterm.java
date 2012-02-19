/*
 * @(#)xterm.java
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

import org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO;

/**
 * This class implements the xterm terminal emulation.
 *
 * @author Dieter Wimberger
 * @version 1.0 06/09/2000 
 */
public class xterm extends BasicTerminal {

	public boolean supportsSGR(){
        return true;
	}//supportsSGR      
	
 	public boolean supportsScrolling(){
 		return true;
	}//supportsScrolling
	
	// this is overridden to get around stupid redhat translating backspace into deletes
	public int translateControlCharacter(int c) {

		switch (c) {
			case DEL :
				return TerminalIO.BACKSPACE;
			case BS :
				return TerminalIO.BACKSPACE;
			case HT :
				return TerminalIO.TABULATOR;
			case ESC :
				return TerminalIO.ESCAPE;
			case SGR :
				return TerminalIO.COLORINIT;
			case EOT :
				return TerminalIO.LOGOUTREQUEST;
			default :
				return c;
		}
	} //translateControlCharacter

}//class xterm
