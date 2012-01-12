/*
 * @(#)WedgeTransfer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.io.Serializable;
import java.util.*;

import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.WedgeAttribute;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class WedgeTransfer implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private WedgeAttribute wedgeAttribute;
	private Vector facetAttributes = new Vector();
	
	public void addFacetAttribute(FacetAttribute attribute) {
		facetAttributes.add(attribute);
	}
	
	public Collection getFacetAttributes() {
		return facetAttributes;
	}

	public WedgeAttribute getWedgeAttribute() {
		return wedgeAttribute;
	}

	public void setWedgeAttribute(WedgeAttribute attribute) {
		wedgeAttribute = attribute;
	}

}
