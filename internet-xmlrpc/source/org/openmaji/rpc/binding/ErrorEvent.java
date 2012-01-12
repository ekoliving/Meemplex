package org.openmaji.rpc.binding;

import javax.xml.bind.annotation.XmlType;

import org.openmaji.rpc.binding.MeemEvent;

@XmlType
public class ErrorEvent extends MeemEvent {
	
	private static final long serialVersionUID = 0L;

	private String message;
	
	public ErrorEvent() {
		super.setEventType("ErrorEvent");
    }

	public void setMessage(String message) {
	    this.message = message;
    }

	public String getMessage() {
	    return message;
    }
}
