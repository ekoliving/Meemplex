/*
 * @(#)MenuAction.java
 * Created on 3/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;

/**
 * <code>MenuAction</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class MenuAction extends SelectionAction implements IMenuCreator {
	//private MenuManager menuManager;
	private List actions;

	/**
	 * Constructs an instance of <code>MenuAction</code>.
	 * <p>
	 * @param part
	 */
	public MenuAction(IWorkbenchPart part) {
		super(part);
		actions = createActions();
		setMenuCreator(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose() {
	}
	
	/**
	 * Creates all the sub-actions.<p>
	 * @return A list that contains all the sub-actions.
	 */
	protected List createActions() {
		ArrayList actions = new ArrayList();
		return actions;
	}

	//=== IMenuCreator Implementation ====
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		updateActions();
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Action action = (Action)iter.next();
			addActionToMenu(menu, action);
		}	
		setEnabled(!actions.isEmpty());
		return menu;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		Menu menu = new Menu(parent);
		updateActions();
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Action action = (Action)iter.next();
			addActionToMenu(menu, action);
		}	
		setEnabled(!actions.isEmpty());
		return menu;
	}

	/**
	 */
	protected void updateActions() {
	}
	
	/**
	 * Helper method that wraps the given action in an ActionContributionItem and then adds it
	 * to the given menu.
	 * 
	 * @param	parent	The menu to which the given action is to be added
	 * @param	action	The action that is to be added to the given menu
	 */
	private void addActionToMenu(Menu parent, IAction action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}
