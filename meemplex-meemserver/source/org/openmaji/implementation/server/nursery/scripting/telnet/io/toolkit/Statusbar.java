/*
 * @(#)Statusbar.java
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
 * Class that implements a statusbar, for the bottom of the
 * Terminal Window. 
 *  
 * @author Dieter Wimberger
 * @version 1.0 09/03/2000
 */
 public class Statusbar extends InertComponent {
   
   //Members
   private String myStatus; 
   private int myAlign;
   private String myBgColor;
   private String myFgColor;
   
   
   /**
    * Constructor for a simple statusbar instance.
    */
    public Statusbar(BasicTerminalIO io, String name) {
   		super(io,name);	
   		
   	}//constructor
   

 	/**
 	 * Mutator method for the statustext property of the statusbar component.
 	 * 
 	 * @param text status String displayed in the titlebar.
 	 */
 	 public void setStatusText(String text) {
 	 	myStatus=text;
 	 }//setStatusText
 
 	/**
 	 * Accessor method for the statustext property of the statusbar component.
 	 * 
 	 * @return String that is displayed when the bar is drawn.
 	 */ 
 	 public String getStatusText() {
 	 	return myStatus;
 	 }//getStatusText
   
 	/**
  	 * Mutator method for the alignment property.
  	 *
  	 * @param alignment integer, valid if one of  the ALIGN_* constants.
  	 */
	 public void setAlignment(int alignment){
		if(alignment<1||alignment>3) {
			alignment=2;	//left default
		} else {
			myAlign=alignment;
		}
	 }//setAlignment
	 
	/**
  	 * Mutator method for the SoregroundColor property.
  	 *
  	 * @param color String, valid if it is a ColorHelper color constant.
  	 */
  	 public void setForegroundColor(String color) {
  	 	myFgColor=color;
  	 }//setForegroundColor
  	 
  	/**
  	 * Mutator method for the BackgroundColor property.
  	 *
  	 * @param color String, valid if it is a ColorHelper color constant.
  	 */
  	 public void setBackgroundColor(String color) {
  		myBgColor=color;
  	 }//setBackgroundColor
  	 
  	 
   
 	/**
 	 * Method that draws the statusbar on the screen.
 	 */
	 public void draw(){
	 		myIO.storeCursor();
	 		myIO.setCursor(myIO.getRows(),1);
	 		myIO.write(getBar());
	 		myIO.restoreCursor();
	 }//draw


	/**
	 * Internal method that creates the true titlebarstring displayed
	 * on the terminal. 
	 */
	 private String getBar() {
	 	String tstatus=myStatus;
	 	//get actual screen width
	 	int width=myIO.getColumns()-1;
	 	//get actual statustext width
	 	int textwidth=(int) ColorHelper.getVisibleLength(myStatus);
	 	
	 	if(textwidth>width) tstatus=myStatus.substring(0,width);
	 	textwidth=(int) ColorHelper.getVisibleLength(tstatus);
	 	
	 	//prepare a buffer with enough space
	 	StringBuffer bar=new StringBuffer(width+textwidth);
		switch(myAlign) {
			case ALIGN_LEFT:
				bar.append(tstatus);
				appendSpaceString(bar,width-textwidth);
				break;
			case ALIGN_RIGHT:
				appendSpaceString(bar,width-textwidth);
				bar.append(tstatus);
				break;
			case ALIGN_CENTER:
				int left=((width-textwidth!=0)? ((width-textwidth)/2):(0));
				int right=width-textwidth-left;
				appendSpaceString(bar,left);
				bar.append(tstatus);
				appendSpaceString(bar,right);	
		}
		return ColorHelper.boldcolorizeText(bar.toString(),myFgColor,myBgColor);
	 }//getBar

	
	 private void appendSpaceString(StringBuffer sbuf,int length) {
	 	for (int i=0;i<length;i++) {
	 		sbuf.append(" ");
	 	}
	 }//appendSpaceString


 // Constant definitions
  public static final int ALIGN_RIGHT=1;
  public static final int ALIGN_LEFT=2;
  public static final int ALIGN_CENTER=3;
  


}//class Statusbar

