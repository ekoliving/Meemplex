package org.openmaji.implementation.server.log;

/**
 * receives log events, which are simply Strings.
 * 
 * @author stormboy
 *
 */
public interface LogListener {
	void message(String message);
}
