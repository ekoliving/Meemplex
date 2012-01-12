package org.meemplex.internet.gwt.client.widget;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.client.binding.PropertiesBinding;
import org.meemplex.internet.gwt.client.binding.impl.PropertiesSpecInboundBinding;
import org.meemplex.internet.gwt.client.facets.PropertiesSpecification;
import org.meemplex.internet.gwt.client.facets.PropertiesSpecification.PropertySpecification;
import org.meemplex.internet.gwt.shared.BindingFactory;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Allow user to view and edit properties of a Meem.
 * 
 * @author stormboy
 *
 */
public class MeemPropertiesList extends Composite implements MeemPathHandler {
	private BindingFactory bindingFactory;
	
	private PropertiesBinding propertiesBinding;
	
	private FlowPanel panel;
	
	private FlexTable table;
	
	private String meemPath;

	private Map<String, PropertySpecification> specs = new TreeMap<String, PropertySpecification>();
	
	private Map<String, Serializable> values = new HashMap<String, Serializable>();


	public MeemPropertiesList(BindingFactory bindingFactory) {
		this.bindingFactory = bindingFactory;
		initWidget(getPanel());
	}

	public FlowPanel getPanel() {
		if (panel == null) {
			panel = new FlowPanel();
			panel.add(getTable());
		}
		return panel;
	}
	public void meemPath(String meemPath) {
		if (meemPath.equals(this.meemPath)) {
			return;
		}
		
		release();
		
		this.meemPath = meemPath;
		propertiesBinding = new PropertiesBinding(bindingFactory, meemPath);
		PropertiesSpecInboundBinding propertiesSpecBinding = (PropertiesSpecInboundBinding) propertiesBinding.getInboundBinding();
		propertiesSpecBinding.addHandler(propertiesSpecListener);
	}
	
	public void release() {
		if (propertiesBinding != null) {
			PropertiesSpecInboundBinding propertiesSpecBinding = (PropertiesSpecInboundBinding) propertiesBinding.getInboundBinding();
			propertiesSpecBinding.removeHandler(propertiesSpecListener);
			propertiesBinding.release();
			propertiesBinding = null;
		}
		getTable().removeAllRows();
	}
	
	private FlexTable getTable() {
		if (table == null) {
			table = new FlexTable();
			table.setWidth("100%");
			refresh();
		}
		return table;
	}
	
	private void refresh() {
		getTable().removeAllRows();
		int row = 0;
		for (PropertySpecification spec : specs.values()) {
			getTable().setHTML(row, 0, spec.getPropertyName());
			getTable().setHTML(row, 1, spec.getDescription());
			getTable().setHTML(row, 2, spec.getType());
			String value = values.get(spec.getPropertyName()) == null ?  ""+spec.getDefaultValue() : "" + values.get(spec.getPropertyName());
			getTable().setHTML(row, 3, value);
			row++;
		}
	}
	
	private PropertiesSpecification propertiesSpecListener = new PropertiesSpecification() {
		public void specificationChanged(PropertySpecification[] oldSpecifications, PropertySpecification[] newSpecifications) {
			for (PropertySpecification spec : oldSpecifications) {
				specs.remove(spec.getPropertyName());
			}
			for (PropertySpecification spec : newSpecifications) {
				specs.put(spec.getPropertyName(), spec);
			}
			refresh();
		}
		
		public void valueAccepted(String propertyName, Serializable value) {
			values.put(propertyName, value);
			refresh();
		}
		
		public void valueRejected(String propertyName, Serializable value, String reason) {
			refresh();
		}
	};
}
