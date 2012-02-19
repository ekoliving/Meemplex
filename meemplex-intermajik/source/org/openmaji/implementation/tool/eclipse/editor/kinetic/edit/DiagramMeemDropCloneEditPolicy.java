/*
 * @(#)DiagramMeemDropCloneEditPolicy.java
 * Created on 13/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.util.Iterator;
import java.util.List;


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.MeemDropCloneEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.MeemCloneRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.NamedMeemRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.ConfigurationHandlerCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.MeemUpdateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.VariableMapCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.VariableSourceFactory;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemAttribute;


/**
 * <code>DiagramMeemDropCloneEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class DiagramMeemDropCloneEditPolicy extends MeemDropCloneEditPolicy {
	
	protected Diagram getDiagram() {
		return (Diagram)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.dnd.MeemDropCloneEditPolicy#getNamedMeemCommand(org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.NamedMeemRequest)
	 */
	protected Command getNamedMeemCommand(NamedMeemRequest request) {
		CompoundCommand allCmds = new CompoundCommand();
		Point location = getPointFromRequest(request);
		List newObjects = request.getNewObjects();
		for (Iterator iter = newObjects.iterator(); iter.hasNext();) {
			Meem meem = (Meem)iter.next();
			allCmds.add(createCommand(request, location, meem));
			
		}
		return allCmds;
	}
	protected Command createCommand(NamedMeemRequest request, Point location, Meem meem) {
		CompoundCommand cmds = new CompoundCommand();
		//String identifier = meem.getName();
		cmds.add(createAddCommand(location, meem));
		cmds.add(createCategoryCommand(meem));
		cmds.add(createVariableMapCommand(meem));
		//cmds.add(createMeemUpdateCommand(meem, identifier));
		
		if(request.getType().equals(MeemCloneRequest.REQ_MEEM_CLONE))
			cmds.add(createMeemIdentifierCommand(meem));
		
		return cmds;
	}
	
	protected Command createAddCommand(Point location, Meem meem) {
		ElementContainer parent = (ElementContainer)getHost().getModel();
		if(!parent.isValidNewChild(meem)) return null;

		Rectangle bounds = new Rectangle(location, new Dimension(-1,-1));
		meem.setBounds(bounds);
		return new CreateCommand(parent, meem, bounds.getCopy());
	}

	protected Command createCategoryCommand(Meem meem) {
		MeemPath meemPath = meem.getMeemPath();
		CategoryProxy category = getDiagram().getProxy().getCategoryProxy();
		String entryName = CategoryEntryNameFactory.
			createUniqueEntryName(category, meem.getName());
		meem.setName(entryName);
		return new CategoryEntryCreateCommand(category, entryName, meemPath);
	}
	
	protected Command createMeemUpdateCommand(Meem meem, String identifier) {
		MeemAttribute attribute = (MeemAttribute)
			meem.getProxy().getMetaMeem().getStructure().getMeemAttribute().clone();
		attribute.setIdentifier(identifier);
		
		return new MeemUpdateCommand(meem.getProxy().getMetaMeem(), attribute);
	}
	
	protected Command createVariableMapCommand(Meem meem) {
		Diagram diagram = getDiagram();
		IVariableSource vs = VariableSourceFactory.getInstance().createVariableSource(meem);
		ElementPath path = getDiagram().getPath().append(meem.getPath());
		return new VariableMapCreateCommand(diagram.getProxy().getVariableMapProxy(), path, vs.extractAll());
		
	}
	protected Command createMeemIdentifierCommand(Meem meem) {
		return new ConfigurationHandlerCommand(
				meem.getProxy().getConfigurationHandler(), 
				ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER,
				meem.getName());
	}
}