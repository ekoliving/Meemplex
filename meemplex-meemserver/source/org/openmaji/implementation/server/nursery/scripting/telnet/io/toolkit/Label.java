/*
 * @(#)Label.java
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
import org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.ColorHelper;

/**
 * Class that represents a label.
 * 
 * @author Dieter Wimberger
 * @version 1.0 
 */
 public class Label extends InertComponent {
  
  //Members
  private String myContent;
 
 	/**
 	 * Constructs a Label instance.
 	 *
 	 * @param io Instance of a class implementing the BasicTerminalIO interface.
 	 * @param name String that represents the components name.
 	 * @param text String that represents the visible label. 
 	 */  
	 public Label(BasicTerminalIO io, String name, String text) {
	 	super(io,name);
	 	setText(text);
	 }//constructor
 	
 	/**
 	 * Constructs a Label instance, using the name as visible content. 
     *
 	 * @param io Instance of a class implementing the BasicTerminalIO interface.
 	 * @param name String that represents the components name.
 	 */  
	 public Label(BasicTerminalIO io, String name) {
	 	super(io,name);
	 	setText(name);
	 }//constructor
 
 	/**
 	 * Mutator method for the text property of the label component.
 	 * 
 	 * @param text String displayed on the terminal.
 	 */
 	 public void setText(String text) {
 	 	//set member
 	 	myContent=text;
 	 	//set Dimensions
 	 	myDim=new Dimension((int)ColorHelper.getVisibleLength(text),1);
 	 
 	 }//setText
 
 	/**
 	 * Accessor method for the text property of the label component.
 	 * 
 	 * @return String that is displayed when the label is drawn.
 	 */ 
 	 public String getText() {
 	 	return myContent;
 	 }//getText
 	
 	
 	/**
 	 * Method that draws the label on the screen.
 	 */
	 public void draw(){
		if(myPosition==null) {
			myIO.write(myContent);
		} else {
			myIO.storeCursor();
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
			myIO.write(myContent);
			myIO.restoreCursor();
			myIO.flush();
		}
	 }//draw
	
	
 }//class Label

