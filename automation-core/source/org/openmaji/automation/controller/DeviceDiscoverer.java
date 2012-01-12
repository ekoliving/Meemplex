/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.controller;

import org.openmaji.meem.Facet;

/**
 * <p>
 * The DeviceDiscoverer facet is implented by Wedges that will respond to a request to
 * discover all of the devices on a network.
 * A DeviceDiscoverer may actively scan a network or may passively listen for
 * devices to arrive or depart.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface DeviceDiscoverer extends Facet
{
  /**
   * Initiates a network discovery.
   */

  public void discoverDevices();
}

