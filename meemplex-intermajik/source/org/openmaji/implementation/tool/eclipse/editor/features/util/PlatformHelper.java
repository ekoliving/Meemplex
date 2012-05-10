/*
 * @(#)PlatformHelper.java
 * Created on 21/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.util;

import org.eclipse.core.runtime.Platform;

/**
 * <code>PlatformHelper</code>.
 * <p>
 * @author Kin Wong
 */
public class PlatformHelper {
	static private Object WIN32 = "Win32";
	static private Object LINUX = "Linux";
	static private Object MACINTOSH = "Macintosh";
	
	static private boolean platformConfirmed = false;
	static private Object platform = LINUX;
	
	static public boolean runningOnWindow() {
		confirmPlatform();
		return platform == WIN32;
	}
	
	static public boolean runningOnLinux() {
		confirmPlatform();
		return platform == LINUX;
	}
	
	static public boolean runningOnMac() {
		confirmPlatform();
		return platform == MACINTOSH;
	}
	
	static private void confirmPlatform() {
		if(platformConfirmed) return;
		
		String os = Platform.getOS();
		if (os.equals(Platform.OS_WIN32)) {
			platform = WIN32;
		}
		else
		if (os.equals(Platform.OS_MACOSX)) {
			platform = MACINTOSH;
		}
		platformConfirmed = true;
	}
	
	static public int getMinFontHeight() {
		if(runningOnWindow()) {
			return 8;
		}
		else
		return 9;
	}
}
