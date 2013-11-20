/*
 * @(#)BasicTerminal.java
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
 * A basic terminal implementation with the focus on vt100
 * related sequences. This terminal type is most common out
 * there, with sequences that are normally also understood 
 * by its successors.
 *
 * @author Dieter Wimberger
 * @version 1.0 29/08/2000 
 *
 */
public abstract class BasicTerminal implements Terminal {

  public static final String PROPERTY_CONVERT_DEL_TO_BACKSPACE = "bsh.convertDelToBackSpace";

  //Associations
  protected Colorizer myColorizer;
  private boolean convertDelToBackSpace = false;

	/**
	 * Constructs an instance with an associated colorizer.
	 */
	public BasicTerminal() {
		myColorizer = Colorizer.getReference();
    if ( System.getProperty(PROPERTY_CONVERT_DEL_TO_BACKSPACE) != null ) {
      convertDelToBackSpace = true;
    }
	} //constructor

	public int translateControlCharacter(int c) {

		switch (c) {
			case DEL :
        if ( convertDelToBackSpace ) {
          return TerminalIO.BACKSPACE;
        }
        else {
          return TerminalIO.DELETE;
        }
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

	public int translateEscapeSequence(int[] buffer) {
		try {
			if (buffer[0] == LSB) {
				switch (buffer[1]) {
					case A :
						return TerminalIO.UP;
					case B :
						return TerminalIO.DOWN;
					case C :
						return TerminalIO.RIGHT;
					case D :
						return TerminalIO.LEFT;
					default :
						break;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return TerminalIO.BYTEMISSING;
		}

		return TerminalIO.UNRECOGNIZED;
	} //translateEscapeSequence 

	public byte[] getCursorMoveSequence(int direction, int times) {
		byte[] sequence = null;

		if (times == 1) {
			sequence = new byte[3];
		} else {
			sequence = new byte[times * 3];
		}

		for (int g = 0; g < times * 3; g++) {

			sequence[g] = ESC;
			sequence[g + 1] = LSB;
			switch (direction) {
				case TerminalIO.UP :
					sequence[g + 2] = A;
					break;
				case TerminalIO.DOWN :
					sequence[g + 2] = B;
					break;
				case TerminalIO.RIGHT :
					sequence[g + 2] = C;
					break;
				case TerminalIO.LEFT :
					sequence[g + 2] = D;
					break;
				default :
					break;
			}
			g = g + 2;
		}

		return sequence;
	} // getCursorMoveSequence

	public byte[] getCursorPositioningSequence(int[] pos) {

		byte[] sequence = null;

		if (pos == TerminalIO.HOME) {
			sequence = new byte[3];
			sequence[0] = ESC;
			sequence[1] = LSB;
			sequence[2] = H;
		} else {
			//first translate integer coords into digits
			byte[] rowdigits = translateIntToDigitCodes(pos[0]);
			byte[] columndigits = translateIntToDigitCodes(pos[1]);
			int offset = 0;
			//now build up the sequence:
			sequence = new byte[4 + rowdigits.length + columndigits.length];
			sequence[0] = ESC;
			sequence[1] = LSB;
			//now copy the digit bytes
			System.arraycopy(rowdigits, 0, sequence, 2, rowdigits.length);
			//offset is now 2+rowdigits.length
			offset = 2 + rowdigits.length;
			sequence[offset] = SEMICOLON;
			offset++;
			System.arraycopy(columndigits, 0, sequence, offset, columndigits.length);
			offset = offset + columndigits.length;
			sequence[offset] = H;
		}
		return sequence;
	} //getCursorPositioningSequence

	public byte[] getEraseSequence(int eraseFunc) {

		byte[] sequence = null;

		switch (eraseFunc) {
			case TerminalIO.EEOL :
				sequence = new byte[3];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = LE;
				break;
			case TerminalIO.EBOL :
				sequence = new byte[4];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = 49; //Ascii Code of 1	
				sequence[3] = LE;
				break;
			case TerminalIO.EEL :
				sequence = new byte[4];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = 50; //Ascii Code 2
				sequence[3] = LE;
				break;
			case TerminalIO.EEOS :
				sequence = new byte[3];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = SE;
				break;
			case TerminalIO.EBOS :
				sequence = new byte[4];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = 49; //Ascii Code of 1
				sequence[3] = SE;
				break;
			case TerminalIO.EES :
				sequence = new byte[4];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = 50; //Ascii Code of 2
				sequence[3] = SE;
				break;
			default :
				break;
		}

		return sequence;
	} //getEraseSequence

	public byte[] getSpecialSequence(int function) {

		byte[] sequence = null;

		switch (function) {
			case TerminalIO.STORECURSOR :
				sequence = new byte[2];
				sequence[0] = ESC;
				sequence[1] = 55; //Ascii Code of 7
				break;
			case TerminalIO.RESTORECURSOR :
				sequence = new byte[2];
				sequence[0] = ESC;
				sequence[1] = 56; //Ascii Code of 8
				break;

		}

		return sequence;
	} //getSpecialSequence

	public byte[] getGRSequence(int type, int param) {

		byte[] sequence = new byte[0];
		int offset = 0;

		switch (type) {
			case TerminalIO.FCOLOR :
			case TerminalIO.BCOLOR :
				byte[] color = translateIntToDigitCodes(param);
				sequence = new byte[3 + color.length];

				sequence[0] = ESC;
				sequence[1] = LSB;
				//now copy the digit bytes
				System.arraycopy(color, 0, sequence, 2, color.length);
				//offset is now 2+color.length
				offset = 2 + color.length;
				sequence[offset] = 109; //ASCII Code of m
				break;

			case TerminalIO.STYLE :
				byte[] style = translateIntToDigitCodes(param);
				sequence = new byte[3 + style.length];

				sequence[0] = ESC;
				sequence[1] = LSB;
				//now copy the digit bytes
				System.arraycopy(style, 0, sequence, 2, style.length);
				//offset is now 2+style.length
				offset = 2 + style.length;
				sequence[offset] = 109; //ASCII Code of m
				break;

			case TerminalIO.RESET :
				sequence = new byte[5];
				sequence[0] = ESC;
				sequence[1] = LSB;
				sequence[2] = 52; //ASCII Code of 4
				sequence[3] = 56; //ASCII Code of 8
				sequence[4] = 109; //ASCII Code of m
				break;
		}

		return sequence;
	} //getGRsequence

	public byte[] getScrollMarginsSequence(int topmargin, int bottommargin) {

		byte[] sequence = new byte[0];

		if (supportsScrolling()) {
			//first translate integer coords into digits
			byte[] topdigits = translateIntToDigitCodes(topmargin);
			byte[] bottomdigits = translateIntToDigitCodes(bottommargin);
			int offset = 0;
			//now build up the sequence:
			sequence = new byte[4 + topdigits.length + bottomdigits.length];
			sequence[0] = ESC;
			sequence[1] = LSB;
			//now copy the digit bytes
			System.arraycopy(topdigits, 0, sequence, 2, topdigits.length);
			//offset is now 2+topdigits.length
			offset = 2 + topdigits.length;
			sequence[offset] = SEMICOLON;
			offset++;
			System.arraycopy(bottomdigits, 0, sequence, offset, bottomdigits.length);
			offset = offset + bottomdigits.length;
			sequence[offset] = r;
		}

		return sequence;
	} //getScrollMarginsSequence

	public String format(String str) {
		return myColorizer.colorize(str, supportsSGR());
	} //format

	public byte[] getInitSequence() {
		byte[] sequence = new byte[0];

		return sequence;
	} //getInitSequence

	public int getAtomicSequenceLength() {
		return 2;
	} //getAtomicSequenceLength

	/**
	 * Translates an integer to a byte sequence of its
	 * digits.<br>
	 * 
	 * @param in integer to be translated.
	 *
	 * @return the byte sequence representing the digits.
	 */
	public byte[] translateIntToDigitCodes(int in) {
		return Integer.toString(in).getBytes();
	} //translateIntToDigitCodes

	public abstract boolean supportsSGR();
	public abstract boolean supportsScrolling();

} //class BasicTerminal
