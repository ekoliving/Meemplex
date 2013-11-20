/*
 * Copyright 2008 (c) by ekoLiving, Pty. Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of ekoLiving Pty. Ltd.
 * Use is subject to license terms.
 */

/*
 * PulseTimerWedge produces periodic Unary triggers or Binary/Linear pulses.
 *
 * The "pulseWidth (ms)" and the "timePeriod (ms)" between pulses can be
 * configured.  The "timePeriod (ms) can also be a varying random value.
 * The PulseTimeWedge output can be inverted.
 *
 * To Do
 * - Fix up BooleanConfigurationSpecification so that it works properly ?
 * - Consider providing a "maximum count" configuration parameter.
 * - Consider providing an "end date" configuration parameter.
 *
 *   Note: Terminating this Meem under various conditions could be
 *         performed by other specialized Meems, via the Unary Input Facet.
 */

package org.openmaji.implementation.util.wedge;

import java.util.Random;

import org.openmaji.common.*;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PulseTimerWedge implements Runnable, Unary, Wedge {

  private static Logger logger = Logger.getAnonymousLogger();

/* -------- Facets --------------------------------------------------------- */

// Unary out-bound Facet

  public Unary unaryOutput;

// Binary out-bound Facet

  public Binary binaryOutput;

// Linear out-bound Facet

  public Linear linearOutput;

/* -------- Conduits ------------------------------------------------------- */

// Conduit for sending configuration changes

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

// Conduit through which we are alerted to life cycle changes

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

/* -------- Configurable properties ---------------------------------------- */

  public boolean debug = false;

  public transient ConfigurationSpecification debugFlagSpecification =
    new IntegerConfigurationSpecification("Debug mode on=1 off=0", LifeCycleState.READY);

  public boolean invert = false;

  public transient ConfigurationSpecification invertFlagSpecification =
    new IntegerConfigurationSpecification("Invert output", LifeCycleState.READY);

  public int pulseWidth = 30;  // milliseconds

  public transient ConfigurationSpecification pulseWidthSpecification =
    new IntegerConfigurationSpecification("Width of pulse (milliseconds)", LifeCycleState.READY);

  public boolean random = false;

  public transient ConfigurationSpecification randomFlagSpecification =
    new IntegerConfigurationSpecification("Random time period", LifeCycleState.READY);

  public int timePeriod = 60000;  // milliseconds

  public transient ConfigurationSpecification timePeriodSpecification =
    new IntegerConfigurationSpecification("Pulse period time (milliseconds)", LifeCycleState.READY);

/* -------- Private properties --------------------------------------------- */

  private Random randomize = new Random();

  private Thread thread;

/* -------- Configuration methods ------------------------------------------ */

  public Integer getDebugFlag() {
    return(Integer.valueOf(debug  ?  1  :  0));
  }

  public void setDebugFlag(
    Integer debugFlag) {

    debug = (debugFlag.intValue() == 1);
  }

  public Integer getInvertFlag() {
    return(Integer.valueOf(invert  ?  1  :  0));
  }

  public void setInvertFlag(
    Integer invertFlag) {

    debug = (invertFlag.intValue() == 1);
  }

  public void setPulseWidth(
    Integer pulseWidth) {

    this.pulseWidth = pulseWidth;
  }

  public Integer getRandomFlag() {
    return(Integer.valueOf(random  ?  1  :  0));
  }

  public void setRandomFlag(
    Integer randomFlag) {

    random = (randomFlag.intValue() == 1);
  }

  public void setTimePeriod(
    Integer timePeriod) {

    this.timePeriod = timePeriod;
  }

/* -------- LifeCycle methods ---------------------------------------------- */

  public void commence() {
    if (thread == null) { 
      thread = new Thread(this);
      thread.start();
    }
  }

  public synchronized void conclude() {
    thread = null;
    this.notifyAll();
  }

  public void validate()
    throws WedgeValidationException {

    if (pulseWidth >= timePeriod) {
      throw new WedgeValidationException(
        "'Pulse width' must be less than the 'Time period'"
      );
    }
  }
/* -------- Main methods --------------------------------------------------- */

  public void run() {
    try {
      synchronized(this) {
        while (thread == Thread.currentThread()) {
          long timeStart = System.currentTimeMillis();
          long timeSleep = timePeriod;          
          if (random) timeSleep = Math.abs(randomize.nextLong()) % timePeriod; 

          if (debug) logger.log(Level.INFO, "Pulse generated, next pulse in " + timeSleep + " ms");

          stateChanged(true);
          wait(pulseWidth);
          stateChanged(false);

          long timeWait = timeSleep + timeStart - System.currentTimeMillis();
          wait(timeWait > 1000  ?  timeWait  :  1000);
        }
      }
    }
    catch (InterruptedException interruptedException) {}
  }

  public void stateChanged(
    boolean state) {
    
    if (state) unaryOutput.valueChanged();  // only trigger Unary, when "true"

    if (invert) state = ! state;
    
    binaryOutput.valueChanged(state);
    linearOutput.valueChanged(new IntegerPosition(state ? 100 : 0, 1, 0, 100));
  }

  public synchronized void valueChanged(){
    conclude();
  }
}