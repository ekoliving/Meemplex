package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a method that should be called when the initial content intended to be 
 * sent to a Facet has failed.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface ContentFailed {

	/**
	 * Name of the Facet the data is to be received on.
	 * 
	 * @return
	 */
	String name();
}
