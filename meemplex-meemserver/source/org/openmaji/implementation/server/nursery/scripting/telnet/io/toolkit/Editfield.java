/*
 * @(#)Editfield.java
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
 * Class that implements an Editfield.
 *  
 * @author Dieter Wimberger
 * @version 1.0 21/04/2000
 */
 public class Editfield extends ActiveComponent {

	//Associations
	private InputFilter myInputFilter=null;
	private InputValidator myInputValidator=null;
	//Aggregations (inner class!)
   private Buffer buf;
	//Members
	private int Cursor=0;
	private boolean InsertMode=true;
	private int lastSize=0;
	private boolean PasswordField=false;

	/**
	 * Constructs an Editfield.
	 */
	 public Editfield(BasicTerminalIO io, String name,int length) {
		//init superclass
		super(io,name);
		//init class params
		buf=new Buffer(length);
		setDimension(new Dimension(length,1));
		Cursor=0;
		InsertMode=true;
	 }//constructor

	/**
	 * Accessor method for field length.
	 * @return int that represents length of editfield.
	 */
	 public int getLength() {
		return myDim.getWidth();
	 }//getLength

	/**
	 * Accessor method for field buffer size.
	 * @return int that represents the number of chars in the fields buffer.
	 */
	 public int getSize() {
		return buf.size();
	 }//getSize

	 public String getValue() {
		return buf.toString();
	 }//getValue
     
	 public void setValue(String str)
	 		throws BufferOverflowException {
		
		storeSize();
		//buffer
		buf.clear();
		//cursor
		Cursor=0;
		append(str);
	 }//setValue
	 
	 public void clear() {
		
		storeSize();
		//Buffer
		buf.clear();
 	   //Cursor
		Cursor=0;
		//Screen
		draw();
	 }//clear
     
	 public char getCharAt(int pos) 
			throws IndexOutOfBoundsException {
		
		return buf.getCharAt(pos);
	 }//getCharAt
	
	 public void setCharAt(int pos,char ch) 
			throws IndexOutOfBoundsException {

		//buffer
		buf.setCharAt(pos,ch);
		//cursor
		//implements overwrite mode no change
		//screen
		draw();
	 }//setCharAt

	 public void insertCharAt(int pos, char ch)
			throws BufferOverflowException,IndexOutOfBoundsException {
		
		storeSize();
		//buffer
		buf.ensureSpace(1);
		buf.insertCharAt(pos,ch);
		//cursor adjustment (so that it stays in "same" pos)
		if (Cursor >= pos) {
			Cursor++;
		}
		//screen
		draw();
	 }//insertCharAt

	 public void removeCharAt(int pos) 
	 		throws IndexOutOfBoundsException {

		storeSize();
		//buffer
		buf.removeCharAt(pos);
		//cursor adjustment
		if(Cursor>pos) {
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
//		draw();
	 }//removeChatAt

	 public void insertStringAt(int pos, String str) 
			throws BufferOverflowException, IndexOutOfBoundsException {

		storeSize();
		//buffer
		buf.ensureSpace(str.length());
		for (int i=0;i<str.length();i++) {
			buf.insertCharAt(pos,str.charAt(i));
			//Cursor
			Cursor++;
		}
	 	//screen 
		draw();

	 }//insertStringAt
	
	 public void append(char ch) 
	 		throws BufferOverflowException {
	 	if (ch == 0) {
	 		return;
	 	}

		storeSize();
		//buffer
		buf.ensureSpace(1);
		buf.append(ch);
		//cursor
		Cursor++;
		//screen
		if(!PasswordField) {
			myIO.write(ch);
		} else {
			myIO.write('.');
		}
	 }//append(char)

	 public void append(String str) 
	 		throws BufferOverflowException {
		
		storeSize();
		//buffer
		buf.ensureSpace(str.length());
		for (int i=0;i<str.length();i++) {
			buf.append(str.charAt(i));
			//Cursor
			Cursor++;
		}
		//screen
		if(!PasswordField) {
			myIO.write(str);
		} else {
			StringBuffer sbuf=new StringBuffer();
			for(int n=0;n<str.length();n++){
				sbuf.append('.');
			}
			myIO.write(sbuf.toString());
		}
	 }//append(String)

	 public int getCursorPosition() {
		return Cursor;
	 }//getCursorPosition      
	 
	/**
	 *
	 * @param filter Object instance that implements the InputFilter interface.
	 */
	 public void registerInputFilter(InputFilter filter) {
		myInputFilter=filter;
	 }//registerInputFilter

	/**
	 *
	 * @param validator Object instance that implements the InputValidator interface.
	 */
	 public void registerInputValidator(InputValidator validator) {
		myInputValidator=validator;
	 }//registerInputValidator

	 public boolean isInInsertMode() {
	 	return InsertMode;
	 }//isInInsertMode

	 public void setInsertMode(boolean b) {
	 	InsertMode=b;
	 }//setInsertMode

    public boolean isPasswordField() {
	 	return PasswordField;
	 }//isPasswordField

	 public void setPasswordField(boolean b) {
	 	PasswordField=b;
	 }//setPasswordField

	/**
	 * Method that will be
	 * reading and processing input.
	 */
	 public void run() {
		int in=0;
		myIO.setAutoflushing(false);
		draw();
		myIO.flush();
		do {
			//get next key
			in=myIO.read();
			//send it through the filter if one is set
			if(myInputFilter!=null) {
				in=myInputFilter.filterInput(in);
			}
			switch(in) {
				case InputFilter.INPUT_HANDLED:
					continue;
				case InputFilter.INPUT_INVALID:
					myIO.bell();
					break;
				case BasicTerminalIO.LEFT:
					moveLeft();
					break; 
				case BasicTerminalIO.RIGHT:
					moveRight();
					break;
				case BasicTerminalIO.UP:
				case BasicTerminalIO.DOWN:
					myIO.bell();
					break;
				case BasicTerminalIO.ENTER:
					if(myInputValidator!=null) {
						if(myInputValidator.validate(buf.toString())) {
							in=-1;
						} else {
							myIO.bell();
						}
					} else {
						in=-1;
					}
					break;
				case BasicTerminalIO.BACKSPACE:
					try {
						removeCharAt(Cursor-1);
					} catch (IndexOutOfBoundsException ioobex){
						myIO.bell();
					}
					break;
				case BasicTerminalIO.DELETE:
					try {
						removeCharAt(Cursor);
					} catch (IndexOutOfBoundsException ioobex){
						myIO.bell();
					}
					break;
				case BasicTerminalIO.TABULATOR:
					in=-1;
					break;
				default:
					handleCharInput(in);
			}
			myIO.flush();
		} while (in!=-1);
	 }//run
	
	 
	 public void draw() {
	 	//System.out.println("DEBUG: Buffer="+buf.toString());
	 	//System.out.println("DEBUG: Cursor="+Cursor);

//	 	int diff=lastSize-buf.size();
		String output=buf.toString();
		if(PasswordField) {
			StringBuffer stbuf=new StringBuffer();
			for(int n=0;n<output.length();n++){
				stbuf.append('.');
			}
			output=stbuf.toString();
		}
		//System.out.println("DEBUG: Sizediff="+diff);
//		if(diff>0){
//			StringBuffer sbuf=new StringBuffer();
//				sbuf.append(output);
//				for (int i=0;i<diff;i++) {
//					sbuf.append(" ");
//				}
//				output=sbuf.toString();
//		} 
		
		if(myPosition != null) {
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
		} else {
			myIO.moveLeft(lastSize);
		}
		myIO.write(output);
		//adjust screen cursor hmm
		if (Cursor<output.length()) {
			myIO.moveLeft(output.length()-Cursor);
	 	} 	
	}//draw

	 private void moveRight() {
	 	//cursor
		if(Cursor<buf.size()) {
			Cursor++;
			//screen
			myIO.moveRight(1);
		} else {
			myIO.bell();
		}
	 }//moveRight
	
	 private void moveLeft() {
		//cursor
		if(Cursor>0) {
			Cursor--;
			//screen
			myIO.moveLeft(1);
		} else {
			myIO.bell();
		}
	 }//moveLeft
	
	 private boolean isCursorAtEnd() {
		return (Cursor==buf.size());
	 }//isCursorAtEnd

	 private void handleCharInput(int ch) {
		if (isCursorAtEnd()) {
				try {
					//Field
					append((char) ch);
				} catch (BufferOverflowException bex) {
					myIO.bell();
				}
		} else {
		 	if (isInInsertMode()) {
				try {	
					//Field
					insertCharAt(Cursor,(char) ch); 
				} catch (BufferOverflowException bex) {
					myIO.bell();	
				}
			} else {
				try {
					//Field
					setCharAt(Cursor,(char) ch);
				} catch (IndexOutOfBoundsException bex) {
					myIO.bell();
				}
			} 
		}
	 }//handleCharInput
	
	 private void storeSize() {
	 	lastSize=buf.size();
	 }//storeSize


	//inner class Buffer
	class Buffer extends CharBuffer {

	public Buffer(int size) {
			super(size);
		}//constructor
		
	}//class Buffer

 }//class Editfield

