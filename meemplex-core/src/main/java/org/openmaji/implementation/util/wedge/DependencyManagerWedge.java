package org.openmaji.implementation.util.wedge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.StringValue;
import org.openmaji.common.Variable;
import org.openmaji.diagnostic.Debug;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;

/**
 * This Wedge sets up dependencies to this Meem on Facets on another given Meem.  
 * This Wedge manages dependencies on only one depended-on meem at any moment.  
 * The meem to be depended on is provided by the CategoryEntryConsumer inbound facet.
 * 
 * When a new CategoryEntry is received old dependencies are removed and new ones
 * set up for the meem given in the CategoryEntry meempath.
 * 
 * The Facets for dependencies are configured and stored in a Map.
 * 
 * @author stormboy
 *
 */
public class DependencyManagerWedge implements Wedge, CategoryEntryConsumer {
	private static final Logger logger = Logger.getAnonymousLogger();
	private int debugLevel;

	/* ----------------- outbound facets ----------------- */
	
	public CategoryEntryConsumer categoryEntryOutput;
	public final ContentProvider categoryEntryOutputProvider = new ContentProvider() {
		public void sendContent(Object object, Filter filter) throws ContentException {
			((CategoryEntryConsumer)object).entry(currentEntry);
		};
	};


	/* --------------------- conduits -------------------- */

	/**
	 * Conduit on which to send CategoryEntries.
	 */
	public CategoryEntryConsumer categoryEntryConduit;

	/**
	 * Conduit on which to send a name.
	 */
	public Variable nameConduit;

	/**
	 * Conduit by which to add and remove dependencies to this Meem.
	 */
	public DependencyHandler dependencyHandlerConduit;

	/**
	 * Conduit on which to receive dependency events.
	 */
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	/**
	 * The conduit through which incoming configuration changes arrive
	 */
	public ConfigurationClient  configurationClientConduit = new ConfigurationClientAdapter(this);
	/**
	 * The conduit to enable debug
	 */
	public Debug debugConduit = new MyDebugConduit();
	

	/* -------------- configuration specs ----------- */
	
	public transient ConfigurationSpecification facetMappingsSpecification  = 
		new ConfigurationSpecification("Facet mappings, e.g. \"localFacet1 remoteFacet1; localFacet2 remoteFacet2;\"");

	public transient ConfigurationSpecification dependencyTypeSpecification  = 
		new ConfigurationSpecification("Whether the dependency type is " + 
				DependencyType.STRONG.getIdentifier() + ", " +
				DependencyType.WEAK.getIdentifier() + ", " +
				DependencyType.STRONG_MANY.getIdentifier() + " or " +
				DependencyType.WEAK_MANY.getIdentifier() + "." 
			);


	/* ------------------- persisted properties -------------- */

	/**
	 * Mapping of local facetId to remote facetId.
	 * A dependency ios created for each of these mappings.
	 */
	public HashMap<String,String> facetMappings = new HashMap<String,String>();
	
	/**
	 * Whether dependencies are strong or weak.
	 */
	public DependencyType dependencyType = DependencyType.STRONG;


	/* ----------------- private members --------------- */

	/**
	 * The current CategoryEntry, the meem of which dependencies are managed against.
	 * Dependencies on this Meem are created.
	 */
	private CategoryEntry currentEntry = null;

	/**
	 * A set of dependencies on the appropriate Meem.
	 * Other dependencies this Meem has are not stored on this set.
	 */
	private HashSet<DependencyAttribute> dependencies = new HashSet<DependencyAttribute>();

	
	/* --------------- configuration methods ------------------ */
	
	public void setDependencyType(String type) {
		if (DependencyType.STRONG.getIdentifier().equalsIgnoreCase(type)) {
			dependencyType = DependencyType.STRONG;
		}
		else if (DependencyType.WEAK.getIdentifier().equalsIgnoreCase(type)) {
			dependencyType = DependencyType.WEAK;
		}
		else if (DependencyType.STRONG_MANY.getIdentifier().equalsIgnoreCase(type)) {
			dependencyType = DependencyType.STRONG_MANY;
		}
		else if (DependencyType.WEAK_MANY.getIdentifier().equalsIgnoreCase(type)) {
			dependencyType = DependencyType.WEAK_MANY;
		}
	}

	public String getDependencyType() {
		return dependencyType.getIdentifier();		
	}
	
	/**
	 * Of the form "name1 name2; name3 name4;
	 */
	public void setFacetMappings(String mappings) {

		synchronized (facetMappings) {
			facetMappings.clear();
			
			String[] pairs = mappings.split("[;,]");
			for (int i=0; i<pairs.length; i++) {
				String[] pair = pairs[i].split("[\\s=]");
				if (pair.length == 2) {
					facetMappings.put(pair[0].trim(), pair[1].trim());					
				}
			}
		}
	}

	public String getFacetMappings() {
		StringBuffer sb = new StringBuffer();
		synchronized (facetMappings) {
			Iterator keyIter = facetMappings.keySet().iterator();
			while (keyIter.hasNext()) {
				String localFacetId = (String) keyIter.next();
				String facetId = (String) facetMappings.get(localFacetId);
				sb.append(localFacetId);
				sb.append(" ");
				sb.append(facetId);
				sb.append(";");
			}
		}
		return sb.toString();
	}

	
	/* ---------------- CategoryEntryConsumer interfacet ------------- */

	/**
	 * Remove existing dependencies on the old meem and
	 * add new dependencies on the new meem.
	 */
	public void entry(CategoryEntry entry) {
		removeDependencies();
		this.currentEntry = entry;
		if(debugLevel>0) {
			logger.log(Level.INFO, "The dependency is set to "+entry.getName());
		}
		addDependencies();
		
		// send entry to output facet and conduit
		categoryEntryConduit.entry(entry);
		categoryEntryOutput.entry(entry);
		
		// send name
		String name = null;
		if (entry != null) {
			name = entry.getName();
		}
		nameConduit.valueChanged(new StringValue(name));
	}

	
	/* ------------------------ private methods -------------- */

	/**
	 * Remove existing dependencies on the current Meem.
	 */
	private void removeDependencies() {
		DependencyAttribute[] deps = (DependencyAttribute[]) dependencies.toArray(new DependencyAttribute[]{});
		for (int i=0; i<deps.length; i++) {
//			logger.log(Level.INFO, "Removing dependency: " + deps[i]);
			dependencyHandlerConduit.removeDependency(deps[i]);
		}
	}
	
	private void addDependencies() {

		if (currentEntry != null) {
			synchronized (facetMappings) {
				Iterator keyIter = facetMappings.keySet().iterator();
				while (keyIter.hasNext()) {
					String localFacetId = (String) keyIter.next();
					String facetId = (String) facetMappings.get(localFacetId);
		
					DependencyAttribute dependencyAttribute = 
						new DependencyAttribute(dependencyType, Scope.DISTRIBUTED, currentEntry.getMeem(), facetId);
					
//					logger.log(Level.INFO, "Adding dependency: " + dependencyAttribute + " on local facet \"" + localFacetId + "\"");
					
					dependencyHandlerConduit.addDependency(localFacetId, dependencyAttribute, LifeTime.TRANSIENT);			
				}
			}
		}
	}
	
	/* ---------------- inner classes ---------------------- */
	
	/**
	 * 
	 */
	private final class DependencyClientConduit implements DependencyClient {
		public void dependencyAdded(String facetId, DependencyAttribute dependency) {
			if ( currentEntry != null && currentEntry.getMeem().getMeemPath().equals(dependency.getMeemPath()) ) {
				dependencies.add(dependency);
			}
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyRemoved(DependencyAttribute dependency) {
			dependencies.remove(dependency);
		}
		
		public void dependencyConnected(DependencyAttribute dependency) {
		}
		
		public void dependencyDisconnected(DependencyAttribute dependency) {
		}
		
	}
	
	private class MyDebugConduit implements Debug {
	      public void debugLevelChanged(int level) {
	        debugLevel = level;
	      }
	}

}
