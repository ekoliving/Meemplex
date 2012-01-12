package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;

@XmlType
public enum LifeCycleState {

	  /**
	   * Absent state: Meem does not exist
	   */

	  ABSENT("absent"),

	  /**
	   * Dormant state: Definition exists in MeemStore.  Meem isn't instantiated
	   */

	  DORMANT("dormant"),

	  /**
	   * Loaded state: Meem instantiated.  Only system defined Facets usable
	   */

	  LOADED("loaded"),

		/**
		 * Pending state: Meem trying to become ready but waiting for resources to become available.  
		 * Only system defined Facets usable
		 */

		PENDING("pending"),

	  /**
	   * Ready state: Meem instantiated.  All Facets are usable
	   */

	  READY("ready");



	  /**
	   * Uniquely distinguishes one "current state" from another
	   */

	  private String stateString = null;

	  /**
	   * Create LifeCycleState.
	   *
	   * @param currentState Unique current state distinguisher
	   * @exception IllegalArgumentException Current state must not be null
	   */

	  private LifeCycleState(String stateString) {
	    this.stateString = stateString;
	  }

	  public static LifeCycleState fromString(String str) {
		 for (LifeCycleState state : values()) {
			 if (state.stateString.equals(str)) {
				 return state;
			 }
		 }
		 return null;
	  }
	  
	  /**
	   * Provides the current state for this LifeCycleState.
	   *
	   * @return String The current for this LifeCycleState
	   */

	  public String toString() {
	    return(stateString);
	  }
}
