/*
 * @(#)ExporterHelper.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import java.rmi.server.ExportException;

import org.openmaji.implementation.server.meem.invocation.Revokable;
import org.openmaji.implementation.server.meem.invocation.RevokableTarget;
import org.openmaji.implementation.server.meem.invocation.RevokableTargetImpl;
import org.openmaji.meem.Facet;

import net.jini.config.ConfigurationException;
import net.jini.export.Exporter;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0 
 */
public class RevokableExporterHelper {

	public static RevokableTarget export(Facet obj) {
		try {
			Exporter remoteMeemExporter = ExporterHelper.getExporter();

			Facet target = (Facet) remoteMeemExporter.export(obj);
			Revokable revokable = new RevokableExporter(remoteMeemExporter);

			return new RevokableTargetImpl(target, revokable);
		}
		catch (ConfigurationException e) {
			e.printStackTrace();
		}
		catch (ExportException e) {
			e.printStackTrace();
		}

		return null;
	}
}
