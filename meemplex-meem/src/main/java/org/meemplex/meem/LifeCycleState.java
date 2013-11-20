package org.meemplex.meem;

public enum LifeCycleState {
	/**
	 * Absent state: Meem does not exist
	 */

	ABSENT,

	/**
	 * Dormant state: Definition exists in MeemStore.  Meem isn't instantiated
	 */

	DORMANT,

	/**
	 * Loaded state: Meem instantiated.  Only system defined Facets usable
	 */

	LOADED,

	/**
	 * Pending state: Meem trying to become ready but waiting for resources to become available.  
	 * Only system defined Facets usable
	 */

	PENDING,

	/**
	 * Ready state: Meem instantiated.  All Facets are usable
	 */

	READY

}
