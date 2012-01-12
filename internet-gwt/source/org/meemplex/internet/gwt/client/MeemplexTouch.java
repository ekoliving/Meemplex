package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.client.notification.NotificationService;
import org.meemplex.internet.gwt.client.widget.CategoryDeck;
import org.meemplex.internet.gwt.client.widget.CategoryGroup;
import org.meemplex.internet.gwt.client.widget.ClockWidget;
import org.meemplex.internet.gwt.client.widget.FilterSelector;
import org.meemplex.internet.gwt.client.widget.NotificationWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MeemplexTouch implements EntryPoint {

	interface TouchUiBinder extends UiBinder<DockLayoutPanel, MeemplexTouch> {
	}

	private static TouchUiBinder uiBinder = GWT.create(TouchUiBinder.class);
	
	/**
	 * Home of the UI
	 */
	private static final String HOME_PATH = "hyperSpace:/site/smart/ui";
	
	@UiField HorizontalPanel header;
	
	//@UiField FlowPanel footer;

	@UiField FlowPanel contentPanel;
	
	@UiField CategoryGroup widgetGroup;
	
	@UiField FilterSelector filterSelector;
	
	/**
	 * 
	 */
	private CategoryDeck categoryDeck;
	
	private static NotificationWidget notificationWidget = new NotificationWidget();
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		GWT.log("module loading");

		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(uiBinder.createAndBindUi(this));

	    // Connected out the category deck
		//contentPanel.add(getControlCanvas());
	    
	    filterSelector.addFiltered(widgetGroup);
	    widgetGroup.filter(".house");
	}
	
	public NotificationService getNotificationService() {
		return notificationWidget;
	}
	
	private CategoryDeck getControlCanvas() {
		if (categoryDeck == null) {
			GWT.log("creating new  CategoryDeck");
			categoryDeck = new CategoryDeck(ClientFactory.spi.singleton().getFacetEventHub(), HOME_PATH);
		}
		return categoryDeck;
	}

	

}
