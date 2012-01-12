/*
 * @(#)InternalMeemFactory.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * @author Peter
 */
public interface InternalMeemFactory extends Facet
{
	void createMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState, String location);
}
