package org.meemplex.meem;

/**
 * to filter invocations coming in or going out of a Meem
 * 
 * @author stormboy
 *
 */
public interface InvocationFilter {

	boolean shouldInvoke(Object target, String methodName, Object... params);
}
