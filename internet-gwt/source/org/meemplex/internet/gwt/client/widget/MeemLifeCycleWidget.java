package org.meemplex.internet.gwt.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MeemLifeCycleWidget extends Composite {

	public MeemLifeCycleWidget() {
		initWidget(getContent());
	}

	private HTML getContent() {
		return new HTML("LifeCycle widget");
	}
}
