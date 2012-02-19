/*
 * @(#)SpecificationType.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 *
 * - Implement equals() and hashCode() methods.
 */

package org.openmaji.system.spi;

/**
 * <p>
 * Specification type for a Maji service.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class SpecificationType {

  public static final SpecificationType ESSENTIAL_MEEM =
    new SpecificationType("essentialMeem");

  public static final SpecificationType MEEM =
    new SpecificationType("meem");

  public static final SpecificationType OBJECT =
    new SpecificationType("object");

  public static final SpecificationType SYSTEM_HOOK =
    new SpecificationType("systemHook");

  public static final SpecificationType SYSTEM_MEEM =
    new SpecificationType("systemMeem");

  public static final SpecificationType SYSTEM_WEDGE =
    new SpecificationType("systemWedge");

  public static final SpecificationType WEDGE =
    new SpecificationType("wedge");

  private final String identifier;

  public SpecificationType(
    String identifier) {

    this.identifier = identifier;
  }

  public String getIdentifier() {
    return(identifier);
  }

  public String toString() {
    return(identifier);
  }
}