/*
 * @(#)MetaMeemStub.java
 * Created on 8/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;


import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.MetaMeem;

/**
 * <code>MetaMeemStub</code>.
 * <p>
 * @author Kin Wong
 */
public class MetaMeemStub implements MetaMeem {
	
	public void updateMeemAttribute(MeemAttribute meemAttribute) {}
	
	public void addWedgeAttribute(WedgeAttribute wedgeAttribute) {}
	
	public void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {}
	
	public void removeWedgeAttribute(Serializable wedgeKey) {}
	
	public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute) {}
	
	public void updateFacetAttribute(FacetAttribute facetAttribute) {}
	
	public void removeFacetAttribute(String facetKey) {}
	
	public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute) {}
	
	public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {}
	
	public void removeDependencyAttribute(Serializable dependencyKey) {}
}
