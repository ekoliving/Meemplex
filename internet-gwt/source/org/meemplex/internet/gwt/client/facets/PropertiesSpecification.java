package org.meemplex.internet.gwt.client.facets;

import java.io.Serializable;

import org.meemplex.internet.gwt.shared.LifeCycleState;

public interface PropertiesSpecification {
	
	/**
	 * 
	 * @param oldSpecifications
	 * @param newSpecifications
	 */
	public void specificationChanged(
			PropertySpecification[] oldSpecifications, 
			PropertySpecification[] newSpecifications
		);

	/**
	 * Announce that a change has been accepted.
	 * 
	 * @param id
	 *            the identifier associated with the property that has changed.
	 * @param value
	 *            the value the property now has.
	 */
	public void valueAccepted(String propertyName, Serializable value);

	/**
	 * Announce that an attempted change has been rejected.
	 * <p>
	 * Note: toString() on the reason object should always produce a sensible error message.
	 * 
	 * @param id
	 *            the identifier associated with the property that the change was attempted on.
	 * @param value
	 *            the value that was attempted to be assigned to the property.
	 * @param reason
	 *            the reason the change was rejected
	 */
	public void valueRejected(String propertyName, Serializable value, String reason);

	/**
	 * 
	 * @author stormboy
	 *
	 */
	public class PropertySpecification {
		/**
		 * Property name. wedgId.propName
		 */
		private String propertyName;
		
		/**
		 * Description of the property
		 */
		private String description;
		
		/**
		 * Facet class
		 */
		private String type;
		
		/**
		 * 
		 */
		private Serializable defaultValue;
		
		/**
		 * 
		 */
		private LifeCycleState maxLifeCycleState;

		public void setPropertyName(String propertyName) {
	        this.propertyName = propertyName;
        }

		public String getPropertyName() {
	        return propertyName;
        }

		public void setDescription(String description) {
	        this.description = description;
        }

		public String getDescription() {
	        return description;
        }

		public void setType(String type) {
	        this.type = type;
        }

		public String getType() {
	        return type;
        }

		public void setDefaultValue(Serializable defaultValue) {
	        this.defaultValue = defaultValue;
        }

		public Serializable getDefaultValue() {
	        return defaultValue;
        }

		public void setMaxLifeCycleState(LifeCycleState maxLifeCycleState) {
	        this.maxLifeCycleState = maxLifeCycleState;
        }

		public LifeCycleState getMaxLifeCycleState() {
	        return maxLifeCycleState;
        }
	}
	
	
}
