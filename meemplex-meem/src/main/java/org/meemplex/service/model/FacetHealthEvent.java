package org.meemplex.service.model;

/**
 * @author Warren Bloomer
 *
 */
public class FacetHealthEvent extends MeemEvent {

	private static final long serialVersionUID = 0L;
	
	public static final String NAME = "FacetHealthEvent";

	public static final String LIFECYCLESTATE = "lifeCycleState";
	
	public static final String BINDINGSTATE   = "bindingState";
	
	private String facetId;
	
	private String facetClass;

	private String lifeCycleState   = null;
	
	private HealthState bindingState     = HealthState.UNKNOWN;

	/**
	 * 
	 */
	public FacetHealthEvent() {
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
	public void setBindingState(HealthState bindingState) {
		this.bindingState = bindingState;
	}


	/**
	 * @return Returns the bindingState.
	 */
	public HealthState getBindingState() {
		return bindingState;
	};

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(getClass().getName());
		sb.append(" : " );
		sb.append(getMeemId());
		sb.append(" | ");
		sb.append(getFacetId());
		sb.append(" | ");
		sb.append(getBindingState());
		sb.append(", ");
		sb.append(getLifeCycleState());
		
		return sb.toString();
	}
}
