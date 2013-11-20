package org.openmaji.implementation.network;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;

/**
 * 
 */
public class WakeOnLanMeem implements MeemDefinitionProvider {
	private MeemDefinition meemDefinition = null;

	/**
	 * Return a MeemDefinition for this Meem which lists all of the Wedges
	 * required to assemble this Meem.
	 * 
	 * @return The MeemDefinition for this Meem
	 * 
	 */
	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			Class<?>[] wedges = new Class[] { WakeOnLanWedge.class, };
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
			meemDefinition.getMeemAttribute().setIdentifier("WakeOnLan");
		}

		return meemDefinition;
	}
}
