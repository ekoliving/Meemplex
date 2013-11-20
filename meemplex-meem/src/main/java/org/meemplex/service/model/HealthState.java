package org.meemplex.service.model;

public enum HealthState {
	UNKNOWN,           
	OFFLINE,           	// no connection to the the RPC server
	MEEM_RESOLVED,     	// the Meem has been resolved
	MEEM_NOTRESOLVED,  	// the Meem has been resolved
	FACET_RESOLVED,    	// the Facet has been resolved
	FACET_NOTRESOLVED, 	// the Facet has not been resolved
}
