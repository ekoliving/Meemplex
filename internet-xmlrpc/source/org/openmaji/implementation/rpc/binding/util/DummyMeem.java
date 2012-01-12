/*
 * Created on 18/10/2004
 */
package org.openmaji.implementation.rpc.binding.util;

import java.net.URISyntaxException;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.wedge.reference.Reference;

/**
 * @author Warren Bloomer
 *
 */
public class DummyMeem implements Meem {
	MeemPath meemPath = null;
	
	/**
	 * 
	 *
	 */
	DummyMeem(String pathString) 
		throws URISyntaxException
	{
		meemPath = MeemPathHelper.toMeemPath(pathString);
	}
	
	DummyMeem(MeemPath meemPath) {
		this.meemPath = meemPath;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.meem.Meem#getMeemPath()
	 */
	public MeemPath getMeemPath() {
		return meemPath;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.Meem#addOutboundReference(org.openmaji.meem.wedge.reference.Reference, boolean)
	 */
	public void addOutboundReference(Reference ref, boolean arg1) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.Meem#removeOutboundReference(org.openmaji.meem.wedge.reference.Reference)
	 */
	public void removeOutboundReference(Reference ref) {
		// do nothing
	}
	
	public void addDependency(Facet arg0, DependencyAttribute arg1, LifeTime arg2) {
	}
	
	public void addDependency(String arg0, DependencyAttribute arg1, LifeTime arg2) {
	}
	
	public void removeDependency(DependencyAttribute arg0) {
	}
	
	public void updateDependency(DependencyAttribute arg0) {
	}
	

}
