/*
 * @(#)Dimension.java
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

/**
 * Class that represents a components dimension on the
 * terminal, it therefore encapsulates the coordinates 
 * given by columns(width) and rows(height).
 * 
 * @author Dieter Wimberger
 * @version 1.0 02/03/2000
 */
 public class Dimension {

  //Members
  private int myHeight;
  private int myWidth;
 
 	/**
	 * Constructs an instance with zero width and zero height.
	 */
	 public Dimension() {
 		myHeight=0;
 		myWidth=0;
 	 }//constructor
 
 
	/**
	 * Constructs an instance with width and height.
	 *
     * @param width Integer that represents a width in amount of columns.
     * @param height Integer that represents a height in amount of rows.  
     */
	 public Dimension(int width,int height) {
 		myHeight=height;
 		myWidth=width;
 	 }//constructor
	
	/**
	 * Accessor method for the width.
	 *
	 * @return int that represents the width in number of columns.
	 */
	 public int getWidth() {
	 	return myWidth;
	 }//getWidth


	/**
	 * Mutator method for the width.
	 * 
	 * @param width Integer that represents a width in numbers of columns.
	 */ 
	 public void setWidth(int width) {
	 	myWidth=width;
	 }//setWidth

	
	/**
	 * Accessor method for the height.
	 *
	 * @return int that represents the height in number of rows.
	 */
	 public int getHeight() {
	 	return myHeight;
	 }//getHeight
 
 
 	/**
	 * Mutator method for the height.
	 * 
	 * @param height Integer that represents a height in numer of rows.
	 */ 
	 public void setHeight(int height) {
	 	myHeight=height;
	 }//setHeight
 
}//class Dimension
