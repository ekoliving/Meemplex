package org.meemplex.internet.gwt.client.widget;

import java.util.Iterator;

import org.meemplex.internet.gwt.client.StyleNames;
import org.meemplex.internet.gwt.client.event.Filtered;
import org.meemplex.internet.gwt.client.util.WidgetIdGenerator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class CategoryGroup extends Composite implements HasWidgets, Filtered {

	interface CategoryGroupUiBinder extends UiBinder<Panel, CategoryGroup> {
	}

	private static CategoryGroupUiBinder uiBinder = GWT.create(CategoryGroupUiBinder.class);

	private String id;

	// private JSONObject options;

	@UiField
	FlowPanel panel;

	private Timer layoutTimer = new Timer() {
		public void run() {
			reLayoutJS(CategoryGroup.this, id);
		}
	};

	public CategoryGroup() {
		initWidget(uiBinder.createAndBindUi(this));
		id = WidgetIdGenerator.getId();
		getElement().setId(id); // make an id, unique to this app
		addStyleName("clearfix");
		addStyleName("isotope");
		addStyleName("fitRows");
		// addStyleName(StyleNames.UI_GROUP);
	}

	public void filter(String selector) {
		filterJS(id, selector);
	}

	public void layout() {
		reLayoutJS(CategoryGroup.this, id);
	}

	@Override
	public void add(Widget w) {
		panel.add(w);
		ResizeHandler resizeHandler = new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				GWT.log("group: got resize: " + event);
				reLayoutJS(CategoryGroup.this, id);
			}
		};
		w.addHandler(resizeHandler, ResizeEvent.getType());

		addChildJS(this, getElement().getId(), w.getElement());

		// relayout
		layoutTimer.cancel();
		layoutTimer.schedule(100);
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLoad() {
		// if (options == null) {
		// options = new JSONObject();
		// }

		createDivJS(this, getElement().getId() /* , options.getJavaScriptObject() */);
		super.onLoad();
	}

	/**
	 * Set isotype settings
	 * 
	 * @param x
	 *            this object, for callbacks
	 * @param id
	 */
	private native void createDivJS(CategoryGroup x, String id /* , JavaScriptObject options */) /*-{
		$wnd.$("#" + id).isotope({
			layoutMode : 'fitRows',
			masonry : {
				columnWidth : 100
			},
			masonryHorizontal : {
				rowHeight : 100
			},
			cellsByRow : {
				columnWidth : 290,
				rowHeight : 400
			}
		});
	}-*/;

	private native void addChildJS(CategoryGroup x, String id, Element child) /*-{
	                                                                          var $newEls = $wnd.$( [child] );
	                                                                          $wnd.$("#" + id).isotope( 'appended', $newEls );
	                                                                          
	                                                                          }-*/;

	/**
	 * Redo layout
	 * 
	 * @param x
	 * @param id
	 */
	private native void reLayoutJS(CategoryGroup x, String id) /*-{
	                                                           $wnd.$("#" + id).isotope( 'reLayout' );
	                                                           }-*/;

	/**
	 * select = "*", "light"
	 * 
	 * @param id
	 * @param selector
	 */
	private native void filterJS(String id, String selector) /*-{
	                                                         $wnd.$("#" + id).isotope( { filter : selector } );
	                                                         }-*/;

}
