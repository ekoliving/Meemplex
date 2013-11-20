package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meemplex.service.model.Direction;

/**
 * Indicates that an attribute is a Facet.  Is the attribute is null, it is an outbound
 * Facet, otherwise it is an inbound Facet.
 * 
 * If a name is not given, the attribute name is used as the name of the Facet.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Facet {

	/**
	 * Name of the facet
	 * 
	 * @return
	 */
	String name() default "";
	
	/**
	 * Whether the Facet is inbound or outbound
	 * 
	 * @return
	 */
	Direction direction();
}
