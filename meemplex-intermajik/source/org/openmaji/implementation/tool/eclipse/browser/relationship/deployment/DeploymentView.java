/*
 * @(#)DeploymentView.java
 * Created on 3/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment;


import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd.DeploymentDropAdapter;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd.MeemNodeDragSourceListener;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels.NodeConfigurationLabelProvider;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels.StandardMeemTableLabelProvider;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels.SubsytemManagerTableLabelProvider;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.MeemServerManagerNode;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemFactoryNode;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.NodeSorter;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.LabelNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeTreeContentProvider;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemNodeTransfer;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meemserver.MeemServer;


/**
 * <code>DeploymentView</code>.
 * <p>
 * @author Kin Wong
 */
public class DeploymentView extends ExplorerView {
	private StandardMeemTableLabelProvider standardMeemTableLabelProvider = 
		new StandardMeemTableLabelProvider();
		
	private SubsytemManagerTableLabelProvider subsytemManagerTableLabelProvider = 
		new SubsytemManagerTableLabelProvider();

	/**
	 * Constructs an instance of <code>DeploymentView</code>.<p>
	 */
	public DeploymentView() {
		setContentNodeFactory(new DeploymentContentNodeFactory());
		setControllerFactory(new DeploymentControllerFactory());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createTreeViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer treeViewer = super.createTreeViewer(parent);
		
		Node rootNode = new LabelNode();
		treeViewer.setContentProvider(new NodeTreeContentProvider());
		treeViewer.setLabelProvider(new NodeConfigurationLabelProvider());
		treeViewer.setSorter(new NodeSorter());
		treeViewer.setInput(rootNode);

		// Add MeemServer Manager

		MeemClientProxy meemServerManager = 
			InterMajikClientProxyFactory.getInstance().
				getMeem(MeemPath.spi.create(Space.HYPERSPACE, "/deployment/" + MeemServer.spi.getName() + "/essential/meemServerController"));
			
		rootNode.addChild("MeemServerManager", 
			new MeemServerManagerNode("EdgeSystem", meemServerManager));
		return treeViewer;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createContentViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected TableViewer createContentViewer(Composite parent) {
		TableViewer tableViewer = super.createContentViewer(parent);
		tableViewer.setSorter(new NodeSorter());
		return tableViewer;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createLoginView(org.eclipse.swt.widgets.Composite)
	 */
	protected void createLoginView(Composite parent) {
		super.createLoginView(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#initialiseDragAndDrop()
	 */
	protected void initialiseDragAndDrop() {
		int ops = DND.DROP_COPY|DND.DROP_MOVE;
		
		Transfer[] dragTransfers = new Transfer[] { MeemNodeTransfer.getInstance()};
		Transfer[] dropTransfers = new Transfer[] { MeemNodeTransfer.getInstance()};
		
		getTreeViewer().addDragSupport(ops, dragTransfers, 
			new MeemNodeDragSourceListener(getTreeViewer()));

		getContentViewer().addDragSupport(ops, dragTransfers, 
			new MeemNodeDragSourceListener(getContentViewer()));

		getTreeViewer().addDropSupport(
			ops | DND.DROP_DEFAULT, 
			dropTransfers, 
			new DeploymentDropAdapter(getTreeViewer()));
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createContentLabelProvider(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	protected ITableLabelProvider createContentLabelProvider(Node node) {
		if(node instanceof SubsystemFactoryNode) {
			return subsytemManagerTableLabelProvider;
		}
		else
		return standardMeemTableLabelProvider;
	}
}
