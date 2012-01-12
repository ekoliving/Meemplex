/*
 * @(#)Titlebar.java
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
 * Class that implements a titlebar, for the top of the
 * Terminal Window.
 * 
 * @author Dieter Wimberger
 * @version 1.0 09/03/2000
 */
 public class Titlebar extends InertComponent {
   
   //Members
   private String myTitle; 
   private int myAlign;
   private String myBgColor;
   private String myFgColor;
   
   
   /**
    * Constructor for a simple titlebar instance.
    */
    public Titlebar(BasicTerminalIO io, String name) {
   		super(io,name);
   	}//constructor
   

 	/**
 	 * Mutator method for the titletext property of the titlebar component.
 	 * 
 	 * @param text title String displayed in the titlebar.
 	 */
 	 public void setTitleText(String text) {
 	 	myTitle=text;
 	 }//setTitleText
 
 	/**
 	 * Accessor method for the titletext property of the titlebar component.
 	 * 
 	 * @return String that is displayed when the bar is drawn.
 	 */ 
 	 public String getTitleText() {
 	 	return myTitle;
 	 }//getTitleText
   
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
 	 * Method that draws the titlebar on the screen.
 	 */
	 public void draw(){
	 		myIO.storeCursor();
	 		myIO.homeCursor();
	 		myIO.write(getBar());
	 		myIO.restoreCursor();
	 }//draw


	/**
	 * Internal method that creates the true titlebarstring displayed
	 * on the terminal. 
	 */
	 private String getBar() {
	 	String ttitle=myTitle;
	 	//get actual screen width , remove the correction offset
	 	int width=myIO.getColumns()-1;
	 	//get actual titletext width
	 	int textwidth=(int) ColorHelper.getVisibleLength(myTitle);
	 	
	 	if(textwidth>width) ttitle=myTitle.substring(0,width);
	 	textwidth=(int) ColorHelper.getVisibleLength(ttitle);
	 	
	 	//prepare a buffer with enough space
	 	StringBuffer bar=new StringBuffer(width+textwidth);
		switch(myAlign) {
			case ALIGN_LEFT:
				bar.append(ttitle);
				appendSpaceString(bar,width-textwidth);
				break;
			case ALIGN_RIGHT:
				appendSpaceString(bar,width-textwidth);
				bar.append(ttitle);
				break;
			case ALIGN_CENTER:
				int left=((width-textwidth!=0)? ((width-textwidth)/2):(0));
				int right=width-textwidth-left;
				appendSpaceString(bar,left);
				bar.append(ttitle);
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
  


}//class Titlebar

