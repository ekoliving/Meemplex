package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetReference;

/**
 * 
 * @author stormboy
 *
 */
public class LinearBinding extends InOutBinding {
	private static final String inboundFacet = "linearInput";
	private static final String outboundFacet = "linearOutput";

	public LinearBinding(BindingFactory bindingFactory, String meemPath) {
		super(
				bindingFactory,
				new FacetReference(meemPath, inboundFacet, FacetClasses.LINEAR),
				new FacetReference(meemPath, outboundFacet, FacetClasses.LINEAR)
			);
    }
}
