package org.meemplex.internet.gwt.client.widget;


import org.meemplex.internet.gwt.shared.FacetHealthEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class BinaryButton extends BinaryWidget {

	private static BinaryButtonUiBinder uiBinder = GWT.create(BinaryButtonUiBinder.class);

	interface BinaryButtonUiBinder extends UiBinder<Panel, BinaryButton> {
	}

	private String meemPath;
	
	@UiField
	Label lbName;
	
	@UiField
	ElementWidget btOff;

	@UiField
	ElementWidget btOn;
	
	private boolean meemResolved = false;

	private boolean facetResolved = false;

	private boolean ready = false;

	/**
	 * 
	 */
	public BinaryButton() {
	}

	@Override
	protected Widget createWidget() {
		return uiBinder.createAndBindUi(this);
	}

	@UiHandler("btOff")
	void onOffClick(ClickEvent e) {
		JSONValue[] params = new JSONValue[] {
				JSONBoolean.getInstance(false)
		};
		getOutboundBinding().send("valueChanged", params);
	}

	@UiHandler("btOn")
	void onOnClick(ClickEvent e) {
		JSONValue[] params = new JSONValue[] {
				JSONBoolean.getInstance(true)
		};
		getOutboundBinding().send("valueChanged", params);
	}

	public void setName(String name) {
		lbName.setText(name);
	}
	
	public String getName() {
		return lbName.getText();
	}
	
	/*
	public String getMeemPath() {
	    return meemPath;
    }

	public void setMeemPath(String meemPath) {
	    this.meemPath = meemPath;
    }
    */
	
	public void setOnText(String text) {
		super.setOnText(text);
		btOn.setName(text);
	}

	public void setOffText(String text) {
		super.setOffText(text);
		btOff.setName(text);
	}
	
	@Override
	public void addStyleName(String style) {
	    super.addStyleName(style);
	    if ("hvac light security lock blind disabled".contains(style)) {
	    	btOff.addStyleName(style);
	    	btOn.addStyleName(style);
	    }
	}


	@Override
	protected void handleValue(Boolean value) {
		//this.value = value;
		
		String text = value ? getOnText() : getOffText();
		//button.setName(text);
		btOff.setNumber(value ? 0 : 1);
		btOn.setNumber(value ? 1 : 0);
	}

	@Override
	protected void handleHealth(FacetHealthEvent event) {
		GWT.log("BinaryToggleButton: handle health: " + event);
		updateResolved(event.getBindingState());
		ready = "ready".equals(event.getLifeCycleState());
		updateEnabled();
	}
	
	private void updateEnabled() {
		boolean enabled = meemResolved && facetResolved && ready;
		btOff.setEnabled(enabled);
		btOn.setEnabled(enabled);
	}

	private void updateResolved(int bindingState) {
		switch (bindingState) {
		case FacetHealthEvent.FACET_RESOLVED:
			facetResolved = true;
			meemResolved = true;
			break;
		case FacetHealthEvent.MEEM_RESOLVED:
			meemResolved = true;
			break;
		default:
			meemResolved = false;
			facetResolved = false;
		}
	}
}
