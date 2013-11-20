/*
 * @(#)MeemStore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.space.meemstore;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * Facet interface for a meem that represents a meem store.
 * </p>
 * <p>
 * Note: this class is almost definitely subject to change.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.space.Space
 */

public interface MeemStore extends Facet {
/* -mg- put these back at some stage
  public void storeMeemContent(
    Meem meem, MeemContent meemContent);

  public void storeMeemDefinition(
    Meem meem, MeemDefinition meemDefinition);

  public void destroyMeem(
    Meem meem);
*/    
	public void storeMeemContent(
		MeemPath meemPath, MeemContent meemContent);

	public void storeMeemDefinition(
		MeemPath meemPath, MeemDefinition meemDefinition);

	public void destroyMeem(
		MeemPath meemPath);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static MeemStore create() {
      return(
        (MeemStore) MajiSPI.provider().create(MeemStore.class));
    }

    public static String getIdentifier() {
      return("meemStore");
    };
  }		
}