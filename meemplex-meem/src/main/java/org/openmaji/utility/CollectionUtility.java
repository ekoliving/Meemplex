/*
 * @(#)CollectionUtility.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.utility;

import java.util.*;

/**
 * <p>
 * A utility class for creating collections.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @author  Greg Davis
 * @version 1.0
 */

public class CollectionUtility {

  /**
   * Provide a new HashMap instance that will be monitored.
   *
   * @return Monitored HashMap instance
   */

  public static <T, V> HashMap<T, V> createHashMap() {
    HashMap<T, V> hashMap = new HashMap<T, V>();

    return(hashMap);
  }

  /**
   * Provide a new HashSet instance that will be monitored.
   *
   * @return Monitored HashSet instance
   */

  public static <T> HashSet<T> createHashSet() {
    HashSet<T> hashSet = new HashSet<T>();

    return(hashSet);
  }

  /**
   * Provide a new Hashtable instance that will be monitored.
   *
   * @return Monitored Hashtable instance
   */

  public static <T, V> Hashtable<T, V> createHashtable() {
    Hashtable<T, V> hashtable = new Hashtable<T, V>();

    return(hashtable);
  }

  /**
   * Provide a new LinkedHashMap instance that will be monitored.
   *
   * @return Monitored LinkedHashMap instance
   */

  public static <T, V> LinkedHashMap<T, V> createLinkedHashMap() {
    LinkedHashMap<T, V> linkedHashMap = new LinkedHashMap<T, V>();

    return(linkedHashMap);
  }

  /**
   * Provide a new LinkedHashSet instance that will be monitored.
   *
   * @return Monitored LinkedHashSet instance
   */

  public static <T> LinkedHashSet<T> createLinkedHashSet() {
    LinkedHashSet<T> linkedHashSet = new LinkedHashSet<T>();

    return(linkedHashSet);
  }

  /**
   * Provide a new Vector instance that will be monitored.
   *
   * @return Monitored Vector instance
   */

  public static <T> Vector<T> createVector() {
    Vector<T> vector = new Vector<T>();

    return(vector);
  }

  /**
   * Compares two Iterators for equality.
   *
   * @return true if Iterator elements are equal
   */

  public static boolean equals(
    Iterator<?> iterator1,
    Iterator<?> iterator2) {

    HashSet<Object> hashSet1 = new HashSet<Object>();
    HashSet<Object> hashSet2 = new HashSet<Object>();

    while (iterator1.hasNext()) hashSet1.add(iterator1.next());
    while (iterator2.hasNext()) hashSet2.add(iterator2.next());

    return(hashSet1.equals(hashSet2));
  }
  
  public static boolean equals(
		    Collection<?> collection1,
		    Collection<?> collection2) {
	  return (collection1.containsAll(collection2) && collection2.size() == collection1.size());
  }
 
}
