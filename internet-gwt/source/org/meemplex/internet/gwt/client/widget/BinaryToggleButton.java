package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.shared.FacetHealthEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A ToggleButton to interface with binary facets of a meem.
 * 
 * @author stormboy
 * 
 */
public class BinaryToggleButton extends BinaryWidget {
	interface ButtonUiBinder extends UiBinder<Widget, BinaryToggleButton> {
	}

	private static ButtonUiBinder uiBinder = GWT.create(ButtonUiBinder.class);

	@UiField Label lbName;
	
	/**
	 * Toggle button
	 */
	@UiField ElementWidget button;

	private boolean meemResolved = false;

	private boolean facetResolved = false;

	private boolean ready = false;

	/**
	 * binary value of the meem
	 */
	private Boolean value;

	public BinaryToggleButton() {
    }
	
	/**
	 * Constructor
	 * 
	 * @param bindingFactory
	 * @param name
	 * @param meemPath
	 */
	public BinaryToggleButton(String name, String meemPath) {
		super(name, meemPath);
		lbName.setText(getName());
		button.setTitle(getName());

		//GWT.log("created BinaryToggleButton for " + meemPath);
	}

	@Override
	public void setName(String text) {
	    super.setName(text);
	    lbName.setText(text);
		button.setTitle(getName());
	}
	
	@Override
	public void addStyleName(String style) {
	    super.addStyleName(style);
	    if ("hvac light security lock blind disabled".contains(style)) {
	    	button.addStyleName(style);
	    }
	}


	@Override
	protected Widget createWidget() {
		return uiBinder.createAndBindUi(this);
	}

	/**
	 * Handle value received from InboundBinding
	 * 
	 * @param value
	 */
	@Override
	protected void handleValue(Boolean value) {
		this.value = value;
		
		String text = value ? getOnText() : getOffText();
		button.setName(text);
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
		button.setEnabled(enabled);
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

	@UiHandler("button")
	void handleClick(ClickEvent event) {
		GWT.log("BinaryToggleButton: got button click");
		if (value == null) {
			JSONValue[] args = new JSONValue[] { JSONBoolean.getInstance(true) };
			getOutboundBinding().send("valueChanged", args);
		}
		else /* if (isDown() != value) */{
			JSONValue[] args = new JSONValue[] { JSONBoolean.getInstance(!value) };
			getOutboundBinding().send("valueChanged", args);
		}
	}
	
}
