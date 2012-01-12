package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetReference;

public class CategoryBinding extends InOutBinding {
	private static String inboundFacet = "category";
	
	private static String outboundFacet = "categoryClient";

	/**
	 * 
	 * @param bindingFactory
	 * @param meemPath
	 */
	public CategoryBinding(BindingFactory bindingFactory, String meemPath) {
		super(
				bindingFactory,
				new FacetReference(meemPath, inboundFacet, FacetClasses.CATEGORY),
				new FacetReference(meemPath, outboundFacet, FacetClasses.CATEGORY_CLIENT)
			);
    }
}
