/*
 * @(#)Filter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.filter;

import java.io.Serializable;

/**
 * <p>
 * Filter is a marker interface that indicates that the implementation
 * can be utilized by a FilterChecker.
 * </p>
 * <p>
 * Currently, there is no exact specification for Filters.  There must be
 * explicit agreement between the party that creates a certain type of Filter
 * and the party that implements FilterChecker and uses the Filter.
 * </p>
 * <p>
 * Since the Filter interface provides no specification, the creator of a
 * Filter has no feedback that the particular Filter was used in the manner
 * intended.  Similarly, the FilterChecker must check that it received the
 * correct type of Filter under the given circumstances.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.filter.FilterChecker
 * @see org.openmaji.meem.filter.ExactMatchFilter
 * @see org.openmaji.meem.wedge.reference.Reference
 */
public interface Filter extends Serializable {
}
