/*
 * @(#)StandardMeemTableLabelProvider.java
 * Created on 17/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.Column;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.MeemIdentifierColumn;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.TableLabelProviderAdaptor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;



/**
 * <code>StandardMeemTableLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class StandardMeemTableLabelProvider extends TableLabelProviderAdaptor implements ILabelProvider {
	static private String ID_IDENTIFIER = StandardMeemTableLabelProvider.class + ".identifier";
	static private String ID_LIFE_CYCLE_STATE = StandardMeemTableLabelProvider.class + ".lifecyclestate";
	
	private MeemIdentifierColumn meemIdentifierColumn;
	
	/**
	 * Constructs an instance of <code>SubsystemTableLabelProvider</code>.
	 * <p>
	 */
	public StandardMeemTableLabelProvider() {
		//=== MeemIdentifier Column ===
		meemIdentifierColumn = new MeemIdentifierColumn(ID_IDENTIFIER, "Identifier", 100);
		addColumn(meemIdentifierColumn);

		//=== Status Column ===			
		Column column = new Column(ID_LIFE_CYCLE_STATE, "State", 100) {
			protected String getText(Node node) {
				if(!(node instanceof MeemNode)) return null;
				MeemNode meem = (MeemNode)node;
				LifeCycleState state = 
					meem.getProxy().getLifeCycle().getState();
				if(state.equals(LifeCycleState.READY)) {
					return "READY";
				}
				else
				if(state.equals(LifeCycleState.PENDING)) {
					return "PENDING";
				}
				else
				if(state.equals(LifeCycleState.LOADED)) {
					return "LOADED";
				}
				else
				if(state.equals(LifeCycleState.DORMANT)) {
					return "DORMANT";
				}
				else
				if(state.equals(LifeCycleState.ABSENT)) {
					return "ABSENT";
				}
				return null;
			}
			
			protected Image getImage(Node node) {
				if(!(node instanceof MeemNode)) return null;
				MeemNode meem = (MeemNode)node;
				LifeCycleState state = 
					meem.getProxy().getLifeCycle().getState();
				if(state.equals(LifeCycleState.READY)) {
					return Images.ICON_MEEM_STATE_READY.createImage();					
				}
				else
				if(state.equals(LifeCycleState.PENDING)) {
					return Images.ICON_MEEM_STATE_PENDING.createImage();					
						
				}
				else
				if(state.equals(LifeCycleState.LOADED)) {
					return Images.ICON_MEEM_STATE_LOADED.createImage();					
						
				}
				else
				if(state.equals(LifeCycleState.DORMANT)) {
					return Images.ICON_MEEM_STATE_DORMANT.createImage();					
				}
				else
				if(state.equals(LifeCycleState.ABSENT)) {
					return Images.ICON_MEEM_STATE_ABSENT.createImage();					
				}
				return null;
			}
		};
		addColumn(column);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return meemIdentifierColumn.getText((Node) element);
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		return null;
	}
}
