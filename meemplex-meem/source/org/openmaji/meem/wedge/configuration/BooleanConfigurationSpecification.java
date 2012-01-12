package org.openmaji.meem.wedge.configuration;

import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * Represents the specification of a configurable Boolean property exposed
 * from a wedge.
 * 
 * @author Chris Kakris
 */

public class BooleanConfigurationSpecification extends ConfigurationSpecification
{
	private static final long serialVersionUID = -1178365040590887015L;

  /**
   * Creates an instance with a default initial value of 'false'.
   * 
   * @param description A description of the configurable property
   */
  public BooleanConfigurationSpecification(String description)
  {
    super(description,Boolean.class);
		setDefaultValue(new Boolean(false));
  }

	/**
   * Creates an instance with a default initial value of 'false' and
   * the maximum lifecycle state that the property can be changed in.
   * 
   * @param description A description of the configurable property
   * @param maxLifeCycleState The maximum lifecycle state that the property can be changed in
   */
  public BooleanConfigurationSpecification(String description, LifeCycleState maxLifeCycleState)
	{
		super(description,Boolean.class,maxLifeCycleState);
		setDefaultValue(new Boolean(false));
	}

	/**
   * This validate method always returns null.
   * 
   * @param value The value to validate
   * @return Allways returns null
   */
  public String validate(boolean value)
  {
    return null;
	}
}
