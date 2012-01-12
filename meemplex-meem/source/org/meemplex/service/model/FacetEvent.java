package org.meemplex.service.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Warren Bloomer
 *
 */
@XmlRootElement(name="facetEvent")
public class FacetEvent extends MeemEvent {
	private static final long serialVersionUID = 0L;

	public static final String NAME = "FacetEvent";

	private String facetPath;

	private String facetClass;

	private String method;
	
	private Object[] params;

	/**
	 * 
	 */
	public FacetEvent() {
	}


	public void setFacetPath(String facetPath) {
	    this.facetPath = facetPath;
    }

	@XmlAttribute
	public String getFacetPath() {
	    return facetPath;
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
	@XmlElement(name="param")
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
	@XmlAttribute
	public String getFacetClass() {
		return facetClass;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append(getClass().getName());
		sb.append(" : ");
		sb.append(getMeemId());
		sb.append("|");
		sb.append(getFacetPath());
		sb.append(".");
		sb.append(getMethod());
		sb.append(" ( ");
		sb.append(getParams());
		sb.append(" )]");
		return sb.toString();
	}
}
