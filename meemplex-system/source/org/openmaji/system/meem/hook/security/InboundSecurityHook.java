/*
 * @(#)IncomingSecurityHook.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.security;

import org.openmaji.system.meem.hook.Hook;

/**
 * <p>
 * Profile for the inbound hook that vets invocations as they are passed into a Meem.
 * </p>
 */
public interface InboundSecurityHook extends Hook {
	
  /**
   * Service provider interface.
   */
  public class spi {
    public static String getIdentifier() {
      return("inboundSecurityHook");
    };
  }
}