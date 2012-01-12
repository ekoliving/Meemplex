package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import org.eclipse.gef.commands.Command;

/**
 * <code>ConnectionCommand</code> is a generic command that works with 
 * <code>IConnectable</code> and <code>IConnection</code> to establish a
 * connection with source and target connectable.
 * @author Kin Wong
 */
public class ConnectionCommand extends Command {
	protected IConnectable source;
	protected IConnectable oldSource;
	protected IConnectable target; 
	protected IConnectable oldTarget;
	protected IConnection connection;
	
	/**
	 * Sets the connection element.
	 * @param connection The connection object to be associated with this 
	 * command.
	 */
	public void setConnection(IConnection connection) {
		this.connection = connection;
		this.oldSource = connection.getSource();
		this.oldTarget = connection.getTarget();
	}

	/**
	 * Sets the source connectable.
	 * @param source The connectable used as the source.
	 */	
	public void setSource(IConnectable source) {
		this.source = source;
	}

	/**
	 * Sets the target connectable.
	 * @param target The connectable used as the target.
	 */
	public void setTarget(IConnectable target) {
		this.target = target;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		//System.out.println("ConnectionCommand.execute()");
		if (source != null){
			//System.out.println("Detach/Attach Source");
			connection.detachSource();
			connection.setSource(source);
			connection.attachSource();
		}
		if (target != null) {
			//System.out.println("Detach/Attach target");
			connection.detachTarget();
			connection.setTarget(target);
			connection.attachTarget();
		}
		
		if (source == null && target == null){
			connection.detachSource();
			connection.detachTarget();
			connection.setTarget(null);
			connection.setSource(null);
		}
	}

	/**
	 * Gets the source connectable.
	 * @return IConnectable The source connectable.
	 */
	public IConnectable getSource() {
		return source;
	}

	/**
	 * Gets the target connectable.
	 * @return IConnectable The target connectable.
	 */
	public IConnectable getTarget() {
		return target;
	}

	/**
	 * Gets the connection object.
	 * @return IConnectable The connection object associates with this command.
	 */
	public IConnection getConnection() {
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() { 
		execute(); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		source = connection.getSource();
		target = connection.getTarget();

		connection.detachSource();
		connection.detachTarget();

		connection.setSource(oldSource);
		connection.setTarget(oldTarget);

		connection.attachSource();
		connection.attachTarget();
	}
}
