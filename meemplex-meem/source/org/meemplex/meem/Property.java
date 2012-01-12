package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a field is a configuration property for the Meem.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Property {

	/**
	 * Will default to the name of the annotated field.
	 * 
	 * @return
	 */
	String name();

	/**
	 * 
	 * @return
	 */
	String description();

}
