/*
 * @(#)GroupManagerWedge.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.server.security.auth.GroupFile;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.system.manager.user.GroupManagement;
import org.openmaji.system.manager.user.GroupMapper;
import org.openmaji.system.manager.user.GroupMonitor;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * General superclass for a group management wedge.
 */
public abstract class GroupManagerWedge implements Wedge, GroupManagement, GroupMapper, WedgeDefinitionProvider, FilterChecker {
	public GroupManagement groupManagementClient;
	public GroupMapper groupMapperClient;

	public GroupMonitor groupMonitor;
	
	public final ContentProvider<GroupMonitor> groupMonitorProvider = new ContentProvider<GroupMonitor>() {
		public void sendContent(GroupMonitor monitor, Filter filter) throws ContentException {

			if (filter instanceof ExactMatchFilter) {
				ExactMatchFilter<?> f = (ExactMatchFilter<?>) filter;

				if (f.getTemplate() instanceof String) {
					Map groups = groupFile.getGroupsAndMembers();

					if (groups.containsKey(f.getTemplate())) {
						monitor.groupsAdded(Collections.singletonList(f.getTemplate()));

						monitor.groupsUpdated(Collections.singletonMap(f.getTemplate(), groups.get(f.getTemplate())));
					}
				}
			}
			else {
				monitor.groupsAdded(groupFile.getGroups());
				monitor.groupsUpdated(groupFile.getGroupsAndMembers());
			}
		}
	};

	public ErrorHandler errorHandlerConduit;

	private GroupFile groupFile;

	protected GroupManagerWedge(GroupFile groupFile) {
		this.groupFile = groupFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.system.manager.user.GroupManagement#groupAdded(java.lang
	 * .String)
	 */
	public void groupAdded(String group) {
		String error = groupFile.addGroup(group);

		if (error != null) {
			errorHandlerConduit.thrown(new RuntimeException(error));
		}
		else {
			groupManagementClient.groupAdded(group);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.system.manager.user.GroupManagement#groupRemoved(java.lang
	 * .String)
	 */
	public void groupRemoved(String group) {
		String error = groupFile.removeGroup(group);

		if (error != null) {
			errorHandlerConduit.thrown(new RuntimeException(error));
		}
		else {
			groupManagementClient.groupRemoved(group);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.system.manager.user.GroupManagement#memberAdded(java.lang
	 * .String, java.lang.String)
	 */
	public void memberAdded(String group, String memberName) {
		String error = groupFile.addMember(group, memberName);

		if (error != null) {
			errorHandlerConduit.thrown(new RuntimeException(error));
		}
		else {
			groupManagementClient.memberAdded(group, memberName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.system.manager.user.GroupManagement#memberRemoved(java.lang
	 * .String)
	 */
	public void memberRemoved(String memberName) {
		String error = groupFile.removeMember(memberName);

		if (error != null) {
			errorHandlerConduit.thrown(new RuntimeException(error));
		}
		else {
			groupManagementClient.memberRemoved(memberName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.system.manager.user.GroupManagement#memberRemoved(java.lang
	 * .String, java.lang.String)
	 */
	public void memberRemoved(String group, String memberName) {
		String error = groupFile.removeMember(group, memberName);

		if (error != null) {
			errorHandlerConduit.thrown(new RuntimeException(error));
		}
		else {
			groupManagementClient.memberRemoved(group, memberName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.system.manager.user.GroupMapper#groupMap(java.util.Map)
	 */
	public void groupMap(Map groupMap) {
		if (groupMap == null) {
			groupMapperClient.groupMap(groupFile.getGroupsAndMembers());
		}
		else {
			Map groups = new HashMap();
			Map existingGroups = groupFile.getGroupsAndMembers();

			Iterator it = groupMap.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();

				if (existingGroups.containsKey(key)) {
					groups.put(key, existingGroups.get(key));
				}
				else {
					errorHandlerConduit.thrown(new RuntimeException("Request for group named " + key + " that does not exist in manager."));
				}
			}

			groupMapperClient.groupMap(groups);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmaji.meem.filter.FilterChecker#invokeMethodCheck(org.openmaji
	 * .meem.filter.Filter, java.lang.String, java.lang.Object[])
	 */
	public boolean invokeMethodCheck(Filter filter, String facetName, String methodName, Object[] args) throws IllegalFilterException {
		if (filter instanceof ExactMatchFilter) {
			ExactMatchFilter f = (ExactMatchFilter) filter;

			if (f.getTemplate() instanceof String) {
				if (methodName.equals("groupAdded") || methodName.equals("groupRemoved")) {
					return f.getTemplate().equals(args[0]);
				}
				if (methodName.equals("memberAdded") || (args.length == 2 && methodName.equals("memberRemoved"))) {
					return f.getTemplate().equals(args[0]);
				}
			}
		}

		return true;
	}
}
