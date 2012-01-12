/*
 * Created on 18/10/2004
 */
package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Warren Bloomer
 *
 */

@XmlType
public class FacetHealthEvent extends MeemEvent {

	private static final long serialVersionUID = 0L;
	
	public static final int UNKNOWN           = -1;
	public static final int OFFLINE           =  0;		// no connection to the the RPC server
	public static final int MEEM_RESOLVED     =  10;	// the Meem has been resolved
	public static final int MEEM_NOTRESOLVED  =  11;	// the Meem has been resolved
	public static final int FACET_RESOLVED    =  20;	// the Facet has been resolved
	public static final int FACET_NOTRESOLVED    =  21;	// the Facet has not been resolved

	private String facetId;
	private String facetClass;		// is this redundant?

	private String lifeCycleState   = null;
	private int    bindingState     = UNKNOWN;

	public FacetHealthEvent() {
		super(Names.FacetHealthEvent.NAME);
    }
	
	/**
	 * 
	 */
	public FacetHealthEvent(int bindingState, String lifCycleState) {
		super(Names.FacetHealthEvent.NAME);
		setBindingState(bindingState);
		setLifeCycleState(lifCycleState);
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


	/**
	 * @param lifeCycleState The lifeCycleState to set.
	 */
	public void setLifeCycleState(String lifeCycleState) {
		this.lifeCycleState = lifeCycleState;
	}


	/**
	 * @return Returns the lifeCycleState.
	 */
	public String getLifeCycleState() {
		return lifeCycleState;
	}


	/**
	 * @param bindingState The bindingState to set.
	 */
	public void setBindingState(int bindingState) {
		this.bindingState = bindingState;
	}


	/**
	 * @return Returns the bindingState.
	 */
	public int getBindingState() {
		return bindingState;
	};

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(getClass().getName());
		sb.append(" : " );
		sb.append(getMeemPath());
		sb.append(" | ");
		sb.append(getFacetId());
		sb.append(" | ");
		sb.append(getBindingState());
		sb.append(", ");
		sb.append(getLifeCycleState());
		
		return sb.toString();
	}
}
