/*
 * @(#)MeemStoreClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.space.meemstore;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

/**
 * <p>
 * Client facet for picking up meem store events.
 * </p>
 * <p>
 * Note: this class is almost definitely subject to change.
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface MeemStoreClient extends Facet {

//-mg- this should be put back in once unbound meems are going

	//public void meemStored(Meem meem);

	//public void meemDestroyed(Meem meem);
	
	public void meemStored(MeemPath meemPath);

	public void meemDestroyed(MeemPath meemPath);

}
