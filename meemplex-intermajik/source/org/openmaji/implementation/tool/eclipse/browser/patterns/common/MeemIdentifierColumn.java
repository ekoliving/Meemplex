/*
 * @(#)MeemIdentifierColumn.java
 * Created on 14/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.presentation.IconExtractor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;


/**
 * <code>MeemIdentifierColumn</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemIdentifierColumn extends Column {

	/**
	 * Constructs an instance of <code>MeemColumn</code>.
	 * <p>
	 * @param id
	 * @param name
	 * @param width
	 * @param style
	 */
	public MeemIdentifierColumn(Object id, String name, int width, int style) {
		super(id, name, width, style);
	}

	/**
	 * Constructs an instance of <code>MeemColumn</code>.
	 * <p>
	 * @param id
	 * @param name
	 * @param width
	 */
	public MeemIdentifierColumn(Object id, String name, int width) {
		super(id, name, width);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.Column#getImage(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	protected Image getImage(Node node) {
		if(!(node instanceof MeemNode)) return null;
		MeemNode meem = (MeemNode)node;
		return IconExtractor.extractSmall(meem.getProxy());
	}
	
	public String getText(Node node) {
		if (node instanceof MeemNode) {
			MeemNode meem = (MeemNode)node;
			return getMeemText(meem);
		} else {
			return node.getText();
		}
	}

	protected String getMeemText(MeemNode meem) {
		ConfigurationHandlerProxy config = meem.getProxy().getConfigurationHandler();
		Object value = config.
			getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
		if(value == null) return meem.getText();
		return value.toString();
	}
}
