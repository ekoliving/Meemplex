/*
 * @(#)InvocationListGenerator.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmaji.implementation.server.meem.WedgeImpl;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.hook.Hook;
import org.openmaji.system.meem.hook.invoke.InvocationList;
import org.openmaji.system.meem.hook.invoke.InvocationListProvider;

/**
 * Interface that a generator for invocation lists must conform to.
 */
public class InvocationListProviderImpl implements InvocationListProvider {
	// private InvocationListIdentifierProvider prov;
	private InvocationList inList;

	private Map<String, Hook> hooks = new HashMap<String, Hook>();

	private InvocationList makeInvocationList(Iterator<String> it) {
		List<Hook> l = new ArrayList<Hook>();

		while (it.hasNext()) {
			String key = it.next();
			Hook hook = hooks.get(key);

			if (hook != null) {
				l.add(hook);
			}
			else {
				throw new RuntimeException("can't find hook name: " + key);
			}
		}

		return new InvocationList(l.toArray(new Hook[l.size()]));
	}

	public InvocationListProviderImpl(MeemCore meemCore, InvocationListIdentifierProvider prov) {
		for (WedgeImpl w : ((MeemCoreImpl) meemCore).getWedgeImpls()) {
			if (w.getImplementation() instanceof Hook) {
				hooks.put(w.getImplementationClassName(), (Hook)w.getImplementation());
			}
		}
		inList = makeInvocationList(prov.generate());
	}

	public InvocationList generate() {
		return inList;
	}
}
