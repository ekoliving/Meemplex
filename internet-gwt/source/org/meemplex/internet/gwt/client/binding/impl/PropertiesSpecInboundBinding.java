package org.meemplex.internet.gwt.client.binding.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.facets.PropertiesSpecification;
import org.meemplex.internet.gwt.client.facets.PropertiesSpecification.PropertySpecification;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.LifeCycleState;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * 
 * @author stormboy
 *
 */
public class PropertiesSpecInboundBinding extends InboundBinding {
	
	private HashSet<PropertiesSpecification> propertySpecHandlers = new HashSet<PropertiesSpecification>();

	public PropertiesSpecInboundBinding(FacetReference facetReference, FacetEventHub eventHub) {
		super(facetReference, eventHub);
		addListener(propertySpecListener);
    }

	public void addHandler(PropertiesSpecification handler) {
		propertySpecHandlers.add(handler);
	}

	public void removeHandler(PropertiesSpecification handler) {
		propertySpecHandlers.remove(handler);
	}

	
	private void sendSpecs(PropertySpecification[] oldSpecs, PropertySpecification[] newSpecs) {
		for (PropertiesSpecification handler : propertySpecHandlers) {
			handler.specificationChanged(oldSpecs, newSpecs);
		}
	}
	
	private void sendValueAccepted(String propertyName, Serializable value) {
		for (PropertiesSpecification handler : propertySpecHandlers) {
			handler.valueAccepted(propertyName, value);
		}
	}
	
	private void sendValueRejected(String propertyName, Serializable value, String reason) {
		for (PropertiesSpecification handler : propertySpecHandlers) {
			handler.valueRejected(propertyName, value, reason);
		}
	}
	
	private static String getPropertyName(Map<String, String> configId) {
   		String wedgeId = configId.get("wedgeID");
   		String propName = configId.get("propertyName");
   		
   		return wedgeId + " " + propName;
	}
	
	private static String getPropertyName(JSONObject configId) {
   		String wedgeId = configId.get("wedgeID").isString().stringValue();
   		String propName = configId.get("propertyName").isString().stringValue();
   		
   		return wedgeId + " " + propName;
	}
	
	/**
	 * 
	 */
	private static PropertySpecification toPropertySpecification(Map<String, Serializable> spec) {
		
   		String propertyName = getPropertyName((Map<String,String>) spec.get("id"));
		String description = (String) spec.get("description");
		LifeCycleState lcs = LifeCycleState.fromString((String)spec.get("maxLifeCycleState"));
		String type = (String) spec.get("type");
		String defaultValue = (String) spec.get("defaultValue");
		
		PropertySpecification propertySpec = new PropertySpecification();
		propertySpec.setPropertyName(propertyName);
		propertySpec.setDescription(description);
		propertySpec.setMaxLifeCycleState(lcs);
		propertySpec.setType(type);
		propertySpec.setDefaultValue(defaultValue);

		return propertySpec;
	}
	
	private static PropertySpecification toPropertySpecification(JSONObject spec) {
		
   		String propertyName = getPropertyName( spec.get("id").isObject() );
		String description = spec.get("description").isString().stringValue();
		LifeCycleState lcs = LifeCycleState.fromString(spec.get("maxLifeCycleState").isString().stringValue());
		String type = spec.get("type").isString().stringValue();
		String defaultValue =  spec.get("defaultValue").isString().stringValue();
		
		PropertySpecification propertySpec = new PropertySpecification();
		propertySpec.setPropertyName(propertyName);
		propertySpec.setDescription(description);
		propertySpec.setMaxLifeCycleState(lcs);
		propertySpec.setType(type);
		propertySpec.setDefaultValue(defaultValue);

		return propertySpec;
	}
	
	private FacetEventListener propertySpecListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			//GWT.log("PropertiesSpec Inbound : got event: " + event.getMethod());
			if ("specificationChanged".equals(event.getMethod())) {
				String[] params = event.getParams();
				JSONArray oldSpecsMap = JSONParser.parseLenient(params[0]).isArray();
				JSONArray newSpecsMap = JSONParser.parseLenient(params[1]).isArray();
				
				List<PropertySpecification> oldSpecs = new ArrayList<PropertySpecification>();
				//for (Map<String,Serializable> map : oldSpecsMap) {
				for (int i=0; i<oldSpecs.size(); i++) {
					JSONObject map = oldSpecsMap.get(i).isObject();
					PropertySpecification spec = toPropertySpecification(map);
					oldSpecs.add(spec);
				}
				List<PropertySpecification> newSpecs = new ArrayList<PropertySpecification>();
				for (int i=0; i<newSpecsMap.size(); i++) {
					JSONObject map = oldSpecsMap.get(i).isObject();
					PropertySpecification spec = toPropertySpecification(map);
					newSpecs.add(spec);
				}
				sendSpecs(oldSpecs.toArray(new PropertySpecification[]{}), newSpecs.toArray(new PropertySpecification[]{}));
			}
			else if ("valueAccepted".equals(event.getMethod())) {
				String[] params = event.getParams();
				String propertyName = getPropertyName( JSONParser.parseLenient(params[0]).isObject() );
				Serializable value = params[1];
				sendValueAccepted(propertyName, value);
			}
			else if ("valueRejected".equals(event.getMethod())) {
				String[] params = event.getParams();
				String propertyName = getPropertyName( JSONParser.parseLenient(params[0]).isObject() );
				Serializable value = params[1];
				String reason = (String) params[3];
				sendValueRejected(propertyName, value, reason);
			}
		}
	};
	
}
