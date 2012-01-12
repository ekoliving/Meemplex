/*
 * @(#)InvocationTrace.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic;

import java.util.*;

import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.*;


/**
 * @author mg
 */
public class InvocationTrace {
	
	private static EventIDComparator eventIDComparator = new EventIDComparator();

	/**
	 * Given an array of InvocationEvents and a chosen event,
	 * find the calling event and events initiated by the chosen event.
	 * 
	 * @param eventID
	 * @param events
	 * @return Array of eventIDs. The first entry will be the calling event (or null if not found). 
	 * Subsequent entries will be events invoked by the chosen event. 
	 */
	public InvocationEvent[] createInvocationTree(long eventID, DiagnosticEvent[] events) {
		
		InvocationEvent callingEvent = null;
		List childEvents = new LinkedList();
		
		DiagnosticEvent event = findEvent(eventID, events);
		
		if (event instanceof InvocationEvent) {
			callingEvent = findCaller((InvocationEvent) event, events);
			childEvents = findChildren((InvocationEvent) event, events);
		}
		
		InvocationEvent[] invocationEvents = new InvocationEvent[childEvents.size() + 1];
		invocationEvents[0] = callingEvent;
		
		for (int i = 0; i < childEvents.size(); i++) {
			invocationEvents[i + 1] = (InvocationEvent) childEvents.get(i);
		}
		
		return invocationEvents;
	}
	
	private InvocationEvent findCaller(InvocationEvent targetEvent, DiagnosticEvent[] events) {
		int location = getEventIndex(targetEvent.getEventID(), events);
		
		Set lookingForClassSet = determineOppositeClass(targetEvent.getClass(), false);
		boolean targetEventIsInbound = isClassInboundInvocation(targetEvent.getClass().getName());
		
		for (int i = location - 1; i > 0; i--) {
			if (!(events[i] instanceof InvocationEvent)) {
				continue;
			}
			
			InvocationEvent sourceEvent = (InvocationEvent) events[i];
			
			if (!(lookingForClassSet.contains(sourceEvent.getClass()))) {
				continue;
			}
			
			boolean match = false;
			
			if (targetEventIsInbound) {
				match = matchDifferentThread(sourceEvent, targetEvent);				
			} else {
				match = matchSameThread(sourceEvent, targetEvent);	
			}
			
			if (!match) {
				continue;
			} else {
				// found a match
				return sourceEvent;
			}
			
		}
		
		return null;
	}
	
	private List findChildren(InvocationEvent selectedEvent, DiagnosticEvent[] events) {
		List childEvents = new LinkedList();
		
		int location = getEventIndex(selectedEvent.getEventID(), events);
		
		Set lookingForClassSet = determineOppositeClass(selectedEvent.getClass(), true);
		boolean selectedEventIsInbound = isClassInboundInvocation(selectedEvent.getClass().getName());
		
		for (int i = location ; i < events.length; i++) {
			if (!(events[i] instanceof InvocationEvent)) {
				continue;
			}
			
			InvocationEvent childEvent = (InvocationEvent) events[i];
			
			if (!(lookingForClassSet.contains(childEvent.getClass()))) {
				continue;
			}
			
			boolean match = false;
			
			if (selectedEventIsInbound) {
				match = matchSameThread(selectedEvent, childEvent);	
			} else {				
				match = matchDifferentThread(selectedEvent, childEvent);			
			}
			
			if (!match) {
				continue;
			} else {
				// found a match
				childEvents.add(childEvent);
			}
			
		}
		
		return childEvents;
	}
		
	private boolean matchSameThread(InvocationEvent sourceEvent, InvocationEvent targetEvent) {
		// check the meempath
		if (!(sourceEvent.getTargetMeemPath().equals(targetEvent.getSourceMeemPath()))) {
			return false;
		}
		
		// check the thread id
		if (!(sourceEvent.getThreadID() == targetEvent.getThreadID())) {
			return false;
		}
		
		return true;
	}

	private boolean matchDifferentThread(InvocationEvent sourceEvent, InvocationEvent targetEvent) {
		// check the source meempath
		if (sourceEvent.getSourceMeemPath() != null && targetEvent.getSourceMeemPath() != null) {
			if (!(sourceEvent.getSourceMeemPath().equals(targetEvent.getSourceMeemPath()))) {
				return false;
			}
		}
		
		// check the target meempath
		if (!(sourceEvent.getTargetMeemPath().equals(targetEvent.getTargetMeemPath()))) {
			return false;
		}
		
		// check the method name
		if (!(sourceEvent.getTargetMethodName().equals(targetEvent.getTargetMethodName()))) {
			return false;
		}
		
		// compare the request stacks
		if (!(sourceEvent.getRequestStack().equals(targetEvent.getRequestStack()))) {
			return false;
		}
		
		// compare the method arguments 
		Object[] sourceArgs = sourceEvent.getTargetMethodArgs();
		Object[] targetArgs = targetEvent.getTargetMethodArgs();
		
		if (sourceArgs.length != targetArgs.length) {
			return false;
		}
		
		boolean match = true;
		for (int j = 0; j < targetArgs.length; j++) {
			Object sourceArg = sourceArgs[j];
			Object targetArg = targetArgs[j];
			
			if (!sourceArg.equals(targetArg)) {
				match = false;
				break;
			}
		}
		
		return match;
	}
	
	private Set determineOppositeClass(Class selectedEventClass, boolean lookingForChildren) {
		String selectedEventClassName = selectedEventClass.getName();
		
		boolean selectedIsInbound = false;
		boolean selectedIsRemote = false;
		
		selectedIsInbound = isClassInboundInvocation(selectedEventClassName);

		selectedIsRemote = isClassRemoteInvocation(selectedEventClassName);

		Set targetEventClassSet = new HashSet();
		if (lookingForChildren) {
			if (selectedIsInbound && !selectedIsRemote) { // Inbound
				targetEventClassSet.add(OutboundInvocationEvent.class);
				targetEventClassSet.add(RemoteOutboundInvocationEvent.class);
			} else 
			if (selectedIsInbound && selectedIsRemote) { // Remote Inbound
				targetEventClassSet.add(InboundInvocationEvent.class);
			} else
			if (!selectedIsInbound && !selectedIsRemote) {// Outbound
				targetEventClassSet.add(InboundInvocationEvent.class);
			} else {
				// Remote Outbound
				targetEventClassSet.add(RemoteInboundInvocationEvent.class);
			}
		} else { // Looking for parent
			if (selectedIsInbound && !selectedIsRemote) {// Inbound
				targetEventClassSet.add(OutboundInvocationEvent.class);
				targetEventClassSet.add(RemoteInboundInvocationEvent.class);
			} else 
			if (selectedIsInbound && selectedIsRemote) {// Remote Inbound
				targetEventClassSet.add(RemoteOutboundInvocationEvent.class);
			} else
			if (!selectedIsInbound && !selectedIsRemote) {// Outbound
				targetEventClassSet.add(InboundInvocationEvent.class);
			} else {
				// Remote Outbound
				targetEventClassSet.add(InboundInvocationEvent.class);
			}
		}
		return targetEventClassSet;
	}

	private DiagnosticEvent findEvent(long eventID, DiagnosticEvent[] events) {
		
		int location = getEventIndex(eventID, events);
		
		if (location == -1) {
			return null;
		}
		
		return events[location];
	}
	
	private int getEventIndex(long eventID, DiagnosticEvent[] events) {
		// get first id
		long firstEventID = events[0].getEventID();
		
		long requestedEventLocation = eventID - firstEventID;
		
		if (requestedEventLocation < 0) {
			// not in the passed in array
			return -1;
		}
		
		int index = Arrays.binarySearch(events, new Long(eventID), eventIDComparator);
						
		return index;
	}
	
	public static boolean isClassRemoteInvocation(String className) {
		if (className.indexOf("Remote") > -1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isClassInboundInvocation(String className) {
		if (className.indexOf("Inbound") > -1) {
			return true;
		} else {
			return false;
		}
	}
	
	private static class EventIDComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			long diff =((DiagnosticEvent) o1).getEventID() - ((Long) o2).longValue();
			
			return diff < 0 ? -1 : (diff > 0) ? 1 : 0;  
		}

		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}
	
}
