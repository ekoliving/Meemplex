package org.openmaji.util;

import org.openmaji.meem.Facet;
import org.openmaji.system.space.CategoryEntry;

public interface CategoryEntryConsumer extends Facet {

	/**
	 * 
	 * @param entry
	 */
	void entry(CategoryEntry entry);
}
