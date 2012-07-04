package org.openmaji.server.utility;

import java.lang.reflect.ParameterizedType;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.security.DoAsMeem;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;

/**
 * 
 * @author stormboy
 *
 * Gets content from a Facet on a Meem and then delivers the desired result to the
 * AsyncCallback.
 * 
 * A subclass must implement the getFacetLister() function.  The facet listener should extend
 * the inner FacetListener class.
 * 
 * @param <F>
 * 	The type of the facet to listen to to get the result
 * @param <T>
 * 	Type of value to return in client callback
 */
public abstract class FacetCallbackTask <F extends Facet, T> {

	private static final long timeout = Long.parseLong(System.getProperty(PigeonHole.PROPERTY_TIMEOUT, "60000"));

	/**
	 * Client callback to send result to.
	 */
	protected AsyncCallback<T> callback;

	/**
	 * Facet listener
	 */
	protected F listener;
	
	/**
	 * This will be called back via reference
	 */
	protected F clientProxy;
	
	private Runnable doTimeout = new Runnable() {
		public void run() {
			timeout();
		}
	};
	
	private T result;

	/**
	 * 
	 * @param callback
	 * 	The client callback on which to send the result.
	 * @param meem
	 * @param facetName
	 * @param filter
	 * @param listenerParams
	 */
	//@SuppressWarnings("unchecked")
	public FacetCallbackTask(final Meem meem, String facetName, Filter filter, AsyncCallback<T> callback) {
		this.callback = callback;
		this.listener = getFacetListener();
		
		ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
		Class<F> type = (Class<F>) superclass.getActualTypeArguments()[0];
		
		// create target proxy for facet to call
		this.clientProxy = GatewayManagerWedge.getTargetFor(listener, type);
		
		// queue timeout event
		Subject.doAs(MeemCoreRootAuthority.getSubject(), new PrivilegedAction<Void>() {
			public Void run() {
				ThreadManager.spi.create().queue(doTimeout, System.currentTimeMillis()+timeout);
				return null;
			}
		});

		// create a reference to add to the meem
		Reference<F> reference = Reference.spi.create(facetName, clientProxy, true, filter);
		
		meem.addOutboundReference(reference, true);
	}

	/**
	 * Use a pigeon hole for synchronous call.
	 * 
	 * @param meem
	 * @param facetName
	 * @param filter
	 * @param pigeonHole
	 */
	public FacetCallbackTask(Meem meem, String facetName, Filter filter, final PigeonHole<T> pigeonHole) {
		this(meem, facetName, filter, new AsyncCallback<T>() {
			public void result(T result) {
				pigeonHole.put(result);
			}
			public void exception(Exception e) {
				pigeonHole.exception(e);
			}
		});
	}
	
	abstract protected  F getFacetListener();

	/**
	 * Put the value in place
	 * @param value
	 */
	protected void setResult(T value) {
		this.result = value;
	}

	private void timeout() {
		if (callback != null) {
			String message = "Timeout waiting for result";
			callback.exception(new TimeoutException(message));
			callback = null;
			cleanup();
		}
	}
	
	private void cleanup() {
		if (clientProxy != null) {
			ThreadManager.spi.create().cancel(doTimeout);
			GatewayManagerWedge.revokeTarget(clientProxy, listener);
			clientProxy = null;
		}
	}
	
	/**
	 * Extend this for the listener
	 *
	 */
	public abstract class FacetListener implements ContentClient {
		
		public void contentSent() {
			if (callback != null) {
				callback.result(result);
				callback = null;
				cleanup();
			}
		}
	
		public void contentFailed(String reason) {
			if (callback != null) {
				callback.exception(new ContentException(reason));
				callback = null;
				cleanup();
			}
		}
	}

}
