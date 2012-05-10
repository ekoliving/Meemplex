/*
 * @(#)DependencyInfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.definition.DependencyType;



/**
 * <code>DependencyInfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyInfoTip extends InfoTip {
	static private Map typeToImageName;
	static {
		typeToImageName = new HashMap();
		typeToImageName.put(DependencyType.WEAK, 				"dependency_weaksingle16.gif");
		typeToImageName.put(DependencyType.WEAK_MANY,		"dependency_weakmany16.gif");
		typeToImageName.put(DependencyType.STRONG, 			"dependency_strongsingle16.gif");
		typeToImageName.put(DependencyType.STRONG_MANY, "dependency_strongmany16.gif");
	}
	
	//private Dependency dependency;
	
	/**
	 * Constructs an instance of <code>DependencyInfoTip</code>.
	 * <p>
	 * 
	 */
	public DependencyInfoTip(Dependency dependency) {
		//this.dependency = dependency;
		build(dependency);
	}

	private void build(Dependency dependency) {
		DependencyType type = dependency.getAttribute().getDependencyType();
		setIcon(Images.getIcon((String)typeToImageName.get(type)));
		setCaption(dependency.getName());
	}
}
