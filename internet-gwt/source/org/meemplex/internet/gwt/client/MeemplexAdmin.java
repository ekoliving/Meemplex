package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.client.gwtrpc.GwtRpcFacetEventHub;
import org.meemplex.internet.gwt.client.notification.NotificationService;
import org.meemplex.internet.gwt.client.widget.CategoryDeck;
import org.meemplex.internet.gwt.client.widget.HyperspaceTree;
import org.meemplex.internet.gwt.client.widget.MeemDetail;
import org.meemplex.internet.gwt.client.widget.NotificationWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MeemplexAdmin implements EntryPoint {

	private GwtRpcFacetEventHub meemRpcClient = null;
	
	private boolean debug = true;

	private ScrollPanel contentPanel;
	
	private FlowPanel header;
	
	private FlowPanel footer;
	
	private MeemDetail meemDetailPanel;
	
	private CategoryDeck categoryDeck;
	
	private ScrollPanel hyperSpacePanel;
	
	private HyperspaceTree hyperspaceTree;

	private static NotificationWidget notificationWidget = new NotificationWidget();
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		GWT.log("module loading");
		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(getMainPanel());
	}
	
	public NotificationService getNotificationService() {
		return notificationWidget;
	}
	
	private DockLayoutPanel getMainPanel() {
		DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		p.addNorth(getHeader(), 2);
		p.addSouth(getFooter(), 2);
		p.addWest(getHyperspacePanel(), 30);
		p.addEast(getContentPanel(), 30);
		p.add(getControlCanvas());
		return p;
	}
	
	private ScrollPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new ScrollPanel();
			contentPanel.add(getMeemDetailPanel());
		}
		return contentPanel;
	}
	
	private FlowPanel getHeader() {
		if (header == null) {
			header = new FlowPanel();
			header.addStyleName("header");
			header.add(new HTML("Meemplex Web View"));
		}
		return header;
	}
	
	private FlowPanel getFooter() {
		if (footer == null) {
			footer = new FlowPanel();
			footer.addStyleName("footer");
			footer.add(new HTML("Brought to you by Sugar Coding"));
		}
		return footer;
	}
	
	private MeemDetail getMeemDetailPanel() {
		if (meemDetailPanel == null) {
			meemDetailPanel = new MeemDetail(meemRpcClient);
		}
		return meemDetailPanel;
	}
	
	private CategoryDeck getControlCanvas() {
		if (categoryDeck == null) {
			GWT.log("creating new  CategoryDeck");

			categoryDeck = new CategoryDeck(getRpcClient(), "hyperSpace:/work/test");
		}
		return categoryDeck;
	}
	
	private ScrollPanel getHyperspacePanel() {
		if (hyperSpacePanel == null) {
			hyperSpacePanel = new ScrollPanel();
			hyperSpacePanel.addStyleName("hyperspacePanel");
			//hyperSpacePanel.add(new HTML("<h2>hyperspace</h2>"));
			hyperSpacePanel.add(getHyperspaceTree());
		}
		return hyperSpacePanel;
	}
	
	private HyperspaceTree getHyperspaceTree() {
		if (hyperspaceTree == null) {
			hyperspaceTree = new HyperspaceTree(getRpcClient());
			hyperspaceTree.addHandler(new MeemPathHandler() {
				public void meemPath(String meemPath) {
					getMeemDetailPanel().meemPath(meemPath);
					getControlCanvas().meemPath(meemPath);
				}
			});
		}
		return hyperspaceTree;
	}

	private GwtRpcFacetEventHub getRpcClient() {
		if (meemRpcClient == null) {
			meemRpcClient = new GwtRpcFacetEventHub();
		}
		return meemRpcClient;
	}
}
