/*
 * @(#)CompositeGroupManagerWedge.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.user;

import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionUtility;


/**
 */
public class CompositeGroupManagerWedge
    extends GroupManagerWedge
{
    public CompositeGroupManagerWedge()
    {
        super(MeemCoreRootAuthority.getCompositeGroupFile());
    }
    
    public WedgeDefinition getWedgeDefinition()
    {
      WedgeDefinition wedgeDefinition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
      
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMapper", "compositeGroupMapper");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMapperClient", "compositeGroupMapperClient");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMonitor", "compositeGroupMonitor");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupManagement", "compositeGroupManagement");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupManagementClient", "compositeGroupManagementClient");
      
      return wedgeDefinition;
    }
}
