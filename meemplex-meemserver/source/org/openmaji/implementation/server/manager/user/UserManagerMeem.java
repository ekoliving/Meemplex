/*
 * @(#)UserManagerMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.user;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;

/**
 */
public class UserManagerMeem 
    implements MeemDefinitionProvider 
{
    
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  public MeemDefinition getMeemDefinition() {
    return (
      MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {
          CompositeGroupManagerWedge.class,
          UserGroupManagerWedge.class
        }
      )
    );
  }
  
/* ---------- Nested class for SPI ----------------------------------------- */

  public static class spi {
    public static String getIdentifier() {
      return("userManager");
    };
  }
}
