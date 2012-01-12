/*
 * @(#)IconExtractor.java
 * Created on 4/03/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.presentation;

import java.io.ByteArrayInputStream;

//import org.openmaji.system.manager.lifecycle.subsystem.SubsystemManagerClient;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.intermajik.worksheet.Worksheet;
import org.openmaji.implementation.server.nursery.pattern.MeemPattern;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.meemserver.controller.MeemServerController;
import org.openmaji.system.presentation.InterMajik;
import org.openmaji.system.presentation.MeemIconicPresentation;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.hyperspace.HyperSpace;


/**
 * <code>IconExtractor</code>.
 * <p>
 * @author Kin Wong
 */
public class IconExtractor {
	static public Image extractSmall(MeemClientProxy proxy) {
		Image image = null;
		if(proxy.getVariableMapProxy().get(InterMajik.ICONIC_PRESENTATION_KEY) != null)
		image = extractCustomSmall(proxy);
		if(image == null) return getDefaultSmall(proxy);
		return image;
	}
	
	static public Image extractCustomSmall(MeemClientProxy proxy) {
		MeemIconicPresentation icon = 
			(MeemIconicPresentation)proxy.getVariableMapProxy().get(InterMajik.ICONIC_PRESENTATION_KEY);
		if(icon == null) return null;
		return loadImage(icon.getSmallIcon());
	}
	
	static public Image extractLarge(MeemClientProxy proxy) {
		Image image = null;
		if(proxy.getVariableMapProxy().get(InterMajik.ICONIC_PRESENTATION_KEY) != null)
		image = extractCustomLarge(proxy);
		if(image == null) return getDefaultLarge(proxy);
		return image;
	}

	static public Image extractCustomLarge(MeemClientProxy proxy) {
		MeemIconicPresentation icon = 
			(MeemIconicPresentation)proxy.getVariableMapProxy().get(InterMajik.ICONIC_PRESENTATION_KEY);
		if(icon == null) return null;
		return loadImage(icon.getLargeIcon());
	}
	
	static public Image getDefaultLarge(MeemClientProxy proxy) {
		if(proxy == null) return Images.getIcon("meem32.gif");

		if(proxy.isA(Worksheet.class)) {
			// Large Worksheet
			return Images.getIcon("worksheet32.gif");
		}
		else
		if(proxy.isA(CategoryClient.class)) {
			// Large Category
			return Images.getIcon("category32.gif");
		}
		else {
			return Images.getIcon("meem32.gif");
		}
	}
	
	static public Image getDefaultSmall(MeemClientProxy proxy) {
		if(proxy == null) return Images.getIcon("meem16.gif");
		
		if(proxy.isA(MeemServerController.class)) {
			return Images.getIcon("meemspace16.gif");
		}
		else
		if(proxy.isA(MeemServer.class)) {
			return Images.getIcon("meemserver16.gif");
		}
//		else
//		if(proxy.isA(SubsystemManagerClient.class)) {
//			return Images.getIcon("deployment16.gif");
//		}
		else
		if(proxy.isA(SubsystemClient.class)) {
			return Images.getIcon("subsystem16.gif");
		}
		else
		if(proxy.isA(Worksheet.class)) {
			return Images.getIcon("worksheet16.gif");
		}
		else
		if(proxy.isA(CategoryClient.class)) {
			// Small Category
			if(proxy.getCategoryProxy().isReadOnly()) {
				// Read-Only
				return Images.getIcon("category_readonly16.gif");
			}
			else {
				// Modifiable
				if(proxy.isA(HyperSpace.class)) {
					return Images.ICON_HYPERSPACE.createImage();
				}
				else
				return Images.getIcon("category16.gif");
			}
		}
		else
		if(proxy.isA(MeemPattern.class)) {
			return Images.ICON_MEEMPATTERN.createImage();
		}
		else
		return Images.ICON_MEEM.createImage();
	}
	
	static private Image loadImage(Object image) {
		if(image == null) return null;
		if(!(image instanceof byte[])) return null;
		byte[] imageData = (byte[])image;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(imageData);
			return new Image(Display.getDefault(), new ImageData(is));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
