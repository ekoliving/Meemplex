package org.meemplex.system;

import java.net.URL;

import org.openmaji.system.meemkit.core.MeemkitDescriptor;

public interface MeemkitService {
	
	URL getDescriptorUrl();
	
	MeemkitDescriptor getDescriptor();
	
	/*
	String getName();
	
	String getDescription();

	Collection<MeemEntry> getMeems();

	Collection<WedgeEntry> getWedges();
	
	class MeemEntry {
		String path;
		Object meemDefinition;
		String icon;
	}
	
	class WedgeEntry {
		String path;
		Object meemDefinition;
		String icon;
	}
	*/
}
