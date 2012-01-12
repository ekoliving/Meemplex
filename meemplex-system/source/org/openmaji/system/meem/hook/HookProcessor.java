/*
 * Created on 7/01/2005
 */
package org.openmaji.system.meem.hook;

/**
 * @author Warren Bloomer
 *
 */
public interface HookProcessor {

	/**
	 * Process a list of Hooks.
	 * 
	 * @return a boolean to indicate whether invocation or further processing should continue.
	 */
	boolean processHooks()
		throws Throwable;
}
