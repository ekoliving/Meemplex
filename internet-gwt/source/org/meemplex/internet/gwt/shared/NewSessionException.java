package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;

/**
 * Thrown when a new Session client-server has been initiated by the server-side.
 * A session id is provided in the response.
 * 
 * @author stormboy
 *
 */
@XmlType
public class NewSessionException extends Exception {
	private static final long serialVersionUID = 0L;

	public NewSessionException() {
    }
	
	public NewSessionException(String msg) {
		super(msg);
	}
}
