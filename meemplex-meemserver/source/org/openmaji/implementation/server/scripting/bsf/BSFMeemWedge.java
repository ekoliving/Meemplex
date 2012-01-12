/*
 * @(#)BSFMeemWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider calling this a "ScriptEngine", rather than "BSFMeem".
 *
 * - Determine why BSFMeem doesn't work in InterMajik Eclipse plug-in.
 *
 * - Plagarize "mr-sw-administration/source/beanshell/*.bsh" some more.
 *
 * - Currently, the BeanShell is keeping the JVM running.  However, in
 *   time the BeanShell should become a daemon thread.  The correct
 *   way to keep the JVM running is to do so, as long as there are
 *   "loaded" or "ready" Meems in the LifeCycleTree.
 *
 * - Implement the conclude() method.
 *   It appears that there is no easy way to conclude() bsh.util.Sessiond.
 *   Will probably have to implement our own, better version.  Sigh.
 *
 * - Properly assess the manner in which bsh.util.Sessiond actually works.
 *   Sessiond should become a daemon Thread.
 *   Should be able to list and kill any BeanShell connections.
 *   Will probably have to implement our own, better version.  Sigh.
 *
 * - As a temporary measure, the BSFMeemWedge is providing a minimal number
 *   of "logging hooks", for external scripts.  As soon as possible, these
 *   "logging hooks" should be implemented within the Log Meem.
 *
 * - Provide a means of adding variables to the BeanShell NameSpace, that
 *   doesn't clobber previously entered variables.  For example, add an
 *   incrementing integer to the variable name.
 *
 * - Allow the BeanShell initialization script pathname to be configured.
 *
 * - Create a utility method to read integer properties - use for reading
 *   beanShellPort
 */

package org.openmaji.implementation.server.scripting.bsf;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.openmaji.implementation.server.Common;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * BSFMeemWedge provides an interactive means of scripting the Maji system.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class BSFMeemWedge implements BSFMeem, MeemDefinitionProvider, Wedge {

	protected static Logger logger = LogFactory.getLogger();

	public MeemContext meemContext;

	public ErrorHandler errorHandlerConduit;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public Vote lifeCycleControlConduit = null;

	protected String myName = null;

	protected Class<?> myClass = null;

	protected String[] commands = null;

	/**
	 * BeanShell directory path
	 */

	protected String beanShellDirectory = DEFAULT_BEANSHELL_DIRECTORY;

	/**
	 * BeanShell initialization script
	 */

	protected String beanShellScript = DEFAULT_BEANSHELL_SCRIPT;

	private BSFManager bsfManager = null;

	private BSFEngine bsfEngine = null;

	public BSFMeemWedge() {
		String propertyBeanShellDirectory = System.getProperty(PROPERTY_BEANSHELL_DIRECTORY);

		if (propertyBeanShellDirectory != null) {
			beanShellDirectory = propertyBeanShellDirectory;
		}

		String propertyMajitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
		propertyMajitekDirectory = propertyMajitekDirectory.replace('\\', '/');

		if (propertyMajitekDirectory != null) {
			beanShellDirectory = propertyMajitekDirectory + beanShellDirectory;
		}

		setCommands();
	}

	/**
	 * Override this method to execute an alternative set of beanshell commands.
	 */
	protected void setCommands() {
		int beanShellPort = DEFAULT_BEANSHELL_PORT;

		commands = new String[] { 
				"cd(\"" + beanShellDirectory + "\");", 
				"source(\"" + beanShellScript + "\");", 
				"import bsh.util.Sessiond;", 
				"new Thread(new org.openmaji.implementation.server.scripting.bsf.Sessiond(global.namespace, " + beanShellPort + ")).start();", 
			};

		myName = "BSFMeemWedge";
		myClass = this.getClass();
	}

	/**
	 * Export an internal Maji object instance.
	 * 
	 * @param name
	 *            Object exported using this name
	 * @param value
	 *            Object instance to export
	 * @param specification
	 *            Object type
	 * @exception BSFException
	 *                BeanShell couldn't evaluate the variable value
	 */

	public void export(String name, Object value, Class specification) throws BSFException {

		bsfManager.declareBean(name, value, specification);
	}

	/**
	 * @param scriptFilename
	 * @exception BSFException
	 */

	public void source(String scriptFilename) throws BSFException {

		bsfEngine.eval(myName, 0, 0, "source(\"" + scriptFilename + "\");");
	}

	/**
	 * Convenience method for creating error logging messages. This "hook" provides an easy way for external scripts to enter a message into the Maji platform log system.
	 * 
	 * @param message
	 *            Error message to be logged
	 */

	public void error(String message) {

		LogTools.error(logger, message);
	}

	/**
	 * Convenience method for creating informational logging messages. This "hook" provides an easy way for external scripts to enter a message into the Maji platform log system.
	 * 
	 * @param message
	 *            Informational message to be logged
	 */

	public void info(String message) {

		LogTools.info(logger, message);
	}

	/**
	 * Convenience method for creating trace logging messages. This "hook" provides an easy way for external scripts to enter a message into the Maji platform log system.
	 * 
	 * @param message
	 *            Trace message to be logged
	 */

	public void trace(String message) {

		LogTools.trace(logger, logLevel, message);
	}

	/**
	 * Convenience method for creating verbose logging messages. This "hook" provides an easy way for external scripts to enter a message into the Maji platform log system.
	 * 
	 * @param message
	 *            Verbose message to be logged
	 */

	public void verbose(String message) {

		LogTools.trace(logger, logLevelVerbose, message);
	}

	/**
	 * Convenience method for creating warning logging messages. This "hook" provides an easy way for external scripts to enter a message into the Maji platform log system.
	 * 
	 * @param message
	 *            Warning message to be logged
	 */

	public void warn(String message) {

		LogTools.warn(logger, message);
	}

	/**
	 * <p>
	 * Initialize BeanShell and start Interpreter Session daemon.
	 * </p>
	 * <p>
	 * Once the BSFMemeImpl is started, an "initialization" script is automagically run that will set-up Maji platform specific stuff, which can make using the BeanShell easier.
	 * </p>
	 * <p>
	 * The BeanShell session daemon accepts incoming "telnet connections". Those "telnet sessions" can then use a comprehensive interactive Java scripting language to access and
	 * manipulate all sort of internal Maji platform details.
	 * </p>
	 * <p>
	 * For more information about BeanShell, see ...
	 * <ul>
	 * <li><a href="http://www.beanshell.org">BeanShell web site</a></li>
	 * </ul>
	 * </p>
	 */

	public void commence() {

		if (commands == null || commands.length == 0) {
			LogTools.info(logger, "Commands array empty - nothing to do");
			return;
		}

		try {
			doCommence();
		}
		catch (BSFException ex) {
			errorHandlerConduit.thrown(ex);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
		}
	}

	private void doCommence() throws BSFException {

		bsfManager = new BSFManager();
		String[] bshFileExtensions = { "bsh" };
		BSFManager.registerScriptingEngine("beanshell", "bsh.util.BeanShellBSFEngine", bshFileExtensions);
		export("log", this, getClass());
		bsfEngine = bsfManager.loadScriptingEngine("beanshell");

		for (int i = 0; i < commands.length; i++) {
			String command = commands[i];
			bsfEngine.eval(myName, 0, 0, command);
		}
	}

	public void conclude() {
		if (bsfEngine != null) {
			// stop the Sessiond thread
			bsfEngine.terminate();
			bsfManager.terminate();
		}
	}

	/* ---------- MeemDefinitionProvider method(s) ---------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { myClass });
		}

		return (meemDefinition);
	}

	/* ---------- Logging fields ---------------------------------------------- */

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static int logLevel = Common.getLogLevel();

	/**
	 * Acquire the Maji system-wide verbose logging level.
	 */

	private static int logLevelVerbose = Common.getLogLevelVerbose();
}