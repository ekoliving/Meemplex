package org.openmaji.implementation.util.wedge;

import java.util.HashMap;
import java.util.HashSet;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.common.Variable;
import org.openmaji.diagnostic.Debug;
import org.openmaji.util.CategoryEntryConsumer;

/**
 * This Wedge will keep a set of depended-on meems and observe whether a required
 * value has been received from all meems.
 * 
 * @author stormboy
 *
 */
public class DependencyCounterWedge implements Wedge {
	
	private int debugLevel;

	/* ----------------- outbound facets ----------------- */
	

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
	 * Whether to do AND or OR.
	 */
	public boolean doAnd = true;
	
	/**
	 * The facet on this meem that is to be monitored.
	 */
	public String facetId;


	/* ----------------- private members --------------- */

	/**
	 * the meems to receive messages from.
	 */
	private HashMap<MeemPath, Meem> meems = new HashMap<MeemPath, Meem>();
	
	/**
	 * A set of dependencies on the appropriate Meem.
	 * Other dependencies this Meem has are not stored on this set.
	 */
	private HashSet<DependencyAttribute> dependencies = new HashSet<DependencyAttribute>();

	
	/* --------------- Configuration methods ------------------ */

	
	/* ------------------------ private methods ----------------- */
	
	private void checkConnected() {
		if (false) {
			// TODO send state on binary output
		}
	}

	/* ---------------- inner classes ---------------------- */
	
	/**
	 * 
	 */
	private final class DependencyClientConduit implements DependencyClient {
		public void dependencyAdded(String facetId, DependencyAttribute dependency) {
			
			if (DependencyCounterWedge.this.facetId.equals(facetId)) {
				// check set of meems to see if this dep is for one of those
				if (meems.get(dependency.getMeemPath()) != null) {
					dependencies.add(dependency);
				}				
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
