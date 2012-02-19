/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.controller;

/**
 * <p>
 * The DeviceController facet is implemented by a Wedge that accepts
 * commands issued by Meem devices and either directly interacts with the hardware
 * network or passes the commands onto other Meems that can handle the request.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface DeviceController extends RequestProcessor
{
}

