/*
 * Created on 29/09/2004
 */
package org.openmaji.implementation.rpc.binding;

import org.openmaji.rpc.binding.FacetEventListener;
import org.openmaji.rpc.binding.FacetEventSender;
import org.openmaji.rpc.binding.FacetHealthSender;

/**
 * @author Warren Bloomer
 *
 */
public interface RpcClient extends FacetEventListener, FacetEventSender, FacetHealthSender {

}
