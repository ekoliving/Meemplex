/*
 * @(#)InertComponent.java
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
 * Class that represents an abstract inert toolkit component.
 * Components derived from this class are simply decorative or
 * informative. They are not supposed to be actively processing and reacting
 * on Input.
 * 
 * @author Dieter Wimberger
 * @version 1.0
 */
 public abstract class InertComponent extends Component {
   
	/**
	 * Constructor for an InertComponent instance.
	 */  
     public InertComponent(BasicTerminalIO io, String name) {
   		super(io,name);
     }//InertComponent

 }//class InertComponent

