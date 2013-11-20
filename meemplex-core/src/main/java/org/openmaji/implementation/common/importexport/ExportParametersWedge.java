/*
 * @(#)ExportParametersWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

import java.net.URL;

import org.openmaji.meem.*;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class ExportParametersWedge implements ExportParameters, Wedge {

  public ExportParameters exportParametersConduit;

  public void exportParametersChanged(
    MeemPath sourceMeemPath,
    URL      exportURL,
    int      depthLimit) {

    exportParametersConduit.exportParametersChanged(
      sourceMeemPath, exportURL, depthLimit
    );      
  }
}