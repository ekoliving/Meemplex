/*
 * @(#)ResourceExporter.java
 * Created on 4/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * <code>ResourceExporter</code> exports resource located by a Java class to a 
 * byte array.
 * <p>
 * @author Kin Wong
 */
public class ResourceExporter {
	static private final int BUFFER_SIZE = 4096;
	
	private Class location;
	
	/**
	 * Constructs an instance of <code>ResourceExporter</code>.
	 * <p>
	 * @param location A class that indentifies the location of resource.
	 */
	public ResourceExporter(Class location) {
		this.location = location;
	}
	
	/**
	 * Returns the location of the resource.
	 * <p>
	 * @return The Java class the identifies the location of the resource.
	 */	
	public Class getLocation() {
		return location;
	}
	
	/**
	 * Extracts the resource identified by the name relative to the location.
	 * <p>
	 * @param name The file name of the resource.
	 * @return A byte array containing the resource, or null if failed.
	 */
	public byte[] extract(String name) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InputStream is = location.getResourceAsStream(name);
		if(is == null) {
			System.err.println("Unable to extract: " + name);
			return null;
		} 
		
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			while(true) {
				int read = is.read(buffer);
				if(read == -1) break;
				os.write(buffer, 0, read);
			}
			os.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		return os.toByteArray();	
	}
}

