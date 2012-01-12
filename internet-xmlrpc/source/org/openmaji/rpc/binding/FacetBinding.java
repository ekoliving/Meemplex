/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.rpc.binding;

import java.util.HashSet;

import org.openmaji.meem.Facet;

/**
 * @author Warren Bloomer
 *
 */
public class FacetBinding implements FacetHealthListener {
	
	protected String                   meemPath   = null;
	
	protected String                   facetId    = null;
	
	protected Class<? extends Facet>   facetClass = Facet.class;   // implementations of a binding must set this
	
	private final HashSet<FacetHealthListener> healthListeners = new HashSet<FacetHealthListener>();
	
	private FacetHealthEvent lastHealthEvent; 


	/**
	 * A URI representation of a MeemPath
	 * 
	 * examples of valid locations:
	 * 		hyperspace:/cat1/cat2/MyMeem
	 * 		meemstore:/uuid
	 * 		transient:/uuid
	 * 
	 * @param location
	 */
	public void setMeemPath(String location) {
		this.meemPath = location;
	}
	
	public String getMeemPath() {
		return meemPath;
	}
	
	public void setFacetId(String facetId) {
		this.facetId = facetId;
	}

	/**
	 * Returns the Facet identifier.
	 */	
	public String getFacetId() {
		return facetId;
	}
	
	/**
	 * Returns the Facet class for this Binding.
	 */
	public Class<? extends Facet> getFacetClass() {
		return facetClass;
	}
	
	/**
	 * 
	 * @param cls
	 */
	protected void setFacetClass(Class<? extends Facet> cls) {
		this.facetClass = cls;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addBindingHealthListener(FacetHealthListener listener) {
		synchronized (healthListeners) {
			healthListeners.add(listener);
		}
		
		// send last health event
		if (lastHealthEvent != null) {
			listener.facetHealthEvent(lastHealthEvent);
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeBindingHealthListener(FacetHealthListener listener) {
		synchronized (healthListeners) {			
			healthListeners.remove(listener);
		}
	}
	
	/**
	 * 
	 */
	public boolean equals(Object o) {
		if (o instanceof FacetBinding) {
			FacetBinding fb = (FacetBinding)o;
			boolean test1 = meemPath == null ? meemPath == fb.meemPath : meemPath.equals(fb.meemPath);
			boolean test2 = facetId  == null ? facetId  == fb.facetId  : facetId.equals(fb.facetId);

			return test1 && test2;
		}
		return false;
	}

	/**
	 * 
	 */
	public int hashCode() {
		int hash1 = meemPath == null ? 0 : meemPath.hashCode();
		int hash2 = facetId == null ? 0  : facetId.hashCode();
		
		return hash1 ^ hash2;
	}
		
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass());
		sb.append("{");
		sb.append(getMeemPath());
		sb.append(" . ");
		sb.append(getFacetId());
		sb.append(" : ");
		sb.append(getFacetClass());
		sb.append("}");
		
		return sb.toString();
	}
	
	/* ------------------ FacetHealthEventListener interface ------------------------ */
	
	/**
	 * FacetHealthEvent received by the binding.
	 * 
	 * Usually called from the RPC client or server.
	 * 
	 */
	public final void facetHealthEvent(FacetHealthEvent event) {
		// ignore events that do not relate to this binding
		if (isForThis(event)) {
			//System.out.println("got health event for this binding: " + event);
			sendHealth(event);
		}
	}
	
	/* ------------------------- utility methods ---------------------------- */
	
	/**
	 * 
	 * @param event
	 */
	private boolean isForThis(FacetHealthEvent event) {
		return  (
				meemPath != null  &&
				facetId  != null  &&
				( meemPath.equalsIgnoreCase(event.getMeemPath()) || event.getMeemPath() == null ) && 
				( facetId.equalsIgnoreCase(event.getFacetId())   || event.getFacetId()  == null)
			);
	}

	
	/**
	 *
	 * @param event
	 */
	private void sendHealth(FacetHealthEvent event) {
		synchronized (healthListeners) {
			for (FacetHealthListener listener : healthListeners) {
				listener.facetHealthEvent(event);
			}
		}
	}

}
