package org.meemplex.meem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use to annotate a MeemInstance within a Wedge.  This will provide access to the MeemInstance within the Wedge.
 * 
 * @author stormboy
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface MeemContext {

}
