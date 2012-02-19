/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

import org.openmaji.meem.Facet;

public interface ProgressClient extends Facet {
	public void updateProgress(Progress progress);
}
