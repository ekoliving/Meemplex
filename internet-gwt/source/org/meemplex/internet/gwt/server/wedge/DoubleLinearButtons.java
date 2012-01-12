package org.meemplex.internet.gwt.server.wedge;

import org.meemplex.meem.ConfigProperty;

/**
 * Two buttons: one for increasing the value, and one for decreasing the value.
 * 
 * @author stormboy
 */
public class DoubleLinearButtons {

	/**
	 * The class
	 */
	private String className;

	@ConfigProperty
	private String name;

	@ConfigProperty
	private Number min;

	@ConfigProperty
	private Number max;
	
	/**
	 * A unit to display. e.g. "%"
	 */
	@ConfigProperty
	private String unit;
	
	/**
	 * 
	 */
	private String incIcon;

	/**
	 * 
	 */
	private String decIcon;
	
}
