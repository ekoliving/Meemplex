package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class ErrorEvent extends MeemEvent {
	
	private static final long serialVersionUID = 0L;

	private String message;
	
	public ErrorEvent() {
		super.setEventType(Names.ErrorEvent.NAME);
    }

	public void setMessage(String message) {
	    this.message = message;
    }

	public String getMessage() {
	    return message;
    }
}
