/*
 * @(#)StatisticsClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.statistics;

import org.openmaji.meem.Facet;

/**
 * Inbound facet for a wedge that wishes to receive statistics information from a statistics provider.
 */
public interface StatisticsClient
	extends Facet
{
	public void statisticsChanged(Statistics statistics);
}
