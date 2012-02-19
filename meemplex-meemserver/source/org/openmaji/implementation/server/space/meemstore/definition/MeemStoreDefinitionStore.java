/*
 * @(#)MeemStoreDefinitionStore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.definition;

import java.util.Properties;
import java.util.Set;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * @author mg
 * Created on 20/12/2002
 */
public interface MeemStoreDefinitionStore {
	
	void configure(MeemStore meemStore, Properties properties);
	
	void close();
	
	MeemDefinition load(MeemPath meemPath);
	
	void store(MeemPath meemPath, MeemDefinition definition);
	
	void remove(MeemPath meemPath);
	
	int getVersion(MeemPath meemPath);
	
	Set<MeemPath> getAllPaths();
}
