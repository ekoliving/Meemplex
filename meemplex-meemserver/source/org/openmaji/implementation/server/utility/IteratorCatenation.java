/*
 * @(#)IteratorCatenation.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.utility;

import java.util.Iterator;

/**
 * <p>
 * IteratorCatenation is an Iterator that combines two other Iterators.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-07-18)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class IteratorCatenation implements Iterator {

  /**
   * Iterator that is currently being used.
   */

  private Iterator currentIterator;

  /**
   * Iterator that is to be used after the current Iterator has run out.
   */

  private Iterator nextIterator;

  /**
   * Catenate the elements of two Iterators into a single Iterator.
   *
   * @param iterator1 First Iterator to be catenated
   * @param iterator2 Second Iterator to be catenated
   */

  public IteratorCatenation(
    Iterator iterator1,
    Iterator iterator2) {

    currentIterator = iterator1;
    nextIterator    = iterator2;
  }

  /**
   * <p>
   * Returns true, if the iteration has more elements.
   * In other words, returns true, if the next() method will return an
   * element rather than throwing an Exception.
   * </p>
   * <p>
   * If the first Iterator has run out of elements, then switch over to
   * using the second Iterator.  Until it has also run out of elements.
   * </p>
   * @return true If the Iterator has more elements
   */

  public boolean hasNext() {
  	boolean hasNext = currentIterator.hasNext();
    if (!hasNext && nextIterator != null) {
        currentIterator = nextIterator;
        nextIterator = null;
        hasNext = currentIterator.hasNext();
    }
    return hasNext;
  }

  /**
   * Provides the next element in the iteration.
   *
   * @return Next element in the iteration.
   */

  public Object next() {
    return currentIterator.next();
  }

  /**
   * <p>
   * Removes from the underlying Collection the last element returned by the
   * Iterator.  This is an optional operation.  This method can only be called
   * once per call to next().
   * </p>
   * <p>
   * The behavior of an Iterator is unspecified if the underlying Collection
   * is modified in any other way than by calling this method, while the
   * iteration is in progress.
   * </p>
   * @exception UnsupportedOperationException if the operation is not supported
   * @exception IllegalStateException is the next() method has not been called
   */

  public void remove() {
    currentIterator.remove();
  }
}
