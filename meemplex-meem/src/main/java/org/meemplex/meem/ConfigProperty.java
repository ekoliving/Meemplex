package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an attribute to be used as a configuration property
 * for a Wedge.
 * 
 * TODO add input checking ability
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface ConfigProperty {

	/**
	 * The name of the configuration property.
	 * 
	 * If no name is given, use the name of the attribute.
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * A description of the configuration property.
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * The type
	 * 
	 * @return
	 */
	PropertyType type() default PropertyType.STRING;

	/**
	 * The maximum lifecycle state that the property can be changed in.
	 * 
	 * @return
	 */
	LifeCycleState maxLifeCycleState() default LifeCycleState.LOADED;
}
