package org.openmaji.implementation.server.meem;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.meem.definition.WedgeAttribute;

public class WedgeSpecCache {
	private static final Map<String, WedgeAttribute> cache = new HashMap<String, WedgeAttribute>();

	public static WedgeAttribute get(Class<?> wedgeClass) {
		return cache.get(wedgeClass.getName());
	}
	
	public static WedgeAttribute put(Class<?> wedgeClass, WedgeAttribute spec) {
		return cache.put(wedgeClass.getName(), spec);
	}
}
