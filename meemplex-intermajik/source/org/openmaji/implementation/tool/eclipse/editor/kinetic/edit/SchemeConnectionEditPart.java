/*
 * @(#)SchemeConnectionEditPart.java
 * Created on 17/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionBendpointEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.ConnectionFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.ConnectionVariableSource;


/**
 * <code>SchemeConnectionEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class SchemeConnectionEditPart extends ConnectionElementEditPart {
	private IFigureSchemeProvider schemeProvider;
	/**
	 * Constructs an instance of <code>SchemeConnectionEditPart</code>.
	 * <p>
	 * @param connectionElement The Connection element associates with this edit
	 * part.
	 */
	public SchemeConnectionEditPart(ConnectionElement connectionElement) {
		super(connectionElement);
		schemeProvider = createSchemeProvider();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		//EditPart Parent = getParent();
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, 
			new ConnectionBendpointEditPolicy());

		installEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_ROLE, 
			new PropertyChangeToVariableMapConnectionEditPolicy(new ConnectionVariableSource(getConnectionElementModel())));
	}

	/**
	 * Creates the figure scheme provider for the figure associates with this 
	 * edit part.
	 * @return IFigureSchemeProvider The figure scheme provider.
	 */
	protected IFigureSchemeProvider createSchemeProvider() {
		return new ConnectionFigureSchemeProvider();
	}
	/**
	 * Returns the figure scheme provider for the figure associates with this 
	 * edit part.
	 * @return IFigureSchemeProvider The figure scheme provider.
	 */
	protected IFigureSchemeProvider getFigureSchemeProvider() {
		return schemeProvider;
	}
	protected FigureScheme getScheme(EditPart editPart) {
		return getFigureSchemeProvider().getScheme(editPart);
	}
}
