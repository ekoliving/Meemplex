/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.openmaji.meem.Facet;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;

public interface MeemPatternControl extends Facet
{
  public void installPatternMeems(MeemkitDescriptor descriptor);
  
  public void uninstallPatternMeems(MeemkitDescriptor descriptor);
}
