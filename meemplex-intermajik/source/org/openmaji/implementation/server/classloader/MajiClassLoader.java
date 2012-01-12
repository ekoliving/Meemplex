/*
 * @(#)MajiClassLoader.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.classloader;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openmaji.implementation.server.Common;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author mg
 */
public class MajiClassLoader extends URLClassLoader {

	public static final String CLASSLOADER_DEBUG = "org.openmaji.server.classloader.debug";

	public static final String CLASSPATH_FILE = "org.openmaji.server.classpath";

	private static final String DEFAULT_CLASSPATH_FILE = "conf/maji-classpath.xml";
	
	private boolean debug = false;

	private String majitekDirectoryBaseURL;

	private int id = (int) Math.round(Math.random() * 1000);

	public MajiClassLoader(ClassLoader parent) {
		super(new URL[] {}, parent);

		String debug = System.getProperty(CLASSLOADER_DEBUG);
		if (debug != null && debug.equalsIgnoreCase("true")) {
			this.debug = true;
			System.err.println("MajiClassLoader debug on");
		}

		if (this.debug) {
			System.err.println("+++ Creating Intermajik MajiClassLoader");
		}

		SystemExportList.getInstance().setMajiClassLoader(this);

		loadClassPaths();
	}

	public void loadClassPaths() {
		String cp = System.getProperty(CLASSPATH_FILE);
		if (cp == null) {
			System.setProperty(CLASSPATH_FILE, DEFAULT_CLASSPATH_FILE);
			cp = DEFAULT_CLASSPATH_FILE;
			//String msg =  "Classpath file property \"" + CLASSPATH_FILE + "\" not specified";
			//throw new RuntimeException(msg);
		}

		File classpathFile = new File(cp);
		if (!classpathFile.isAbsolute()) {
			classpathFile = new File(System.getProperty(Common.PROPERTY_MAJI_HOME) + System.getProperty("file.separator") + cp);
			cp = classpathFile.getAbsolutePath();
		}
		
		if (!classpathFile.exists()) {
			throw new RuntimeException("Classpath file doesn't exist: " + cp);
		}

		majitekDirectoryBaseURL = classpathFile.getParentFile().getPath().replace('\\', '/');
		if (debug) {
			System.err.println("MajiClassLoader majitekDirectoryBaseURL : " + majitekDirectoryBaseURL);
		}

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = builder.parse(classpathFile);

			NodeList jarsList = doc.getDocumentElement().getElementsByTagName("path");

			for (int i = 0; i < jarsList.getLength(); i++) {
				Element path = (Element) jarsList.item(i);
				
				File file = new File(path.getAttribute("value"));
				if (!file.exists()) {
					file = new File(majitekDirectoryBaseURL + "/" + path.getAttribute("value"));
					if (debug) {
						System.err.println("MajiClassLoader, trying path : " +
							 majitekDirectoryBaseURL + "/" + path.getAttribute("value"));
					}
				}
				
				if (!file.exists()) {
					System.err.println("Error parsing classpath file: Incorrect path value: " + path.getAttribute("value"));
					continue;
				}

				String filePath = file.getCanonicalPath().replace('\\', '/');

				boolean isJar = true;
				if (!(filePath.endsWith(".zip") || filePath.endsWith(".jar"))) {
					isJar = false;
					if (!filePath.endsWith("/")) {
						filePath += "/";
					}
				}

				URL url = makeURL(filePath);

				URLConnection c = url.openConnection();

				if (c == null) {
					System.err.println("Incorrect path value: " + url);
					continue;
				} else {
					try {
						c.connect();
					} catch (IOException ex) {
						System.err.println("Incorrect path value: " + url);
						continue;
					}
				}
				if (debug) {
					System.err.println("MajiClassLoader addURL: " + url);
				}

				//System.err.println("intermajik addURL(" + url + ")");

				addURL(url);

				if (isJar) {
					parseJarFile(url);
				}
				else {
					parsePath(filePath);
				}
			}

		} catch (Exception e) {
			System.err.println("Exception parsing classpath file: " + e.getMessage());
		}
	}
	
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		
		if (
				name.startsWith("org.openmaji.implementation.server.classloader") || 
				name.startsWith("org.openmaji.implementation.tool.eclipse")) 
		{
			return super.loadClass(name, resolve);
		}
		
		if (debug) {
			System.err.println("Intermajik MajiClassLoader.loadClass: " + name + " : " + resolve + " : " + this);
		}

		Class<?> c = findLoadedClass(name);
		if (c == null) {
			// TODO synchronized part commented out by Warren 14/7/2011
//			synchronized (name.intern()) {
				c = findClass(name);
//			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	/**
	 * @see java.net.URLClassLoader#findClass(java.lang.String)
	 */
	public Class<?> findClass(String name) throws ClassNotFoundException {
		if (debug) {
			System.err.println("Intermajik MajiClassLoader.findClass: " + name + " : " + this);
		}
		
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			return clazz;
		}
		try {
			// check system
			clazz = findSystemClass(name);
		} 
		catch (ClassNotFoundException e) {
			try {
				clazz = super.findClass(name);
			}
			catch (ClassNotFoundException ex) {
				ClassLoader classLoader = SystemExportList.getInstance().getClassLoaderFor(name);
				if (classLoader != null && classLoader != this) {
					clazz = ((MeemkitClassLoader) classLoader).findClass(name);
				}
			}
		}
		if (clazz != null) {
			return clazz;
		}
		else {
			throw new ClassNotFoundException(name);
		}
	}
	
	public Package getPackage(String packageName) {
		return super.getPackage(packageName);
	}
	
	public Package[] getPackages(String packageName) {
		return super.getPackages();
	}

	private URL makeURL(String location) {
		try {
			return new URL("file", null, 0, location);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void parseJarFile(URL jarUrl) {

		URL url = null;
		try {
			url = new URL("jar:" + jarUrl + "!/");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		try {
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();

			JarFile jarFile = jarConnection.getJarFile();

			Set<String> exportSet = new HashSet<String>();

			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				if (!ze.isDirectory()) {
					String name = ze.getName().replace('/', '.');
					if (name.endsWith(".class")) {
						name = name.substring(0, name.length() - 6);
						exportSet.add(name);
						if (debug) {
							//System.err.println("Intermajik MajiClassLoader.parseJarFile: " + name);
						}
					}
				}
			}

			SystemExportList.getInstance().addMajiClassLoaderExport(exportSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parsePath(String path) {
		
		File file = new File(path);
		
		Collection<File> files = getFileListing(file);
		
		Set<String> exportSet = new HashSet<String>();
		
		for (File f : files) {
			if (f.getName().endsWith(".class")) {
				String className = f.getPath().substring(file.getPath().length() + 1, f.getPath().length() - 6);
				className = className.replace(File.separatorChar, '.');
				exportSet.add(className);
				if (debug) {
					//System.err.println("Intermajik MajiClassLoader.parsePath: " + className);
				}
			}
		}

		SystemExportList.getInstance().addMajiClassLoaderExport(exportSet);
	}
	
	public String toString() {
		return "MajiClassLoader[" + id + "]";
	}

	static public Collection<File> getFileListing(File startingDir) {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = startingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			result.add(file); 
			if (file.isDirectory()) {
				Collection<File> deeperList = getFileListing(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}
}
