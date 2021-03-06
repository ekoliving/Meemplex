/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import org.openmaji.meem.Facet;

/**
 * <p>
 * The Device facet is implented by a Wedge that represents a hardware device
 * on some sort of network. Each device has a unique address
 * and its current condition is represented by its health and by its state.
 * </p>
 * 
 * <p>
 * This facet has been designed to be symetric so can be used for both inbound
 * and outbound messages.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public interface Device extends Facet
{
  /**
   * Tells the wedge that it should change its device description.
   * 
   * @param description The new device description.
   */
  void descriptionChanged(DeviceDescription description);
}
