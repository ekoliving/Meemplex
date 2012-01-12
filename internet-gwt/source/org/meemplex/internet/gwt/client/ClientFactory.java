package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.client.gwtrpc.GwtRpcFacetEventHub;

import com.google.gwt.core.client.GWT;

public interface ClientFactory {

	GwtRpcFacetEventHub getFacetEventHub();
	

	public static class spi {
		private static ClientFactory singleton;
		public static  ClientFactory singleton() {
			if (singleton == null) {
				singleton = GWT.create(ClientFactory.class);
			}
			return singleton;
		}
	}
}
