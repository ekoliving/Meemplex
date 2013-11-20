package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * specifies that the annotated method is used to provide initial content on a facet when an
 * interested party is connected to the facet.
 * 
 * @author stormboy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface FacetContent {
	/**
	 * Name of the Facet to provide content for.
	 * @return
	 */
	String facet();
}
