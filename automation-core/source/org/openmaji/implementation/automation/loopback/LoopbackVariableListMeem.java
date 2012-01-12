/*
 *  Copyright 2004 by Majitek Limited.
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

import org.openmaji.implementation.common.VariableListWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

/**
 * A Meem that represents a variable loopback device.
 * This class implements <code>MeemDefinitionProvider</code> so that
 * <code>the MeemkitInstaller</code> can obtain a <code>MeemDefinition</code>
 * from which to create a <code>LoopbackVariableMeem</code>
 *
 * @author Warren Bloomer
 */

public class LoopbackVariableListMeem implements MeemDefinitionProvider
{
  private MeemDefinition meemDefinition = null;

  /**
   * Return the MeemDefinition for this Meem. This MeemDefinition lists all of the
   * Wedges required to assemble a <code>LoopbackVariableMeem</code>.
   * 
   * @return The MeemDefinition for this Meem
   * 
   */
  public MeemDefinition getMeemDefinition()
  {
    if ( meemDefinition == null )
    {
      Class[] wedges = new Class[2];
      wedges[0] = VariableListWedge.class;
      wedges[1] = LoopbackVariableListWedge.class;
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
      meemDefinition.getMeemAttribute().setIdentifier("LoopbackVariableList");
      MeemDefinitionUtility.renameFacetIdentifier(
      		meemDefinition,
			"VariableListWedge",
			"variableList",
			"variableListInput"
		);
      MeemDefinitionUtility.renameFacetIdentifier(
      		meemDefinition,
			"VariableListWedge",
			"variableListClient",
			"variableListOutput"
		);
    }

    return meemDefinition;
  }
}
