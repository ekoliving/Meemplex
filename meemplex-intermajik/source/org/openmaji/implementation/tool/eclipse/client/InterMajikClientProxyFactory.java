/*
 * Created on 27/08/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;


/**
 * <code>InterMajikClientProxyFactory</code> represents the global instance of
 * proxy factory in InterMajik.<p>
 * @author Kin Wong
 */
public class InterMajikClientProxyFactory extends MeemClientProxyFactory {
	static private InterMajikClientProxyFactory factory = new InterMajikClientProxyFactory();
	
	private ClientSynchronizer synchronizer = SWTClientSynchronizer.getDefault();

	/**
	 * Gets the singleton client proxy factory for InterMajik.<p>
	 * @return The singleton client proxy factory for InterMajik.
	 */
	static public InterMajikClientProxyFactory getInstance() {
		return factory;
	}
	/**
	 * Protects this from instantiation externally.<p>
	 */
	private InterMajikClientProxyFactory() {
	}
	/**
	 * Returns a shared ClientSynchronizer for SWT.<p>
	 * @return A shared ClientSynchronizer for SWT.
	 */
	protected ClientSynchronizer createSynchronizer() { 
		return synchronizer;
	}

	public MeemClientProxy locateWorksheetLifeCycleManager() {
		String worksheetLCMLocation = 
			StandardHyperSpaceCategory.DEPLOYMENT + "/" + 
			MeemServer.spi.getName() + "/worksheet";

		MeemPath worksheetLCMMeemPath = 
			MeemPath.spi.create(Space.HYPERSPACE, worksheetLCMLocation);
		return locate(worksheetLCMMeemPath);
	}
	
	public MeemClientProxy locateHyperSpace() {
		return locate(HyperSpaceHelper.getInstance().getHyperSpaceMeem().getMeemPath());
	}
	
	public MeemClientProxy getMeem(final MeemPath path) {
		return locate(path);
	}

	public MeemClientProxy locateSubsystemFactory() {
		String subsystemFactoryLocation = 
			StandardHyperSpaceCategory.MAJI_SUBSYSTEM_FACTORY;

		MeemPath subsystemFactoryMeemPath = 
			MeemPath.spi.create(Space.HYPERSPACE, subsystemFactoryLocation);
		return locate(subsystemFactoryMeemPath);
	}

}
