/*
 * Created on 18/10/2004
 */
package org.openmaji.implementation.rpc.binding.util;

import java.net.URISyntaxException;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;

/**
 * @author Warren Bloomer
 *
 */
public class MeemHelper {

	public static Meem getMeem(String pathString) 
		throws URISyntaxException
	{
		MeemPath meemPath = MeemPathHelper.toMeemPath(pathString);
		return Meem.spi.get(meemPath);
	}

}
