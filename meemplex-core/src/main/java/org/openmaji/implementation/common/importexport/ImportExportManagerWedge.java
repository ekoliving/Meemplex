/*
 * @(#)ImportExportManagerWedge.java
 *
 *  Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 *  This software is the proprietary information of EkoLiving Pty Ltd.
 *  Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.common.importexport;

import java.net.URL;

import org.openmaji.common.Binary;
import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.meem.MeemFactory;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemserver.MeemServer;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class ImportExportManagerWedge implements Wedge, ImportExportManager {

	public MeemClientConduit meemClientConduit;

	
    private static final MeemPath transientLcmPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/transientLifeCycleManager");

	/**
	 * @see org.openmaji.implementation.common.importexport.ImportExportManager#createExportMeem(org.openmaji.meem.MeemPath, java.net.URL, int)
	 */
	public void createExportMeem(MeemPath sourceMeemPath, URL exportURL, int depthLimit) {
        ServerGateway   g = ServerGateway.spi.create();
		Meem exportMeem =
			MeemFactory.spi.get().create(MeemDefinitionFactory.spi.create().createMeemDefinition(ExportMeem.class), g.getMeem(transientLcmPath));

		MeemClientCallback callback = new ExportCallback(sourceMeemPath, exportURL, depthLimit);
		meemClientConduit.provideReference(exportMeem, "exportParameters", ExportParameters.class, callback);
	}

	/**
	 * @see org.openmaji.implementation.common.importexport.ImportExportManager#createImportMeem(java.net.URL, org.openmaji.meem.MeemPath)
	 */
	public void createImportMeem(URL importURI, MeemPath targetMeemPath) {
        ServerGateway   g = ServerGateway.spi.create();
		Meem importMeem =
			MeemFactory.spi.get().create(MeemDefinitionFactory.spi.create().createMeemDefinition(ImportMeem.class), g.getMeem(transientLcmPath));

		MeemClientCallback callback = new ImportCallback(importMeem, importURI, targetMeemPath);
		meemClientConduit.provideReference(importMeem, "importParameters", ImportParameters.class, callback);
	}

	private class ExportCallback implements MeemClientCallback {
		private final MeemPath sourceMeemPath;
		private final URL exportURL;
		private final int depthLimit;

		public ExportCallback(MeemPath sourceMeemPath, URL exportURL, int depthLimit) {
			this.sourceMeemPath = sourceMeemPath;
			this.exportURL = exportURL;
			this.depthLimit = depthLimit;
		}

		public void referenceProvided(Reference reference) {
			ExportParameters exportParameters = (ExportParameters) reference.getTarget();

			exportParameters.exportParametersChanged(sourceMeemPath, exportURL, depthLimit);
		}
	}

	private class ImportCallback implements MeemClientCallback {

		private final Meem meem;
		private final MeemPath targetMeemPath;
		private final URL importURL;

		public ImportCallback(Meem meem, URL importURL, MeemPath targetMeemPath) {
			this.meem = meem;
			this.importURL = importURL;
			this.targetMeemPath = targetMeemPath;
		}

		public void referenceProvided(Reference reference) {
			if (reference.getTarget() instanceof ImportParameters) {
				ImportParameters importParameters = (ImportParameters) reference.getTarget();

				importParameters.importParametersChanged(importURL, targetMeemPath);

				meemClientConduit.provideReference(meem, "binary", Binary.class, this);
			} else if (reference.getTarget() instanceof Binary) {
				Binary binary = (Binary) reference.getTarget();
				binary.valueChanged(true);
			}
		}
	}

}
