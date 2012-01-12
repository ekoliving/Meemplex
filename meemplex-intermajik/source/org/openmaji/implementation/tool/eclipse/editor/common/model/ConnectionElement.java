package org.openmaji.implementation.tool.eclipse.editor.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.Bendpoint;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.*;


/**
 * @author Kin Wong
 * ConnectionElement represents the polyline model of a connection joined 
 * between two objects that implements IConnectable.
 */
abstract public class ConnectionElement extends Element implements IConnection {
	static public final String ID_SOURCE	= "source";
	static public final String ID_TARGET	= "target";
	static public final String ID_BENDPOINT	= "bendpoint";
	
	private List bendpoints;			// A List contains all the bend points
	private IConnectable source;	// The source IConnectable of this connection
	private IConnectable target;	// The taget IConnectable of this connection
	
	/**
	 * Gets the source connectable object.
	 * @return IConnectable The source connectable object.
	 */
	public IConnectable getSource() {
		return source;
	}
	
	/**
	 * Gets the target connectable object.
	 * @return IConnectable  The target connectable object.
	 */
	public IConnectable getTarget() {
		return target;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#buildPath(org.openmaji.implementation.tool.eclipse.editor.common.model.ElementPath)
	 */
	protected void buildPath(ElementPath path) {
		path.setConnection(true);
		path.pushTail(getId());
	}

	/**
	 * Gets the bendpoints as a list.
	 * @return List The bendpoints as a list.
	 */
	public List getBendpoints() {
		if(bendpoints == null) bendpoints = new ArrayList();
		return bendpoints;
	}
	
	/**
	 * Sets the source connectable element of this connection element.
	 * @param connectable The connectable element to be set as the source.
	 */
	public void setSource(IConnectable connectable) {
		Object oldSource = source;
		source = connectable;
		firePropertyChange(ID_SOURCE, oldSource, source);
	}
	
	/**
	 * Sets the target connectable element of this connection element.
	 * @param connectable The connectable element to be set as the target.
	 */
	public void setTarget(IConnectable connectable) {
		Object oldTarget = target;
		target = connectable;
		firePropertyChange(ID_TARGET, oldTarget, target);
	}
	
	/**
	 * Attaches both the source and the target connectable object to this connection.
	 */
	public void attach() {
		attachSource();
		attachTarget();	
	}
	
	/**
	 * Detaches both the source and the target connectable object from this connection.
	 */
	public void detach() {
		detachTarget();
		detachSource();
	}
	
	/**
	 * Attaches the source connectable object.
	 */
	public void attachSource(){
		if(getSource() == null) return;
		try {
			getSource().connectSource(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Attaches the target connectable object.
	 */
	public void attachTarget(){
		if(getTarget() == null) return;
		try {
			getTarget().connectTarget(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Detaches the source connectable object.
	 */
	public void detachSource() {
		if(source == null) return;
		source.disconnectSource(this);
		source = null;
	}
	
	/**
	 * Detaches the target connectable object.
	 */
	public void detachTarget() {
		if(target == null) return;
		target.disconnectTarget(this);
		target = null;
	}
	
	/**
	 * Inserts a bendpoint at the index.
	 * @param index The index where the bendpoint is inserted.
	 * @param point The bend point to be inserted.
	 */	
	public void insertBendpoint(int index, Bendpoint point) {
		getBendpoints().add(index, point);
		firePropertyChange(ID_BENDPOINT, null, null);
	}
	
	/**
	 * Removes a bendpoint at the index.
	 * @param index The index where the bendpoint is removed.
	 */
	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		firePropertyChange(ID_BENDPOINT, null, null);
	}
	
	/**
	 * Sets the bendpoint at the index.
	 * @param index The index where the bendpoint is replaced.
	 * @param point The new bendpoint to replace the one at index.
	 */
	public void setBendpoint(int index, Bendpoint point) {
		getBendpoints().set(index, point);
		firePropertyChange(ID_BENDPOINT, null, null);
	}
	
	/**
	 * Sets all the bendpoints in the connection.
	 * @param points A vector of bendpoints.
	 */
	public void setBendpoints(Vector points) {
		if(bendpoints == points) return;
		boolean changed = true;

		if(bendpoints != null) {
			if(bendpoints.size() == points.size()) {
				changed = false;
				for(int n = 0; n < bendpoints.size(); n++) {
					if(!bendpoints.get(n).equals(points.get(n))) {
						changed = true;
						break; 
					} 
				}
			}
		}
		
		if(changed) {
			bendpoints = (Vector)points.clone();
			firePropertyChange(ID_BENDPOINT, null, null);
		}
	}
}
