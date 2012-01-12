package org.meemplex.internet.gwt.client.gwtrpc;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

/**
 * A RequestBuilder for the MeemGwtRpc service.
 * 
 * @author stormboy
 *
 */
public class MeemRpcRequestBuilder extends RpcRequestBuilder {
	private static final int RPC_TIMEOUT = 60000;
	
	@Override
	protected RequestBuilder doCreate(String serviceEntryPoint) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, serviceEntryPoint);
		builder.setTimeoutMillis(RPC_TIMEOUT);		// add a timeout
		return builder;
	}

}