/*
 * @(#)TerminalIO.java
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

package org.openmaji.implementation.server.nursery.scripting.telnet.io;

import java.io.OutputStream;

import org.openmaji.implementation.server.nursery.scripting.telnet.TelnetConnection;
import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.Terminal;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.TerminalManager;



/** 
 * Class for Terminal specific I/O. 
 * It represents the layer between the application layer and the generic telnet I/O.
 * Terminal specific I/O is achieved via pluggable terminal classes 
 *  
 * 
 * @author Dieter Wimberger
 * @version 2.0 18/02/2000 
 *
 * @see org.openmaji.implementation.server.nursery.scripting.telnet 
 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.Terminal 
 * 
 * <LI>
 * Clean up terminal setup (dynamic loading from properties etc.) 
 */
public class TerminalIO implements BasicTerminalIO {

	//Associations
	private TelnetIO telio; //low level I/O
	private TelnetConnection telnetConnection; //the connection this instance is working for
	private Terminal terminal; //active terminal object

	//Members		
	private boolean acousticSignalling; //flag for accoustic signalling
	private boolean autoflush; //flag for autoflushing mode

	/** 
	 * Constructor of the TerminalIO class. 
	 * 
	 * @param telnetConnection Connection the instance will be working for	
	 */
	public TerminalIO(TelnetConnection telnetConnection) {
		this.telnetConnection = telnetConnection;
		acousticSignalling = true;
		autoflush = true;

		//create a new telnet io
		telio = new TelnetIO(telnetConnection);

		//set default terminal
		setDefaultTerminal();
	} 

	/************************************************************************
	 * Visible character I/O methods   				                        *
	 ************************************************************************/

	/**
	 * Read a single character and take care for terminal function calls.
	 *
	 * @return <ul>
	 *			<li>character read
	 *			<li>IOERROR in case of an error
	 *			<li>DELETE,BACKSPACE,TABULATOR,ESCAPE,COLORINIT,LOGOUTREQUEST
	 *			<li>UP,DOWN,LEFT,RIGHT
	 *		   </ul>		
	 */
	public int read() {
		int i = telio.read();
		//translate possible control sequences
		i = terminal.translateControlCharacter(i);

		//catch & fire a logoutrequest event
		if (i == LOGOUTREQUEST) {
			telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_LOGOUTREQUEST));
			i = HANDLED;
		} else if (i > 256 && i == ESCAPE) { //translate an incoming escape sequence
			i = handleEscapeSequence(i);
		}

		//return i holding a char or a defined special key	
		return i;
	} //read

	public void write(char ch) {
		telio.write(ch);
		if (autoflush) {
			flush();
		}
	} //write(char)

	public void write(String str) {
		telio.write(terminal.format(str));
		if (autoflush) {
			flush();
		}
	} //write(String)

	/*** End of Visible character I/O methods  ******************************/

	/************************************************************************
	 * Erase methods							                            *
	 ************************************************************************/

	public void eraseToEndOfLine() {
		doErase(EEOL);
	} //eraseToEndOfLine

	public void eraseToBeginOfLine() {
		doErase(EBOL);
	} //eraseToBeginOfLine

	public void eraseLine() {
		doErase(EEL);
	} //eraseLine

	public void eraseToEndOfScreen() {
		doErase(EEOS);
	} //eraseToEndOfScreen

	public void eraseToBeginOfScreen() {
		doErase(EBOS);
	} //eraseToBeginOfScreen

	public void eraseScreen() {
		doErase(EES);
	} //eraseScreen

	private void doErase(int funcConst) {

		telio.write(terminal.getEraseSequence(funcConst));
		if (autoflush) {
			flush();
		}
	} //erase

	/*** End of Erase methods  **********************************************/

	/************************************************************************
	 * Cursor related methods							                    *
	 ************************************************************************/

	public void moveCursor(int direction, int times) {

		telio.write(terminal.getCursorMoveSequence(direction, times));
		if (autoflush) {
			flush();
		}
	} //moveCursor

	public void moveLeft(int times) {
		moveCursor(LEFT, times);
	} //moveLeft

	public void moveRight(int times) {
		moveCursor(RIGHT, times);
	} //moveRight

	public void moveUp(int times) {
		moveCursor(UP, times);
	} //moveUp

	public void moveDown(int times) {
		moveCursor(DOWN, times);
	} //moveDown

	public void setCursor(int row, int col) {
		int[] pos = new int[2];
		pos[0] = row;
		pos[1] = col;
		telio.write(terminal.getCursorPositioningSequence(pos));
		if (autoflush) {
			flush();
		}
	} //setCursor

	public void homeCursor() {
		telio.write(terminal.getCursorPositioningSequence(HOME));
		if (autoflush) {
			flush();
		}
	} //homeCursor

	public void storeCursor() {
		telio.write(terminal.getSpecialSequence(STORECURSOR));
	} //store Cursor

	public void restoreCursor() {
		telio.write(terminal.getSpecialSequence(RESTORECURSOR));
	} //restore Cursor

	/*** End of cursor related methods **************************************/

	/************************************************************************
	 * Special terminal function methods							        *
	 ************************************************************************/

	public void setSignalling(boolean bool) {
		acousticSignalling = bool;
	} //setAcousticSignalling

	public boolean isSignalling() {
		return acousticSignalling;
	} //isAcousticSignalling

	/** 
	 * Method to write the NVT defined BEL onto the stream. 
	 * If signalling is off, the method simply returns, without
	 * any action.
	 */
	public void bell() {
		if (acousticSignalling) {
			telio.write(BEL);
		}
		if (autoflush) {
			flush();
		}
	} //bell

	/**
	 * EXPERIMENTAL, not defined in the interface.
	 */
	public boolean defineScrollRegion(int topmargin, int bottommargin) {
		if (terminal.supportsScrolling()) {
			telio.write(terminal.getScrollMarginsSequence(topmargin, bottommargin));
			flush();
			return true;
		} else
			return false;
	} //defineScrollRegion

	public void setForegroundColor(int color) {
		if (terminal.supportsSGR()) {
			telio.write(terminal.getGRSequence(FCOLOR, color));
			if (autoflush) {
				flush();
			}
		}
	} //setForegroundColor

	public void setBackgroundColor(int color) {
		if (terminal.supportsSGR()) {
			//this method adds the offset to the fg color by itself
			telio.write(terminal.getGRSequence(BCOLOR, color + 10));
			if (autoflush) {
				flush();
			}
		}
	} //setBackgroundColor

	public void setBold(boolean b) {
		if (terminal.supportsSGR()) {
			if (b) {
				telio.write(terminal.getGRSequence(STYLE, BOLD));
			} else {
				telio.write(terminal.getGRSequence(STYLE, BOLD_OFF));
			}
			if (autoflush) {
				flush();
			}
		}
	} //setBold

	public void setUnderlined(boolean b) {
		if (terminal.supportsSGR()) {
			if (b) {
				telio.write(terminal.getGRSequence(STYLE, UNDERLINED));
			} else {
				telio.write(terminal.getGRSequence(STYLE, UNDERLINED_OFF));
			}
			if (autoflush) {
				flush();
			}

		}
	} //setUnderlined

	public void setItalic(boolean b) {
		if (terminal.supportsSGR()) {
			if (b) {
				telio.write(terminal.getGRSequence(STYLE, ITALIC));
			} else {
				telio.write(terminal.getGRSequence(STYLE, ITALIC_OFF));
			}
			if (autoflush) {
				flush();
			}
		}
	} //setItalic

	public void setBlink(boolean b) {
		if (terminal.supportsSGR()) {
			if (b) {
				telio.write(terminal.getGRSequence(STYLE, BLINK));
			} else {
				telio.write(terminal.getGRSequence(STYLE, BLINK_OFF));
			}
			if (autoflush) {
				flush();
			}
		}
	} //setItalic

	public void resetAttributes() {
		if (terminal.supportsSGR()) {
			telio.write(terminal.getGRSequence(RESET, 0));
		}
	} //resetGR

	/*** End of special terminal function methods ***************************/

	/************************************************************************
	 * Auxiliary I/O methods						                        *
	 ************************************************************************/

	/**
	 * Method that parses forward for escape sequences
	 *  
	 */
	private int handleEscapeSequence(int i) {
		if (i == ESCAPE) {
			int[] bytebuf = new int[terminal.getAtomicSequenceLength()];
			//fill atomic length
			//FIXME: ensure CAN, broken Escapes etc.
			for (int m = 0; m < bytebuf.length; m++) {
				bytebuf[m] = telio.read();
			}
			return terminal.translateEscapeSequence(bytebuf);
		}
		if (i == BYTEMISSING) {
			//FIXME:longer escapes etc...
		}

		return HANDLED;
	} //handleEscapeSequence

	/** 
	 * Accessor method for the autoflushing mechanism.
	 * 
	 */
	public boolean isAutoflushing() {
		return autoflush;
	} //isAutoflushing

	/**
	 * Mutator method for the autoflushing mechanism.
	 */
	public void setAutoflushing(boolean b) {
		autoflush = b;
	} //setAutoflushing

	/** 
	 * Method to flush the Low-Level Buffer 
	 */
	public void flush() {
		telio.flush();
	} //flush (implements the famous iToilet)

	public void close() {
		telio.closeOutput();
	} //close

	public OutputStream getOutputStream() {
		return telio;//.getOutputStream();
	}
	/*** End of Auxiliary I/O methods  **************************************/

	/************************************************************************
	 * Terminal management specific methods			                        *
	 ************************************************************************/

	/** 
	 * Accessor method to get the active terminal object  
	 *
	 * @return Object that implements Terminal  
	 */
	public Terminal getTerminal() {
		return terminal;
	} //getTerminal

	/**
	 * Sets the default terminal ,which will either be
	 * the negotiated one for the connection, or the systems
	 * default.
	 */
	public void setDefaultTerminal() {
		//set the terminal passing the negotiated string
		setTerminal(telnetConnection.getNegotiatedTerminalType());
		
		//setTerminal("default");
		
	} //setDefaultTerminal

	/** 
	 *  Mutator method to set the active terminal object
	 *  If the String does not name a terminal we support
	 *  then the vt100 is the terminal of selection automatically.
	 *  
	 *  @param  terminalName String that represents common terminal name  
	 */
	public void setTerminal(String terminalName) {

		terminal = TerminalManager.getReference().getTerminal(terminalName);

		//Terminal is set we init it....
		initTerminal();

		//debug message
		//TelnetD.debuglog.write(this.toString() + ":Set terminal " + terminal.toString());

	} //setTerminal

	/**
	 * Terminal initialization
	 */
	private void initTerminal() {
		telio.write(terminal.getInitSequence());
		flush();
	} //initTerminal

	/**
	 *
	 */
	public int getRows() {
		return telnetConnection.getTerminalRows();
	} //getRows

	/**
	 * 
	 */
	public int getColumns() {
		return telnetConnection.getTerminalColumns();
	} //getColumns

	/**
	* Accessor Method for the terminal geometry changed flag
		 */
	public boolean isTerminalGeometryChanged() {
		return telnetConnection.isTerminalGeometryChanged();
	} //isTerminalGeometryChanged

	/*** End of terminal management specific methods  ***********************/

	/** Constants Declaration  **********************************************/

	/** 
	 * Terminal independent representation constants for terminal
	 * functions. 
	 */
	public static final int[] HOME = { 0, 0 };

		public static final int IOERROR = -1, //IO error
		// Positioning 10xx
		UP = 1001, //one up
		DOWN = 1002, //one down
		RIGHT = 1003, //one left
		LEFT = 1004, //one right
		//HOME=1005,		//Home cursor pos(0,0)

		// Functions 105x
		STORECURSOR = 1051, //store cursor position + attributes	
		RESTORECURSOR = 1052, //restore cursor + attributes

		// Erasing 11xx 
		EEOL = 1100, //erase to end of line 
		EBOL = 1101, //erase to beginning of line
		EEL = 1103, //erase entire line	
		EEOS = 1104, //erase to end of screen 
		EBOS = 1105, //erase to beginning of screen
		EES = 1106, //erase entire screen

		// Escape Sequence-ing 12xx
		ESCAPE = 1200, //Escape 
		BYTEMISSING = 1201, //another byte needed
		UNRECOGNIZED = 1202, //escape match missed

		// Control Characters 13xx
		ENTER = 1300, //LF is ENTER at the moment
		TABULATOR = 1301, //Tabulator
		DELETE = 1302, //Delete		
		BACKSPACE = 1303, //BACKSPACE
		COLORINIT = 1304, //Color inited
	HANDLED = 1305, LOGOUTREQUEST = 1306; //CTRL-D beim login		

	/** 
	 * Internal UpdateType Constants 
	 */
	public static final int LineUpdate = 475, CharacterUpdate = 476, ScreenpartUpdate = 477;

	/** 
	 * Internal BufferType Constants 
	 */
	public static final int EditBuffer = 575, LineEditBuffer = 576;

	/** 
	 * Network Virtual Terminal Specific Keys 
	 * Thats what we have to offer at least. 
	 */
	public static final int BEL = 7;
	public static final int BS = 8;
	public static final int DEL = 127;
	public static final int CR = 13;
	public static final int LF = 10;

	public static final int FCOLOR = 10001,
		BCOLOR = 10002,
		STYLE = 10003,
		RESET = 10004,
		BOLD = 1,
		BOLD_OFF = 22,
		ITALIC = 3,
		ITALIC_OFF = 23,
		BLINK = 5,
		BLINK_OFF = 25,
		UNDERLINED = 4,
		UNDERLINED_OFF = 24;

	/**
	 * FIXME: Move telnet specific stuff to TelnetIO and clean up TelnetIO
	 */
	public static final String CRLF = "\r\n";

	/**
	
	
	
	/** end Constants Declaration  ******************************************/

} //class TerminalIO
