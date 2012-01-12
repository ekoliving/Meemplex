/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.util.wedge;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openmaji.common.StringValue;
import org.openmaji.common.Variable;
import org.openmaji.diagnostic.Debug;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.definition.WedgeDefinitionUtility;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationRejectedException;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.system.manager.thread.ThreadManager;

/**
 * Iterates through a directory of images and sends each one in turn.
 */
public class FilePickerWedge implements Wedge, SpeedControl, WedgeDefinitionProvider {
	private static final int PAUSED = 0;

	private static final int RUNNING = 1;

	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemContext meemContext;

	/* --------------------- conduits ------------------------------- */
	
	public Variable variableStateConduit;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public ThreadManager threadManagerConduit;

	public Vote lifeCycleControlConduit;

	public Debug debugConduit = new MyDebugConduit();

	/* ----------------------- persisted properties --------------------------- */
	
	public String directory;

	public String pattern = "\\w*\\.jpg";

	public volatile int sleeptime = 5;

	public volatile int index;

	public File file;

	public int state = RUNNING;

	
	/* ---------------------------- config specs ----------------------------- */
	
	public transient ConfigurationSpecification directorySpecification = new ConfigurationSpecification("Directory containing images", String.class,
			LifeCycleState.READY);

	public transient ConfigurationSpecification patternSpecification = new ConfigurationSpecification("Regular expression to match files", String.class,
			LifeCycleState.READY);

	public transient ConfigurationSpecification sleeptimeSpecification = new ConfigurationSpecification("Seconds to wait before each update", Integer.class,
			LifeCycleState.READY);

	
	/* ------------------------- private members ----------------------------- */
	
	private String[] images;

	private int debugLevel;

	private volatile boolean stop = false;

	public String getDirectory() {
		return directory;
	}

	public String getPattern() {
		return pattern;
	}

	public int getSleeptime() {
		return sleeptime;
	}

	public void setDirectory(String newValue) throws ConfigurationRejectedException {
		// First check to see if newValue is a URI
		URI uri = null;
		try {
			uri = new URI(newValue);
			file = new File(uri);
			verifyDirectory(file);
		}
		catch (URISyntaxException ex) {
			// Maybe it's just a regular filename
			file = new File(newValue);
			verifyDirectory(file);
		}

		directory = file.getAbsolutePath();
	}

	public void setPattern(String newValue) throws ConfigurationRejectedException {
		try {
			Pattern.compile(newValue);
		}
		catch (PatternSyntaxException ex) {
			throw new ConfigurationRejectedException("Bad regular expression");
		}
		pattern = newValue;
	}

	public void setSleeptime(Integer newValue) {
		sleeptime = newValue.intValue();
	}

	/* -------------------- FilePickerController ------------------------------ */

	public void backward() {
		state = PAUSED;
		sendPreviousImage();
	}

	public void forward() {
		state = PAUSED;
		sendNextImage();
	}

	public void pause() {
		if (state == PAUSED) {
			state = RUNNING;
			sleeptime = 5;
		}
		else {
			state = PAUSED;
		}
	}

	public void slowDown() {
		sleeptime = sleeptime + 5;
		if (sleeptime > 30) {
			sleeptime = 30;
		}

	}

	public void speedUp() {
		sleeptime = sleeptime - 5;
		if (sleeptime < 1) {
			sleeptime = 1;
		}
	}

	/* -------------------- WedgeDefinitionProvider interface --------------------- */

	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition definition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(definition, "speedControl", "speedControlInput");
		return definition;
	}

	/* -------------------- lifecyclefunctionality ---------------------------- */

	public void commence() {
		if (directory == null) {
			logger.log(Level.INFO, "The directory property has not been set");
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}

		scanDirectory();

		Runnable runnable = new DirectoryWatcher();
		stop = false;
		threadManagerConduit.queue(runnable, System.currentTimeMillis() + sleeptime * 1000);
	}

	public void conclude() {
		stop = true;
	}

	/* ---------------------- private methods --------------------------------- */

	private void verifyDirectory(File file) throws ConfigurationRejectedException {
		if (!file.exists()) {
			throw new ConfigurationRejectedException("That directory does not exist");
		}
		if (!file.isDirectory()) {
			throw new ConfigurationRejectedException("That is not a directory");
		}
	}

	private void scanDirectory() {
		if (state == PAUSED) {
			return;
		}

		FilenameFilter filter = new MyFilenameFilter(pattern);
		String[] temp = file.list(filter);
		if (images == null || temp.length != images.length) {
			images = temp;
			index = 0;
			Arrays.sort(images);
			logger.info("Found " + images.length + " images in '" + directory + "'");
		}

		if (images.length == 0) {
			if (debugLevel > 0) {
				logger.info("No files matched");
			}
			return;
		}

		sendNextImage();
	}

	private void sendNextImage() {
		sendImage(1);
	}

	private void sendPreviousImage() {
		sendImage(-1);
	}

	private void sendImage(int direction) {
		index = index + direction;
		if (index >= images.length) {
			index = 0;
		}
		else if (index < 0) {
			index = images.length - 1;
		}

		if (debugLevel > 0) {
			logger.info(images[index]);
		}

		StringBuffer fileAsUrl = new StringBuffer();
		fileAsUrl.append("file:");
		fileAsUrl.append(directory);
		fileAsUrl.append(File.separator);
		fileAsUrl.append(images[index]);

		variableStateConduit.valueChanged(new StringValue(fileAsUrl.toString()));
	}

	/* ------------------------------------------------------------------------ */

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}

	private class MyFilenameFilter implements FilenameFilter {
		private Pattern pattern;

		public MyFilenameFilter(String patternString) {
			pattern = Pattern.compile(patternString);
		}

		public boolean accept(File dir, String name) {
			return pattern.matcher(name).matches();
		}
	}

	private class DirectoryWatcher implements Runnable {
		public void run() {
			if (stop) {
				return;
			}

			scanDirectory();

			long delayMillis = System.currentTimeMillis() + sleeptime * 1000;
			threadManagerConduit.queue(this, delayMillis);
		}
	}

}
