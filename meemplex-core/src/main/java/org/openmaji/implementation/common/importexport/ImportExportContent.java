/*
 * @(#)ImportExportContent.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.meem.definition.MeemContent;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public interface ImportExportContent extends CategoryContent {
  
  public void meemChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition,
    MeemContent    meemContent
  );
}