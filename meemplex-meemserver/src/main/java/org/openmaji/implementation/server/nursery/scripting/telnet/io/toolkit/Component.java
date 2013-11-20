/*
 * @(#)Component.java
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

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;

/**
 * Class that represents an abstract toolkit component.
 * 
 * @author Dieter Wimberger
 * @version 1.0
 */
public abstract class Component {
  
   protected String myName;
   protected BasicTerminalIO myIO;
   protected Point myPosition;
   protected Dimension myDim;
  
    /**
     * Constructor for an abstract toolkit component.
     * 
     * @param io Instance of a class implementing the BasicTerminalIO.
     * @param name String that represents the components name.
     */
     public Component(BasicTerminalIO io,String name) {
   		myIO=io;
   		myName=name;
     }//constructor

   
    /**
     * Method that draws the component.
     */
     abstract public void draw();
   
    /**
     * Accessor method for the name property of a component.
     *
     * @return String that represents the components name.
     */
     public String getName() {
   		return myName;
     }//getName
   
   
    /**
     * Accessor method for a component's location.
     * 
     * @return Point that encapsulates the location.
     */
     public Point getLocation(){
    	return myPosition;
     }//getLocation
   
    /**
     * Mutator method for a component's location.
     * 
     * @param pos Point that encapsulates the (new) Location.
     */
     public void setLocation(Point pos){
    	myPosition=pos;
	 }//setLocation
   
   
	/**
     * Convenience mutator method for a component's location.
     * 
     * @param col int that represents a column coordinate.
     * @param row int that represents a row coordinate.
     */
	 public void setLocation(int col,int row) {
		if(myPosition!=null) {
			myPosition.setColumn(col);
			myPosition.setRow(row);
		} else {
			myPosition=new Point(col,row);
		}
	 }//set Location
   
   
   
    /**
     * Accessor method for a component's dimension.
     * 
     * @return Dimension that encapsulates the dimension in cols and rows.
     */
     public Dimension getDimension(){
    	return myDim;
     }//getDimension
     
     
    /**
     * Mutator method for a component's dimension.
     * 
     * @param dim Dimension that encapsulates the dimension in cols and rows.
     */
     protected void setDimension(Dimension dim){
    	myDim=dim;
     }//setDimension
   
     
   
  
}//class Component

