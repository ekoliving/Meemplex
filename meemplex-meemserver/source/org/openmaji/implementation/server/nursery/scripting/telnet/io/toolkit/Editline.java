/*
 * @(#)Editline.java
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

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;

/**
 * Class that implements an Editline
 */
public class Editline {

	//Aggregations (inner class!)
	private Buffer buf;
	//Members
	private BasicTerminalIO myIO;
	private int Cursor = 0;
	private boolean InsertMode = true;
	private int lastSize = 0;
	private boolean hardwrapped = false;
	private char lastread;
	private int lastcurspos = 0;

	/**
	 * Constructs an Editline.
	 */
	public Editline(BasicTerminalIO io) {
		myIO = io;
		//allways full length
		buf = new Buffer(myIO.getColumns() - 1);
		Cursor = 0;
		InsertMode = true;
	} //constructor

	/**
	 * Accessor method for line buffer size.
	 * @return int that represents the number of chars in the fields buffer.
	 */
	public int size() {
		return buf.size();
	} //getSize

	public String getValue() {
		return buf.toString();
	} //getValue

	public void setValue(String str) throws BufferOverflowException {

		storeSize();
		//buffer
		buf.clear();
		//cursor
		Cursor = 0;

		//screen
		myIO.moveLeft(lastSize);
		myIO.eraseToEndOfLine();
		append(str);
	} //setValue

	public void clear() {

		storeSize();
		//Buffer
		buf.clear();
		//Cursor
		Cursor = 0;
		//Screen
		draw();
	} //clear

	public String getSoftwrap() throws IndexOutOfBoundsException {

		//Wrap from Buffer
		String content = buf.toString();
		int idx = content.lastIndexOf(" ");
		if (idx == -1) {
			content = "";
		} else {
			//System.out.println("Line:softwrap:lastspace:"+idx);
			content = content.substring(idx + 1, content.length());

			//System.out.println("Line:softwrap:wraplength:"+content.length());

			//Cursor
			//remeber relative cursor pos
			Cursor = size();
			Cursor = Cursor - content.length();

			//buffer
			for (int i = 0; i < content.length(); i++) {
				buf.removeCharAt(Cursor);
			}

			//screen
			myIO.moveLeft(content.length());
			myIO.eraseToEndOfLine();

			//System.out.println("Line:softwrap:buffercontent:"+buf.toString());
		}
		return content + getLastRead();
	} //getSoftWrap

	public String getHardwrap() throws IndexOutOfBoundsException {

		//Buffer
		String content = buf.toString();
		content = content.substring(Cursor, content.length());
		//System.out.println("buffer:tostring:"+buf.toString()+":");
		//System.out.println("buffer:size:"+buf.size());
		int lastsize = buf.size();
		for (int i = Cursor; i < lastsize; i++) {
			buf.removeCharAt(Cursor);
			//System.out.println("buffer:removing char #"+i);
		}
		//System.out.println("buffer:tostring:"+buf.toString()+":");
		//cursor stays
		//screen
		myIO.eraseToEndOfLine();
		return content;
	} //getHardWrap

	private void setCharAt(int pos, char ch) throws IndexOutOfBoundsException {

		//buffer
		buf.setCharAt(pos, ch);
		//cursor
		//implements overwrite mode no change
		//screen
		draw();
	} //setCharAt

	private void insertCharAt(int pos, char ch) throws BufferOverflowException, IndexOutOfBoundsException {

		storeSize();
		//buffer
		buf.ensureSpace(1);
		buf.insertCharAt(pos, ch);
		//cursor adjustment (so that it stays in "same" pos)
		if (Cursor >= pos) {
			Cursor++;
		}
		//screen
		draw();
	} //insertCharAt

	private void removeCharAt(int pos) throws IndexOutOfBoundsException {

		storeSize();
		//buffer
		buf.removeCharAt(pos);

		//cursor
		if (Cursor > pos) {
			Cursor--;
		}
		
		myIO.moveLeft(1);
		myIO.eraseToEndOfLine();
		
		
		String s = buf.toString();
		
		s = s.substring(pos);
		if (s.length() > 0 ) {
			myIO.write(s);
		}
		
		//screen
		//draw();

	} //removeChatAt

//	private void insertStringAt(int pos, String str) throws BufferOverflowException, IndexOutOfBoundsException {
//
//		storeSize();
//		//buffer
//		buf.ensureSpace(str.length());
//		for (int i = 0; i < str.length(); i++) {
//			buf.insertCharAt(pos, str.charAt(i));
//			//Cursor
//			Cursor++;
//		}
//		//screen 
//		draw();
//
//	} //insertStringAt

	public void append(char ch) throws BufferOverflowException {

		storeSize();
		//buffer
		buf.ensureSpace(1);
		buf.append(ch);
		//cursor
		Cursor++;
		//screen
		myIO.write(ch);
	} //append(char)

	public void append(String str) throws BufferOverflowException {

		storeSize();
		//buffer
		buf.ensureSpace(str.length());
		for (int i = 0; i < str.length(); i++) {
			buf.append(str.charAt(i));
			//Cursor
			Cursor++;
		}
		//screen
		myIO.write(str);
	} //append(String)

	public int getCursorPosition() {
		return Cursor;
	} //getCursorPosition      

	public void setCursorPosition(int pos) {

		if (buf.size() < pos) {
			Cursor = buf.size();
		} else {
			Cursor = pos;
		}
		//System.out.println("Editline:cursor:"+Cursor);
	} //setCursorPosition

	private char getLastRead() {
		return lastread;
	} //getLastRead

	private void setLastRead(char ch) {
		lastread = ch;
	} //setLastRead

	public boolean isInInsertMode() {
		return InsertMode;
	} //isInInsertMode

	public void setInsertMode(boolean b) {
		InsertMode = b;
	} //setInsertMode

	public boolean isHardwrapped() {
		return hardwrapped;
	} //isHardwrapped

	public void setHardwrapped(boolean b) {
		hardwrapped = b;
	} //setHardwrapped

	/**
	 * Method that will be
	 * reading and processing input.
	 */
	public int run() {
		int in = 0;
		//draw();
		//myIO.flush();
		while (true) {
			//get next key
			in = myIO.read();
			if (in < 0)
				return in;
			//store cursorpos
			lastcurspos = Cursor;

			switch (in) {
				case BasicTerminalIO.LEFT :
					if (!moveLeft()) {
						return in;
					}
					break;
				case BasicTerminalIO.RIGHT :
					if (!moveRight()) {
						return in;
					}
					break;
				case BasicTerminalIO.BACKSPACE :
					try {
						if (Cursor == 0) {
//							return in;
						} else {
							removeCharAt(Cursor - 1);
						}
					} catch (IndexOutOfBoundsException ioobex) {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.DELETE :
					try {
						removeCharAt(Cursor);
					} catch (IndexOutOfBoundsException ioobex) {
						myIO.bell();
					}
					break;
				case BasicTerminalIO.ENTER :
					try {
						if (in != 0)
							handleCharInput(in);
					} catch (BufferOverflowException boex) {
						setLastRead((char) in);
						return in;
					}
					return in;
				case BasicTerminalIO.UP :
				case BasicTerminalIO.DOWN :
				case BasicTerminalIO.TABULATOR :
					return in;
				default :
					try {
						if (in != 0)
							handleCharInput(in);
					} catch (BufferOverflowException boex) {
						setLastRead((char) in);
						return in;
					}
			}
			myIO.flush();
		}
	} //run

	public void draw() {
		//System.out.println("DEBUG: Buffer="+buf.toString());
		//System.out.println("DEBUG: Cursor="+Cursor);
		//System.out.println("DEBUG: lastcurspos="+lastcurspos);

		/*
			int diff=lastSize-buf.size();
		StringBuffer output=new StringBuffer(buf.toString());
		if(diff>0){
				for (int i=0;i<diff;i++) {
					output.append(" ");
				}
		} 
		*/

		myIO.moveLeft(lastcurspos);
		myIO.eraseToEndOfLine();
		myIO.write(buf.toString());
		//adjust screen cursor hmm
		if (Cursor < buf.size()) {
			myIO.moveLeft(buf.size() - Cursor);
		}
	} //draw

	private boolean moveRight() {
		//cursor
		if (Cursor < buf.size()) {
			Cursor++;
			//screen
			myIO.moveRight(1);
			return true;
		} else {
			return false;
		}
	} //moveRight

	private boolean moveLeft() {
		//cursor
		if (Cursor > 0) {
			Cursor--;
			//screen
			myIO.moveLeft(1);
			return true;
		} else {
			return false;
		}
	} //moveLeft

	private boolean isCursorAtEnd() {
		return (Cursor == buf.size());
	} //isCursorAtEnd

	private void handleCharInput(int ch) throws BufferOverflowException {

		if (isCursorAtEnd()) {
			append((char) ch);
		} else {
			if (isInInsertMode()) {
				try {
					insertCharAt(Cursor, (char) ch);
				} catch (BufferOverflowException ex) {
					//ignore buffer overflow on insert
					myIO.bell();
				}
			} else {
				setCharAt(Cursor, (char) ch);
			}
		}
	} //handleCharInput

	private void storeSize() {
		lastSize = buf.size();
	} //storeSize

	//inner class Buffer
	class Buffer extends CharBuffer {

		public Buffer(int size) {
			super(size);
		} //constructor

	} //class Buffer

} //class Editfield