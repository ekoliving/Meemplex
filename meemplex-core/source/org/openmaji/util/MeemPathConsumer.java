package org.openmaji.util;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;

public interface MeemPathConsumer extends Facet {

	void meem(MeemPath meemPath);
}
