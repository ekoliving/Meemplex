package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.MeemOpenHandler;
import org.meemplex.internet.gwt.client.MeemPathHandler;
import org.meemplex.internet.gwt.shared.BindingFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A stack of panels that displays widgets representing Meems within a category and
 * allows a user to navigate down a category tree, and back.
 * 
 * Each time a child category is navigated to, a new panel is added to the stack, displaying
 * widgets for it's Meem entries.
 * 
 * @author stormboy
 *
 */
public class CategoryDeck extends Composite implements MeemPathHandler {
	
	interface DeckUiBinder extends UiBinder<DeckPanel, CategoryDeck> {
	}

	private static DeckUiBinder uiBinder = GWT.create(DeckUiBinder.class);

	private BindingFactory bindingFactory;
	
	/**
	 * MeemPath.  May be a HyperSpace path.
	 */
	private String meemPath;
	
	@UiField DeckPanel panel;
	
	public CategoryDeck(BindingFactory bindingFactory, String meemPath) {
		this.bindingFactory = bindingFactory;
		this.meemPath = meemPath;
		initWidget(uiBinder.createAndBindUi(this));
		
		addPanel("Home", meemPath, true);
    }
	
	@Override
	public void meemPath(String meemPath) {
		if (meemPath.equals(this.meemPath)) {
			return;
		}
		this.meemPath = meemPath;
		panel.clear();
		
		addPanel("Home", meemPath, true);
	}
	

	private void addPanel(String name, String categoryPath, boolean top) {
		GWT.log("adding panel for " + categoryPath);
		

		CategoryPanel categoryPanel = new CategoryPanel(bindingFactory, name, categoryPath, top);
		categoryPanel.addHandler(new MeemOpenHandler() {
			public void open(String name, String meemPath) {
				addPanel(name, meemPath, false);
			}
			public void close(String meemPath) {
				popPanel();
			}
		});
		
		FlowPanel newPanel = new FlowPanel();
		newPanel.add(categoryPanel);
		
		panel.add(newPanel);
		panel.showWidget(panel.getWidgetCount()-1);
	}
	
	private void popPanel() {
		panel.remove(panel.getWidgetCount()-1);
		panel.showWidget(panel.getWidgetCount()-1);
	}
	
}
