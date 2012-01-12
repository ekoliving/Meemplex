package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.client.gwtrpc.GwtRpcFacetEventHub;

public class ClientFactoryImpl implements ClientFactory {

	private GwtRpcFacetEventHub facetEventHub;
	
	@Override
	public GwtRpcFacetEventHub getFacetEventHub() {
		if (facetEventHub == null) {
			facetEventHub = new GwtRpcFacetEventHub();
		}
		return facetEventHub;
	}

}
