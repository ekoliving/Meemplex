package org.meemplex.internet.gwt.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class TestWidget extends Composite {

	interface TestWidgetUiBinder extends UiBinder<Widget, TestWidget> {
	}

	private static TestWidgetUiBinder uiBinder = GWT.create(TestWidgetUiBinder.class);

	@UiField ParagraphElement number;
	@UiField HeadingElement symbol;
	@UiField HeadingElement name;
	@UiField ParagraphElement weight;
	@UiField ToggleButton toggle;
	
	
	public TestWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		getWidget().addStyleName("element");
		getWidget().addStyleName("alkaline-earth");
		
		getElement().setAttribute("data-symbol", "Ca");
		getElement().setAttribute("data-category", "alkaline-earth");
		//class="metal" data-symbol="Ca" data-category="alkaline-earth"
			
		number.setInnerText("34");
    }
	
	public void setNumber(String text) {
		number.setInnerText(text);
	}
	
	public void setName(String text) {
		this.name.setInnerText(text);
	}
	
	public void setSymbol(String text) {
		toggle.getUpFace().setText(text);
		toggle.getUpHoveringFace().setText(text+".");
		toggle.getUpDisabledFace().setText(text+"x");
		toggle.getDownFace().setText(text+"-");
		toggle.getDownHoveringFace().setText(text+"+");
		toggle.getDownDisabledFace().setText(text+"_");
		
		//this.symbol.setInnerText(text);
	}
	
	public void setWeight(String text) {
		this.weight.setInnerText(text);
	}
	
	public void setClass(String newStyle) {
		Widget w = getWidget();
		for (String style : styles) {
			w.removeStyleName(style);
		}
		w.addStyleName(newStyle);
	}
	
	private static final String STYLE_A = "alkali";
	private static final String STYLE_B = "alkaline-earth";
	private static final String STYLE_C = "lanthanoid";
	private static final String STYLE_D = "actinoid";
	private static final String STYLE_E = "transition";
	private static final String STYLE_F = "post-transition";
	private static final String STYLE_G = "metalloid";
	private static final String STYLE_H = "other.nonmetal";
	private static final String STYLE_I = "halogen";
	private static final String STYLE_J = "noble-gas";
	
	private static final String[] styles = {
		STYLE_A,
		STYLE_B,
		STYLE_C,
		STYLE_D,
		STYLE_E,
		STYLE_F,
		STYLE_G,
		STYLE_H,
		STYLE_I,
		STYLE_J,
	};

}
