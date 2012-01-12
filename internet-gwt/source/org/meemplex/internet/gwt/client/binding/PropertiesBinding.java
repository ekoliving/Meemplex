package org.meemplex.internet.gwt.client.binding;

import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetReference;

public class PropertiesBinding extends InOutBinding {
	private static String inboundFacet = "configurationHandler";
	
	private static String outboundFacet = "configClient";

	/**
	 * 
	 * @param bindingFactory
	 * @param meemPath
	 */
	public PropertiesBinding(BindingFactory bindingFactory, String meemPath) {
		super(
				bindingFactory,
				new FacetReference(meemPath, inboundFacet, FacetClasses.CONFIG_HANDLER),
				new FacetReference(meemPath, outboundFacet, FacetClasses.CONFIG_CLIENT)
		);		
    }
}
