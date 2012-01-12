/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.automation.device.DeviceDescription;
import org.openmaji.automation.device.DeviceType;
import org.openmaji.implementation.automation.subsystem.SubsystemCommissioningWedge;
import org.openmaji.meem.definition.MeemDefinition;


public class LoopbackSubsystemWedge extends SubsystemCommissioningWedge
{
  public LoopbackSubsystemWedge()
  {
    super(
    		new DeviceDescription[] {
    			new LoopbackDeviceDescription(DeviceType.BINARY),
    			new LoopbackDeviceDescription(DeviceType.LINEAR),
    			new LoopbackDeviceDescription(DeviceType.VARIABLE)
    		},
    		new MeemDefinition[] {
    			new LoopbackBinaryMeem().getMeemDefinition(),
    			new LoopbackLinearMeem().getMeemDefinition(),
    			new LoopbackVariableMeem().getMeemDefinition(),
    		},
    		null, 
    		null, 
    		null
    	);
    

  }
}
