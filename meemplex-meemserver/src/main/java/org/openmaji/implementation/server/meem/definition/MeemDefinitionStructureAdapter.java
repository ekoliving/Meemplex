/*
 * @(#)MeemDefinitionStructureAdapter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider calling this class "MeemDefinitionStructureUtility".
 */

package org.openmaji.implementation.server.meem.definition;

import java.io.Serializable;

import org.openmaji.implementation.server.meem.core.MeemCoreStructure;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetDefinition;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.system.meem.definition.MeemStructure;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.MeemDefinition
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.FacetDefinition
 * @see org.openmaji.meem.definition.WedgeAttribute
 * @see org.openmaji.meem.definition.WedgeDefinition
 */

public class MeemDefinitionStructureAdapter {

	/**
	 * 
	 * @param meemDefinition
	 * @param meemStructure
	 */
	public static void mergeMeemDefinition(MeemDefinition meemDefinition, MeemStructure meemStructure) {
		mergeMeemDefinition(meemDefinition, meemStructure, null, false);
	}

	/**
	 * 
	 * @param meemDefinition
	 * @param meemStructure
	 * @param existingWedgeImplementation
	 * @param systemWedgeFlag
	 */
	public static void mergeMeemDefinition(MeemDefinition meemDefinition, MeemStructure meemStructure, Object existingWedgeImplementation, boolean systemWedgeFlag) {

		MeemAttribute meemAttribute = meemDefinition.getMeemAttribute();

		if (meemStructure.set(meemAttribute) == false) {
			if (meemStructure.update(meemAttribute) == false) {
				throw new RuntimeException("Couldn't add or update MeemAttribute: " + meemAttribute);
			}
		}

		for (WedgeDefinition wedgeDefinition : meemDefinition.getWedgeDefinitions()) {
			WedgeAttribute wedgeAttribute = wedgeDefinition.getWedgeAttribute();

			boolean success = false;

			if (meemStructure instanceof MeemCoreStructure) {
				MeemCoreStructure meemCoreStructure = (MeemCoreStructure) meemStructure;
				if (existingWedgeImplementation != null && existingWedgeImplementation.getClass().getName().equals(wedgeAttribute.getImplementationClassName())) {
					success = meemCoreStructure.add(wedgeAttribute, systemWedgeFlag, existingWedgeImplementation);
				}
				else {
					success = meemCoreStructure.add(wedgeAttribute, systemWedgeFlag, null);
				}
			}
			else {
				success = meemStructure.add(wedgeAttribute);
			}

			if (success == false) {
				if (meemStructure.update(wedgeAttribute) == false) {
					throw new RuntimeException("Couldn't add or update WedgeAttribute: " + wedgeAttribute);
				}
			}

			for (FacetDefinition facetDefinition : wedgeDefinition.getFacetDefinitions()) {
				FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();
				if (meemStructure.add(wedgeAttribute, facetAttribute) == false) {
					if (meemStructure.update(facetAttribute) == false) {
						throw new RuntimeException("Couldn't add or update FacetAttribute: " + facetAttribute);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param meemStructure
	 * @return
	 */
	public static MeemDefinition getMeemDefinition(MeemStructure meemStructure) {

		// Only extract system wedges
		MeemDefinition meemDefinition = new MeemDefinition(meemStructure.getMeemAttribute());

		synchronized (meemStructure) {
			for (Serializable wedgeKey : meemStructure.getWedgeAttributeKeys()) {
				WedgeDefinition wedgeDefinition = new WedgeDefinition(meemStructure.getWedgeAttribute(wedgeKey));
				meemDefinition.addWedgeDefinition(wedgeDefinition);
				for (String facetKey : meemStructure.getFacetAttributeKeys(wedgeKey)) {
					FacetDefinition facetDefinition = new FacetDefinition(meemStructure.getFacetAttribute(facetKey));
					wedgeDefinition.addFacetDefinition(facetDefinition);
				}
			}
		}

		return (meemDefinition);
	}
}
