/*
 * @(#)Point.java
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

/**
 * Class that represents a point on the terminal. 
 * Respectively it specifies a character cell, encapsulating 
 * column and row coordinates.
 * 
 * @author Dieter Wimberger
 * @version 1.0 02/03/2000
 */
 public class Point {

  //Members
  private int myRow;
  private int myCol;
 
	/**
	 * Constructs an instance with its coordinates set to the origin (0/0).
     */
	 public Point() {
 		myCol=0;
 		myRow=0;
 	 }//constructor
	
	
	/**
	 * Constructs an instance with given coordinates.
	 *
     * @param col Integer that represents a column position.
     * @param row Integer that represents a row position  
     */
	 public Point(int col,int row) {
 		myCol=col;
 		myRow=row;
 	 }//constructor

	
	/**
	 * Mutator method to set the points coordinate at once.
	 * 
     * @param col Integer that represents a column position.
     * @param row Integer that represents a row position  
	 */
	 public void setLocation(int col, int row){
		myCol=col;
		myRow=row;
	 }//setLocation
	
	/**
	 * Convenience method to set the points coordinates.
	 * 
     * @param col Integer that represents a column position.
     * @param row Integer that represents a row position  
	 */
	 public void move(int col, int row){
		myCol=col;
		myRow=row;
	 }//move
	
	/**
	 * Accessor method for the column coordinate.
	 *
	 * @return int that represents the cells column coordinate.
	 */
	 public int getColumn() {
	 	return myCol;
	 }//getColumn


	/**
	 * Mutator method for the column coordinate of this
	 * Cell.
	 * 
	 * @param col Integer that represents a column position.
	 */ 
	 public void setColumn(int col) {
	 	myCol=col;
	 }//setColumn

	
	/**
	 * Accessor method for the row coordinate.
	 *
	 * @return int that represents the cells row coordinate.
	 */
	 public int getRow() {
	 	return myRow;
	 }//getRow
 
 
 	/**
	 * Mutator method for the row coordinate of this
	 * Cell.
	 * 
	 * @param row Integer that represents a row position.
	 */ 
	 public void setRow(int row) {
	 	myRow=row;
	 }//setRow
 
}//class Point
