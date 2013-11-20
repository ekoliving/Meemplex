/*
 * @(#)GroupMonitor.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.user;

import java.util.List;
import java.util.Map;

import org.openmaji.meem.Facet;


/**
 * A general monitor facet for a wedge implementing a {@link GroupManagement GroupManagement} facet.
 * <p>
 * The UserManager meem for the given MeemServer has two GroupMonitor facets on it, one
 * for monitoring which users are in which groups - the <b>userGroupMonitor</b> facet - and one for
 * monitoring composite groups - the <b>compositeGroupMonitor</b> facet.
 * </p>
 * <p>
 * If a request for initial content is made when a connection to the GroupMonitor facet
 * is made, the initial state of the management facet is sent, first as a List of group added,
 * and then as a Map of groups updated.
 * </p>
 * <p>
 * If the connection is maintained the state is updated in accordance with the rules applying
 * to the methods below.
 * </p>
 * <p>
 * Groups and their members are represented in the returned structures as String objects.
 * </p>
 */
public interface GroupMonitor extends Facet
{
    /**
     * Return a list of groups that have been added since the last invocation.
     * 
     * @param groups the list of new groups.
     */
    public void groupsAdded(List groups);

    /**
     * Return a list of groups removed since the last invocation.
     * 
     * @param groups the list of groups removed.
     */
    public void groupsRemoved(List groups);

    /**
     * Retrun a map of groups that have changed together with their current members.
     * 
     * @param groupsAndMembers a map of changed groups including their membership.
     */
    public void groupsUpdated(Map groupsAndMembers); 
}
