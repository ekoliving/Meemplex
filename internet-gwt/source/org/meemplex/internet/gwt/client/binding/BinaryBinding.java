package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetReference;

/**
 * 
 * @author stormboy
 *
 */
public class BinaryBinding extends InOutBinding {
	private static final String inboundFacet = "binaryInput";
	private static final String outboundFacet = "binaryOutput";

	public BinaryBinding(BindingFactory bindingFactory, String meemPath) {
		super(
				bindingFactory,
				new FacetReference(meemPath, inboundFacet, FacetClasses.BINARY),
				new FacetReference(meemPath, outboundFacet, FacetClasses.BINARY)
			);
    }
}
