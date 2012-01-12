/*
 * @(#)ContentClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.reference;

/**
 * <p>
 * ContentClient should be implemented on an inbound facet if the developer passing
 * that target off to another meem wants to request initial content from the meem and
 * wants to know when the initial content has finished arriving, or if an error occured either
 * in adding the reference or sending the content.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.system.meem.wedge.reference.ContentProvider
 */
public interface ContentClient {

  /**
   * This will be called by the meem receiving the inbound facet when initial content
   * has been sent.
   */
  public void contentSent();
  
  /**
   * This will be called by the meem receiving the inbound facet if there is a problem in
   * sending the initial content.
   * 
   * @param reason a message giving the reason for failure.
   */
  public void contentFailed(String reason);
}
