package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;


@XmlType
public class KnockOutEvent extends MeemEvent {
	private static final long serialVersionUID = 0L;
	
	public KnockOutEvent() {
		setEventType(Names.KnockOutEvent.NAME);
    }
}
