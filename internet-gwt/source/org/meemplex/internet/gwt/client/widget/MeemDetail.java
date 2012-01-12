package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.shared.BindingFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * Listens for 
 * 	- Wedges of the meem.
 * 	- Facets.
 * 	- config properties
 * 	- dependencies
 * 	- lifecycle state
 * 
 * Allows for editing of
 * 	- Properties
 * 	- dependencies
 * 	- lifecycle state
 * 
 * @author stormboy
 *
 */
public class MeemDetail extends Composite implements MeemPathHandler {
	private BindingFactory bindingFactory;

	private StackLayoutPanel panel;
	
	private MeemPropertiesList propertiesList;
	
	private MeemLifeCycleWidget lifeCycleWidget;
	
	private MeemFacetList facetList;
	
	private MeemDependencyList dependencyList;
	
	public MeemDetail(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
		initWidget(getPanel());
	}
	
	public void meemPath(String meemPath) {
		getFacetList().meemPath(meemPath);
		getPropertiesList().meemPath(meemPath);
		//getDependencyList().meemPath(meemPath);
		//getLifeCycleWidget().meemPath(meemPath);
	}

	private StackLayoutPanel getPanel() {
		if (panel == null) {
			panel = new StackLayoutPanel(Unit.EM);
			panel.add(getFacetList(), "Facets", 2);
			panel.add(getPropertiesList(), "Properties", 2);
			panel.add(getDependencyList(), "Dependencies", 2);
			panel.add(getLifeCycleWidget(), "LifeCycle", 2);
			panel.setHeight("600px");
		}
		return panel;
	}
	
	private MeemPropertiesList getPropertiesList() {
		if (propertiesList == null) {
			propertiesList = new MeemPropertiesList(bindingFactory);
		}
		return propertiesList;
	}
	
	private MeemDependencyList getDependencyList() {
		if (dependencyList == null) {
			dependencyList = new MeemDependencyList();
		}
	    return dependencyList;
    }
	
	private MeemFacetList getFacetList() {
		if (facetList == null) {
			facetList = new MeemFacetList(bindingFactory);
		}
	    return facetList;
    }
	
	private MeemLifeCycleWidget getLifeCycleWidget() {
		if (lifeCycleWidget == null) {
			lifeCycleWidget = new MeemLifeCycleWidget();
		}
	    return lifeCycleWidget;
    }
	
}
