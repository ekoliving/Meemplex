/*
 * @(#)MeemStoreContentStore.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.content;

import java.util.Properties;
import java.util.Set;

import org.openmaji.meem.MeemPath;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * @author mg
 * Created on 20/12/2002
 */
public interface MeemStoreContentStore {
	
	void configure(MeemStore meemStore, Properties properties);
	
	void close();
	
	MeemContent load(MeemPath meemPath);
	
	void store(MeemPath meemPath, MeemContent content);
	
	void remove(MeemPath meemPath);
	
	Set<MeemPath> getAllPaths();
}
