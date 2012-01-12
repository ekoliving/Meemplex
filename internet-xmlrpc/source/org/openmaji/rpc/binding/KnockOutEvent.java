package org.openmaji.rpc.binding;

import javax.xml.bind.annotation.XmlType;

import org.openmaji.rpc.binding.MeemEvent;

@XmlType
public class KnockOutEvent extends MeemEvent {
	private static final long serialVersionUID = 0L;
	
	public KnockOutEvent() {
		setEventType(Names.KnockOutEvent.NAME);
    }
}
