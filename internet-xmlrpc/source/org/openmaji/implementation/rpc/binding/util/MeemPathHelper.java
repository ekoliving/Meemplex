/*
 * Created on 8/09/2004
 *
 */
package org.openmaji.implementation.rpc.binding.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;

/**
 * @author Warren Bloomer
 *
 */
public class MeemPathHelper {
	private static final Logger logger = LogFactory.getLogger();

	String meempath;
	String method;
	
	
	/**
	 * 
	 * @param pathString
	 * @throws URISyntaxException
	 */
	public static MeemPath toMeemPath(String pathString) 
		throws URISyntaxException
	{
		MeemPath meemPath = null;

		URI meemPathURI = new URI(pathString);

		String scheme = meemPathURI.getScheme();	// scheme should represent the meemspace
		String path   = meemPathURI.getPath();
		
		if (path == null) {
			// an opaque URI such as meemStore:9b252ce1-a1fa-4c9f-b702-d2f579fc6c9a
			path = meemPathURI.getSchemeSpecificPart();
		}

		if ( scheme.equalsIgnoreCase(Space.HYPERSPACE.getType()) ) {
			meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
		}
		else  if ( scheme.equalsIgnoreCase(Space.MEEMSTORE.getType()) ) {
			meemPath = MeemPath.spi.create(Space.MEEMSTORE, path);			
		}
		else  if ( scheme.equalsIgnoreCase(Space.TRANSIENT.getType()) ) {
			meemPath = MeemPath.spi.create(Space.TRANSIENT, path);
		}
		else {
			LogTools.info(logger, "Unknown meemspace in meempath: " + scheme);
		}

		return meemPath;
	}
	
	/**
	 * 
	 * @param meemPath
	 */
	public static String fromMeemPath(MeemPath meemPath) {
		String uriString = meemPath.getSpace().getType() + ":" + meemPath.getLocation();

		return uriString;
	}
}
