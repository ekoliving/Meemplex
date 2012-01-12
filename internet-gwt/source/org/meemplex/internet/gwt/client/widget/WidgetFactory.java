package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;

import com.google.gwt.user.client.ui.Widget;

public class WidgetFactory {

	public static Widget createWidget(String name, String meemPath, String type) {
		Widget widget = null;
		if (FacetClasses.BINARY.equals(type)) {
			widget = createBinaryButton(name, meemPath);
		}
		else if (FacetClasses.CATEGORY.equals(type)) {
			widget = createCategoryButton(name, meemPath);
		}
		// TODO more widgets

		return widget;
	}
	
	private static BinaryWidget createBinaryButton(String name, String meemPath) {
		BinaryWidget button = new BinaryToggleButton(name, meemPath);
		return button;
	}

	private static CategoryButton createCategoryButton(String name, String meemPath) {
		CategoryButton button = new CategoryButton(name, meemPath);
		return button;
	}
}
