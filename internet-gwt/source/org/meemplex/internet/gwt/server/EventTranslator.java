package org.meemplex.internet.gwt.server;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.meemplex.internet.gwt.shared.ErrorEvent;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.MeemEvent;

/**
 * Translates Meemplex Event Objects to those used by the Meem GWT RPC protocol.
 * 
 * @author stormboy
 *
 */
public class EventTranslator {
	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * 
	 * @param meemEvent
	 * @return
	 */
	public static MeemEvent toServiceEvent(org.openmaji.rpc.binding.MeemEvent meemEvent) {
		MeemEvent serviceEvent = null;
		if (meemEvent instanceof org.openmaji.rpc.binding.FacetEvent) {
			FacetReference from = new FacetReference(
					((org.openmaji.rpc.binding.FacetEvent)meemEvent).getMeemPath(), 
					((org.openmaji.rpc.binding.FacetEvent)meemEvent).getFacetId(), 
					((org.openmaji.rpc.binding.FacetEvent)meemEvent).getFacetClass()
				);
			FacetEvent facetEvent = new FacetEvent();
			facetEvent.setMeemPath(from.getMeemPath());
			facetEvent.setFrom(from);
			facetEvent.setMethod( ((org.openmaji.rpc.binding.FacetEvent)meemEvent).getMethod());
			
			// TODO this is temporary comment
			Object[] params = ((org.openmaji.rpc.binding.FacetEvent)meemEvent).getParams();
			String[] stringParams = null;
			if (params != null) {
				stringParams = new String[params.length];
				int i=0;
				for (Object s : params) {
					stringParams[i++] = marshall(s);
				}
			}

			facetEvent.setParams( stringParams );
			
			serviceEvent = facetEvent;
		}
		
		else if (meemEvent instanceof org.openmaji.rpc.binding.FacetHealthEvent) {
			FacetHealthEvent facetHealthEvent = new FacetHealthEvent(
					((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getBindingState(),
					((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getLifeCycleState()
				);
			facetHealthEvent.setMeemPath( ((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getMeemPath());
			facetHealthEvent.setFacetId( ((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getFacetId());
			facetHealthEvent.setFacetClass( ((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getFacetClass());
			facetHealthEvent.setBindingState( ((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getBindingState());
			facetHealthEvent.setLifeCycleState( ((org.openmaji.rpc.binding.FacetHealthEvent)meemEvent).getLifeCycleState());
			serviceEvent = facetHealthEvent;
		}
		
		else if (meemEvent instanceof org.openmaji.rpc.binding.ErrorEvent) {
			ErrorEvent errorEvent = new ErrorEvent();
			errorEvent.setMessage( ((org.openmaji.rpc.binding.ErrorEvent)meemEvent).getMessage() );
			serviceEvent = errorEvent;
		}
		
		else {
			// unknown event
		}
		
		return serviceEvent;
	}
	
	private static String marshall(Object object) {
		String result = null;
		
		if (object instanceof Object[]) {
			try {
				JSONArray array = new JSONArray( object );
				result = array.toString();
			}
			catch (Exception e) {
				logger.info("could not marshal array: " + object);
			}
		}
		else if (object instanceof Collection<?>) {
			JSONArray array = new JSONArray( (Collection<?>)object );
			result = array.toString();
		}
		/*
		else if (object instanceof Map<?, ?>) {
			JSONObject jsonObj = new JSONObject( (Map<?, ?>)object );
			JSON
			result = jsonObj.toString();
		}
		*/
		else if (object instanceof Boolean) {
			result = object.toString();
		}
		else if (object instanceof Number) {
			result = object.toString();
		}
		else {
			result = new JSONObject(object).toString();
		}
		return result;
	}

}
