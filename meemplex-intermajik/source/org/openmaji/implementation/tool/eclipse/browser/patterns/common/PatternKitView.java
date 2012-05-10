/*
 * @(#)PatternKitView.java
 * Created on 4/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import java.util.Iterator;


import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.openmaji.implementation.server.nursery.pattern.MeemPattern;
import org.openmaji.implementation.tool.eclipse.filters.MeemTypeFilter;
import org.openmaji.implementation.tool.eclipse.filters.MeemTypeInclusionFilter;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.server.presentation.PatternGroup;


/**
 * <code>PatternKitView</code>.
 * <p>
 * @author Kin Wong
 */
public class PatternKitView extends ExplorerView {
	static private MeemAbstractTableLabelProvider labelProvider = 
		new MeemAbstractTableLabelProvider();
	
	/**
	 * Constructs an instance of <code>PatternKitView</code>.
	 * <p>
	 */
	public PatternKitView() {
		setContentNodeFactory(new ContentNodeFactory());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createTreeViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer treeViewer = super.createTreeViewer(parent);

		MeemTypeFilter filter = new MeemTypeInclusionFilter();
		filter.addFacet(PatternGroup.class);
		treeViewer.addFilter(filter);
		return treeViewer;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createContentViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected TableViewer createContentViewer(Composite parent) {
		TableViewer tableViewer = super.createContentViewer(parent);
		MeemTypeFilter filter = new MeemTypeInclusionFilter();
		filter.addFacet(MeemPattern.class);
		tableViewer.addFilter(filter);
		tableViewer.setSorter(new PatternSorter());
		return tableViewer;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createContentLabelProvider(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	protected ITableLabelProvider createContentLabelProvider(Node node) {
		return labelProvider;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#getDocumentation(org.eclipse.jface.text.Document, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	protected boolean getDocumentation(
		Document document,
		IStructuredSelection selection) {
			if(selection.size() == 0) {
				document.set("No Pattern is currently selected.\n");
				return true;
			}

			if(selection.size() > 1) {
				document.set(Integer.toString(selection.size()) + " Patterns have been selected.\n");
			}
		
			Iterator it = selection.iterator();
			while(it.hasNext()) {
				MeemNode node = (MeemNode)it.next();
				MeemDocumentationAuthor author = 
					new MeemDocumentationAuthor(node.getText(), node.getProxy());
				try {
					author.write(document);
				}
				catch(Exception e){ 			
				}
			}
			return true;
	}
}
