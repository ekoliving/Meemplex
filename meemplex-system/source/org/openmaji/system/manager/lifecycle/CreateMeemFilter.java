/*
 * @(#)CreateMeemFilter.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.lifecycle;

import java.io.Serializable;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * Filter for listening for the creation of a Meem using the given meem definition and the with the
 * given life cycle state.
 * 
 * @author Peter
 */
public class CreateMeemFilter implements Filter, Serializable
{
	private static final long serialVersionUID = 534540102928363464L;

	/**
	 * Base constructor.
	 * 
	 * @param meemDefinition the definition for the meem we are looking for.
	 * @param lifeCycleState the state we want the meem returned in.
	 */
	public CreateMeemFilter(MeemDefinition meemDefinition, LifeCycleState lifeCycleState)
	{
		this.meemDefinition = meemDefinition;
		this.lifeCycleState = lifeCycleState;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = meemDefinition.hashCode();
		hashCode ^= lifeCycleState.hashCode();

		return hashCode;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == this) return(true);

    if ((object instanceof CreateMeemFilter) == false) return(false);

    CreateMeemFilter thatFilter = (CreateMeemFilter) object;

    if (meemDefinition.equals(thatFilter.meemDefinition) == false) {
      return(false);
    }

    if (lifeCycleState.equals(thatFilter.lifeCycleState) == false) {
    	return(false);
    }
    
    return true;
	}

	public final MeemDefinition meemDefinition;
	public final LifeCycleState lifeCycleState;
}
