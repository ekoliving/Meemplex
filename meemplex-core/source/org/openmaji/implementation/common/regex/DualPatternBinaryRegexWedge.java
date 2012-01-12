/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
 * The DualPatternBinaryRegexWedge Wedge is configurable with a pair of 
 * Java Regex patterns, representing the results of "true" and "false"
 * respectively when matched against the incoming Variable facet.
 * </p>
 * <p>
 * This wedge has an initial (arbitrary) state of false, but from then on,
 * will only transition between states when a match occurs on either of 
 * the configured patterns.
 * </p>
 * <p>
 * There are three configurable properties.
 * </p>
 * <p>
 * <b>truePattern<b> must be set to the pattern representing a "true" result
 * that the wedge attempts to match against the variable inbound facet.
 * </p>
 * <p>
 * <b>falsePattern<b> must be set to the pattern representing a "false" result
 * that the wedge attempts to match against the variable inbound facet.
 * </p>
 * <p>
 * <b>exactMatch</b> defaults to false. 
 * If set true, the wedge only matches if the pattern is an exact match for the
 * entire value. 
 * If set false, the wedge will allow a match of the pattern on any part of the
 * input value.
 * </p>
 * 
 * <p>
 * In the case where both the true pattern and the false pattern match, the
 * result will be indeterminate (actually false). Use of this wedge is 
 * recommeneded for situations where the data set being matched can be divided 
 * into mutually exclusive pattern sets.
 * </p>
 * 
 * TODO[ben]: Refactor so the bulk of the code common to single and dual pattern 
 * binary regex meems is not duplicated.
 * 
 * @author Ben Stringer
 * @version 1.0
 */

public class DualPatternBinaryRegexWedge implements Wedge, Variable {

	private static final Logger logger = LogFactory.getLogger();

	/*
	 * Binary client (out-bound Facet)
	 */
	
	public Binary binaryClient;

	/*
	 * Transient boolean state maintained by this Wedge
	 */

	private boolean state;

	/*
	 * Regex patterns to search for
	 */

	private static Pattern compiledTruePattern;
	
	private static Pattern compiledFalsePattern;

	/*
	 * The conduit through which this Wedge alerts errors in configuration
	 * changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(
			this);

	/*
	 * The conduit through which we are alerted to life cycle changes
	 */

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(
			this);

	public String truePattern;
	
	public transient ConfigurationSpecification truePatternSpecification = new StringConfigurationSpecification(
			"The Regular Expression pattern representing a result of TRUE",
			LifeCycleState.READY);

	
	public String falsePattern;
	
	public transient ConfigurationSpecification falsePatternSpecification = new StringConfigurationSpecification(
			"The Regular Expression pattern representing a result of FALSE",
			LifeCycleState.READY);

	public boolean exactMatch;

	public transient ConfigurationSpecification exactMatchSpecification = new BooleanConfigurationSpecification(
			"If true, pattern must match entire value. If false, pattern can partially match value",
			LifeCycleState.READY);

	public boolean regexMatch(Value value, Pattern compiledPattern) {

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
		
		if (regexMatch(value, compiledTruePattern) ) {
			state = true;
			binaryClient.valueChanged(state);
		}
		if (regexMatch(value, compiledFalsePattern) ) {
			state = false;
			binaryClient.valueChanged(state);
		}
		
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "valueChanged() - " + state);
	}

	public void setTruePattern(String truePattern) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "setTruePattern() - invoked");
		this.truePattern = truePattern;
		compiledTruePattern = Pattern.compile(truePattern);
	}
	
	public void setFalsePattern(String falsePattern) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "setFalsePattern() - invoked");
		this.falsePattern = falsePattern;
		compiledFalsePattern = Pattern.compile(falsePattern);
	}
	
	public void setExactMatch(boolean exactMatch) {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "setPattern() - invoked");
		this.exactMatch = exactMatch;
	}

	public void validate() throws WedgeValidationException {
		if (DebugFlag.TRACE)
			LogTools.trace(logger, 20, "validate() - invoked");
		if (truePattern == null || falsePattern == null) {
			throw new WedgeValidationException(
					"Both patterns not configured yet, can not go READY.");
		}
	}
	
	public void commence() {
		/*
		 * Initialise the regex patterns used when matching.
		 */
		
		compiledTruePattern = Pattern.compile(truePattern);
		compiledFalsePattern = Pattern.compile(falsePattern);
	}
	
}