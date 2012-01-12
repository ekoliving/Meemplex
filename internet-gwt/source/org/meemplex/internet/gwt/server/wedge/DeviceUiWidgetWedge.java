package org.meemplex.internet.gwt.server.wedge;

import org.meemplex.meem.Wedge;

@Wedge(name="DeviceUi")
public abstract class DeviceUiWidgetWedge {
	
	/**
	 * The name of the device.
	 */
	String name;

	/**
	 * An icon representing the device. This does not represent state; just a neutral view
	 * of the device.
	 */
	String icon;

	/**
	 * Class may include device type.  This can allow the UI to provide different look
	 * for each class.
	 * 
	 * lighting
	 * security
	 * hvac
	 * entertainment
	 */
	String deviceClass;

	/**
	 * The facets to bind to; comma-separated
	 */
	String facets;
	
	/**
	 * The facet class
	 */
	String facetClass;
	
}
