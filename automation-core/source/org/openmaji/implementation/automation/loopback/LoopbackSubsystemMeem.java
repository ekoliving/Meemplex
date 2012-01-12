package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;

public class LoopbackSubsystemMeem implements MeemDefinitionProvider
{
	private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition()
  {
    if (meemDefinition == null)
    {
      Class subsystemImplClass = Subsystem.spi.create().getClass();
    	meemDefinition = LifeCycleManagerDefinitionFactory.spi.get().createPersisting(new Class[] {subsystemImplClass, LoopbackSubsystemWedge.class});
      meemDefinition.getMeemAttribute().setIdentifier("Loopback Subsystem");
    }

    return meemDefinition;
  }
}
