/*
 * @(#)FilterChecker.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.filter;

/**
 * <p>
 * A FilterChecker uses a Filter to determine whether a given method
 * invocation should occur.  This can be used as either a means for
 * ensuring correctness or for optimization purposes.
 * </p>
 * <p>
 * For example, if a Meem client expects a particular MeemPath when
 * the Meem provider has a huge list of MeemPaths.  Then the Meem client
 * could include an ExactMatchFilter in its Dependency on the Meem provider.
 * This is a convenient way of ensuring correctness, because the Meem client
 * can't differentiate between all the potentially provided MeemPaths.
 * Also, it provides a much needed optimization, because the ExactMatchFilter
 * prevents the Meem provider from making an expensive delivery of superfluous
 * MeemPaths.
 * </p>
 * <p>
 * The parameters provided to invokeMethodCheck() are not veted.  This means
 * that the FilterChecker implementation needs to ensure that the Filter type
 * is one that it can deal with.  Also, the context in which the Filter has
 * been provided, e.g. methodName and args, must also be checked to ensure
 * that they make sense in regards to the given Filter type.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.filter.Filter
 * @see org.openmaji.meem.filter.ExactMatchFilter
 * @see org.openmaji.meem.filter.IllegalFilterException
 * @see org.openmaji.meem.wedge.reference.Reference
 */

public interface FilterChecker {

  /**
   * <p>
   * Determine whether the given method should be invoked.
   * The Filter is applied, typically in the context of the given method
   * and its arguments, to provide a simple decision on whether the
   * method should be invoked or not.
   * </p>
   * <p>
   * The implementation should check that the Filter is of an appropriate
   * type.  Based on the given method, the arguments may be extracted to
   * provide the required context for the Filter to operate.
   * </p>
   * @param filter     Filter to apply in the given context
   * @param methodName Name of the method that may be filtered
   * @param args       Arguments for the method that may be filtered
   * @return           True, if the method should not be invoked
   * @exception IllegalFilterException Unknown or inappropriate Filter type
   */

  public boolean invokeMethodCheck(
    Filter   filter,
    String   methodName,
    Object[] args)
    throws   IllegalFilterException;
}
