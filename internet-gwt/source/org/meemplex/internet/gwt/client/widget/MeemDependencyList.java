package org.meemplex.internet.gwt.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MeemDependencyList extends Composite {

	public MeemDependencyList() {
		initWidget(getContent());
	}

	public void facetPath(String facetPath) {
		// TODO show dependencies for the given facet
	}
	
	private HTML getContent() {
		return new HTML("Dependency widget");
	}
}
