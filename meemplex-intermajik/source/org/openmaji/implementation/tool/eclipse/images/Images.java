/*
 * @(#)Images.java
 * Created on 30/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.images;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

class ImageCache {
	private Device device;
	private Map imageMap = new HashMap();

	public ImageCache(Device device) {
		this.device = device;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public Image getImage(Class location, String filename) {
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(location, filename);
		Image image = (Image)imageMap.get(descriptor);
		if(image != null){
			// The image has been previously loaded.
			//System.out.println("Hitting Cache Image: " + filename);
			return image; 
		}	
		
		image = descriptor.createImage(getDevice());
		//System.out.println("Creating Image: " + filename);
		imageMap.put(descriptor, image);
		return image;
	}
};
	
/**
 * <code>Images</code>.
 * <p>
 * @author Kin Wong
 */
public class Images {
	static public ImageDescriptor IMAGE_CONFIGURATION;
	static public ImageDescriptor IMAGE_WORKSHEET;
	static public ImageDescriptor IMAGE_WORKSHEET_HL;
	
	static public ImageDescriptor ICON_MAJI;
	static public ImageDescriptor ICON_LOGIN;
	static public ImageDescriptor ICON_LOGOUT;
	
	static public ImageDescriptor ICON_HYPERSPACE;
	static public ImageDescriptor ICON_MEEMSERVER;
	static public ImageDescriptor ICON_MEEMPATTERN;
	static public ImageDescriptor ICON_MEEM;

	static public ImageDescriptor ICON_LAYOUT;
	static public ImageDescriptor ICON_CATEGORY;
	static public ImageDescriptor ICON_WORKSHEET;
	
	static public ImageDescriptor ICON_DEFINITION;
	static public ImageDescriptor ICON_CONFIGURATION;
	static public ImageDescriptor ICON_PRESENTATION;
	static public ImageDescriptor ICON_FILTER_NONE;
	
	static public ImageDescriptor ICON_SUBSYSTEM;
	static public ImageDescriptor ICON_START;
	static public ImageDescriptor ICON_STOP;
	static public ImageDescriptor ICON_DELETE;
	
	static public ImageDescriptor ICON_MEEM_STATE_ABSENT;
	static public ImageDescriptor ICON_MEEM_STATE_DORMANT;
	static public ImageDescriptor ICON_MEEM_STATE_LOADED;
	static public ImageDescriptor ICON_MEEM_STATE_PENDING;
	static public ImageDescriptor ICON_MEEM_STATE_READY;
	
	static public ImageDescriptor ICON_RENAME;
	static public ImageDescriptor ICON_MEEM_DESTROY;
	static public ImageDescriptor ICON_MEEM_REMOVE;
	
	static public ImageDescriptor ICON_SORT_ASCENDING;
	static public ImageDescriptor ICON_SORT_DESCENDING;
	
	static public ImageDescriptor ICON_DETAILED_VIEW;
	static public ImageDescriptor ICON_ICONIC_VIEW;
	static public ImageDescriptor ICON_DEVICE_VIEW;

	static public ImageDescriptor ICON_COLLAPSE;
	static public ImageDescriptor ICON_COLLAPSE_ALL;
	static public ImageDescriptor ICON_EXPAND;
	static public ImageDescriptor ICON_EXPAND_ALL;

	static public ImageDescriptor ICON_ROUTER_STANDARD;
	static public ImageDescriptor ICON_ROUTER_MANHATTAN;
	
	static public ImageDescriptor ICON_SHOW_SYSTEM_WEDGES;
	static public ImageDescriptor ICON_HIDE_SYSTEM_WEDGES;
	
	static public ImageDescriptor ICON_VIEW_CONTENT_ONLY;
	static public ImageDescriptor ICON_VIEW_VERITICAL_ORIENTATION;
	static public ImageDescriptor ICON_VIEW_HORIZONTAL_ORIENTATION;

	static public ImageDescriptor ICON_DEPENDENCY_WEAK_SINGLE;
	static public ImageDescriptor ICON_DEPENDENCY_WEAK_MANY;
	
	static public ImageDescriptor ICON_HIDE_INFO;
	
	static public ImageDescriptor ICON_MEEM_UNAVAILABLE;
	
	static {
		IMAGE_CONFIGURATION = loadImage("configuration.bmp");
		IMAGE_WORKSHEET = loadImage("worksheet.bmp");
		IMAGE_WORKSHEET_HL = loadImage("worksheet_hl.bmp");
		
		ICON_MAJI = loadIcon("Maji_System-16.gif");
		ICON_LOGIN = loadIcon("login16.gif");
		ICON_LOGOUT = loadIcon("logout16.gif");

		ICON_HYPERSPACE = loadIcon("hyperspace16.gif");
		ICON_MEEMSERVER = loadIcon("meemserver16.gif");
		ICON_MEEMPATTERN = loadIcon("meempattern16.gif");
		ICON_MEEM = loadIcon("meem16.gif");
				
		ICON_LAYOUT = loadIcon("layout16.gif");
		
		ICON_MEEM_UNAVAILABLE = loadIcon("unavailable.gif");
		
		ICON_MEEM_STATE_ABSENT = loadIcon("state_absent16.gif");
		ICON_MEEM_STATE_DORMANT = loadIcon("state_dormant16.gif");
		ICON_MEEM_STATE_LOADED = loadIcon("state_loaded16.gif");
		ICON_MEEM_STATE_PENDING = loadIcon("state_pending16.gif");
		ICON_MEEM_STATE_READY = loadIcon("state_ready16.gif");
		
		ICON_CATEGORY = loadIcon("category16.gif");
		ICON_WORKSHEET = loadIcon("worksheet16.gif");
		
		ICON_RENAME = loadIcon("rename16.gif");
		ICON_MEEM_DESTROY = loadIcon("meem_destroy.gif");
		ICON_MEEM_REMOVE = loadIcon("meem_remove.gif");
		
		ICON_SORT_ASCENDING = loadIcon("sort_ascending.gif");
		ICON_SORT_DESCENDING = loadIcon("sort_descending.gif");

		ICON_DETAILED_VIEW = loadIcon("detailed_view.gif");
		ICON_ICONIC_VIEW = loadIcon("meem16.gif");
		ICON_DEVICE_VIEW = loadIcon("device_view.gif");

		ICON_COLLAPSE = loadIcon("collapse.gif");
		ICON_COLLAPSE_ALL = loadIcon("collapse_all.gif");
		ICON_EXPAND = loadIcon("expand.gif");
		ICON_EXPAND_ALL = loadIcon("expand_all.gif");

		ICON_ROUTER_STANDARD = loadIcon("router_standard.gif");
		ICON_ROUTER_MANHATTAN = loadIcon("router_manhattan.gif");
		
		ICON_SHOW_SYSTEM_WEDGES = loadIcon("system_wedges_show.gif");
		ICON_HIDE_SYSTEM_WEDGES = loadIcon("system_wedges_hide.gif");

		ICON_VIEW_CONTENT_ONLY = loadIcon("view_content.gif");
		ICON_VIEW_VERITICAL_ORIENTATION = loadIcon("view_vertical.gif");
		ICON_VIEW_HORIZONTAL_ORIENTATION = loadIcon("view_horizontal.gif");
		ICON_HIDE_INFO = loadIcon("hide_info.gif");
		
		ICON_DEPENDENCY_WEAK_SINGLE = loadIcon("dependency_weaksingle16.gif");
		ICON_DEPENDENCY_WEAK_MANY = loadIcon("dependency_weakmany16.gif");
		
		ICON_DEFINITION = loadIcon("definition16.gif");
		ICON_CONFIGURATION = loadIcon("configuration16.gif");
		ICON_PRESENTATION = loadIcon("presentation16.gif");
		ICON_FILTER_NONE = loadIcon("filter_none16.gif");

		ICON_SUBSYSTEM = loadIcon("subsystem16.gif");
		ICON_START = loadIcon("start16.gif");
		ICON_STOP = loadIcon("stop16.gif");
		ICON_DELETE = loadIcon("delete16.gif");
	}
	
	static private Map deviceToCacheMap = new HashMap();	// Map device to Cache
	
	static public Image getImage(Device device, Class location, String filename) {
		ImageCache cache = (ImageCache)deviceToCacheMap.get(device);
		if(cache == null) {
			// The image cache for the device doesn't exist yet
			cache = new ImageCache(device);
			deviceToCacheMap.put(device, cache);
		}
		return cache.getImage(location, filename);
	}
	
	static public Image getIcon(String filename) {
		return getImage(Display.getDefault(), Images.class, "icons/" + filename);
	}
	
	static public ImageDescriptor loadImage(String filename) {
		return ImageDescriptor.createFromFile(Images.class, filename);
	}

	static public ImageDescriptor loadIcon(String filename) {
		return loadImage("icons/" + filename);
	}
}
