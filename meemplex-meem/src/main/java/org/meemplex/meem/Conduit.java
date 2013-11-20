package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Indicates that an attribute is a Facet between Wedges.  Is the attribute is null, 
 * it is an outbound (from this Wedge) InterFacet, otherwise it is an inbound (to 
 * this Wedge) InterFacet.
 * 
 * If a name is not given, the attribute name is used as the name of the InterFacet.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface Conduit {

	String name() default "";
}
