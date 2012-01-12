package org.meemplex.internet.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.meemplex.internet.gwt.client.ClientFactory;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.MeemOpenHandler;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A button to interface with binary facets of a meem.
 * 
 * TODO get icon(s) from meem.
 * 
 * @author stormboy
 *
 */
public class CategoryButton extends Composite {
	
	interface CategoryUiBinder extends UiBinder<Widget, CategoryButton> {}

	private static CategoryUiBinder uiBinder = GWT.create(CategoryUiBinder.class);

	private static boolean debug = false;
	
	private String name;
	
	private String meemPath;
	
	private String inboundFacet = "category";

	private String outboundFacet = "categoryClient";
	
	private InboundBinding inboundBinding;
	
	private OutboundBinding outboundBinding;
	
	private boolean meemResolved = false;
	
	private boolean facetResolved = false;
	
	private boolean ready = false;
	
	private List<MeemOpenHandler> meemPathHandlers = new ArrayList<MeemOpenHandler>();

	@UiField Anchor anchor;
	
	/**
	 * Constructor
	 * 
	 * @param rpcClient
	 * @param name
	 * @param meemPath
	 */
	public CategoryButton(final String name, final String meemPath) {
		this.name = name;
		this.meemPath = meemPath;
		
		BindingFactory bindingFactory = ClientFactory.spi.singleton().getFacetEventHub();
		
		initWidget(uiBinder.createAndBindUi(this));
		//initWidget(anchor);
		
		outboundBinding = bindingFactory.createOutboundBinding(
				new FacetReference(meemPath, inboundFacet, FacetClasses.CATEGORY)
			);
		outboundBinding.addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				updateHealth(event);
			}
		});
		
		inboundBinding = bindingFactory.createInboundBinding(
				new FacetReference(meemPath, outboundFacet, FacetClasses.CATEGORY_CLIENT)
			);
		inboundBinding.addBindingHealthListener(new FacetHealthListener() {
			public void facetHealthEvent(FacetHealthEvent event) {
				updateHealth(event);
			}
		});
		
    }
	
	@UiHandler("anchor")
	void handleClick(ClickEvent event) {
		sendOpenMeemPath(name, meemPath);
	}
	
	public void addHandler(MeemOpenHandler handler) {
		meemPathHandlers.add(handler);
	}
	
	public void removeHandler(MeemOpenHandler handler) {
		meemPathHandlers.remove(handler);
	}
	
	private void sendOpenMeemPath(String name, String meemPath) {
		for (MeemOpenHandler handler : meemPathHandlers) {
			handler.open(name, meemPath);
		}
	}
	
	private void updateEnabled() {
		boolean enabled = meemResolved /*&& facetResolved*/ && ready;
		if (enabled) {
			anchor.setText(this.name);
		}
		anchor.setEnabled(enabled);
	}
	
	private void updateHealth(FacetHealthEvent event) {
		updateResolved(event.getBindingState());
		ready = "ready".equals(event.getLifeCycleState());
		updateEnabled();
	}
	
	private void updateResolved(int bindingState) {
		switch (bindingState) {
		case FacetHealthEvent.FACET_RESOLVED:
			facetResolved = true;
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
