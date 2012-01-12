/*
 * @(#)MeemKitTransfer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.io.*;


import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.openmaji.meem.MeemPath;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemKitTransfer extends ByteArrayTransfer {
	
	private static final String TYPE_NAME = "meemkit_transfer";
	private static final int TYPE_ID = registerType(TYPE_NAME);

	private static MeemKitTransfer instance = new MeemKitTransfer();

	private MeemKitTransfer() {
	}

	public static MeemKitTransfer getInstance() {
		return instance;
	}
	
	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	protected void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof MeemPath[]))
			return;

		if (isSupportedType(transferData)) {
			try {
				// write data to a byte array and then ask super to convert to pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(out);

				oos.writeObject(object);

				byte[] buffer = out.toByteArray();
				oos.close();

				super.javaToNative(buffer, transferData);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	protected Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			MeemPath[] myData = null;
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream ois = new ObjectInputStream(in);

				Object obj = ois.readObject();
				ois.close();

				myData = (MeemPath[]) obj;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return myData;
		}

		return null;
	}

}
