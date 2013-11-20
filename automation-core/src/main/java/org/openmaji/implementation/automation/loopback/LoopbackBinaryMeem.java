/*
 *  Copyright 2004 by EkoLiving Pty Ltd.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

import org.openmaji.implementation.automation.common.DeviceWedge;
import org.openmaji.implementation.common.BinaryWedge;

/**
 * A Meem that represents a binary loopback device.
 * This class implements <code>MeemDefinitionProvider</code> so that
 * <code>the MeemkitInstaller</code> can obtain a <code>MeemDefinition</code>
 * from which to create an <code>LoopbackBinaryMeem</code>
 *
 * @author Chris Kakris
 */

public class LoopbackBinaryMeem implements MeemDefinitionProvider
{
  private MeemDefinition meemDefinition = null;

  /**
   * Return a MeemDefinition for this Meem which lists all of the
   * Wedges required to assemble a <code>LoopbackBinaryMeem</code>.
   * 
   * @return The MeemDefinition for this Meem
   * 
	 */
	public MeemDefinition getMeemDefinition()
  {
    if ( meemDefinition == null )
    {
      Class<?>[] wedges = new Class[] {
    		  	BinaryWedge.class,
    		  	DeviceWedge.class,
    		  	LoopbackBinaryWedge.class,
      };
      
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("LoopbackBinary");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"DeviceWedge","device","deviceInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"BinaryWedge","binary","binaryInput");
      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"BinaryWedge","binaryClient","binaryOutput");
    }

    return meemDefinition;
  }
}
