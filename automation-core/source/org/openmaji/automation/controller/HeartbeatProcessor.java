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
 * A HeartbeatProcessor may either passively sit and waits to receive heartbeats from
 * a network or it may actively poll a network for its state of health when there is
 * no heartbeat available. For example, nodes in
 * a Lontalk network can individually generate heartbeats. However some networks can
 * not do so, for example X10, and so some active polling may be required.
 * </p>
 * <p>
 * If the heartbeat processor fails to receive heartbeats from the network, because
 * the hardware network is unavailable, the heartbeat processor becomes LOADED
 * rather then READY. In such a case any Meems that have a dependency on
 * a HeartbeatProcessor will also go LOADED thus providing an easy means of reflecting
 * the current health of a network. 
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface HeartbeatProcessor extends Facet
{
}

