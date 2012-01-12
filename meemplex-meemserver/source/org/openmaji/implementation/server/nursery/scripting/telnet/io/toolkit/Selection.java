/*
 * @(#)Selection.java
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

import java.util.Vector;

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;



/**
 * @author Dieter Wimberger
 * @version 1.0 04/03/2000
 */ 
 public class Selection extends ActiveComponent {
   
   //Members & Associations
   private Vector myOptions;
   private int selected;
   private int lastselected;
  
	/**
	 * Constructs a Selection instance.
	 *
	 * @param io Object instance implementing the BasicTerminalIO interface.
	 * @param name String representing this instances name.
	 */   
	 public Selection(BasicTerminalIO io, String name) {
   		super(io,name);	
   		myOptions=new Vector(10,5);
   		lastselected=0;
   		selected=0;
     }//constructor
   	
	/**
	 * Method to add an Option to a Selection instance
	 *
	 * @param str String representing the option.
	 */   	
   	 public void addOption(String str) {
   		myOptions.addElement(str);
   	 }//addOption
   	
  /**
	 * Method to insert an Option to a Selection instance at a specific
	 * index. Falls back to add, if index is corrupt.
	 *
	 * @param str String representing the option.
	 * @param index int representing the desired index. 
	 */ 
   	 public void insertOption(String str,int index) {
   		try {
   			myOptions.insertElementAt(str,index);
   	 	} catch (ArrayIndexOutOfBoundsException aex){
   	 		addOption(str);
   	 	}
   	 }//insertOption
   	
  /**
	 * Method to remove an existing Option from a Selection instance.
	 *
	 * @param str String representing the option.
	 */ 
   	 public void removeOption(String str) {
   		for(int i=0;i<myOptions.size();i++){
   			if (((String)myOptions.elementAt(i)).equals(str)){
   				removeOption(i);
   				return;
   			}
   		}
   	 }//removeOption
   	
 	/**
	 * Method to remove an existing Option from a Selection instance.
	 * Does nothing if the index is corrupt.
	 * 
	 * @param index int representing the options index.
	 */ 
   	 public void removeOption(int index){
   		try {
   			myOptions.removeElementAt(index);
   	 	} catch (ArrayIndexOutOfBoundsException aex){
   	 		//nothing
   	 	}		
   	 }//removeOption
   	
 	/**
	 * Accessor method for an option of this selection.
	 * Returns null if index is corrupt.
	 *  
	 * @param index int representing the options index.
	 * @return Strnig that represents the option.
	 */ 
   	 public String getOption(int index){
   		try {
   			Object o=myOptions.elementAt(index);
   			if(o!=null) {
   				return (String)o;
   			}
   	 	} catch (ArrayIndexOutOfBoundsException aex){
   	 		//nothing
   	 	}
   	 	return null;
   	 }//getOption
   	
   	
 	/**
	 * Accessor method to retrieve the selected option.
	 * Returns -1 if no option exists.
	 *  
	 * @return index int representing index of the the selected option.
	 */ 	
     public int getSelected(){
   		return selected;
     }//getSelected
   	
   	
  /**
	 * Mutator method to set selected option programatically.
	 * Does nothing if the index is corrupt.
	 * 
	 * @param index int representing an options index.
	 */  	
   	 public void setSelected(int index){
   		if(index<0 || index>myOptions.size()) {
   			return;
   		} else {
   			lastselected=selected;
   			selected=index;
   			//needs redraw
   			draw();
   		}
   	 }//setSelected
   	
 	/**
	 * Method that will make the selection active,
	 * reading and processing input.
	 */
   	 public void run() {
   		int in=0;
   		draw();
			myIO.flush();
   		do {
  			//get next key
  			in=myIO.read();
  			switch(in) {
  				case BasicTerminalIO.LEFT:
  				case BasicTerminalIO.UP:
  					if(!selectPrevious()) {
  						myIO.bell();	
  					}
  					break; 
  				case BasicTerminalIO.RIGHT:
  				case BasicTerminalIO.DOWN:
  					if(!selectNext()){
  						myIO.bell();
  					}
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
	
		String opttext=getOption(selected);
		int diff=getOption(lastselected).length()-opttext.length();
		
		//System.out.println("DEBUG: selected="+selected+"/"+opttext.length()+" last="+lastselected+"/"+lastlength+" diff="+diff);
		if(diff>0){
			StringBuffer sbuf=new StringBuffer();
				sbuf.append(opttext);
				for (int i=0;i<diff;i++) {
					sbuf.append(" ");
				}
				opttext=sbuf.toString();
		}
		
		if(myPosition != null) {
			myIO.setCursor(myPosition.getRow(),myPosition.getColumn());
		}	
		myIO.write(opttext);
		myIO.moveLeft(opttext.length());
	 }//draw

	
	private boolean selectNext() {
		if (selected<(myOptions.size()-1)) {
			setSelected(selected+1);
			return true;
		} else {
			return false;
		}
	}//selectNext
	
	
	private boolean selectPrevious() {
		if (selected>0) {
			setSelected(selected-1);
			return true;
		} else {
			return false;
		}
	}//selectPrevious
	
 public static final int ALIGN_LEFT=1;
 public static final int ALIGN_RIGHT=2;


}//class Selection


