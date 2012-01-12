/*
 * Created on 26/08/2004
 *
 */
package org.openmaji.rpc.binding;

import java.io.Serializable;

/**
 * @author Warren Bloomer
 *
 */
public class FacetEvent extends MeemEvent {
	private static final long serialVersionUID = 0L;

	private String facetId;
	private String facetClass;		// is this redundant?
	private String method;
	private Object[] params;

	/**
	 * 
	 */
	public FacetEvent() {
		setEventType(Names.FacetEvent.NAME);
	}

	/**
	 * @param facetId The facetId to set.
	 */
	public void setFacetId(String facetId) {
		this.facetId = facetId;
	}

	/**
	 * @return Returns the facetId.
	 */
	public String getFacetId() {
		return facetId;
	}

	/**
	 * @param method The method to set.
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return Returns the method.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param params The params to set.
	 */
	public void setParams(Object[] params) {
		this.params = params;
	}

	/**
	 * @return Returns the params.
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * @param facetClass The facetClass to set.
	 */
	public void setFacetClass(String facetClass) {
		this.facetClass = facetClass;
	}

	/**
	 * @return Returns the facetClass.
	 */
	public String getFacetClass() {
		return facetClass;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append(getClass().getName());
		sb.append(" : ");
		sb.append(getMeemPath());
		sb.append("|");
		sb.append(getFacetId());
		sb.append(".");
		sb.append(getMethod());
		sb.append(" ( ");
		sb.append(getParams());
		sb.append(" )]");
		return sb.toString();
	}
}
