/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.intermajik.model;

import java.io.Serializable;

public class ViewMode implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

  private Object id;
  private String name;
  
  public ViewMode(Object id, String name) {
    this.id = id;
    this.name = name;
  }

  public Object getId()
  {
    return id;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return name;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return id.hashCode();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(!(obj instanceof ViewMode)) return false;
    ViewMode that = (ViewMode)obj;
    if(!id.equals(that.id)) return false;
    return name.equals(that.name);
  }
}
