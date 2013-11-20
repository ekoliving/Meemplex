/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.nursery.startup;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.scripting.bsf.BSFMeemWedge;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;


public class SystemStartupWedge extends BSFMeemWedge implements SystemStartup
{
	
	private static final Logger logger = Logger.getAnonymousLogger();

  public void commence()
  { 
	  logger.log(Level.INFO, "System startup");
	  
    String meemStoreLocation = System.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
    String filename = meemStoreLocation + "/../" + SystemStartup.CREATE_MEEMSPACE_FLAG;
    
    File file = new File(filename);
    if ( file.exists() )
    {
      super.commence();
      file.delete();
    }
  }

  protected void setCommands()
  {
    commands = new String[] {
        "cd(\"" + beanShellDirectory + "\");",
        "source(\"" + beanShellScript + "\");",
        "createMeemSpace()",
    };
    
    myName = "systemStartupWedge";
    myClass = this.getClass();
    super.logger = Logger.getAnonymousLogger();
  }
}