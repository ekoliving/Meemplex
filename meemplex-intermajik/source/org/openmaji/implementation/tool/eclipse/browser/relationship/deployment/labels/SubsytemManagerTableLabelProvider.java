/*
 * @(#)SubsytemManagerTableLabelProvider.java
 * Created on 14/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.Column;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.MeemIdentifierColumn;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.TableLabelProviderAdaptor;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.*;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>SubsytemManagerTableLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class SubsytemManagerTableLabelProvider extends TableLabelProviderAdaptor {
	static private String ID_IDENTIFIER = SubsytemManagerTableLabelProvider.class + ".identifier";
	static private String ID_STATUS = SubsytemManagerTableLabelProvider.class + ".status";
	static private String ID_SIZE = SubsytemManagerTableLabelProvider.class + ".size";
	
	/**
	 * Constructs an instance of <code>SubsytemManagerTableLabelProvider</code>.
	 * <p>
	 */
	public SubsytemManagerTableLabelProvider() {
		//=== MeemIdentifier Column ===
		Column 
		column = new MeemIdentifierColumn(ID_IDENTIFIER, "Identifier", 100);
		addColumn(column);
		
		//=== Status Column ===			
		column = new Column(ID_STATUS, "Status", 100) {
			protected String getText(Node node) {
				if(!(node instanceof SubsystemNode)) return null;
				SubsystemNode subsystem = (SubsystemNode)node;
				if(subsystem.getSubsystem().isStarted()) {
					return "started";
				}
				else {
					return "stopped";
				}
			}
			protected Image getImage(Node node) {
				if(!(node instanceof SubsystemNode)) return null;
				SubsystemNode subsystem = (SubsystemNode)node;
				if(subsystem.getSubsystem().isStarted()) {
					return Images.ICON_START.createImage();
				}
				else {
					return Images.ICON_STOP.createImage();
				}
			}
		};
		addColumn(column);
		
		//=== Size Column ===
		column = new Column(ID_SIZE, "Pool Size", 100) {
			protected String getText(Node node) {
				if(!(node instanceof SubsystemNode)) return null;
				SubsystemNode subsystem = (SubsystemNode)node;
				return Integer.toString(subsystem.getSubsystem().getSetSize());
			}
		};
		addColumn(column);
	}
}
