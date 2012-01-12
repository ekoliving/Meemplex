package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.ClientFactory;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GroupButton extends Composite {

	private static boolean DEBUG = true;
	
	private static GroupButtonUiBinder uiBinder = GWT.create(GroupButtonUiBinder.class);

	interface GroupButtonUiBinder extends UiBinder<Widget, GroupButton> {
	}

	@UiField Label labelName;
	
	@UiField ElementWidget onWidget;
	@UiField ElementWidget offWidget;
	
	int onCount = 0;
	int offCount = 0;

	String meemPath = "hyperSpace:/site/smart/function/light/allHouseLights";
	
	String facetBinaryIn = "binaryInput";
	String facetBinaryOut = "binaryOutput";
	String facetOnCount = "onCount";
	String facetOffCount = "offCount";

	private OutboundBinding outboundBinary;
	private InboundBinding inboundOnCount;
	private InboundBinding inboundOffCount;
	
	private boolean meemResolved = false;
	private boolean facetResolved = false;
	private boolean ready = false;

	
	public GroupButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setMeemPath(String meemPath) {
		GWT.log("GroupButton: setting meempath: " + meemPath);
		
		this.meemPath = meemPath;
		unbind();
		bind();
	}
	
	public String getMeemPath() {
		return meemPath;
	}
	
	public void setName(String name) {
		labelName.setText(name);
	}
	
	public String getName() {
		return labelName.getText();
	}
	
	public void setOnText(String text) {
		onWidget.setName(text);
	}

	public String getOnText() {
		return onWidget.getName();
	}
	
	public void setOffText(String text) {
		offWidget.setName(text);
	}
	public String getOffText() {
		return offWidget.getName();
	}
	
	@Override
	public void addStyleName(String style) {
	    super.addStyleName(style);
	    if ("hvac light security lock blind disabled".contains(style)) {
	    	offWidget.addStyleName(style);
	    	onWidget.addStyleName(style);
	    }
	}

	@UiHandler("onWidget")
	void onOnClick(ClickEvent event) {
		JSONValue[] params = new JSONValue[] {
				JSONBoolean.getInstance(true)
		};
		outboundBinary.send("valueChanged", params);
	}

	@UiHandler("offWidget")
	void onOffClick(ClickEvent event) {
		JSONValue[] params = new JSONValue[] {
				JSONBoolean.getInstance(false)
		};
		outboundBinary.send("valueChanged", params);
	}
	
	/**
	 * Make sure bindings are unloaded
	 */
//	@Override
//	protected void onUnload() {
//	    super.onUnload();
//	    unbind();
//	}

	private void handleHealth(FacetHealthEvent event) {
		if (DEBUG) {
			GWT.log("GroupButton: got health event: " + event);
		}
		updateResolved(event.getBindingState());
		ready = "ready".equals(event.getLifeCycleState());
		updateEnabled();
	}
	
	private void handleOnCount(double on) {
		if (DEBUG) {
			GWT.log("GroupButton: got number on: " + on);
		}
		onWidget.setNumber((int)on);
	}
	
	private void handleOffCount(double off) {
		if (DEBUG) {
			GWT.log("GroupButton: got number off: " + off);
		}
		offWidget.setNumber((int)off);
	}

	
	private void bind() {
		BindingFactory bindingFactory = ClientFactory.spi.singleton().getFacetEventHub();
		
		outboundBinary = bindingFactory.createOutboundBinding(new FacetReference(meemPath, facetBinaryIn, FacetClasses.BINARY));
		
		inboundOnCount = bindingFactory.createInboundBinding(new FacetReference(meemPath, facetOnCount, FacetClasses.LINEAR));
		inboundOnCount.addListener(new FacetEventListener() {
			public void facetEvent(FacetEvent event) {
				if (DEBUG) {
					GWT.log("GroupButton: got onCount event: " + event);
				}
				if ("valueChanged".equals(event.getMethod())) {
					JSONNumber val = JSONParser.parseLenient(event.getParams()[0]).isNumber();
					if (val != null) {
						handleOnCount(val.doubleValue());
					}
				}
			}
		});
		inboundOnCount.addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				handleHealth(event);
			}
		});		
		


		inboundOffCount = bindingFactory.createInboundBinding(new FacetReference(meemPath, facetOffCount, FacetClasses.LINEAR));
		inboundOffCount.addListener(new FacetEventListener() {
			public void facetEvent(FacetEvent event) {
				if (DEBUG) {
					GWT.log("GroupButton: got offCount event: " + event);
				}
				if ("valueChanged".equals(event.getMethod())) {
					JSONNumber val = JSONParser.parseLenient(event.getParams()[0]).isNumber();
					if (val != null) {
						handleOffCount(val.doubleValue());
					}
				}
			}
		});
	}
	
	private void unbind() {
		BindingFactory bindingFactory = ClientFactory.spi.singleton().getFacetEventHub();
		if (outboundBinary != null) {
			bindingFactory.releaseBinding(outboundBinary.getFacetReference());
		}
		if (inboundOnCount != null) {
			bindingFactory.releaseBinding(inboundOnCount.getFacetReference());
		}
		if (inboundOffCount != null) {
			bindingFactory.releaseBinding(inboundOffCount.getFacetReference());
		}
	}
	
	
	private void updateEnabled() {
		boolean enabled = meemResolved && facetResolved && ready;
		//	GWT.log("GroupButton: **** enabled: " + enabled + " - " + meemResolved + " " + facetResolved + " " + ready);
		if (enabled) {
			onWidget.removeStyleName("disabled");
			offWidget.removeStyleName("disabled");
		}
		else {
			onWidget.addStyleName("disabled");
			offWidget.addStyleName("disabled");
		}
		offWidget.setEnabled(enabled);
		onWidget.setEnabled(enabled);

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
