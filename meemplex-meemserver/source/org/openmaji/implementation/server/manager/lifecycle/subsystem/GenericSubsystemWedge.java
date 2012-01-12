/*
 * @(#)GenericSubsystemWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.lifecycle.subsystem;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionControl;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionState;
import org.openmaji.system.utility.MeemUtility;

/**
 * @author mg
 */
public class GenericSubsystemWedge implements Wedge {

	//private static final Logger logger = LogFactory.getLogger();

	public SubsystemCommissionControl commissionControlConduit = new CommissionControlConduit();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new MyLifeCycleManagerClient();

	public SubsystemClient subsystemClientConduit;

	private Map<String, MeemDescription> descriptions = new HashMap<String, MeemDescription>();

	public SubsystemCommissionState commissionStateConduit;

	public void commence() {
		commissionStateConduit.commissionStateChanged(CommissionState.COMMISSIONED);
	}

	class CommissionControlConduit implements SubsystemCommissionControl {

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemCommissionControl#changeCommissionState(org.openmaji.system.manager.lifecycle.subsystem.CommissionState)
		 */
		public void changeCommissionState(CommissionState commissionState) {
			commissionStateConduit.commissionStateChanged(commissionState);
		}
	}

	/* ------------------------------------------------------------------------ */
/*
	private class MeemControlConduit implements SubsystemMeemControl {
		public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription) {

			//      if ( myHyperSpacePath == null || myHyperSpacePath.length() == 0 )
			//      {
			//        LogTools.error(logger,"createMeem() - HyperSpace path has not been configured");
			//        return;
			//      }

			String identifier = meemDefinition.getMeemAttribute().getIdentifier();
			if (identifier == null || identifier.length() == 0) {
				LogTools.error(logger, "createMeem() - identifier not set in MeemDefinition");
				return;
			}

			descriptions.put(identifier, meemDescription);
			lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.READY);
		}
	}
*/
	/* ------------------------------------------------------------------------ */

	private class MyLifeCycleManagerClient implements LifeCycleManagerClient {
		
		public void meemCreated(Meem meem, String identifier) {
			MeemDescription meemDescription = descriptions.remove(identifier);

			if (meemDescription != null) {
				MeemDefinition meemDefinition = MeemUtility.spi.get().getMeemDefinition(meem);
				meemDefinition.getMeemAttribute().setIdentifier(identifier);
				subsystemClientConduit.meemCreated(meem, meemDefinition);
				return;
			}

//			LogTools.error(logger, "meemCreated() - unexpected meem, identifier=[" + identifier + "]");
		}

		public void meemDestroyed(Meem arg0) {
//			LogTools.error(logger, "meemDestroyed() - TODO: finish this ?");
		}

		public void meemTransferred(Meem arg0, LifeCycleManager arg1) {
			// don't care
		}
	}
}