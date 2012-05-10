/*
 * @(#)SerializableTransfer.java
 * Created on 2/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * <code>SerializableTransfer</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class SerializableTransfer extends ByteArrayTransfer {
	/**
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	protected void javaToNative(Object object, TransferData transferData) {
		if((object == null) || (!(object instanceof Serializable))) {
			return;
		} 
		if(!isSupportedType(transferData)) return;
		
		try {
			// write data to a byte array and then ask super to convert to pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream writeOut = new ObjectOutputStream(out);
			writeOut.writeObject(object);
			byte[] buffer = out.toByteArray();
			writeOut.close();
			super.javaToNative(buffer, transferData);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	protected Object nativeToJava(TransferData transferData) {
		if(!isSupportedType(transferData)) return null;

		byte[] buffer = (byte[]) super.nativeToJava(transferData);
		if (buffer == null)
			return null;

		Object serializable = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			ObjectInputStream readIn = new ObjectInputStream(in);
			serializable = readIn.readObject();
			readIn.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return serializable;
	}
}
