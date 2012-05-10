/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmaji.common.Binary;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.BooleanConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.StringConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.WedgeValidationException;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * The SinglePatternBinaryRegexWedge Wedge is configurable with a Java Regex 
 * pattern, and will match that pattern against it's inbound Variable facet, and
 * put the boolean result on it's outgoing Binary facet.
 * </p>
 * <p>
 * The initial state of the binary facet is false. The state transitions to
 * true when the pattern is matched in the inbound variable value, and 
 * transitions to false when the pattern is not matched in the inbound variable
 * value.
 * <p>
 * There are two configurable properties.
 * </p>
 * <p>
 * <b>pattern<b> must be set to the pattern that the wedge attempts to match.
 * </p>
 * <p>
 * <b>exactMatch</b> defaults to false. 
 * If set true, the wedge only matches if the pattern is an exact match for the
 * entire value. 
 * If set false, the wedge will allow a match of the pattern on any part of the
 * input value.
 * </p>
 * 
 * TODO[ben]: Fix the InterMajik property editor so it can handle boolean
 * properties. [IMJ-1463]
 * 
 * @author Ben Stringer
 * @version 1.0
 */

public class SinglePatternBinaryRegexWedge implements Wedge, Variable {

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Binary client (out-bound Facet)
	 */
	public Binary binaryClient;

	/**
	 * Transient boolean state maintained by this Wedge
	 */

	private boolean match = false;

	/*
	 * Regex pattern to search for
	 */

	private static Pattern compiledPattern;

	/**
	 * The conduit through which this Wedge alerts errors in configuration
	 * changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(
			this);

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(
			this);

	public String pattern;

	public transient ConfigurationSpecification patternSpecification = new StringConfigurationSpecification(
			"The Regular Expression pattern to attempt to match",
			LifeCycleState.READY);

	public boolean exactMatch;

	public transient ConfigurationSpecification exactMatchSpecification = new BooleanConfigurationSpecification(
			"If true, pattern must match entire value. If false, pattern can partially match value",
			LifeCycleState.READY);

	public boolean regexMatch(Value value) {
		Matcher matcher;
		String input = value.toString();
		matcher = compiledPattern.matcher(input);

		if (exactMatch) {
			return matcher.matches();
		} else {
			return matcher.find();
		}
	}

	public void valueChanged(Value value) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "valueChanged() - invoked");

		match = regexMatch(value);

		LogTools.info(logger, "valueChanged() - " + Boolean.toString(match));

		binaryClient.valueChanged(match);
	}

	public void setPattern(String pattern) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "setPattern() - invoked");
		this.pattern = pattern;
		compiledPattern = Pattern.compile(pattern);
	}

	public void setExactMatch(boolean exactMatch) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "setPattern() - invoked");
		this.exactMatch = exactMatch;
	}

	public void validate(String pattern) throws WedgeValidationException {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "validate() - invoked");
		if (pattern == null) {
			throw new WedgeValidationException(
					"Pattern not configured yet, can not go READY.");
		}
	}
	
	public void commence() {
		/*
		 * Initialise the regex pattern used to when matching.
		 */
		
		compiledPattern = Pattern.compile(pattern);
	}
}