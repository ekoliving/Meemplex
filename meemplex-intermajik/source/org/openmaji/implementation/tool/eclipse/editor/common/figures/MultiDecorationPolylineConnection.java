/*
 * Created on 6/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.draw2d.ArrowLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.swt.graphics.Color;

/**
 * @author Kin Wong
 *
 * Connection that supports multiple source and target decoration.
 * 
 */
public class MultiDecorationPolylineConnection extends PolylineConnection {
	private ArrayList startDecorations;	// The array to store all start decoration
	private ArrayList endDecorations; // the array to store all end decoration
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.draw2d.PolylineConnection#setSourceDecoration(org.eclipse.draw2d.RotatableDecoration)
	 */
	public void setSourceDecoration(RotatableDecoration dec){
		if (getSourceDecoration() == null) {
			super.setSourceDecoration(dec);
			return;
		}
		if(dec == null) return;
		if(startDecorations == null) startDecorations = new ArrayList(); 
		startDecorations.add(dec);
		add(dec, new ArrowLocator(this, ConnectionLocator.SOURCE));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.PolylineConnection#setTargetDecoration(org.eclipse.draw2d.RotatableDecoration)
	 */
	public void setTargetDecoration(RotatableDecoration dec) {
		if (getTargetDecoration() == null) {
			super.setTargetDecoration(dec);
			return;
		}
		if(dec == null) return;
		if(endDecorations == null) endDecorations = new ArrayList(); 
		endDecorations.add(dec);
		add(dec, new ArrowLocator(this, ConnectionLocator.TARGET));
	}
	
	/**
	 * Removes all decorations from this polyline connection.
	 */
	public void removeAllDecorations() {
		removeAllSourceDecorations();
		removeAllTargetDecorations();
	}
	
	/**
	 * Removes all source decorations from this polyline connection.
	 */	
	public void removeAllSourceDecorations() {
		if(startDecorations == null) return;
		Iterator it = startDecorations.iterator();
		while(it.hasNext()) {
			RotatableDecoration dec = (RotatableDecoration)it.next();
			remove(dec);
		}
		startDecorations = null;
		super.setSourceDecoration(null);
	}
	
	/**
	 * Removes all target decorations from this polyline connection.
	 */
	public void removeAllTargetDecorations() {
		if(endDecorations == null) return;
		Iterator it = endDecorations.iterator();
		while(it.hasNext()) {
			RotatableDecoration dec = (RotatableDecoration)it.next();
			remove(dec);
		}
		endDecorations = null;
		super.setTargetDecoration(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color bg) {
		if(startDecorations != null) {
			for(int i = 0; i < startDecorations.size(); i++)
			((RotatableDecoration)startDecorations.get(i)).setBackgroundColor(bg);
		}
		if(endDecorations != null) {
			for(int i = 0; i < endDecorations.size(); i++)
			((RotatableDecoration)endDecorations.get(i)).setBackgroundColor(bg);
		}
		super.setBackgroundColor(bg);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setForegroundColor(Color fg) {
		if(startDecorations != null) {
			for(int i = 0; i < startDecorations.size(); i++)
			((RotatableDecoration)startDecorations.get(i)).setForegroundColor(fg);
		}
		if(endDecorations != null) {
			for(int i = 0; i < endDecorations.size(); i++)
			((RotatableDecoration)endDecorations.get(i)).setForegroundColor(fg);
		}
		super.setForegroundColor(fg);
	}
}
