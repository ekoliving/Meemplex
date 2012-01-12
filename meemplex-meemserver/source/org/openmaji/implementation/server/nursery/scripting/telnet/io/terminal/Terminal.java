/*
 * @(#)Terminal.java
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
 * On top of the telnet protocol communication layer
 * there is a layer for terminal communication. This layer
 * is represented by so called escape sequences that are filtered
 * and interpreted. <br>
 * This interface defines abstracts filtering and translation
 * methods so that the TerminalIO is independent of a specific
 * terminal implementation.
 *
 * @author Dieter Wimberger 
 * @version 1.0 28/08/2000  
 * 
 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO
 */ 
 public interface Terminal { 
				
	/** 
	 * Translates a control character into terminal independent 
	 * representation.
	 *
	 * @param byteread int read from the input stream.
	 * @return the read int or the internal control character definition.  
	 */ 
	 public int translateControlCharacter(int byteread);
	 
	/**
	 * Translates an escape sequence into a terminal independent
	 * representation.
	 * 
	 * @param buffer array of integers containing a escape sequence.
	 * @return the terminal independent representation. 
	 */  
	 public int translateEscapeSequence(int[] buffer);
	 

	/**
	 * Returns a terminal dependent escape sequence for
	 * a given defined erase function:
	 * <ul>
	 *  <li> erase to end of line (EEOL)
	 *  <li> erase to begin of line (EBOL)
	 *  <li> erase entire line (EEL)
	 *  <li> erase to end of screen (EEOS)
	 *  <li> erase to beginning of screen (EBOS)
	 *  <li> erase entire screen (EES)
	 * </ul>
	 *
	 * @param eraseFunc representing one of the specified erase functions.
 	 * @return the byte sequence representing the terminal dependent escape sequence.
 	 *
 	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EEOL 	
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EBOL	
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EEL
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EEOS
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EBOS
   * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#EES
	 */	
	 public byte[] getEraseSequence(int eraseFunc);
	
	/**
	 * Returns a terminal dependent escape sequence for a 
	 * given cursor movement.<br>
	 * The directions available are:
	 * <ul>
	 *    <li> up (UP)
	 *	  <li> down (DOWN)
	 *    <li> right (RIGHT)
	 *    <li> left (LEFT)
	 * </ul>
	 *
	 * @param dir Direction of movement.
	 * @param times Number of movements into given direction.
	 *
	 * @return the byte sequence representing the terminal dependent escape sequence.
	 */ 
	 public byte[] getCursorMoveSequence(int dir, int times);
	 
	 
	/**
	 * Returns a terminal dependent escape sequence for
	 * positioning the cursor to a given position:
	 * <ul>
	 *   <li> index 0: row coordinate
	 *   <li> index 1: column coordinate
	 * </ul>
	 * Note that <em>home</em> is a special positioning sequence. It
	 * could result in a special sequence different from the positioning
	 * sequence to 0,0 coordinates.  
	 *
	 * @param pos Position to move the cursor to.
	 * @return the byte sequence representing the terminal dependent escape sequence.
	 * 
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#HOME
	 */ 
	 public byte[] getCursorPositioningSequence(int[] pos);
	 
	 
	/**
	 * Returns the terminal dependent escape sequence for
	 * a given defined special function.<br>
	 * There are two special functions that should be implemented:
	 * <ol>
	 *   <li> store current cursor position (STORECURSOR)
	 *   <li> restore previously stored cursor position (RESTORECURSOR)
	 * </ol>
	 *
	 * @param sequence the special sequence defined as int in TerminalIO.
	 * @return the byte sequence representing the terminal dependent escape sequence.
	 *
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#STORECURSOR
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO#RESTORECURSOR   
	 */ 
	 public byte[] getSpecialSequence(int sequence);
	 
 	/**
 	 * Returns the terminal dependent escape sequence
 	 * for establishing the given scrollmargins.<br>
 	 * <em>Note that this method is experimental and not available
 	 * through the BasicTerminalIO interface yet.</em>
 	 *
 	 * @param topmargin upper border of the scrolling area as row coordinate.
 	 * @param bottommargin lower border of the scrolling area as row coordinate. 
	 * 
	 * @return the byte sequence representing the terminal dependent escape sequence.
	 */
 	 public byte[] getScrollMarginsSequence(int topmargin,int bottommargin);
	 
	/**
	 * Returns the terminal dependent escape sequence
	 * for a given graphics rendition request.<br>
	 * Defined are following types:
	 * <ul>
	 *  <li> foreground color (FCOLOR)<br>
	 * 		 with any color defined in BasicTerminalIO as parameter.
	 *  <li> background color (BCOLOR)<br>
	 *       with any color defined in BasicTerminalIO as parameter.
	 *  <li> character style (STYLE)
	 *		<ul>
	 *			<li> turns on/off italic letters (ITALIC,ITALIC_OFF)
	 *			<li> turns on/off bold letters (BOLD,BOLD_OFF)
	 *			<li> turns on/off blinking letters (BLINK,BLINK_OFF)
	 *          <li> turns on/off underlined letters (UNDERLINED,UNDERLINED_OFF)
	 *		</ul>
	 *  <li> reset of set graphics rendition (RESET)<br>
	 *       which does not need any specific parameter.
	 * 
	 * </ul>
	 * <em>Note that by no means, all terminals will support this.
	 *     Display might differ widely from implementation to implementation.
	 *     Yet this is a more or less complete abstraction of what could be possible.
	 *  </em> 
	 * 
	 *
	 * @param type Type of graphics rendition request. 
 	 * @param param Parameter to the type requested.
	 * 
	 * @return the byte sequence representing the terminal dependent escape sequence.
	 */ 
	 public byte[] getGRSequence(int type,int param);
 
 
 
 	/**
 	 * Returns a "formatted" string containing terminal dependent 
 	 * GR escape sequences (i.e. colors and style).<br>
 	 * The string passed as a parameter should contain the internal
 	 * markup based upon <Ctrl>-<a> inited sequences.<br>
 	 * If the terminal does not support graphics rendition, then
 	 * the markup will be parsed out. Its strongly recommended not to
 	 * send escape sequences a client is unable to understand, even
 	 * if some clients filter themselves.
 	 * 
	 * @param str String to be formatted.
	 * 
	 * @return the string with sequences that will render properly 
	 *         on the terminal.
	 */
	 public String format (String str);
 

	/**
	 * Returns the byte sequence that will init the terminal.<br>
	 * 
	 * @return the byte sequence representing the terminal dependent 
	 *         init escape sequence.
	 */
	 public byte[] getInitSequence();	


	/**
 	 * Returns if the terminal implementation supports
 	 * graphics rendition (i.e. colors and styles).<br>
 	 * 
 	 * @return a boolean that flags if the terminal supports GR (true)
 	 *         or not (false). 
	 */
 	 public boolean supportsSGR();
 	 
 	/**
 	 * Returns if the terminal implementation supports
 	 * scrolling (i.e. setting scroll margins).<br>
 	 * <em>Note that this method is addenum to the experimental and 
 	 * not available getScrollMarginsSequence(int,int).</em>
	 *
 	 * @return a boolean that flags if the terminal supports scrolling (true)
 	 *         or not (false). 
 	 */ 
 	 public boolean supportsScrolling();
	 

	/**
	 * Returns the atomic escape sequence length of the terminal 
	 * implementation as integer (without counting the escape itself).<br>
	 * 
	 * @return the atomic escape sequence length.
	 */
	 public int getAtomicSequenceLength();




//Constants

	/**
	 * <b>End of transmission</b><br>
	 * Ctrl-d, which flags end of transmission, or better said
	 * a client logout request.
	 */
	 public static final byte EOT=4;

	/** 
 	 * <b>BackSpace</b><br>
 	 * The ANSI defined byte code of backspace.
	 */
	 public static final byte BS=8; 

	/** 
	 * <b>Delete</b><br>
	 * The ANSI defined byte code of delete.
	 */
	 public static final byte DEL=127;

	
	/** 
	 * <b>Horizontal Tab</b><br>
	 * The ANSI defined byte code of a horizontal tabulator.
	 */
	 public static final byte HT=9;	
	
	/**
	 * <b>FormFeed</b><br> 
	 * The ANSI defined byte code of a form feed.
	 */
	 public static final byte FF=12;

	/**
	 * <b>SGR Input Key</b><br> 
	 * Ctrl-a as defined byte code. It might be of
	 * interest to support graphics rendition in edit mode,
	 * for the user to create marked up (i.e. formatted)
	 * input for the application context.
	 */
	 public static final byte SGR=1;


	/** 
	 * <b>Cancel</b><br>
	 * The ANSI defined byte code for cancelling an 
	 * escape sequence.
	 */
	 public static final byte CAN=24;
	
	/** 
	 * <b>Escape</b><br>
	 * The ANSI definde byte code of escape.
	 */
	 public static final byte ESC=27;
	
	/**
	 * <b>[ Left Square Bracket</b><br>
	 * The ANSI defined byte code of a left square bracket,
	 * as used in escape sequences.
	 */
	 public static final byte LSB=91;

	
	/**
	 * <b>; Semicolon</b><br>
	 * The ANSI defined byte code of a semicolon, as
	 * used in escape sequences.
	 */
	 public static final byte SEMICOLON=59; 
	
		
	/**
 	 * <b>A (UP)</b><br>
 	 * The byte code of A, as used in escape sequences
 	 * for cursor up.
 	 */	
	 public static final byte A=65;

	/**
 	 * <b>B (DOWN)</b><br>
 	 * The byte code of B, as used in escape sequences
 	 * for cursor down.
 	 */	
  	 public static final byte B=66;

	/**
 	 * <b>C (RIGHT)</b><br>
 	 * The byte code of C, as used in escape sequences
 	 * for cursor right.
 	 */	
  	 public static final byte C=67;
	
	/**
 	 * <b>D (LEFT)</b><br>
 	 * The byte code of D, as used in escape sequences
 	 * for cursor left.
 	 */	
	 public static final byte D=68;
		
	/**
	 * <b>Other characters used in escape sequences.</b>
	 */		  
	 public static final byte E=69; // for next Line (like CR/LF)
	 public static final byte H=72; // for Home and Positionsetting or f	
	 public static final byte f=102;
	 public static final byte r=114;

	/**
	 * <b>Characters needed for erase sequences.</B>
	 */				 
	 public static final byte LE = 75; 	// K...line erase actions related
	 public static final byte SE = 74;  	// J...screen erase actions related 


}//interface Terminal
