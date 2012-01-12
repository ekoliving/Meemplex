package org.meemplex.internet.rs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class FacetApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		classes.add(FacetResource.class);
		
		return classes;
	}
	
}
