package org.openmaji.system.gateway;

public interface AsyncCallback<T> {

	/**
	 * Return a result.
	 * 
	 * @param result
	 */
	void result(T result);

	/**
	 * Return an exception.
	 * 
	 * @param e
	 */
	void exception(Exception e);
}
