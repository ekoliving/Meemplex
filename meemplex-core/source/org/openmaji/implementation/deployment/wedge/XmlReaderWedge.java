/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.meemplex.meem.ConfigProperty;
import org.meemplex.meem.PropertyType;
import org.openmaji.diagnostic.Debug;
import org.openmaji.implementation.deployment.ProgressConduit;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.system.meemserver.MeemServer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>This Wedge reads an XML file, performs literal expansions of all defined
 * properties within the text of the XML file, constructs a DOM from the
 * resulting new text and places the DOM
 * onto the domProcessorConduit so that other wedges in the Meem can process it.</p>
 * 
 * <p>Properties are defined and used as the following example shows:</p>
 * 
 * <pre style="font-size: smaller; color: blue; margin-left: 1cm;" >
 *   &lt;property name="testpath" value="/work/example/test" /&gt;
 *   &lt;property name="sub1" value="subpath1/aaa/bbb" /&gt;
 *   &lt;property name="sub2" value="subpath2/aaa/bbb" /&gt;
 *
 *   &lt;path&gt;${testpath}/meem1&lt;path&gt;
 *   &lt;path&gt;${testpath}/${sub1}/meem1&lt;path&gt;
 *   &lt;path&gt;${testpath}/${sub2}/meem1&lt;path&gt;
 * </pre>
 * 
 * <p>The following predefined properties are available for use:</p>
 * 
 * <table border="1" cellspacing="0" cellpadding="5" style="font-size: x-small; margin-left: 1cm;" >
 *   <tr>
 *     <th>property</th><th>value</th><th>note</th>
 *   </tr>
 *   <tr>
 *     <td>${hostname}</td>
 *     <td>The hostname on which the meemserver is running</td>
 *     <td>Just the hostname and not the full domain name</td>
 *   </tr>
 *   <tr>
 *     <td>${user}</td>
 *     <td>The user under which the meemserver is running</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>${meemserver}</td>
 *     <td>The name of the meemserver</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>${meemspace}</td>
 *     <td>The name of the meemspace this meemserver is running in</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>${application}</td>
 *     <td>The 'name' attribute of the 'meem-deployment' tag</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${version}</td>
 *     <td>The 'version' attribute of the 'meem-deployment' tag</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${path.site}</td>
 *     <td>The hyperspace path for the deployed "site"</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${path.device}</td>
 *     <td>The hyperspace path for devices</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${path.function}</td>
 *     <td>The hyperspace path for high-level functional meems</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${path.location}</td>
 *     <td>The hyperspace path for meems grouped by location</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 *   <tr>
 *     <td>${path.gui}</td>
 *     <td>The hyperspace path for GUI meems</td>
 *     <td>Only set for a meem deployment XML file</td>
 *   </tr>
 * </table>
 * <p>
 * Note - you must define a property before attempting to use it. Also, you must
 * not declare or use any recursive or nested property definitions.
 * </p>
 * 
 * @author Chris Kakris 
 * @author Warren Bloomer
 */
public class XmlReaderWedge implements Wedge {
	private static final Logger logger = LogFactory.getLogger();
	
	private static final String PROPERTY_MEEMSPACEID = "org.openmaji.meemSpaceIdentifier";


	public MeemContext meemContext;

	
	/* ---------------------------------- conduits ------------------------------- */
	
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public DomProcessor domProcessorConduit;

	public Vote lifeCycleControlConduit;

	public Debug debugConduit = new MyDebugConduit();

	public ProgressConduit progressConduit = new MyProgressConduit();

	
	/* ---------------------------------- persisted properties ------------------------------- */
	
	@ConfigProperty(name="filename", description="Path to file or directory with deployment descriptor files")
	public String filename;

	
	/* ---------------------------------- configuration ------------------------------- */
	
	public transient ConfigurationSpecification filenameSpecification = new ConfigurationSpecification("The full path to the XML file");

	/* ---------------------------------- private members ------------------------------- */
	
	private final Hashtable<String,String> properties = new Hashtable<String,String>();

	private int linenumber;

	private int debugLevel = 1;

	private volatile int completion;

	private volatile int current;

	private String[] filepaths = null;

	private int indexOfCurrentFile;

	private static final boolean DEBUG = true;
	
	public XmlReaderWedge() {
		initialiseProperties();
	}

	public void setFilename(String filepath) throws ConfigurationRejectedException {
		if (filepath == null) {
			throw new ConfigurationRejectedException("Filename not specified");
		}

		doSetFilename(filepath);

		this.filename = filepath;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}

	private void doSetFilename(String filepath) throws ConfigurationRejectedException {
		File file = new File(filepath);
		if (!file.exists()) {
			throw new ConfigurationRejectedException("Can not find '" + filepath + "' - did you spell it wronge?");
		}

		if (file.isDirectory()) {
			filepaths = getDirectoryContents(file, filepath);
		}
		else {
			filepaths = new String[] { filepath };
		}
	}

	private String[] getDirectoryContents(File file, String filepath) throws ConfigurationRejectedException {
		String[] strings = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith(".")) {
					return false;
				}
				return name.endsWith(".xml");
			}
		});

		if (strings.length == 0) {
			throw new ConfigurationRejectedException("Directory doesn't contain any .xml files");
		}

		List<String> list = Arrays.asList(strings);
		Collections.sort(list);

		String[] paths = new String[strings.length];
		int i = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String filename = (String) iterator.next();
			paths[i++] = filepath + File.separatorChar + filename;
		}

		return paths;
	}

	private void commence() {
		initialiseProperties();
		linenumber = 1;
		completion = 0;
		current = 0;
		indexOfCurrentFile = 0;

		// update filepaths
		try {
			doSetFilename(filename);
		}
		catch (Exception e) {
			LogTools.error(logger, "Could not determine filepaths", e);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}

		if (filepaths == null) {
			LogTools.error(logger, "Unable to go READY: filename property not set");
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		processNextDeploymentFile();
	}

	private void processNextDeploymentFile() {
		Element rootElement = null;
		boolean deployed = false;
		if (DEBUG) {
			LogTools.info(logger, "Current file index: " + indexOfCurrentFile + ", length: " + filepaths.length);
		}
		
		while (indexOfCurrentFile < filepaths.length) {
			deployed = checkIfSiteDeployment(filepaths[indexOfCurrentFile]);

			if (deployed) {
				break;
			}
			else {
				indexOfCurrentFile++;
			}
		}

		if (deployed) {
			progressConduit.reset();
			try {
				LogTools.info(logger, "Processing " + filepaths[indexOfCurrentFile]);
				rootElement = readXMLFile(filepaths[indexOfCurrentFile]);
			}
			catch (Exception ex) {
				LogTools.error(logger, "Error processing deployment file, unable to go READY: " + ex.getMessage());
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
				return;
			}

			domProcessorConduit.process(rootElement);
		}

	}

	private Element readXMLFile(String filepath) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(filepath));

		// Get the first tag in the file and see if it is a 'meem-deployment' tag.
		// If it is then we preset some properties from the tag's attributes.

		checkIfMeemDeployment(reader);

		// Perform a single pass over the whole XML document and replace all
		// instances of ${property} and then rewrite the file.

		File file = File.createTempFile("fudge", null);
		file.deleteOnExit();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		Pattern pattern = Pattern.compile("\\s*<property\\s*name=\"(.*)\"\\s*value=\"(.*)\".*");
		String text;
		while ((text = reader.readLine()) != null) {
			if (text.indexOf("${") != -1) {
				text = expandProperties(text);
			}
			Matcher matcher = pattern.matcher(text);
			if (matcher.matches()) {
				String key = "${" + matcher.group(1) + "}";
				String value = matcher.group(2);
				properties.put(key, value);
			}
			writer.write(text);
			writer.write('\n');
			linenumber++;
		}
		writer.close();

		// Read the newly generated XML file, parse it and create a DOM
		// model from it.

		InputStream inputStream = new FileInputStream(file);
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DOMBuilder domBuilder = new DOMBuilder();
		Document document = domBuilder.build(documentBuilder.parse(inputStream));
		return document.getRootElement();
	}

	private void checkIfMeemDeployment(BufferedReader reader) throws Exception {
		reader.mark(512);
		StringBuffer buffer = new StringBuffer();
		String text;
		while ((text = reader.readLine()) != null) {
			buffer.append(text);
			if (text.indexOf('>') != -1) {
				break;
			}
		}
		reader.reset();

		Pattern pattern = Pattern.compile("\\s*<meem-deployment.*name=\"([\\w\\.\\-]*)\".*version=\"([0-9\\.]*)\".*>");
		Matcher matcher = pattern.matcher(buffer.toString());
		if (matcher.matches()) {
			properties.put("${application}", matcher.group(1));
			properties.put("${version}", matcher.group(2));
			if (debugLevel > 0) {
				LogTools.info(logger, "Processing a meem deployment document");
				LogTools.info(logger, "${application}=" + matcher.group(1) + " ${version}=" + matcher.group(2));
			}
		}
	}

	private boolean checkIfSiteDeployment(String filePath) {
		/*
		 * Note: if deployment file doesn't have site-deployment tag, it will
		 * 	   be deployed by default
		 */
		boolean deployed = true;
		Pattern pattern = Pattern.compile("\\s*<site-deployment\\s*name=\"(.*)\".*");
		String sitename = System.getProperty("org.openmaji.meemSpaceIdentifier").trim();
		//LogTools.info(logger, "deploy-site is "+sitename);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String text;
			while ((text = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(text);
				if (matcher.matches()) {
					String tagName = matcher.group(1).trim();
					if (!tagName.equalsIgnoreCase(sitename) && !tagName.equalsIgnoreCase("base")) {
						deployed = false;
					}
				}
			}
		}
		catch (IOException ioe) {
			LogTools.error(logger, "Error processing deployment file, unable to go READY: " + ioe.getMessage());
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
		}

		return deployed;
	}

	/**
	 * Set up properties to be used in the XML. 
	 */
	private void initialiseProperties() {
		properties.clear();
		
		String hostname = "UNDEFINED";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			int index = hostname.indexOf('.');
			if (index != -1) {
				hostname = hostname.substring(0, index);
			}
		}
		catch (UnknownHostException ex) { /* ignore */
		}
		
		String user         = System.getProperty("user.name", "UNDEFINED");
		String meemserver   = System.getProperty(MeemServer.PROPERTY_MEEMSERVER_NAME, "UNDEFINED");
		String meemspace    = System.getProperty(PROPERTY_MEEMSPACEID, "UNDEFINED");
		
		// TODO allow configurable site name, provided in the deployment files.
		String sitePath          = "/site/" + meemspace;
    String configurationPath = sitePath + "/configuration";
		String devicePath        = sitePath + "/device";
		String functionPath      = sitePath + "/function";
		String locationPath      = sitePath + "/location";
		String servicePath       = sitePath + "/service";
    String guiPath           = sitePath + "/gui";
    String guiMagicWandPath  = sitePath + "/gui/magicWand";
		String guiMeemServerPath = sitePath + "/gui/" + meemserver;

		properties.put("${hostname}",            hostname);
		properties.put("${user}",                user);
		properties.put("${meemserver}",          meemserver);
		properties.put("${meemspace}",           meemspace);
		properties.put("${path.site}",           sitePath);
    properties.put("${path.configuration}",  configurationPath);
		properties.put("${path.device}",         devicePath);
		properties.put("${path.function}",       functionPath);
		properties.put("${path.location}",       locationPath);
		properties.put("${path.service}",        servicePath);
		properties.put("${path.gui}",            guiPath);
    properties.put("${path.gui.magicWand}",  guiMagicWandPath);
    properties.put("${path.gui.meemServer}", guiMeemServerPath);
	}

	private String expandProperties(String text) throws Exception {
		while (text.indexOf("${") != -1) {
			int x1 = text.indexOf("${");
			int x2 = text.indexOf('}');
			if (x1 == -1) {
				throw new Exception("Missing closing bracket on line " + linenumber);
			}
			String key = text.substring(x1, x2 + 1);
			String value = (String) properties.get(key);
			if (value == null) {
				throw new Exception("Unknown property " + key + " on line " + linenumber);
			}
			text = text.substring(0, x1) + value + text.substring(x2 + 1);
		}
		return text;
	}

	/**
	 * Test mail method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		XmlReaderWedge xmlReader = new XmlReaderWedge();

		xmlReader.setFilename("c:/majitek-developer/site-brookwater/deployment/gui");

		for (int i = 0; i < xmlReader.filepaths.length; i++) {

			LogTools.info(logger, "Processing " + xmlReader.filepaths[i]);
			xmlReader.readXMLFile(xmlReader.filepaths[i]);
		}
		xmlReader.hashCode();
	}

	/* ---------------------------------------------------------------------- */

	private class LifeCycleClientHandler implements LifeCycleClient {

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.PENDING_READY)) {
				commence();
			}
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}
	}

	/* ---------------------------------------------------------------------- */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}

	/* ---------------------------------------------------------------------- */

	private class MyProgressConduit implements ProgressConduit {
		
		public void reset() {
			if (DEBUG) {
				LogTools.info(logger, "reset progress");
			}
			completion = 0;
			current = 0;
		}

		public synchronized void addCompletionPoints(int points) {
			completion = completion + points;
			if (DEBUG) {
				LogTools.info(logger, "addCompletionPoints: " + points);
				LogTools.info(logger, "  progress: " + current + "/" + completion);
			}
		}

		public synchronized void addProgressPoints(int points) {
			if (DEBUG) {
				LogTools.info(logger, "Updating progress: " + (current + points) + "/" + completion);
				//LogTools.info(logger, "  current: " + current + ", completion = " + completion);
			}
			current = current + points;
			if (current >= completion) {
				indexOfCurrentFile++;
				processNextDeploymentFile();
			}
		}
	}
}
