/*
 * @(#)TerminalManager.java
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

package org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Class that manages all available terminal implementations.<br>
 * Configuration is stored in a properties file 
 * (normally Terminals.properties).
 * 
 * @author Dieter Wimberger
 * @version 1.0 06/09/2000
 */
public class TerminalManager {

	//Members
	private static TerminalManager self; //Singleton reference
	private Hashtable<String, Terminal> myTerminals; //datastructure for terminals

	/**
	 * Private constructor, instance can only be created
	 * via the public factory method.
	 */
	private TerminalManager() {
		self = this;
		myTerminals = new Hashtable<String, Terminal>(10);
	} //constructor

	/**
	 * Returns a reference to a terminal that has
	 * been set up, regarding to the key given as
	 * parameter.<br>
	 * If the key does not represent a terminal name or
	 * any alias for any terminal, then the returned terminal
	 * will be a default basic terminal (i.e. vt100 without
	 * color support).
	 *
	 * @param key String that represents a terminal name or an alias.
	 *
	 * @return Terminal instance or null if the key was invalid.
	 */
	public Terminal getTerminal(String key) {

		Terminal term = null;

		try {
			if (key.equals("ANSI")) {
				//this is a hack, sorry folks but the *grmpflx* *censored*
				//windoof telnet application thinks its uppercase ansi *brr*
				term = (Terminal) myTerminals.get("windoof");
			} else {
				key = key.toLowerCase();
				if (!myTerminals.containsKey(key)) {
					term = (Terminal) myTerminals.get("default");
				} else {
					term = (Terminal) myTerminals.get(key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return term;
	} //getTerminal

	/**
	 * Loads the terminals and prepares an instance of each.
	 */
	private void setupTerminals(Hashtable terminals) {

		String termname = "";
		String termclass = "";
		Terminal term = null;
		Object[] entry = null;

		for (Enumeration enumeration = terminals.keys(); enumeration.hasMoreElements();) {
			try {
				//first we get the name
				termname = (String) enumeration.nextElement();

				//then the entry
				entry = (Object[]) terminals.get(termname);

				//then the fully qualified class string 
				termclass = (String) entry[0];

				//get a new class object instance (e.g. load class and instantiate it)
				term = (Terminal) Class.forName(termclass).newInstance();

				//and put an instance + references into myTerminals
				myTerminals.put(termname, term);
				String[] aliases = (String[]) entry[1];
				for (int i = 0; i < aliases.length; i++) {
					//without overwriting existing !!!
					if (!myTerminals.containsKey(aliases[i])) {
						myTerminals.put(aliases[i], term);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		//check if we got all
//		for (Enumeration enum = myTerminals.keys(); enum.hasMoreElements();) {
//			String tn = (String) enum.nextElement();
//		}
	} //setupTerminals

	/** 
	 * Factory method for creating the Singleton instance of
	 * this class.
	 * 
	 * @return TerminalManager Singleton instance.
	 */
	public static TerminalManager createTerminalManager() {

		Properties settings = new Properties();
		settings.put("TERMINALS", "vt100,ansi,windoof,xterm");
		
		settings.put("vt100", org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.vt100.class.getName());
		settings.put("vt100.aliases", "default,vt100-am,vt102,dec-vt100");
		
		settings.put("ansi", org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.ansi.class.getName());
		settings.put("ansi.aliases", "color-xterm,xterm-color,vt320,vt220,linux");
		
		settings.put("windoof", org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.Windoof.class.getName());
		settings.put("windoof.aliases", "");
		
		settings.put("xterm", org.openmaji.implementation.server.nursery.scripting.telnet.io.terminal.xterm.class.getName());
		settings.put("xterm.aliases", "");

		Hashtable<String, Object[]> terminals = new Hashtable<String, Object[]>(20); //a storage for class 
		//names and aliases

		boolean defaultFlag = false; //a flag for the default
		TerminalManager tmgr = new TerminalManager();

		//Loading and applying settings
		try {

			//Get the declared terminals
			String terms = settings.getProperty("TERMINALS");
			if (terms == null) {
			}

			//split the names
			String[] tn = terms.split(",");

			//load fully qualified class name and aliases for each
			//storing it in the Hashtable within an objectarray of two slots	
			Object[] entry = null;
			String[] aliases = null;
			for (int i = 0; i < tn.length; i++) {
				entry = new Object[2];
				//load fully qualified classname
				entry[0] = settings.getProperty(tn[i]);
				//load aliases and store as Stringarray
				aliases = settings.getProperty(tn[i] + ".aliases").split(",");
				for (int n = 0; n < aliases.length; n++) {
					//ensure default declared only once as alias
					if (aliases[n].equalsIgnoreCase("default")) {
						if (!defaultFlag) {
							defaultFlag = true;
						} else {
							//throw new BootException("Only one default can be declared.");
						}
					}
				}
				entry[1] = aliases;
				//store
				terminals.put(tn[i], entry);
			}
			if (!defaultFlag) {
				//throw new BootException("No default terminal declared.");
			}

			//construct manager
			tmgr = new TerminalManager();
			tmgr.setupTerminals(terminals);

			return tmgr;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	} //createManager

	/**
	 * Accessor method for the Singleton instance of this class.<br>
	 * Note that it returns null if the instance was not properly
	 * created beforehand.
	 *
	 * @return TerminalManager Singleton instance reference.
	 */
	public static TerminalManager getReference() {
		if (self == null) {
			TerminalManager.createTerminalManager();
		}
		return self;
	} //getReference 

} //class TerminalManager
