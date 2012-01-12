/*
 * @(#)ExportParameters.java
 *
 *  Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 *  This software is the proprietary information of Majitek Limited.
 *  Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.common.importexport;

import java.net.URL;

import org.openmaji.meem.*;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */

public interface ExportParameters extends Facet {

	public void exportParametersChanged(
    MeemPath sourceMeemPath,
    URL      exportURL,
    int      depthLimit);
}