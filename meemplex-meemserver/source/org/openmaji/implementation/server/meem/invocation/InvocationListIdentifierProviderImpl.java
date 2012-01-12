/*
 * @(#)InvocationListIdentifierProviderImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.meem.invocation;

import java.util.*;

import org.openmaji.system.meem.hook.flightrecorder.FlightRecorderHook;
import org.openmaji.system.meem.hook.security.InboundSecurityHook;
import org.openmaji.system.spi.MajiSystemProvider;


/**
 * The normal "production" invocation list generator.
 */

public class InvocationListIdentifierProviderImpl
  implements InvocationListIdentifierProvider {

  private List<String> inList = null;
  
  private MajiSystemProvider majiSystemProvider = null;

  public InvocationListIdentifierProviderImpl() {
    Properties properties = System.getProperties();

    inList = new ArrayList<String>();
    
    if (majiSystemProvider == null) {
      majiSystemProvider = MajiSystemProvider.systemProvider();
    }

    String flightRecorderStatus =
      properties.getProperty(FlightRecorderHook.PROPERTY_ENABLE);

    if (flightRecorderStatus != null) {
      if (flightRecorderStatus.equals(FlightRecorderHook.ENABLE_INBOUND)  ||
          flightRecorderStatus.equals(FlightRecorderHook.ENABLE_INOUTBOUND)) {

        inList.add(
          majiSystemProvider.getImplementation(FlightRecorderHook.class).getName()
        );
      }
    }

    inList.add(
      majiSystemProvider.getImplementation(InboundSecurityHook.class).getName()
    );
  }

  public Iterator<String> generate()
  {
      return(inList.iterator());
  }
}
