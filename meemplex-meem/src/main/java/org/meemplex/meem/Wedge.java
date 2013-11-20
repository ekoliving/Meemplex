package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a Wedge to be used in a Meem.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ ElementType.TYPE, ElementType.FIELD })
public @interface Wedge {
	/**
	 * The name of the wedge. This should be unique for each wedge in a Meem.
	 *  
	 * @return
	 */
	String name() default "";
	
	String description() default "";
}
