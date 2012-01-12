package org.meemplex.internet.gwt.shared;

/**
 * @author Warren Bloomer
 *
 */
public class FacetEvent extends MeemEvent {
	private static final long serialVersionUID = 0L;

	private FacetReference from;
	
	private FacetReference to;

	
	/**
	 * Method on Facet for the Event
	 */
	private String method;
	
	/**
	 * The parameters of the Event.
	 */
	private String[] params;

	/**
	 * 
	 */
	public FacetEvent() {
		setEventType(Names.FacetEvent.NAME);
	}
	
	public static FacetEvent createFrom(FacetReference facetRef, String method, String[] params) {
		FacetEvent facetEvent = new FacetEvent(facetRef, null, method, params);
		return facetEvent;
	}
	
	public static FacetEvent createTo(FacetReference facetRef, String method, String[] params) {
		FacetEvent facetEvent = new FacetEvent(null, facetRef, method, params);
		return facetEvent;
	}
	
	public FacetEvent(FacetReference from, FacetReference to, String method, String[] params) {
		this();
		if (from != null) {
			setMeemPath(from.getMeemPath());
		}
		else if (to != null) {
			setMeemPath(to.getMeemPath());
		}
		
		setFrom(from);
		setTo(to);
		setMethod(method);
		setParams(params);
	}

	public void setFrom(FacetReference from) {
		this.from = from;
	}
	
	public FacetReference getFrom() {
		return from;
	}

	public FacetReference getTo() {
		return to;
	}
	
	public void setTo(FacetReference to) {
	    this.to = to;
    }
	
//	/**
//	 * @param facetId The facetId to set.
//	 */
//	public void setFacetId(String facetId) {
//		this.facetId = facetId;
//	}
//
//	/**
//	 * @return Returns the facetId.
//	 */
//	public String getFacetId() {
//		return facetId;
//	}
//
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
	public void setParams(String[] params) {
		this.params = params;
	}

	/**
	 * @return Returns the params.
	 */
	public String[] getParams() {
		return params;
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append(getClass().getName());
		sb.append(" : ");
		sb.append(getMeemPath());
		sb.append("|");
		sb.append(getFrom());
		sb.append(">");
		sb.append(getTo());
		sb.append(" . ");
		sb.append(getMethod());
		sb.append(" ( ");
		if (getParams() != null) {
			boolean first = true;
			for (String p : getParams()) {
				if (first) {
					first = false;
				}
				else {
					sb.append(",");
				}
				sb.append(p);
			}
		}
		sb.append(" )]");
		return sb.toString();
	}
}
