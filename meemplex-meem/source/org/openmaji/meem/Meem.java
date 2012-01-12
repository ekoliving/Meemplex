/*
 * @(#)Meem.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem;



import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * A Meem is Maji's distributed component.
 * "Every Meem around you is just part of every other Meem".
 * </p>
 * <p>
 * Each Meem has a unique MeemPath that allows it to be distinguished from
 * every other Meem.  The MeemDefinition describes the Meem, e.g. meta-data.
 * The MeemContent is the actual attributes of a Meem, in particular, those
 * attributes that are persisted.
 * </p>
 * <p>
 * MeemPaths are used to locate a particular Meem's MeemDefinition and
 * MeemContent from a storage mechanism, such as MeemStore.  This means
 * that the MeemPath that refers to an actual Meem must refer to a location
 * within a Space that is specifically used for "storage".
 * </p>
 * <p>
 * Apart from assignment and provision of the MeemPath, the Meem interface
 * says very little about the Meem.  This is because Meems make heavy use
 * of the dynamic "interface composition" approach provided by Maji.  This
 * means that the majority of the functionality can be modularly provided
 * through other Java interfaces, that are dynamically declared and added
 * to, or removed from, the MeemDefinition.
 * </p>
 * <p>
 * Meems are contained and managed by LifeCycleManagers (which are also Meems).
 * A Meem operates within a strictly defined LifeCycle.
 * Relationships (object references) between Meems are called Dependencies.
 * </p>
 * <p>
 * An unbound Meem is one that hasn't been resolved to a specific instance
 * of an LOADED or READY Meem.  Typically, an unbound Meem will be used to
 * locate (via a Filter) a specific Meem of interest in the MeemRegistry.
 * </p>
 * <p>
 * Bound Meems may have methods invoked on their various inbound Facets.
 * Such Meems may be either local to the current JVM or a reference to a
 * remote Meem running in a different JVM.  Most of the functionality of
 * a bound Meem is provided by the system and application defined Facets
 * and their implementation Wedges.
 * </p>
 * <p>
 * The object methods equals() and hashCode() are based purely upon the
 * MeemPath.  This means that Meems placed in indexed object structures,
 * e.g. HashMap, use the MeemPath as the key.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleState
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.MeemPath
 * @see org.openmaji.meem.Wedge
 * @see org.openmaji.meem.definition.MeemDefinition
 * @see org.openmaji.meem.space.Space
 */

public interface Meem extends Facet, DependencyHandler /*, Lifecycle */ {

/*------------------------------------------------------------------\
 * The Meem concept is dedicated to ...                             *
 *   Steve for his vision,                                          *
 *   Warren, Christos, MG, Diana, Kin and Ben for their dedication. *
 \------------------------------------------------------------------*/

	/**
	 * Provide the <code>MeemPath</code> that uniquely identifies this <code>Meem</code>
	 *
	 * @return the definitive <code>MeemPath</code> for this <code>Meem</code>
	 */
	public MeemPath getMeemPath();

	/**
	 * Add a Reference to one of this Meem's outbound facets.
	 *
	 * @param reference Reference to add
	 * @param automaticRemove If this is true, the reference will be removed immediately
	 * after initial content is sent to it.
	 */
	public void addOutboundReference(Reference reference, boolean automaticRemove);

	/**
	 * Remove a previously-added Reference from one of this Meem's outbound facets.
	 *
	 * @param reference Reference to remove
	 */
	public void removeOutboundReference(Reference reference);


	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		public static Meem get(MeemPath meemPath) {
			return ((Meem) MajiSPI.provider().create(Meem.spi.class, new Object[] { meemPath }));
		}

		public static String getIdentifier() {
			return ("meemSystemWedge");
		};
	}
}
