package org.meemplex.internet.gwt.shared;

/**
 * Names to use for various Meem Events
 * 
 * @author stormboy
 *
 */
public class Names {
	
	public class FacetEvent {
		// FacetEvent constants
		public static final String NAME = "FacetEvent";
		public static final String EVENTTYPE = "eventType";
		public static final String MEEMPATH  = "meemPath";
		public static final String FACETID   = "facetId";
		public static final String CLASSNAME = "class";
		public static final String METHOD    = "method";
		public static final String PARAMS    = "params";
	}
	
	public class FacetHealthEvent {
		public static final String NAME = "FacetHealthEvent";
		public static final String LIFECYCLESTATE = "lifeCycleState";
		public static final String BINDINGSTATE   = "bindingState";
	}
	
	public class ErrorEvent {
		public static final String NAME = "ErrorEvent";
		public static final String MESSAGE = "message";
	}

	public static class KnockOutEvent {
		public static final String NAME = "KnockedOut";
	}
}
