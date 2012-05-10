/*
 * @(#)GroupManagement.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.user;

import org.openmaji.meem.Facet;

/**
 * General facet for group management. A users group determines what roles they are able to have
 * in the MeemServer.
 * <p>
 * The UserManager meem for the given MeemServer has two group manangement facets on it, one
 * for managing which users are in which groups - the <b>userGroupManagement</b> facet - and one for
 * managing composite groups - the <b>compositeGroupManagement</b> facet. The same facet class is used 
 * for outbound as well, but in that case the outbound facet names are <b>userGroupManagementClient</b> 
 * and <b>compositeGroupManagementClient</b> respectively.
 * <p>
 * Both the group management facets are  request/response facets so need to be used with the
 * request response facilities provided by Maji. See the <a href="package-summary.html">package summary</a>
 * for details. 
 */
public interface GroupManagement extends Facet
{
    /**
     * Add a group to the management object.
     * 
     * @param group the name of the group to be added.
     */
    public void groupAdded(String group);

    /**
     * Remove a group from the management object.
     * 
     * @param group the name of the group to be removed.
     */
    public void groupRemoved(String group);

    /**
     * Add a member to the passed in group in the management object.
     * 
     * @param group the group to be changed.
     * @param memberName the member to be added.
     */
    public void memberAdded(String group, String memberName);
    
    /**
     * Remove a member from any group in the management object.
     * 
     * @param memberName the member to be removed.
     */
    public void memberRemoved(String memberName);
    
    /**
     * Remove a member from the passed in group in the management object.
     * 
     * @param group the group to be changed.
     * @param memberName the member to be removed.
     */
    public void memberRemoved(String group, String memberName);
}
