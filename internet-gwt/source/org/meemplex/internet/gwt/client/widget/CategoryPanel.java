package org.meemplex.internet.gwt.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.MeemOpenHandler;
import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays widgets representing Meems within a category.
 * 
 * @author stormboy
 *
 */
public class CategoryPanel extends Composite implements MeemPathHandler {
	
	interface PanelUiBinder extends UiBinder<FlowPanel, CategoryPanel> {
	}

	private static PanelUiBinder uiBinder = GWT.create(PanelUiBinder.class);

	private boolean debug = true;
	
	private BindingFactory bindingFactory;
	
	private String name;
	
	/**
	 * The MeemPath of the Category Meem this Widget is representing.
	 * May be a HyperSpace path.
	 */
	private String meemPath;
	
	/**
	 * Facet, outbound from the Meem to receive messages from.
	 */
	private String outboundFacet = "categoryClient";
	
	/**
	 * Binding for receiving references to children of this category
	 */
	private InboundBinding inboundBinding;

	/**
	 * The widgets used in the display.
	 */
	private Map<String, MeemControlWidget> controlWidgets = new HashMap<String, MeemControlWidget>();
	
	@UiField HTML nameLabel;
	
	@UiField Anchor backButton;
	
	@UiField Image loadingImage;
	
	@UiField CategoryGroup group;

	/**
	 * These listeners  will receive eents when child categories are selected, and
	 * when the back anchor is selected.
	 */
	private List<MeemOpenHandler> meemPathHandlers = new ArrayList<MeemOpenHandler>();

	/**
	 * 
	 * @param rpcClient
	 * @param name
	 * @param meemPath
	 */
	public CategoryPanel(BindingFactory bindingFactory, String name, String meemPath) {
		this(bindingFactory, name, meemPath, false);
    }
	
	/**
	 * 
	 * @param bindingFactory
	 * @param name
	 * @param meemPath
	 * @param top
	 * 			if this widget is the "top" panel, a "back" anchor is not to be displayed
	 */
	public CategoryPanel(BindingFactory bindingFactory, String name, String meemPath, boolean top) {
		this.bindingFactory = bindingFactory;
		this.name = name;
		this.meemPath = meemPath;

		initWidget(createWidget());
		
		nameLabel.setHTML("<h2>" + name + "</h2>");
		backButton.setVisible(!top);

		setup();
    }
	
	public void meemPath(String meemPath) {
		if (meemPath.equals(this.meemPath)) {
			return;
		}
		this.meemPath = meemPath;
		cleanUp();
		setup();
	}
	
	public void addHandler(MeemOpenHandler handler) {
		meemPathHandlers.add(handler);
	}
	
	public void removeHandler(MeemOpenHandler handler) {
		meemPathHandlers.remove(handler);
	}
	
	public void setName(String name) {
		this.name = name;
		nameLabel.setText("<h2>" + name + "</h2>");
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	protected void onUnload() {
		GWT.log("unloading cat panel: " + meemPath + "|" + inboundBinding);
	    super.onUnload();
	    //meemPathHandlers.clear();
	    cleanUp();
	}
	
	private void cleanUp() {
		// remove widgets
		for (Entry<String, MeemControlWidget> widget : controlWidgets.entrySet()) {
			group.remove(widget.getValue());
		}
		controlWidgets.clear();

		// release the binding
		if (inboundBinding != null) {
			inboundBinding.removeListener(facetEventListener);
			bindingFactory.releaseBinding(inboundBinding.getFacetReference());
			inboundBinding = null;
		}
	}
	
	protected Widget createWidget() {
		return uiBinder.createAndBindUi(this);
	}

	@UiHandler("backButton")
	void onBackClick(ClickEvent event) {
		sendCloseMeemPath(meemPath);
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
	
	private void setup() {
		GWT.log("setting up binding: " + meemPath + " " + outboundFacet);
		inboundBinding = bindingFactory.createInboundBinding(
				new FacetReference(meemPath, outboundFacet, FacetClasses.CATEGORY_CLIENT)
			);
		inboundBinding.addBindingHealthListener(facetHealthListener);
		inboundBinding.addListener(facetEventListener);
	}
	
	/**
	 * Listens on the CategoryClient Facet for Category entries that have been
	 * added, renamed or removed.
	 */
	private FacetEventListener facetEventListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			if (debug) {
				GWT.log("inboundBinding: got CategoryClient event: " + event);
			}

			if ("entriesAdded".equals(event.getMethod())) {
				JSONArray entries = JSONParser.parseLenient(event.getParams()[0]).isArray();
				
				for (int i=0; i<entries.size(); i++) {
					JSONObject entry = entries.get(i).isObject();
					
					// CategoryEntry
					if (debug) {
						GWT.log("got entry: " + entry);
					}
					
					String name = entry.get("name").isString().stringValue();
					String meemPath = entry.get("meem").isString().stringValue();
					if (controlWidgets.get(name) == null) {
						// create UI widget for the entry. Will create binary buttons, linear sliders and category navigators
						MeemControlWidget widget = new MeemControlWidget(bindingFactory, name, meemPath);
						controlWidgets.put(name, widget);
						group.add(widget);
						widget.addHandler(new MeemOpenHandler() {
							public void open(String name, String meemPath) {
								sendOpenMeemPath(name, meemPath);
							}
							public void close(String meemPath) {
								sendCloseMeemPath(meemPath);
							}
						});
					}
				}
				loadingImage.setVisible(false);
			}
			else if ("entriesRemoved".equals(event.getMethod())) {
				JSONArray entries = JSONParser.parseLenient(event.getParams()[0]).isArray();

				for (int i=0; i<entries.size(); i++) {
					JSONObject entry = entries.get(i).isObject();
					if (debug) {
						GWT.log("entry removed: " + entry);
					}
					String name = entry.get("name").isString().stringValue();
					MeemControlWidget button = controlWidgets.remove(name);
					if (button != null) {
						group.remove(button);
					}
				}
			}
			else if ("entryRenamed".equals(event.getMethod())) {
				if (debug) {
					GWT.log("entry renamed: " + event.getParams());
				}
				String origName = (String) event.getParams()[0];
				String newName = (String) event.getParams()[2];
				MeemControlWidget button = controlWidgets.remove(origName);
				if (button != null) {
					button.setName(newName);
					controlWidgets.put(newName, button);
				}
			}
		}
	};
	
	private FacetHealthListener facetHealthListener = new FacetHealthListener() {
		public void facetHealthEvent(FacetHealthEvent event) {
			if (debug) {
				GWT.log("inboundBinding: got CategoryClient health event: " + event);
			}
			// TODO enable/disable this component
			
		}
	};
}
