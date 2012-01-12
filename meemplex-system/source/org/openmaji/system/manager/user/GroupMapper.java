/*
 * @(#)GroupMapper.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.user;

import java.util.Map;

import org.openmaji.meem.Facet;


/**
 * A request/response facet that describes the current content of the group manager.
 * <p>
 * The UserManager meem for the given MeemServer has two GroupMapper facets on it, one
 * for mapping which users are in which groups - the <b>userGroupMapper</b> facet - and one for
 * mapping composite groups - the <b>compositeGroupMapper</b> facet.
 * </p>
 * <p>
 * Groups and their members are represented in the returned structures as String objects.
 * </p>
 */
public interface GroupMapper extends Facet
{
    /**
     * Request a map of a group, or receive one.
     * <p>
     * On inbound this method takes the passed in Map and if it is null assumes any group
     * in its parent manager should be mapped. If the Map is non-null then only those groups
     * in the Map will be mapped - with an error being generator if a group is present in the 
     * groupMap that is not present in the manager.
     * </p>
     * <p>
     * On outbound the Map will be populated with the groups of interest and Set objects corresponding
     * to the members of the group represent by the key in the Map.
     * </p>
     * <p>
     * Both the members and the group names are represented as String objects.
     * </p>
     * @param groupMap the map of interest, or the result of the map of interest as a request.
     */
    public void groupMap(Map groupMap);
}
