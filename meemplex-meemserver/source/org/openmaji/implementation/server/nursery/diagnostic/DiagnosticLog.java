/*
 * @(#)DiagnosticLog.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mg
 */
public class DiagnosticLog {
	
	public static boolean DIAGNOSE = true;

	private static FixedSizeList list = new FixedSizeList(10000);
	
	private static long lastEventID = 0;
	
	public synchronized static long getEventID() {
		return lastEventID++;
	}
	
	public static void log(DiagnosticEvent diagnosticEvent) {
		list.add(diagnosticEvent);
	}
	
	public static void clearLog() {
		list.clear();
	}
	
	public static void dump() {
		String fileName = "c:\\dump.out";
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject((DiagnosticEvent[]) list.getListArray(new DiagnosticEvent[0]));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DiagnosticEvent[] load() {
		DiagnosticEvent[] eventArray = new DiagnosticEvent[0];
		String fileName = "c:\\dump.out";
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			eventArray = (DiagnosticEvent[]) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventArray;
	}
	
	public static DiagnosticEvent[] snapshot() {
		return (DiagnosticEvent[]) list.getListArray(new DiagnosticEvent[0]);
	}
	
	public static DiagnosticEvent[] snapshotFilter(Class filterClass) {
		DiagnosticEvent[] events = (DiagnosticEvent[]) list.getListArray(new DiagnosticEvent[0]);
		
		List newList = new LinkedList();
		
		for (int i = 0; i < events.length; i++) {
			if (filterClass.isInstance(events[i])) {
				newList.add(events[i]);
			}
		}
		
		return (DiagnosticEvent[]) newList.toArray(new DiagnosticEvent[0]);
	}
	
//	public static void dumpToCSV() {
//		String fileName = "c:\\dump.csv";
//		try {
//			FileWriter fw = new FileWriter(fileName);
//
//			Object[] messages = list.getListArray();
//			
//			for (int i = 0; i < messages.length; i++) {
//				fw.write(((DiagnosticLogMessage)messages[i]).toCSV());
//			}
//			
//			fw.close();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
