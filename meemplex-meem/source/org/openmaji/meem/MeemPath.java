/*
 * @(#)MeemPath.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem;

import java.io.Serializable;

import org.openmaji.meem.space.Space;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * MeemPath describes a location that can be resolved into a Meem.
 * </p>
 * <p>
 * A MeemPath consists of a reference to a Space and to a location
 * within that Space, which identifies a specific Meem.
 * </p>
 * @author  Andy Gelme
 * @author  MG
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.space.Space
 */
public interface MeemPath extends Serializable {

	/**
	 * Provide the location within the Space that identifies the specific Meem.
	 *
	 * @return Location within the Space that identifies the specific Meem
	 */
	public String getLocation();

	/**
	 * Provide the type of Space that may contain the Meem.
	 *
	 * @return Type of Space that may contain the Meem
	 */
	public Space getSpace();

	/**
	 * Indicates whether the MeemPath could be used to directly aquire
	 * a Meem's definition and/or content. 
	 *
	 * @return True if the MeemPath is definitive.
	 */
	public boolean isDefinitive();

	/**
	 * Compares MeemPath to the specified object.
	 * The result is true, only if both the spaces and locations are equal.
	 *
	 * @return true if MeemPaths are equal
	 */
	public boolean equals(Object object);

	/**
	 * Provides the Object hashCode.
	 * Must follow the Object.hashCode() and Object.equals() contract.
	 *
	 * @return MeemPath hashCode
	 */
	public int hashCode();

	/**
	 * Provides a String representation of MeemPath.
	 *
	 * @return String representation of MeemPath
	 */
	public String toString();

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		public static MeemPath create(Space space, String location) {
			return ((MeemPath) MajiSPI.provider().create(MeemPath.class, new Object[] { space, location }));
		}
	}
}
