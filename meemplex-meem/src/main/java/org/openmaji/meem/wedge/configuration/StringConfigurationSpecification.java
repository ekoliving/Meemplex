package org.openmaji.meem.wedge.configuration;

import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * Represents the specification of a configurable String property exposed
 * from a wedge.
 * 
 * @author Chris Kakris
 */
public class StringConfigurationSpecification extends ConfigurationSpecification
{
	private static final long serialVersionUID = -1178365040590887015L;

  /**
   * Creates an instance with a default initial value of "".
   * 
   * @param description A description of the configurable property
   */
  public StringConfigurationSpecification(String description)
  {
    super(description,String.class);
    setDefaultValue("");
  }

	/**
   * Creates an instance with a default initial value and
   * the maximum lifecycle state that the property can be changed in.
   * 
   * @param description A description of the configurable property
   * @param maxLifeCycleState The maximum lifecycle state that the property can be changed in
   */
  public StringConfigurationSpecification(String description, LifeCycleState maxLifeCycleState)
	{
		super(description,String.class,maxLifeCycleState);
		setDefaultValue("");
	}

  /**
   * This validate method always returns null.
   * 
   * @param value The value to validate
   * @return Allways returns null
   */
  public String validate(Object value)
  {
    return null;
  }
}
