package org.openmaji.implementation.common;

import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;

/**
 * 
 * 
 */
public class CategoryEntryVariableAdapterWedge implements CategoryEntryConsumer, Wedge {
	
	/* ------------------ outbound facets ------------------ */
	
	/**
	 * Outbound Variable facet
	 */
	public Variable variableOutput;
	
	
	/* ---------------------- conduits -------------------- */
	
	/**
	 * Outbound Variable conduit
	 */
	public Variable variableControlConduit;

	/**
	 * Inbound CategoryEntry conduit
	 */
	public CategoryEntryConsumer categoryEntryConduit = new CategoryEntryConsumer() {
		public void entry(CategoryEntry entry) {
			CategoryEntryVariableAdapterWedge.this.entry(entry);
		}
	};

	
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	
	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	
	/* ---------- CategoryEntryConsumer interface ------------------------- */

	public void entry(CategoryEntry entry) {
		String name = null;
		if (entry != null) {
			name = entry.getName();
		}
		Value value = new StringValue(name);
		
		variableOutput.valueChanged(value);
		variableControlConduit.valueChanged(value);
	}	
}
