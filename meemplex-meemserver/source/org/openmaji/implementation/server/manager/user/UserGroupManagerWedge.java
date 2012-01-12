/*
 * @(#)UserGroupManagerWedge.java
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
public class UserGroupManagerWedge
    extends GroupManagerWedge
{
    public UserGroupManagerWedge()
    {
        super(MeemCoreRootAuthority.getUserGroupFile());
    }
    
    public WedgeDefinition getWedgeDefinition()
    {
      WedgeDefinition wedgeDefinition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
      
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMapper", "userGroupMapper");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMapperClient", "userGroupMapperClient");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupMonitor", "userGroupMonitor");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupManagement", "userGroupManagement");
      WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "groupManagementClient", "userGroupManagementClient");
      
      return wedgeDefinition;
    }  
}