package org.meemplex.internet.gwt.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ElementWidget extends Composite {

	private static ElementWidgetUiBinder uiBinder = GWT.create(ElementWidgetUiBinder.class);

	interface ElementWidgetUiBinder extends UiBinder<Widget, ElementWidget> {
	}

	@UiField HTMLPanel panel;
	
	@UiField ParagraphElement number;
	
	@UiField HeadingElement symbol;
	
	@UiField ToggleButton button;
	
	@UiField HeadingElement name;
	
	
	public ElementWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setName(String text) {
		button.getUpFace().setText(text);
		button.getDownFace().setText(text);
		name.setInnerText(text);
	}
	
	public String getName() {
		return name.getInnerText();
	}
	
	public void setNumber(Integer n) {
		String text = number == null ? "" : n.toString();
		number.setInnerText(text);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		//GWT.log("elementWidget: **** adding handler " + handler);
		//return addHandler(handler, ClickEvent.getType());
		return button.addClickHandler(handler); // addHandler(handler, ClickEvent.getType());
	}
	
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		if (enabled) {
			removeStyleName("disabled");
		}
		else {
			addStyleName("disabled");
		}
	}

	public boolean isEnabled() {
		return button.isEnabled();
	}
	
	@UiHandler("button")
	void handleButtonClick(ClickEvent event) {
		//GWT.log("ElementWidget: got button click");
		fireEvent(event);
	}
}
