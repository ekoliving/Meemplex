/*
 * @(#)PaletteTemplateTransferDropTargetListener.java
 * Created on 4/06/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

/**
 * <code>PaletteTemplateTransferDropTargetListener</code>.
 * <p>
 * @author Kin Wong
 */
public class PaletteTemplateTransferDropTargetListener
	extends TemplateTransferDropTargetListener {
	/**
	 * Constructs an instance of <code>PaletteTemplateTransferDropTargetListener</code>.
	 * <p>
	 * @param viewer The edit-part viewer to associates with this listener.
	 */
	public PaletteTemplateTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.TemplateTransferDropTargetListener#getFactory(java.lang.Object)
	 */
	protected CreationFactory getFactory(Object template) {
		if(!(template instanceof String)) return null;
		return PaletteBuilder.getTemplateFactory((String)template);
	}
}
