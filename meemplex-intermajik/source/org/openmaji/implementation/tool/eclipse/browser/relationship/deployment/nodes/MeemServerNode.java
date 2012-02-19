/*
 * @(#)MeemServerNode.java
 * Created on 26/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;



/**
 * <code>MeemServerNode</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemServerNode extends CategoryNode {
	private static final long serialVersionUID = 6424227717462161145L;

	static final String ENTRYNAME_LIFE_CYCLE_MANAGER = "lifeCycleManager";
	static final String NAME_LIFE_CYCLE_MANAGER = "Life Cycle Manager";
	
	static final String ENTRYNAME_SUBSYSTEM_FACTORY = "subsystemFactory";
	static final String NAME_SUBSYSTEM_FACTORY = "Subsystems";
	
	static final String ENTRYNAME_WORKSHEET_MANAGER = "worksheet";
	static final String NAME_WORKSHEET_MANAGER = "Worksheets";
	/**
	 * Constructs an instance of <code>MeemServerNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public MeemServerNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.CategoryNode#createNode(org.openmaji.system.space.CategoryEntry)
	 */
	protected Node createNode(String name, MeemClientProxy proxy) {

		if(name.equals(StandardHyperSpaceCategory.SUBSYSTEM)) {
			String location = StandardHyperSpaceCategory.DEPLOYMENT + "/" + this.getText() + "/" + StandardHyperSpaceCategory.SUBSYSTEM + "/" + StandardHyperSpaceCategory.SUBSYSTEM_FACTORY;
			
			MeemPath factoryMeemPath = MeemPath.spi.create(Space.HYPERSPACE, location);
			
			return new UnavailableMeemNode(new CategoryEntry(ENTRYNAME_SUBSYSTEM_FACTORY, Meem.spi.get(factoryMeemPath)), this);
		}
		else
		if(name.equals(ENTRYNAME_SUBSYSTEM_FACTORY)) {
			return new SubsystemFactoryNode(
				NAME_SUBSYSTEM_FACTORY, 
				proxy);
		}
		else
		if(name.equals(ENTRYNAME_WORKSHEET_MANAGER)) {
			return new WorksheetManagerNode(
				NAME_WORKSHEET_MANAGER, 
				proxy);
		}
		/*
		else
		return super.createNode(entry);
		*/
		return null;
	}
}
