/*
 * Created on 29/09/2004
 */
package org.openmaji.rpc.binding;

/**
 * @author Warren Bloomer
 *
 */
public class NoSessionException extends Exception {

	private static final long serialVersionUID = -3599756331674689924L;
	
	public NoSessionException() {
		super();
	}
	
	public NoSessionException(String message) {
		super(message);
	}
	
	public NoSessionException(Throwable cause) {
		super(cause);
	}
	
	public NoSessionException(String message, Throwable cause) {
		super(message, cause);
	}

}
