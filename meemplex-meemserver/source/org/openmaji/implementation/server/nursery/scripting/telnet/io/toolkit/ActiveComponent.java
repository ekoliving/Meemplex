/*
 * @(#)ActiveComponent.java
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
 * Class that represents an abstract active toolkit component. 
 * Components derived from this class can be activated and will 
 * interactively do i/o.  
 */
 public abstract class ActiveComponent extends Component {
   
   /**
    * Contructs an active toolkit component
    */
    public ActiveComponent(BasicTerminalIO io, String name) {
   		super(io,name);	
    }//constructor
   
   /**
    * Method to make the instance the active object
    */
    abstract public void run();
 
 }//class ActiveComponent

