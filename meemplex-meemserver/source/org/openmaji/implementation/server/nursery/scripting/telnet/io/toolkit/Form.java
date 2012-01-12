/*
 * @(#)Form.java
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
 * Class that implements an intelligent generic container for toolkit components
 *
 * @author  mg
 * @version 1.0
 */
public class Form extends ActiveComponent {
  
   protected Vector myComponents;
   
   public Form(BasicTerminalIO myio,String name) {
   		super(myio,name);
   		//we are screen large always
   		setLocation(new Point(0,0));
   		setDimension(new Dimension(myIO.getColumns(),myIO.getRows()));
   }//constructor
 
   
   public void run(){
   		
   }//run
   
   public void draw(){
   		
   }//draw
   
 
 
 }//class Form

