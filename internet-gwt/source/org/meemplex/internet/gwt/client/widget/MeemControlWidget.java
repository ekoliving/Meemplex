package org.meemplex.internet.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.MeemOpenHandler;
import org.meemplex.internet.gwt.client.StyleNames;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget to interact with a Meem's Facets.
 * This is a composite of possibly many Widgets that intereact woith the Meem.
 * 
 * The widgets created for this Composite depend on the Facets of the Meem, and any
 * UI descriptor/configuration provided by the Meem.
 * 
 * TODO use the UI descriptor Wedge (if found) to configure the Widget(s)
 * 
 * @author stormboy
 *
 */
public class MeemControlWidget extends Composite implements HasResizeHandlers {

	private static boolean debug = false;
	
	private BindingFactory bindingFactory;
	
	/**
	 * The name given to this Meem.
	 */
	private String name;
	
	/**
	 * The path to the Meem.
	 */
	private String meemPath;

	/**
	 * A panel for containing the Widgets.
	 */
	private FlowPanel panel;
	
	private List<Widget> widgets = new ArrayList<Widget>();
	
	private List<MeemOpenHandler> meemPathHandlers = new ArrayList<MeemOpenHandler>();

	/**
	 * Binding for receiving Facets of the Meem.
	 */
	private InboundBinding inboundBinding;
	
	/**
	 * The facet of the Meem that give us Facets of the Meem.
	 */
	private String outboundFacet = "facetClientFacet";
	
	public MeemControlWidget(BindingFactory bindingFactory, String name, String meemPath) {
		this.bindingFactory = bindingFactory;
		this.name = name;
		this.meemPath = meemPath;
		
		initWidget(getPanel());

		// retrive list of Facets from the Meem. 
		inboundBinding = bindingFactory.createInboundBinding(
				new FacetReference(meemPath, outboundFacet, FacetClasses.FACET_CLIENT)
			);
		inboundBinding.addListener(facetListener);
    }
	
	public void setName(String name) {
		for (Widget w : widgets) {
			if (w instanceof MeemBoundWidget) {
				((MeemBoundWidget)w).setName(name);
			}
		}
	}
	
	public void addHandler(MeemOpenHandler handler) {
		meemPathHandlers.add(handler);
	}
	
	public void removeHandler(MeemOpenHandler handler) {
		meemPathHandlers.remove(handler);
	}
	
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}
	
	@Override
	protected void onUnload() {
	    super.onUnload();
		if (inboundBinding != null) {
			inboundBinding.removeListener(facetListener);
			bindingFactory.releaseBinding(inboundBinding.getFacetReference());
			inboundBinding = null;
		}
	}	
	
	private FlowPanel getPanel() {
		if (panel == null) {
			panel = new FlowPanel();
			//panel.addStyleName(StyleNames.UI_WIDGET);
			//panel.setVisible(false);
		}
		return panel;
	}
	
	private void sendOpenMeemPath(String name, String meemPath) {
		for (MeemOpenHandler handler : meemPathHandlers) {
			handler.open(name, meemPath);
		}
	}
	
	private void sendCloseMeemPath(String meemPath) {
		for (MeemOpenHandler handler : meemPathHandlers) {
			handler.close(meemPath);
		}
	}
	
	private void handleFacet(String facetName, String facetClass, String direction) {
		if (widgets.size() == 0) {	// for now, only allow one widget per Meem
			
			// create a Widget, if a Widget for this Facet exists
			final Widget w = WidgetFactory.createWidget(name, meemPath, facetClass);
			if (w != null) {
				getPanel().add(w);
				widgets.add(w);
				
				panel.addStyleName(StyleNames.UI_WIDGET);
				getPanel().setVisible(true);
				
				// resize event to parent
				ResizeEvent.fire(MeemControlWidget.this, w.getOffsetWidth(), w.getOffsetHeight());
				
				ResizeHandler widgetResizeHandler = new ResizeHandler() {
					public void onResize(ResizeEvent event) {
						ResizeEvent.fire(MeemControlWidget.this, w.getOffsetWidth(), w.getOffsetHeight());
					}
				};
				w.addHandler(widgetResizeHandler, ResizeEvent.getType());

				if (w instanceof CategoryButton) {
					((CategoryButton)w).addHandler(new MeemOpenHandler() {
						public void open(String name, String meemPath) {
							sendOpenMeemPath(name, meemPath);
						}
						public void close(String meemPath) {
							sendCloseMeemPath(meemPath);
						}
					});
				}
			}
		}
	}
	
	/**
	 * Listener on FacetEvents relating to the Facets on the remote Meem.
	 */
	private FacetEventListener facetListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			if (debug) {
				GWT.log("inboundBinding: got FacetClient event: " + event);
			}
			 if ("facetsAdded".equals(event.getMethod())) {
				 JSONArray list = JSONParser.parseLenient(event.getParams()[0]).isArray();
				for (int i=0; i<list.size(); i++) {
					JSONArray facetList = list.get(i).isArray();
					String facetName = facetList.get(0).isString().stringValue();
					String facetClass = facetList.get(1).isString().stringValue();
					String direction = facetList.get(2).isString().stringValue();
					handleFacet(facetName, facetClass, direction);
				}
			}
			else if ("facetsRemoved".equals(event.getMethod())) {
				// TODO remove widgets for this Facet
//				List<?> list = (List<?>) event.getParams()[0];
//				for (int i=0; i<list.size(); i++) {
//					List<?> facetList = (List<?>) list.get(i);
//					String facetName = (String)facetList.get(0);
//					String facetClass = (String)facetList.get(1);
//					String direction = (String)facetList.get(2);
//					removeFacet(facetName);
//				}
			}
		}
	};
}
