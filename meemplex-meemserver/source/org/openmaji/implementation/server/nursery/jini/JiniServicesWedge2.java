package org.openmaji.implementation.server.nursery.jini;

import java.io.*;

import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.Vote;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.utility.PropertiesLoader;

/**
 * This Wedge starts the Jini services, but unlike the first version, does not start a new process.
 * 
 * @author stormboy
 *
 */
public class JiniServicesWedge2 implements Wedge, JiniServices, MeemDefinitionProvider {
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemContext meemContext;

	/* ------------------------------ conduits ------------------------- */
	
	public ErrorHandler errorHandlerConduit;

	public Vote lifeCycleControlConduit = null;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/* -------------------------- private members ---------------------- */
	
	private MeemDefinition meemDefinition = null;

	private JiniStarter2 jiniStarter;

	/**
	 * Constructor
	 */
	public JiniServicesWedge2() {
	}

	/* ------- Meem functionality ------------------------------------- */

	public void commence() {
		logger.log(Level.INFO, "commencing");

		startServices();
	}

	public void conclude() {
		jiniStarter.conclude();
	}

	protected void startServices() {
		try {
			PropertiesLoader.load();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		if (jiniStarter == null) {
			jiniStarter = new JiniStarter2();
		}

		jiniStarter.commence();
	}
	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return meemDefinition;
	}

}
