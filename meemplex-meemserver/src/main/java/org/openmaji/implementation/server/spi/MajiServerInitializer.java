/*
 * @(#)MajiServerInitializer.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 */

package org.openmaji.implementation.server.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationEntry;
import org.openmaji.system.spi.SpecificationType;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class MajiServerInitializer {

	private static String PROPERTY_CLASSNAME_SUFFIX = "ImplClassName";

	/* ---------- SpecificationEntries ----------------------------------------- */

	private static SpecificationEntry specificationEntries[] = {

	/*
	 * ---------- Essential Meem(s) --------------------------------------------
	 * 
	 * These Essential System Meems are loaded into *every* MeemServer during Genesis by the EssentialLifeCycleManager.
	 * 
	 * Note: These SpecificationEntries *must* define an "identifier".
	 * 
	 * Alphabetical order please !
	 */

	        // -ag- Review list of what really is and isn't an Essential Meem

	        new SpecificationEntry(org.openmaji.implementation.server.manager.error.ErrorRepository.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.error.ErrorRepositoryWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.gateway.GatewayManager.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge.class),

	        new SpecificationEntry(org.openmaji.system.manager.registry.MeemRegistry.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.registry.MeemRegistryWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManager.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManagerWedge.class),

	        new SpecificationEntry(org.openmaji.system.manager.registry.MeemRegistryGateway.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge.class),

	        new SpecificationEntry(org.openmaji.system.space.resolver.MeemResolver.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.space.resolver.MeemResolverWedge.class),

	        new SpecificationEntry(org.openmaji.system.meemserver.controller.MeemServerController.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.meemserver.controller.MeemServerControllerMeem.class),

	        new SpecificationEntry(org.openmaji.system.space.meemstore.MeemStore.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.space.meemstore.MeemStoreWedge.class),

	        new SpecificationEntry(org.openmaji.system.manager.thread.ThreadManager.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.thread.PoolingThreadManagerWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.user.UserManagerMeem.class, SpecificationType.ESSENTIAL_MEEM, org.openmaji.implementation.server.manager.user.UserManagerMeem.class),
	        /*
			 * ---------- System Meem(s) -----------------------------------------------
			 * 
			 * These System Meems may be optionally loaded into a given MeemServer during Genesis by specifying the "all" profile or as part of a "custom" profile.
			 * 
			 * Note: These SpecificationEntries *must* define an "identifier".
			 * 
			 * Alphabetical order please !
			 */

	        // -ag- Review list of what really is and isn't a System Meem
	        /*
			 * new SpecificationEntry( org.openmaji.implementation.server.manager.licensing.LicenseStoreFactoryMeem.class, SpecificationType.SYSTEM_MEEM,
			 * org.openmaji.implementation.server.manager.licensing.LicenseStoreFactoryMeem.class ),
			 * 
			 * new SpecificationEntry( org.openmaji.implementation.server.manager.service.ServiceLicenseManagerMeem.class, SpecificationType.SYSTEM_MEEM,
			 * org.openmaji.implementation.server.manager.service.ServiceLicenseManagerMeem.class ),
			 */

	        new SpecificationEntry(org.openmaji.implementation.server.scripting.bsf.BSFMeem.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.scripting.bsf.BSFMeemWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.space.hyperspace.remote.HyperSpaceJiniLookup.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.space.hyperspace.remote.HyperSpaceJiniLookupMeem.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistry.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryGatewayMeem.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryGatewayMeem.class),

	        new SpecificationEntry(org.openmaji.system.meemkit.core.MeemkitManager.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitManagerMeem.class),

	        new SpecificationEntry(org.openmaji.implementation.server.nursery.jini.meemstore.MeemStoreCallForward.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.nursery.jini.meemstore.MeemStoreExporterWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.space.meemstore.remote.MeemStoreJiniLookup.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.space.meemstore.remote.MeemStoreJiniLookupMeem.class),

	        new SpecificationEntry(org.openmaji.implementation.server.nursery.startup.SystemStartup.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.nursery.startup.SystemStartupWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.nursery.scripting.telnet.TelnetServer.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.nursery.scripting.telnet.TelnetServerWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.security.auth.AuthenticatorExporterWedge.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.security.auth.AuthenticatorExporterWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.security.auth.AuthenticatorLookupWedge.class, SpecificationType.SYSTEM_MEEM, org.openmaji.implementation.server.security.auth.AuthenticatorLookupWedge.class),

	        /*
			 * new SpecificationEntry( org.openmaji.implementation.server.meem.wedge.licensing.PrimaryServerLicensingMeem.class, SpecificationType.SYSTEM_MEEM,
			 * org.openmaji.implementation.server.meem.wedge.licensing.PrimaryServerLicensingMeem.class ),
			 * 
			 * new SpecificationEntry( org.openmaji.implementation.server.meem.wedge.licensing.SecondaryServerLicensingMeem.class, SpecificationType.SYSTEM_MEEM,
			 * org.openmaji.implementation.server.meem.wedge.licensing.SecondaryServerLicensingMeem.class ),
			 */

	        /*
			 * ---------- Miscellaneous Meem(s) ----------------------------------------
			 * 
			 * These Meems are generally known to the system and whilst not been either Essential and/or System Meems, they need an SPI based MeemDefinition because they may need
			 * to be created in circumstances that preclude the use of the MeemStore for holding their MeemDefinition and MeemContent.
			 * 
			 * Alphabetical order please !
			 */

	        new SpecificationEntry(org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager.class, SpecificationType.MEEM, org.openmaji.implementation.server.manager.lifecycle.essential.EssentialLifeCycleManagerWedge.class),

	        // -ag- HyperSpaceMeem should use MeemDefinitionFactory
	        // new SpecificationEntry(
	        // org.openmaji.system.space.hyperspace.HyperSpace.class,
	        // SpecificationType.SYSTEM_MEEM,
	        // org.openmaji.implementation.server.space.hyperspace.HyperSpaceImpl.class
	        // ),

	        new SpecificationEntry(org.openmaji.system.manager.lifecycle.LifeCycleManager.class, SpecificationType.MEEM, org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerWedge.class),

	        new SpecificationEntry(org.openmaji.implementation.server.nursery.jini.JiniServices.class, SpecificationType.MEEM, org.openmaji.implementation.server.nursery.jini.JiniServicesWedge.class),

	        new SpecificationEntry(org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory.class, SpecificationType.MEEM, org.openmaji.implementation.server.manager.lifecycle.subsystem.SubsystemFactoryMeem.class),

	        new SpecificationEntry(org.openmaji.system.meemserver.MeemServer.class, SpecificationType.MEEM, org.openmaji.implementation.server.meemserver.MeemServerMeem.class),

	        /*
			 * ---------- System Wedge(s) -----------------------------------------------
			 * 
			 * These System Wedges are loaded into *every* Meem when they are either created or activated.
			 * 
			 * Alphabetical order please !
			 */

	        new SpecificationEntry(org.openmaji.meem.wedge.configuration.ConfigurationHandler.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.configuration.ConfigurationHandlerWedge.class),

	        new SpecificationEntry(org.openmaji.meem.wedge.dependency.DependencyHandler.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.dependency.DependencyHandlerWedge.class),

	        new SpecificationEntry(org.openmaji.meem.wedge.error.ErrorHandler.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.error.ErrorHandlerWedge.class),

	        new SpecificationEntry(org.openmaji.system.meem.FacetClient.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.FacetClientWedge.class),

	        new SpecificationEntry(org.openmaji.meem.wedge.lifecycle.LifeCycle.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.lifecycle.LifeCycleWedge.class),

	        new SpecificationEntry(org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.persistence.PersistenceHandlerWedge.class),

	        new SpecificationEntry(org.openmaji.meem.Meem.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.MeemSystemWedge.class),

	        new SpecificationEntry(org.openmaji.meem.MeemClient.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.reference.MeemClientWedge.class),

	        new SpecificationEntry(org.openmaji.system.meem.definition.MetaMeem.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.definition.MetaMeemWedge.class),

	        new SpecificationEntry(org.openmaji.system.meem.wedge.remote.RemoteMeem.class, SpecificationType.SYSTEM_WEDGE, org.openmaji.implementation.server.meem.wedge.remote.RemoteMeemWedge.class),

	        /*
			 * ---------- System Hook(s) ------------------------------------------------
			 * 
			 * These System Hooks are loaded into *every* Meem when they are either created or activated.
			 * 
			 * Alphabetical order please !
			 */

//	        new SpecificationEntry(org.openmaji.system.meem.hook.flightrecorder.FlightRecorderHook.class, SpecificationType.SYSTEM_HOOK, org.openmaji.implementation.server.meem.hook.flightrecorder.FlightRecorderHookWedge.class),

//	        new SpecificationEntry(org.openmaji.system.meem.hook.security.InboundSecurityHook.class, SpecificationType.SYSTEM_HOOK, org.openmaji.implementation.server.meem.hook.security.InboundSecurityHookWedge.class),

	        /*
			 * ---------- Miscellaneous Object(s) --------------------------------------
			 * 
			 * These Objects are generally useful concepts within a Maji Server implementation that are good candidates for pluggable implementation.
			 * 
			 * Alphabetical order please !
			 */

	        // -ag- Remove DefinitionFactory a.s.a.p

	        new SpecificationEntry(org.openmaji.system.space.Category.class, SpecificationType.OBJECT, org.openmaji.implementation.server.space.CategoryWedge.class),

	        new SpecificationEntry(org.openmaji.system.utility.CategoryUtility.class, SpecificationType.OBJECT, org.openmaji.implementation.server.utility.CategoryUtilityImpl.class),

	        new SpecificationEntry(org.openmaji.system.meem.definition.DefinitionFactory.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.definition.DefinitionFactoryImpl.class),

	        new SpecificationEntry(org.openmaji.system.meem.wedge.jini.Exporter.class, SpecificationType.WEDGE, org.openmaji.implementation.server.meem.wedge.jini.ExportableServiceWedge.class),

	        new SpecificationEntry(org.openmaji.system.genesis.Genesis.class, SpecificationType.OBJECT, org.openmaji.implementation.server.genesis.GenesisImpl.class),

	        new SpecificationEntry(org.openmaji.implementation.server.meem.invocation.InvocationListIdentifierProvider.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.invocation.InvocationListIdentifierProviderImpl.class),

	        new SpecificationEntry(org.openmaji.system.manager.lifecycle.LifeCycleManagerDefinitionFactory.class, SpecificationType.OBJECT, org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerDefinitionFactoryImpl.class),

	        new SpecificationEntry(org.openmaji.implementation.server.meem.core.MeemBuilder.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.core.MeemCoreImpl.class),

	        new SpecificationEntry(org.openmaji.meem.definition.MeemDefinitionFactory.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.definition.MeemDefinitionFactoryImpl.class),

	        new SpecificationEntry(org.openmaji.system.meem.MeemFactory.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.MeemFactoryImpl.class),

	        new SpecificationEntry(org.openmaji.meem.MeemPath.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.MeemPathImpl.class),

	        new SpecificationEntry(org.openmaji.system.meem.definition.MeemStructure.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.definition.MeemStructureImpl.class),

	        new SpecificationEntry(org.openmaji.meem.Meem.spi.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.MeemUnbound.class),

	        new SpecificationEntry(org.openmaji.system.utility.MeemUtility.class, SpecificationType.OBJECT, org.openmaji.implementation.server.utility.MeemUtilityImpl.class),

	        new SpecificationEntry(org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerMeem.class, SpecificationType.OBJECT, org.openmaji.implementation.server.manager.lifecycle.persisting.PersistingLifeCycleManagerMeem.class),

	        new SpecificationEntry(org.openmaji.meem.wedge.reference.Reference.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.wedge.reference.ReferenceImpl.class),

	        new SpecificationEntry(org.openmaji.system.gateway.ServerGateway.class, SpecificationType.OBJECT, org.openmaji.implementation.server.gateway.ServerGatewayImpl.class),

	        new SpecificationEntry(org.openmaji.utility.uid.UID.class, SpecificationType.OBJECT, org.openmaji.implementation.server.utility.uid.UIDImpl.class),

	        new SpecificationEntry(org.openmaji.system.manager.lifecycle.subsystem.Subsystem.class, SpecificationType.WEDGE, org.openmaji.implementation.server.manager.lifecycle.subsystem.SubsystemWedge.class),

	        new SpecificationEntry(org.openmaji.meem.definition.WedgeDefinitionFactory.class, SpecificationType.OBJECT, org.openmaji.implementation.server.meem.definition.WedgeDefinitionFactoryImpl.class)

	/* ---------- End of SpecificationEntries ---------------------------------- */
	};

	public static void initialize(MajiSystemProvider majiSystemProvider) {

		Properties systemProperties = System.getProperties();

		// load the logging properties
		final InputStream inputStream = MajiServerInitializer.class.getResourceAsStream("/logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(inputStream);
		}
		catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}

		for (int index = 0; index < specificationEntries.length; index++) {
			SpecificationEntry specificationEntry = specificationEntries[index];

			String implementationClassNamePropertyName = specificationEntry.getSpecification().getName() + PROPERTY_CLASSNAME_SUFFIX;

			String implementationClassName = systemProperties.getProperty(implementationClassNamePropertyName);

			if (implementationClassName != null) {
				try {
					Class<?> implementationClass = ObjectUtility.getClass(Object.class, implementationClassName);
					specificationEntry = new SpecificationEntry(specificationEntry.getSpecification(), specificationEntry.getSpecificationType(), implementationClass);
				}
				catch (Exception exception) {
					String msg = "Couldn't instantiate " + implementationClassName + " as the custom implementation for " + specificationEntry.getSpecification();
					logger.log(Level.WARNING, msg, exception);

					//System.exit(-1); // Yes, this choice was deliberately made !
				}
			}

			majiSystemProvider.addSpecificationEntry(specificationEntry);
		}
	}

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();
}
