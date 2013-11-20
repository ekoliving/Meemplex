package org.openmaji.implementation.common;

import org.openmaji.common.Unary;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;


/**
 * Triggers a Unary interface when a CategoryEntry is received.
 */
public class CategoryEntryUnaryAdapterWedge implements CategoryEntryConsumer, Wedge {
	
	/* ------------------ outbound facets ------------------ */
	
	/**
	 * Outbound Unary facet
	 */
	public Unary unaryOutput;
	
	
	/* ---------------------- conduits -------------------- */
	
	/**
	 * Outbound Unary conduit
	 */
	public Unary unaryControlConduit;

	/**
	 * Inbound CategoryEntry conduit
	 */
	public CategoryEntryConsumer categoryEntryConduit = new CategoryEntryConsumer() {
		public void entry(CategoryEntry entry) {
			CategoryEntryUnaryAdapterWedge.this.entry(entry);
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
		unaryOutput.valueChanged();
		unaryControlConduit.valueChanged();
	}	
}
