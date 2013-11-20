/*
 * @(#)Checkbox.java
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
 * Class that implements a Checkbox component.
 *
 * @author Dieter Wimberger
 * @version 1.0 05/03/2000
 */
public class Checkbox extends ActiveComponent {

	//Members
	private String myText=""; 
    private boolean selected=false;
    private String myMark;
    private String leftbracket;
	private String rightbracket;
    
    
	/**
	 * Constructs a checkbox instance.
	 */
     public Checkbox(BasicTerminalIO io, String name) {
   		super(io,name);	
   		setBoxStyle(SQUARED_BOXSTYLE);
   		setMarkStyle(LARGE_CHECKMARK);
     }//constructor
   	
   	/**
   	 * Method to set the checkbox`s state.
   	 *
   	 * @param b boolean that represents the state (true equals selected, false equals not selected).
   	 */ 
     public void setSelected(boolean b) {
   		selected=b;
   		drawMark();
     }//setSelected
   
    /**
     * Accessor method for the state of the checkbox instance.
     * 
     * @return boolean that represents the state (true equals selected, false equals not selected).
     */
     public boolean isSelected(){
   		return selected;
     }//isSelected
   
    /**
     * Mutator method for the text property of the checkbox item.
     * This text will be placed like a label, naturally you can
     * leave this text empty and place a label if used in a form.
     * 
     * @param str String that represents the text that will be displayed right
     *        of the checkbox.
     */
     public void setText(String str){
   		myText=str;
     }//setText
   
    /**
     * Mutator method for the boxstyle property of the checkbox.
     * The *_BOXSTYLE constants should be passed as parameter.
     * 
     * @param style int that represents one of the defined constants for boxstyles.
     */ 
     public void setBoxStyle(int style){
    	switch (style){
    		case ROUND_BOXSTYLE:
    			leftbracket="(";
    			rightbracket=")";
    			break;
    		case EDGED_BOXSTYLE:
    			leftbracket="<";
    			rightbracket=">";
    			break;
    		case SQUARED_BOXSTYLE:
    		default:
    			leftbracket="[";
    			rightbracket="]";
    	}
     }//setBoxStyle
   
	/**
	 * Mutator method for the markstyle property of the checkbox.
	 * The *_CHECKMARK constants should be passed as parameter.
	 *
	 * @param style int that represents one of the defined constants for checkmarks. 
	 */
     public void setMarkStyle(int style){
    	switch (style) {
    		case SMALL_CHECKMARK:
    			myMark="x";
    			break;
    		case LARGE_CHECKMARK:
    		default:
    			myMark="X";
    	}
     }//setMarkStyle
	
	/**
	 * Method that will make the checkbox active, reading and processing input.
	 */
	 public void run(){
		int in=0;
   		draw();
			myIO.flush();
   		do {
  			//get next key
  			in=myIO.read();
  			switch(in) {
  				case SPACE:
  					setSelected(!selected);	//toggle actual state, will redraw mark
  					break; 
  				case BasicTerminalIO.TABULATOR:
  				case BasicTerminalIO.ENTER:
  					in=-1;
  					break;
  				default:
  					myIO.bell();
  			}
  			myIO.flush();
  		} while (in!=-1);
	 }//run
	
	/**
     * Method that draws the component.
     */
	 public void draw(){
		StringBuffer sbuf=new StringBuffer();
		sbuf.append(" ");			//1/1
		sbuf.append(leftbracket);	//1/2
		if(selected) {
			sbuf.append(myMark);	//1/3
		} else {
			sbuf.append(" ");
		} 
		sbuf.append(rightbracket);	//1/4
		sbuf.append(" ");			//1/5
		sbuf.append(myText);		//1/5+myText.length
		
		if(myPosition!=null){
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
		}
		
		myIO.write(sbuf.toString());
		myIO.moveLeft(3+myText.length()); //thats the mark position
	 	myIO.flush();
	 }//draw

	 private void drawMark(){
		if(myPosition!=null){
			 myIO.storeCursor();
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
			myIO.moveRight(2);
		}
		if (selected) {
			myIO.write(myMark);
		} else {
			myIO.write(" ");
		}
		if(myPosition==null) {
			myIO.moveLeft(1); //back to mark position
		} else {
			myIO.restoreCursor();
		}
		myIO.flush();
	 }//drawMark


 public static final int SMALL_CHECKMARK=10;
 public static final int LARGE_CHECKMARK=11;

 public static final int SQUARED_BOXSTYLE=1;
 public static final int ROUND_BOXSTYLE=2;
 public static final int EDGED_BOXSTYLE=3;
 private static final int SPACE=32;

}//class Checkbox


