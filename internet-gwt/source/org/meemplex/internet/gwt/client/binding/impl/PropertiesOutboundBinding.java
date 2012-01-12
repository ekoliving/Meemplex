package org.meemplex.internet.gwt.client.binding.impl;

import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.client.facets.PropertyHandler;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author stormboy
 *
 */
public class PropertiesOutboundBinding extends OutboundBinding implements PropertyHandler {

	public PropertiesOutboundBinding(FacetReference facetReference, FacetEventHub eventHub) {
		super(facetReference, eventHub);
    }
	
	@Override
	public void valueChanged(String propertyName, JSONValue value) {
		String[] s = propertyName.split(".");
		if (s.length < 2) {
			GWT.log("problem with property: " + propertyName);
			return;
		}
		String wedgeId = s[0];
		String propName = s[1];
		JSONObject id = new JSONObject();
		id.put("wedgeID", new JSONString(wedgeId));
		id.put("propertyName", new JSONString(propName));
		
		JSONValue[] args = new JSONValue[] { id, value };
		send("valueChanged", args);
	}
}
