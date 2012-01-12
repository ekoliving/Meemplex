/*
 * @(#)DependencyPropertySource.java
 * Created on 24/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.BooleanComboBoxPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.ObjectComboBoxPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.properties.PropertySource;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.Scope;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>DependencyPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyPropertySource extends PropertySource {
	static Object[] scopes = new Object[] {
			Scope.DISTRIBUTED,
			Scope.FEDERATED,
			Scope.LOCAL,
			Scope.MEEMPLEX
	};
	
	static public String ID_MULTIPLICITY = "multiplicity";
	static public String ID_STRENGTH = "strength";
	static public String ID_SCOPE = "scope";
	static public String ID_TARGET_MEEM_PATH = "target meem path";
	static public String ID_TARGET_FACET_IDENTIFIER = "target facet identifier";

	static private PropertyDescriptor[] modifiableDescriptors;
	static private PropertyDescriptor[] readOnlyDescriptors;
	static {
		modifiableDescriptors = new PropertyDescriptor[] {
			new PropertyDescriptor(ID_MULTIPLICITY, Messages.Property_Dependency_Multiplicity_Label),
			new ObjectComboBoxPropertyDescriptor(ID_SCOPE, Messages.Property_Dependency_Scope_Label, scopes),
			new BooleanComboBoxPropertyDescriptor(ID_STRENGTH, Messages.Property_Dependency_Strong_Label),
			new PropertyDescriptor(ID_TARGET_MEEM_PATH, Messages.Property_Dependency_TargetMeemPath_Label),
			new TextPropertyDescriptor(ID_TARGET_FACET_IDENTIFIER, Messages.Property_Dependency_TargetFacetIdentifier_Label)
		};

		readOnlyDescriptors = new PropertyDescriptor[] {
			new PropertyDescriptor(ID_MULTIPLICITY, Messages.Property_Dependency_Multiplicity_Label),
			new PropertyDescriptor(ID_SCOPE, Messages.Property_Dependency_Scope_Label),
			new PropertyDescriptor(ID_STRENGTH, Messages.Property_Dependency_Strong_Label),
			new PropertyDescriptor(ID_TARGET_MEEM_PATH, Messages.Property_Dependency_TargetMeemPath_Label),
			new PropertyDescriptor(ID_TARGET_FACET_IDENTIFIER, Messages.Property_Dependency_TargetFacetIdentifier_Label)
		};
	}
	
	private Dependency dependency;

	/**
	 * Constructs an instance of <code>DependencyPropertySource</code>.
	 * <p>
	 * @param dependency
	 */
	public DependencyPropertySource(Dependency dependency, boolean readOnly) {
		super(readOnly);
		this.dependency = dependency;
	}
	
	protected Dependency getDependency() {
		return dependency;	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(isReadOnly())	return readOnlyDescriptors;
		else							return modifiableDescriptors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		DependencyAttribute attribute = getDependency().getAttribute();
		if(attribute == null) return null;
		
		if(id.equals(ID_MULTIPLICITY)) {
			DependencyType type = attribute.getDependencyType();
			boolean single = (type.equals(DependencyType.STRONG) || type.equals(DependencyType.WEAK));
			return (single)? "SINGLE" : "MANY";
		}
		else
		if(id.equals(ID_STRENGTH)) {
			DependencyType type = attribute.getDependencyType();
			return new Boolean(	type.equals(DependencyType.STRONG) || 
													type.equals(DependencyType.STRONG_MANY));
		}
		else
		if(id.equals(ID_SCOPE)) {
			return attribute.getScope();
		}
		else
		if(id.equals(ID_TARGET_MEEM_PATH)) {
			return attribute.getMeemPath().toString();
		}
		else
		if(id.equals(ID_TARGET_FACET_IDENTIFIER)) {
			return attribute.getFacetIdentifier();
		}
		else
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.properties.PropertySource#setModifiableValue(java.lang.Object, java.lang.Object)
	 */
	protected void setModifiableValue(Object id, Object value) {
		if(getDependency().getSourceFacet() == null) return;
		MetaMeem metaMeem = getDependency().getSourceFacet().getMeem().getProxy().getMetaMeem();
		if(metaMeem == null) return;
		
		DependencyAttribute attribute = getDependency().getAttribute();
		if(attribute == null) return;

		DependencyAttribute newAttribute = (DependencyAttribute)attribute.clone();

		if(id.equals(ID_STRENGTH)) {
			Boolean strong = (Boolean)value;
			newAttribute.setDependencyType(evaluateType(attribute, strong.booleanValue()));
			if(!attribute.contentEquals(newAttribute)) {
				metaMeem.updateDependencyAttribute(newAttribute);
			}
		}
		else
		if(id.equals(ID_SCOPE)) {
			Scope scope = (Scope)value;
			newAttribute.setScope(scope);
			if(!attribute.contentEquals(newAttribute)) {
				metaMeem.updateDependencyAttribute(newAttribute);
			}
		}
		else
		if(id.equals(ID_TARGET_FACET_IDENTIFIER)) {
			String identifier = (String)value;
			newAttribute.setFacetIdentifier(identifier);
			if(!attribute.contentEquals(newAttribute)) {
				metaMeem.updateDependencyAttribute(newAttribute);
			}
		}
	}

	DependencyType evaluateType(DependencyAttribute attribute, boolean strength) {
		DependencyType oldType = attribute.getDependencyType();
		if(oldType.equals(DependencyType.WEAK) || oldType.equals(DependencyType.WEAK_MANY)) {
			if(strength)
				// Strong now
			return (oldType.equals(DependencyType.WEAK))? 
						DependencyType.STRONG : DependencyType.STRONG_MANY;
			else
			return oldType;
		}
		else {
			if(strength) return oldType;
			else
			return (oldType.equals(DependencyType.STRONG))? 
						DependencyType.WEAK : DependencyType.WEAK_MANY;
		}
	}
}
