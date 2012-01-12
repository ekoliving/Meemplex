package org.meemplex.internet.gwt.client.widget;

import java.util.HashSet;

import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.shared.BindingFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * A tree for navigating hyperspace/
 *  
 * @author stormboy
 *
 */
public class HyperspaceTree extends Composite {
	
	interface HyperspaceUiBinder extends UiBinder<Widget, HyperspaceTree> {}

	private static HyperspaceUiBinder uiBinder = GWT.create(HyperspaceUiBinder.class);

	
	private String hyperspacePath = "hyperSpace:/";
	
	@UiField Tree tree;
	
	private HashSet<MeemPathHandler> meemPathHandlers = new HashSet<MeemPathHandler>();


	public HyperspaceTree(BindingFactory bindingFactory) {
		
		initWidget(uiBinder.createAndBindUi(this));
		
		// add root node to tree
		CategoryTreeItem treeItem = new CategoryTreeItem(bindingFactory, hyperspacePath);
		treeItem.setText("hyperspace");
		treeItem.load();
		tree.addItem(treeItem);

	}
	
	public void addHandler(MeemPathHandler handler) {
		meemPathHandlers.add(handler);
	}

	public void removeHandler(MeemPathHandler handler) {
		meemPathHandlers.remove(handler);
	}
	
	private void sendMeemPath(String meemPath) {
		for (MeemPathHandler handler : meemPathHandlers) {
			handler.meemPath(meemPath);
		}
	}
	
	@UiHandler("tree") 
	void handleOpen(OpenEvent<TreeItem> event) {
		// load the treeItem's children
		CategoryTreeItem item = (CategoryTreeItem) event.getTarget();
		for (int i=0; i<item.getChildCount(); i++) {
			((CategoryTreeItem)item.getChild(i)).load();
		}
	}
	
	@UiHandler("tree")
	void handleSelection(SelectionEvent<TreeItem> event) {
		CategoryTreeItem item = (CategoryTreeItem) event.getSelectedItem();
		sendMeemPath(item.getMeemPath());
	}

	@Override
	protected void onUnload() {
		// TODO unload/fiunalize children
		
	    super.onUnload();
	}
}
