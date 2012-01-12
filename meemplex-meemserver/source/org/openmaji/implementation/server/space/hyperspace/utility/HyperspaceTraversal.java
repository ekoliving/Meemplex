/*
 * @(#)HyperspaceTraversal.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.space.hyperspace.utility;

import java.util.List;
import java.util.Set;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.MeemHelper;
import org.openmaji.server.helper.MeemPathResolverHelper;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;


/**
 * @author Peter
 */
public class HyperspaceTraversal implements CategoryClient, ContentClient
{
	public static void traverse(final Set visited, final List toVisit, final Runnable completion)
	{
		for (;;)
		{		
			if (toVisit.isEmpty())
			{
				completion.run();
				break;
			}


			final MeemPath path = (MeemPath) toVisit.remove(0);

			if (!visited.add(path)) continue;


			final Meem meem = MeemPathResolverHelper.getInstance().resolveMeemPath(path);

			if (meem == null) continue;


			boolean isCategory = MeemHelper.hasA(meem, "categoryClient", CategoryClient.class, Direction.OUTBOUND);

			if (!isCategory) continue;


			final HyperspaceTraversal traversal = new HyperspaceTraversal(visited, toVisit);
			final Reference reference = Reference.spi.create("categoryClient", traversal, true, null);

			Runnable after = new Runnable()
			{
				public void run()
				{
					meem.removeOutboundReference(reference);
					traverse(visited, toVisit, completion);
				};
			};

			traversal.init(after);

			meem.addOutboundReference(reference, false);
			
			break;
		}
	}

	private HyperspaceTraversal(Set visited, List toVisit)
	{
		//this.visited = visited;
		this.toVisit = toVisit;
	}

	private void init(Runnable after)
	{
		this.after = after;
	}
	
	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesAdded(CategoryEntry[] newEntries) {
		for (int i = 0; i < newEntries.length; i++) {
			toVisit.add(newEntries[i].getMeem().getMeemPath());
		}
	}
	
	public void entriesRemoved(CategoryEntry[] removedEntries) {
	}


	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry)
	{
	}

	public void contentSent()
	{
		after.run();
	}

	public void contentFailed(String reason)
	{
		toVisit.clear();

		after.run();
	}

	//private final Set visited;
	private final List toVisit;
	private Runnable after = null;
}
