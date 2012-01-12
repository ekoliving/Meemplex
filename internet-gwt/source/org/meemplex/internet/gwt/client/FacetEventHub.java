package org.meemplex.internet.gwt.client;

import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetEventSender;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetHealthSender;

/**
 * 
 * @author stormboy
 *
 */
public interface FacetEventHub extends FacetEventListener, FacetHealthListener, FacetEventSender, FacetHealthSender {
}
