/*
 * @(#)MeemVariableSource.java
 * Created on 5/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import java.io.Serializable;
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.openmaji.implementation.intermajik.model.SimpleDimension;
import org.openmaji.implementation.intermajik.model.SimplePoint;
import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * <code>MeemVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemVariableSource extends ElementContainerVariableSource {
	private Meem meem;

	/**
	 * Constructs an instance of <code>MeemVariableSource</code>.
	 * <p>
	 * @param meem The meem associates with the meem variable source.
	 */
	public MeemVariableSource(Meem meem) {
		super(meem);
		this.meem = meem;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#extract()
	 */
	public ValueBag extractAll() {
		ValueBag bag = new ValueBag();
		extract(Meem.ID_SIZE, bag);
		extract(Meem.ID_LOCATION, bag);
		extract(Meem.ID_COLLAPSED, bag);
		extract(Meem.ID_VIEW_MODE, bag);
		extract(Meem.ID_CHILD_ORDERS, bag);
		extract(Meem.ID_SHOWSYSTEMWEDGES, bag);
		return bag;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource#extract(java.lang.Object, org.openmaji.implementation.tool.eclipse.client.variables.ValueBag)
	 */
	public boolean extract(Object key, ValueBag bag) {
		if (key.equals(Meem.ID_SIZE)) {
			Dimension size = meem.getSize();
			SimpleDimension fudgedSize = new SimpleDimension(size.width, size.height);
			bag.add(Meem.ID_SIZE, fudgedSize);
			return true;
		}
		else if (key.equals(Meem.ID_LOCATION)) {
			Point location = meem.getLocation();
			SimplePoint fudgedLocation = new SimplePoint(location.x, location.y);
			bag.add(Meem.ID_LOCATION, fudgedLocation);
			return true;
		}
		else if (key.equals(Meem.ID_COLLAPSED)) {
			bag.add(Meem.ID_COLLAPSED, new Boolean(meem.isCollapsed()));
			return true;
		}
		else if (key.equals(Meem.ID_VIEW_MODE)) {
			bag.add(Meem.ID_VIEW_MODE, meem.getViewMode());
			return true;
		}
		else if (key.equals(Meem.ID_SHOWSYSTEMWEDGES)) {
			bag.add(Meem.ID_SHOWSYSTEMWEDGES, new Boolean(meem.isSystemWedgeShown()));
			return true;
		}
		else
			return super.extract(key, bag);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#merge(org.openmaji.implementation.tool.eclipse.client.ValueBag)
	 */
	public boolean merge(ValueBag bag) {
		boolean changed = false;
		Iterator<Serializable> it = bag.getIds();
		while (it.hasNext()) {
			Serializable id = it.next();
			Serializable value = bag.get(id);

			if (Meem.ID_SIZE.equals(id)) {
				if (!meem.getSize().equals(value)) {
					SimpleDimension fudgedSize = (SimpleDimension) value;
					Dimension size = new Dimension(fudgedSize.width, fudgedSize.height);
					meem.setSize(size);
					changed = true;
				}
			}
			else if (Meem.ID_LOCATION.equals(id)) {
				if (!meem.getLocation().equals(value)) {
					SimplePoint fudgedLocation = (SimplePoint) value;
					Point location = new Point(fudgedLocation.x, fudgedLocation.y);
					meem.setLocation(location);
					changed = true;
				}
			}
			else if (Meem.ID_COLLAPSED.equals(id)) {
				Boolean collapsed = (Boolean) value;
				if (meem.isCollapsed() != collapsed.booleanValue()) {
					meem.setCollapse(collapsed.booleanValue());
				}
			}
			else if (Meem.ID_VIEW_MODE.equals(id)) {
				if (!meem.getViewMode().equals(value)) {
					meem.setViewMode((ViewMode)value);
					changed = true;
				}
			}
			else if (Meem.ID_SHOWSYSTEMWEDGES.equals(id)) {
				Boolean show = (Boolean) value;
				meem.showSystemWedge(show.booleanValue());
			}
			else if (mergeChildOrder(id, value)) {
				changed = true;
			}
		}
		return changed;
	}
}
