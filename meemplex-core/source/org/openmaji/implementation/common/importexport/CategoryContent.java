/*
 * @(#)CategoryContent.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

import org.openmaji.meem.Facet;

import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryEntry;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public interface CategoryContent extends Facet, ContentClient {

  public void categoryContentChanged(
    CategoryEntry categoryCategoryEntry,
    CategoryEntry categoryEntries[]
  );
}