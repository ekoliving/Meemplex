/*
 * @(#)ResourceManager.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;


/**
 * @author Kin Wong
 */
public class FontManager {
	static private FontManager _instance = new FontManager();
	static public FontManager getInstance() {return _instance;}
	
	class CachedEntry
	{
		public int refCount;
		public Font font;
	}
	
	private Map entryMap = new HashMap();
	
	public Font checkOut(FontDescriptor descriptor) {
		CachedEntry entry = (CachedEntry)entryMap.get(descriptor);
		if(entry == null) {
			entry = new CachedEntry();
			entry.font = new Font(
					Display.getDefault(), 
					descriptor.getName(), 
					descriptor.getHeight(), 
					descriptor.getStyle());
			entryMap.put(descriptor, entry);
		}
		entry.refCount++;
		return entry.font;
	}
	
	public boolean checkIn(FontDescriptor descriptor) {
		if(descriptor == null) return false;
		CachedEntry entry = (CachedEntry)entryMap.get(descriptor);
		if(entry == null) return false;
		entry.refCount--;
		
		if(entry.refCount == 0) {
			if(entry.font != null) {
				entry.font.dispose();
				entry.font = null;
			}
			entryMap.remove(descriptor);
		}
		return true;
	}
}
