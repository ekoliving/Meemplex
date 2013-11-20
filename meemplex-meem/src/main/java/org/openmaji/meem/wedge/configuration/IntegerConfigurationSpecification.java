package org.openmaji.meem.wedge.configuration;

import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * Represents the specification of a configurable Integer property exposed
 * from a wedge.
 * 
 * @author Chris Kakris
 */
public class IntegerConfigurationSpecification extends ConfigurationSpecification
{
	private static final long serialVersionUID = -1178365040590887015L;

  private int minimum = Integer.MIN_VALUE;
  private int maximum = Integer.MAX_VALUE;
  static private final String MINIMUM_MESSAGE = "Minimum allowed value is ";
  static private final String MAXIMUM_MESSAGE = "Maximum allowed value is ";

  /**
   * Creates an instance with a default value of 0 and an allowed range of
   * Integer.MIN_VALUE to Integer.MAX_VALUE.
   * 
   * @param description A description of the configurable property
   */
  public IntegerConfigurationSpecification(String description)
  {
    super(description, Integer.class);
		setDefaultValue(new Integer(0));
  }

	/**
   * Creates an instance with a default value of 0, an allowed range of
   * Integer.MIN_VALUE to Integer.MAX_VALUE, and allows the specification of
   * the maximum lifecycle state that the property can be changed in.
   * 
   * @param description A description of the configurable property
   * @param maxLifeCycleState The maximum lifecycle state that the property can be changed in
   */
  public IntegerConfigurationSpecification(String description, LifeCycleState maxLifeCycleState)
	{
		super(description,Integer.class,maxLifeCycleState);
		setDefaultValue(new Integer(0));
	}

  /**
   * Creates an instance with a default value of 0 and an allowed range of
   * 'minimum' to Integer.MAX_VALUE.
   * 
   * @param description A description of the configurable property
   * @param minimum The minimum value this parameter may take
   */
  public IntegerConfigurationSpecification(String description, int minimum)
  {
    this(description);
    this.minimum = minimum;
  }
    
  public IntegerConfigurationSpecification(String description, int minimum, LifeCycleState maxLifeCycleState)
  {
    super(description,Integer.class,maxLifeCycleState);
    this.minimum = minimum;
  }
    
  /**
   * Creates an instance with a default value of 0 and an allowed range of
   * 'minimum' to 'maximum'.
   * 
   * @param description
   * @param minimum The minimum value this parameter may take
   * @param maximum The maximum value this parameter may take
   */
  public IntegerConfigurationSpecification(String description, int minimum, int maximum)
  {
    this(description);
    this.minimum = minimum;
    this.maximum = maximum;
  }

  public IntegerConfigurationSpecification(String description, int minimum, int maximum, LifeCycleState maxLifeCycleState)
  {
    super(description,Integer.class,maxLifeCycleState);
    this.minimum = minimum;
    this.maximum = maximum;
  }

  /**
   * Validate the passed in value as being suitable when compared with the
   * allowed range.
   * 
   * @param value The value to validate
   * @return null if value is okay, a message string if it isn't.
   */
  public String validate(Integer value)
  {
    if (value.intValue() < minimum)
      return MINIMUM_MESSAGE + minimum;
    if (value.intValue() > maximum)
      return MAXIMUM_MESSAGE + maximum;
    return null;
  }
}
