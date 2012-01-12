package org.meemplex.internet.gwt.shared;

import java.io.Serializable;

public abstract class MeemEvent implements Serializable {
	
	private static final long serialVersionUID = 0L;

	/**
	 * The type of MeemEvent
	 */
	private String eventType;
	
	/**
	 * The definitive UUID for the Meem that this Event came from.
	 */
	private String meemId;

	/**
	 * A MeemPath used to locate the Meem.
	 */
	private String meemPath;
	
	/**
	 * 
	 */
	protected MeemEvent() {
	}
	
	protected MeemEvent(String eventType) {
		this.eventType = eventType;
	}
	
	public void setEventType(String eventType) {
	    this.eventType = eventType;
    }

	public String getEventType() {
	    return eventType;
    }

	public void setMeemId(String meemId) {
	    this.meemId = meemId;
    }

	public String getMeemId() {
	    return meemId;
    }

	public void setMeemPath(String meemPath) {
	    this.meemPath = meemPath;
    }

	public String getMeemPath() {
	    return meemPath;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MeemEvent: ");
		sb.append(getEventType());
		sb.append("{");
		sb.append(getMeemPath());
		sb.append("}");
		return sb.toString();
	}
}
