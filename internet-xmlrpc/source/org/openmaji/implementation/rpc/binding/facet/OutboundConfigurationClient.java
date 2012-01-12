package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meemplex.meem.PropertyType;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.rpc.binding.OutboundBinding;

/**
 * @author Warren Bloomer
 * 
 */
public class OutboundConfigurationClient extends OutboundBinding implements ConfigurationClient {

    /**
     * constructor
     *
     */
    public OutboundConfigurationClient() {
        setFacetClass(ConfigurationClient.class);
    }
    
    public void specificationChanged(
    		ConfigurationSpecification[] oldConfigSpec, 
    		ConfigurationSpecification[] newConfigSpec) 
    {
    	List<Map<String,Serializable>> oldSpecs = (List<Map<String,Serializable>>) new ArrayList<Map<String,Serializable>>();
    	if (oldConfigSpec != null) {
	    	for (int i=0; i<oldConfigSpec.length; i++) {
	    		ConfigurationSpecification spec = oldConfigSpec[i];
	    		Map<String,Serializable> hashSpec = toMap(spec);
	    		oldSpecs.add(hashSpec);
	    	}
    	}
    	
    	List<Map<String,Serializable>> newSpecs = new ArrayList<Map<String,Serializable>>();
    	if (newConfigSpec != null) {
	    	for (int i=0; i<newConfigSpec.length; i++) {
	    		ConfigurationSpecification spec = newConfigSpec[i];
	    		Map<String,Serializable> hashSpec = toMap(spec);
	    		newSpecs.add(hashSpec);
	    	}
    	}
    	
        send("specificationChanged", new Serializable[] { (Serializable) oldSpecs, (Serializable) newSpecs });
    	
    }
    
    public void valueAccepted(ConfigurationIdentifier configurationIdentifier, Serializable value) {
    	HashMap<String,String> configId = new HashMap<String, String>();
   		configId.put("wedgeID",  configurationIdentifier.getWedgeIdentifier());
   		configId.put("propertyName",  configurationIdentifier.getFieldName());
		
        send("valueAccepted", new Serializable[] { configId, ""+value });
    }
    
    public void valueRejected(ConfigurationIdentifier configurationIdentifier, Serializable value, Serializable reason) {
   		HashMap<String,String> configId = new HashMap<String, String>();
   		configId.put("wedgeID",  configurationIdentifier.getWedgeIdentifier());
   		configId.put("propertyName",  configurationIdentifier.getFieldName());
		
        send("valueRejected", new Serializable[] { configId, ""+value, ""+reason });
    }
    
    
    private Map<String, Serializable> toMap(ConfigurationSpecification spec) {
		Map<String,Serializable> specMap = new HashMap<String, Serializable>();
		
		Map<String,String> configId = new HashMap<String, String>();
   		configId.put("wedgeID",  spec.getIdentifier().getWedgeIdentifier());
   		configId.put("propertyName",  spec.getIdentifier().getFieldName());
		
		specMap.put("id",  (Serializable) configId);
		specMap.put("description",  spec.getDescription());
		specMap.put("maxLifeCycleState",  spec.getMaxLifeCycleState().getCurrentState());
		specMap.put("type",  PropertyType.type(spec.getType()).toString());
		Object defValue = spec.getDefaultValue();
		if (defValue != null) {
			specMap.put("defaultValue", defValue.toString());
		}

		return specMap;
    }
    
}
