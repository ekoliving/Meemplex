package org.meemplex.internet.gwt.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.Direction;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetDetails;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class MeemFacetList extends Composite implements MeemPathHandler {

	/**
	 * Factory for creating bindings
	 */
	private BindingFactory bindingFactory;

	private String meemPath;
	
	/**
	 * Receives meem facet details
	 */
	private InboundBinding facetBinding;
	
	private FlowPanel panel;
	
	/**
	 * 
	 */
	private FlexTable facetTable;
	
	/**
	 * 
	 */
	private List<FacetDetails> facets = new ArrayList<FacetDetails>();

	private boolean showSystemFacets = false;
	
	public MeemFacetList(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
		initWidget(getPanel());
	}

	public void meemPath(String meemPath) {
		if (meemPath.equals(this.meemPath)) {
			return;
		}
		this.meemPath = meemPath;

		release();
		
		FacetReference facetReference = new FacetReference(meemPath, "facetClientFacet", FacetClasses.FACET_CLIENT);
		facetBinding = bindingFactory.createInboundBinding(facetReference);
		facetBinding.addListener(facetEventListener);
	}
	
	private void release() {
		if (facetBinding != null) {
			facetBinding.removeListener(facetEventListener);
			bindingFactory.releaseBinding(facetBinding.getFacetReference());
			facetBinding = null;
		}
		facets.clear();
		getTable().removeAllRows();
	}
	
	private FlowPanel getPanel() {
		if (panel == null) {
			panel = new FlowPanel();
			panel.add(getTable());
		}
		return panel;
	}
	
	private FlexTable getTable() {
		if (facetTable == null) {
			facetTable = new FlexTable();
			refreshTable();
		}
		return facetTable;
	}

	private void refreshTable() {
		int row = 0;
		getTable().removeAllRows();
		for (FacetDetails facetDetails : facets) {
			if (!showSystemFacets && !systemFacetNames.contains(facetDetails.getFacetName())) {
				getTable().setText(row, 0, facetDetails.getFacetName());
				getTable().setText(row, 1, facetDetails.getFacetClass());
				getTable().setText(row, 2, ""+facetDetails.getDirection());
				row++;
			}
		}
	}
	
	private FacetEventListener facetEventListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			 if ("facetsAdded".equals(event.getMethod())) {
				JSONArray list = JSONParser.parseLenient(event.getParams()[0]).isArray();
				for (int i=0; i<list.size(); i++) {
					JSONArray facetList = list.get(i).isArray();
					String facetName = facetList.get(0).isString().stringValue();
					String facetClass = facetList.get(1).isString().stringValue();
					String direction = facetList.get(2).isString().stringValue();
					FacetDetails facetDetails = new FacetDetails(facetName, facetClass, Direction.valueOf(direction));
					facets.add(facetDetails);
					refreshTable();
				}
			}
			else if ("facetsRemoved".equals(event.getMethod())) {
				JSONArray list = JSONParser.parseLenient(event.getParams()[0]).isArray();
				for (int i=0; i<list.size(); i++) {
					JSONArray facetList = list.get(i).isArray();
					String facetName = facetList.get(0).isString().stringValue();
					String facetClass = facetList.get(1).isString().stringValue();
					String direction = facetList.get(2).isString().stringValue();
					FacetDetails facetDetails = new FacetDetails(facetName, facetClass, Direction.valueOf(direction));
					facets.remove(facetDetails);
					refreshTable();
				}
			}
		}
	};
	
	private static final String[] systemFacetArray = {
		"errorHandler",
		"errorHandlerClient",
		"dependencyHandler",
		"dependencyClient",
		"lifeCycle",
		"lifeCycleManagement",
		"lifeCycleLimit",
		"lifeCycleManagementClient",
		"lifeCycleLimitClient",
		"lifeCycleClient",
		"managedPersistenceHandler",
		"managedPersistenceClient",
		"deviceInput",
		"deviceOutput",
		"remoteMeem",
		"remoteMeemClientFacet",
		"meem",
		"facetClientFacet",
		"meemClientFacet",
		"configurationHandler",
		"configClient",
		"metaMeem",
		"metaMeemClient",
		"lifeCycleClientCondiut",
		"accessControl",
		"accessControlClient",
		"lifeCycleManagementClientCategory",
		"lifeCycleManagerCategory",
		"lifeCycleManagerCategoryClient",
		"lifeCycleAdapter",
		"lifeCycleLimitedAdapter",
		"hyperSpace",
		"lifeCycleManager",
		"lifeCycleManagerClientLC",
		"meemRegistryClient",
		"variableMap",
		"variableMapClient",
		"meemRegistryClientLCM",
		"lifeCycleManagementClientLCM",
		"lifeCycleManagerClient",
		"meemRegistry",
		"accessControl",
		"accessControlClient",
		
		"meemDefinitionClient",
		"meemContentClient",
		"meemStore",
		"persistenceClientAdapter",
		"persistenceHandlerAdapter",
		"lifeCycleManagerClientLCM",
	};
	
	private static final HashSet<String> systemFacetNames = new HashSet<String>(Arrays.asList(systemFacetArray));
}
