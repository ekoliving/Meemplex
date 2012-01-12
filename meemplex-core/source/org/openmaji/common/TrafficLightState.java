/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.common;


/**
 * The is a concrete class of AbstractState (example)
 * 
 * @author Diana Huang
 * 
 * @see org.openmaji.common.AbstractState
 */
public class TrafficLightState extends AbstractState {
	private static final long serialVersionUID = 6424227717462161145L;

	public static final TrafficLightState RED=new TrafficLightState("red");
	public static final TrafficLightState GREEN=new TrafficLightState("green");
	public static final TrafficLightState ORANGE=new TrafficLightState("orange");
	
	/**
	 * A constructor
	 * 
	 * @param state
	 */	
	public TrafficLightState(String state){
		availableStates=new String[]{"red","green","orange"};
		setState(state);
	}
	
	public void setState(String state){
		if(state.equalsIgnoreCase("red")
		   ||state.equalsIgnoreCase("green")
		   ||state.equalsIgnoreCase("orange")){
				this.state=state;
		}else{
			this.state="red";
		}
	}
	
	
}
