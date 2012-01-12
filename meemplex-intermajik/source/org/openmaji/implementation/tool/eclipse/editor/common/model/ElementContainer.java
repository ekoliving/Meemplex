package org.openmaji.implementation.tool.eclipse.editor.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.ISortableItemContainer;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
abstract public class ElementContainer extends BoundsObject 
	implements IModelContainer, ISortableItemContainer {
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer#addChild(org.openmaji.implementation.tool.eclipse.editor.common.model.Element)
	 */
	public static final String ID_CHILDREN = "children";
	public static final String ID_CHILD_ORDERS = "child orders";
	public static final String ID_REFRESH_CHILD = "refresh child";
	
	protected Hashtable<Serializable, Element> elementTable = new Hashtable<Serializable, Element>();
	protected ArrayList<Element> elements = new ArrayList<Element>();	
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer#addChild(int, java.lang.Object)
	 */	
	public boolean addChild(int index, Object child) {
		Assert.isNotNull(child);
		Assert.isTrue(child instanceof Element);
		
		Element element = (Element)child;
		Assert.isNotNull(element.getId());
		
		if(elementTable.containsKey(element.getId())) {
			return false;
		} 
		
		ElementContainer exParent = element.getParent();
		if(exParent != null) exParent.removeChild(child);	
		
		if(index < 0){
			index = elements.size();
		}
		elements.add(index, element);
		elementTable.put(element.getId(), element);
		element.setParent(this);
		firePropertyChange(ID_CHILDREN, null, element.getId());
		return true;
	}
	
	/**
	 * Adds the child to the end of the child list.
	 * @param child The child to be added.
	 * @return boolean true is the child has been added, false otherwise.
	 */
	public boolean addChild(Object child) {
		return addChild(-1, child);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer#removeChild(java.lang.Object)
	 */	
	public boolean removeChild(Object child) {
		Assert.isNotNull(child);
		Assert.isTrue(child instanceof Element);
		
		Element element = (Element)child;
		Assert.isNotNull(element.getId());
		Assert.isTrue(element.getParent() == this);
		
		if(!elementTable.containsKey(element.getId())) return false;
		elementTable.remove(element.getId());
		elements.remove(element);
		element.setParent(null);
		firePropertyChange(ID_CHILDREN, element, null);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer#moveChild(int, int)
	 */
	public boolean moveChild(int fromIndex, int toIndex) {
		int size = elements.size();
		if(toIndex < 0) toIndex = size - 1;
		//System.out.println("Size " + size + ", From " + fromIndex + ", To " + toIndex);
		if(	(fromIndex >= size) || (toIndex >= size)) return false;

		if(fromIndex == toIndex) {
			return false;
		}
		Element object = elements.remove(fromIndex);
		if(toIndex > elements.size()){
			toIndex = elements.size();
		}
		elements.add(toIndex, object);
		firePropertyChange(ID_CHILD_ORDERS, null, null);
		return true;
	}
	
	/**
	 * Refreshes the child from the element container.
	 * @param child
	 */
	public void refreshChild(Object child) {
		Element element = (Element)child;
		if(!elementTable.containsKey(element.getId())) return;
		
//		elementTable.remove(element.getId());
		int index = elements.indexOf(element);
		elements.remove(element);
		element.setParent(null);
		
		firePropertyChange(ID_CHILDREN, element, null);
		elements.add(index, element);
//		elementTable.put(element.getId(), element);
		element.setParent(this);
		firePropertyChange(ID_CHILDREN, null, element);
		//firePropertyChange(ID_REFRESH_CHILD, null, element);
	}
	
	/**
	 * Finds the element by ID.
	 * @param id the ID that uniquely identifies the child element in the 
	 * element container.
	 * @return Element The elements that was identified by the ID, null if such 
	 * element does not exist.
	 */
	public Element findElement(Object id) {
		return (Element)elementTable.get(id);
	}
	
	/**
	 * Gets all the child elements as an array.
	 * @return Element[] The array that contains all the child elements.
	 */
	public Element[] getElements() {
		return (Element[])getChildren().toArray(new Element[elementTable.size()]);
	}
	
	public void setOrderByIds(Object[] id) {
		
	}
	/**
	 * Clears all the child elements in the element containers.
	 */
	public void clear() {
		elements.clear();
		elementTable.clear();
	}
	
	/**
	 * Checks if the element container contains the child element.
	 * @param child The child element to be checked.
	 * @return boolean true if the element container contains the child element, 
	 * false otherwise.
	 */
	public boolean contains(Element child) {
		return contains(child.getId());
	}
	
	/**
	 * Checks if the element container contains the ID that identifies a child 
	 * element.
	 * @param id The identifier which identifies the child element.
	 * @return boolean true if the element container contains a child element 
	 * with the ID, false otherwise.
	 */
	public boolean contains(Object id) {
		return elementTable.containsKey(id);
	}
	
	public int getElementSize() {
		return getChildren().size();
	}
	
	/**
	 * Overridden to parse the element path to include the child elements.
	 */
	public Element parsePath(ElementPath path) {
		Element element = super.parsePath(path);
		if(element == null) return null;	// Invalid path even at this level
		if(path.getDepth() == 0) return element;	// No need to go further
		
		Object childId = path.getHead();
		element = findElement(childId);
		if(element == null) return null;
		return element.parsePath(path);
	}
	/**
	 * Gets all the children.
	 * @return A list containing all the children.
	 */
	public List<Element> getChildren() {
		return elements;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer#findChildIndex(java.lang.Object)
	 */
	public int childIndexOf(Object child) {
		return elements.indexOf(child);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer#isValidNewChild(java.lang.Object)
	 */
	public boolean isValidNewChild(Object child) {
		if(!(child instanceof Element)) return false;
		Element element = (Element)child;
		if(elementTable.containsKey(element.getId())) return false;
		return true;
	}

	/**
	 * Overridden to return a clone of the child elements.
	 * @return List A list contains all the child elements.
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.ISortableItemContainer#getSortableItems()
	 */
	public List getSortableItems() {
		return (List)elements.clone();
	}

	/**
	 * Overridden to replace the order of child elements to that in the 
	 * passed-in collection.
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.ISortableItemContainer#setSortableItems(java.util.Collection)
	 */
	public void setSortableItems(Collection items) {
		ArrayList<Element> sortedElements = new ArrayList<Element>((Collection<Element>)items);
		sortedElements.retainAll(elements);
		if(sortedElements.size() != elements.size()) {
			return;
		}

		for(int i = 0; i < elements.size(); i++) {
			if(!elements.get(i).equals(sortedElements.get(i))) {
				elements = sortedElements;
				firePropertyChange(ID_CHILD_ORDERS, null, null);
			}
		}
	}
}
