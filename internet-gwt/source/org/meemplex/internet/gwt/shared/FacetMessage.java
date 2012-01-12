package org.meemplex.internet.gwt.shared;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * A message between 2 Facets. One is the sender and one is the receiver.
 * 
 * @author stormboy
 *
 */
@XmlType
public class FacetMessage implements Serializable {
	
	private static final long serialVersionUID = 0L;

	private FacetReference from;
	
	private FacetReference to;
	
	private String method;
	
	private List<Object> params;

	public void setFrom(FacetReference from) {
	    this.from = from;
    }

	public FacetReference getFrom() {
	    return from;
    }

	public void setTo(FacetReference to) {
	    this.to = to;
    }

	public FacetReference getTo() {
	    return to;
    }

	public void setMethod(String method) {
	    this.method = method;
    }

	public String getMethod() {
	    return method;
    }

	public void setParams(List<Object> params) {
	    this.params = params;
    }

	public List<Object> getParams() {
	    return params;
    }
}
