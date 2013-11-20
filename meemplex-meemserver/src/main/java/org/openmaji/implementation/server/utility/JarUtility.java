/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

public class JarUtility {
	private static final int BUFFER_SIZE = 2 ^ 14;

	/**
	 * Thie method will unpack the specified jar into the destination directory. If the directory does not exist it will be created first.
	 * 
	 * @param jarFilename
	 *            The name of the jarfile
	 * @param destinationDirectory
	 *            The name of the destination directory
	 * @throws IOException
	 *             When an error occurs reading the contents of the jar or unpacking it to the directory
	 */
	public static void unjar(String jarFilename, String destinationDirectory) throws IOException {
		String initialDirectory = "";
		JarFile jarFile;

		try {
			jarFile = new JarFile(jarFilename);
		}
		catch (ZipException ex) {
			throw new IOException("No such file: " + jarFilename);
		}

		File destDir = new File(destinationDirectory);
		if (destDir.exists() == false) {
			if (destDir.mkdirs() == false)
				throw new IOException("Unable to create destination directory " + destinationDirectory);
		}

		if (destinationDirectory.endsWith(File.separator))
			initialDirectory = destinationDirectory;
		else
			initialDirectory = destinationDirectory + File.separator;

		int bytes;
		byte[] buffer = new byte[BUFFER_SIZE];

		for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements();) {
			JarEntry entry = enumeration.nextElement();
			String targetName = initialDirectory + entry.getName();
			String targetDirectory = initialDirectory + getPath(entry.getName());

			BufferedInputStream inputStream = null;
			BufferedOutputStream outputStream = null;
			File file = null;
			File dir = new File(targetDirectory);
			if (dir.exists() == false) {
				if (dir.mkdirs() == false) {
					throw new IOException("Unable to create destination directory " + targetDirectory);
				}
			}

			file = new File(targetName);
			if (!file.isDirectory()) {
				outputStream = new BufferedOutputStream(new FileOutputStream(targetName));
				inputStream = new BufferedInputStream(jarFile.getInputStream(entry));
				while ((bytes = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytes);
				}
				outputStream.close();
				inputStream.close();
			}
		}
	}

	/**
	 * Extract the specified entry from the jar file. The first entry whose name matches the entryName arguments will be extracted.
	 * 
	 * @param jarFilename
	 *            The name of the jar file
	 * @param entryName
	 *            The name of the Jar entry to extract - not its full pathname
	 * @param fileName
	 *            The full pathname of the file to create
	 * @throws IOException
	 *             If an error occurs while unpacking the entry
	 */
	public static void unjarEntryWithName(String jarFilename, String entryName, String fileName) throws IOException {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarFilename);
		}
		catch (ZipException ex) {
			throw new IOException("No such file: " + jarFilename);
		}

		for (Enumeration<JarEntry> enumeration = jarFile.entries(); enumeration.hasMoreElements();) {
			JarEntry entry = enumeration.nextElement();
			if (entry.getName().endsWith(entryName)) {
				FileOutputStream outputStream = new FileOutputStream(fileName);
				InputStream inStream = jarFile.getInputStream(entry);
				int c;
				while ((c = inStream.read()) != -1) {
					outputStream.write(c);
				}
				outputStream.close();
				inStream.close();
				return;
			}
		}

		throw new IOException("Jar does not contain " + entryName);
	}

	/**
	 * Extract the specified entry from the jar file. You must provide the full path of the entry.
	 * 
	 * @param jarFilename
	 *            The name of the jar file
	 * @param entryName
	 *            The full pathname of the Jar entry to extract
	 * @param fileName
	 *            The full pathname of the file to create
	 * @throws IOException
	 *             If an error occurs while unpacking the entry
	 */
	public static void unjarFile(String jarFilename, String entryName, String fileName) throws IOException {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarFilename);
		}
		catch (ZipException ex) {
			throw new IOException("No such file: " + jarFilename);
		}

		JarEntry entry = (JarEntry) jarFile.getEntry(entryName);
		if (entry == null)
			throw new IOException("Jar does not contain " + entryName);

		FileOutputStream outputStream = new FileOutputStream(fileName);
		InputStream inStream = jarFile.getInputStream(entry);
		int c;
		while ((c = inStream.read()) != -1)
			outputStream.write(c);
		outputStream.close();
		inStream.close();
	}

	private static String getPath(String fileName) {
		String path = "";
		int lastSeparator;

		lastSeparator = fileName.lastIndexOf("/");

		if (lastSeparator > -1)
			path = fileName.substring(0, lastSeparator);

		return path;
	}

	public static void main(String[] args) throws IOException {
		unjar("/tmp/meemkit-core.jar", "/tmp/burp");
	}

}