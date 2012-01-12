package org.meemplex.internet.gwt.client.facets;

import com.google.gwt.json.client.JSONValue;

public interface PropertyHandler {
	
	/**
	 * propertyName = {WedgeName}.{propertyName}
	 */
	public void valueChanged(String propertyName, JSONValue value);

}
