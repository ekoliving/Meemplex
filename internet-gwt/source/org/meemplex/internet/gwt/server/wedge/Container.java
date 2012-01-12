package org.meemplex.internet.gwt.server.wedge;

import org.meemplex.internet.gwt.server.wedge.types.Layout;

public class Container {

	/**
	 * A name that may be displayed for this container
	 */
	String name;

	/**
	 * An icon that may be displayed for this container.
	 */
	String icon;
	
	/**
	 * The layout of the Widgets within the Container
	 */
	Layout layout = Layout.Flow;

}
