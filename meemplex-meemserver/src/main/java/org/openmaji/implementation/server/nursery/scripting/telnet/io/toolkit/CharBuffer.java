/*
 * @(#)CharBuffer.java
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

package org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit;

import java.util.Vector;

class CharBuffer {
	
	//Members
	private Vector myBuffer; 
	private int mySize;

	public CharBuffer(int size) {
		myBuffer=new Vector(size);
		mySize=size;
	}//constructor
		
	public char getCharAt(int pos) 
			throws IndexOutOfBoundsException {

		return ((Character)myBuffer.elementAt(pos)).charValue();
	}//getCharAt

	public void setCharAt(int pos,char ch) 
			throws IndexOutOfBoundsException {
		
		myBuffer.setElementAt(new Character(ch),pos);
	}//setCharAt

	public void insertCharAt(int pos, char ch) 
			throws BufferOverflowException, IndexOutOfBoundsException {
		
		myBuffer.insertElementAt(new Character(ch),pos);
	}//insertCharAt

	public void append(char aChar) 
			throws BufferOverflowException {

			myBuffer.addElement(new Character(aChar));
	}//append

	public void removeCharAt(int pos) 
			throws IndexOutOfBoundsException {
			
		myBuffer.removeElementAt(pos);
	}//removeCharAt

	public void clear() {
		myBuffer.removeAllElements();
	}//clear
		
	public int size() {
		return myBuffer.size();
	}//size
		
	public String toString() {
		StringBuffer sbuf=new StringBuffer();
		for(int i=0;i<myBuffer.size();i++) {
			sbuf.append(((Character)myBuffer.elementAt(i)).charValue());
		}
		return sbuf.toString();
	}//toString
		
	public void ensureSpace(int chars)
			throws BufferOverflowException {
		
		if (chars > (mySize-myBuffer.size())) {
			throw new BufferOverflowException();
		}
	}//ensureSpace

 }//class CharBuffer