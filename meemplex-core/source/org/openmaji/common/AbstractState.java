/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.common;

import java.io.Serializable;

/**
 * The AbstractState is a base class of State
 * 
 * @author Diana Huang
 * @see org.openmaji.common.State
 */
public abstract class AbstractState implements State, Serializable {
	protected String[] availableStates;
	protected String state;
	public boolean equals(Object object){
		if ( object == null ) return false;
	    if ( object == this ) return true;
	    if (!(object.getClass().equals(this.getClass()))) return false;
	    AbstractState that = (AbstractState) object;
	    boolean result=true;
	    for (int i=0;i<availableStates.length;i++){
	    	result=result && (that.availableStates[i]==this.availableStates[i]);
	    }
		return((that.state==this.state) && result);
	}

	public int hashCode(){
		int hash=state.hashCode();
		for(int i=0;i<availableStates.length;i++){
			hash=hash ^ availableStates[i].hashCode();
		}
		return hash;
	}
	public String toString(){
		String temp=getClass().getName()+"[state="+state+",availableStates=";
		for(int i=0;i<(availableStates.length-1);i++){
			temp=temp+availableStates[i]+", ";
		}
		temp=temp+availableStates[availableStates.length-1]+"]";
		return temp;
	}
	
	public abstract void setState(String state);
	
	public String getState(){
		return state;
	}
	
	public String[] getAvailableStates(){
		return availableStates;
	}
	
	
}

