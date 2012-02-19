/*
 * @(#)Messages.java
 * Created on 17/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

// import org.eclipse.core.internal.dependencies.ResolutionDelta;


/**
 * <code>Messages</code> defines all the localizable string used in 
 * configuration editor.
 * <p>
 * @author Kin Wong
 */
public interface Messages {
	static class Resource{
		public static String getString(String key) {
			return key;
		}
	}

	public String DimensionPropertySource_Property_Width_Label = "Width";
	public String DimensionPropertySource_Property_Height_Label = "Height";
	public String LocationPropertySource_Property_X_Label = "X";
	public String LocationPropertySource_Property_Y_Label = "Y";
	public String PropertyDescriptor_MeemDiagram_Size = "Size";
	public String PropertyDescriptor_MeemDiagram_Location = "Location";
	public String PropertyDescriptor_MeemDiagram_ConnectionRouter = "Connection Router";
	public String PropertyDescriptor_MeemDiagram_Manual = "Manual";
	public String PropertyDescriptor_MeemDiagram_Manhattan = "Manhattan";

	public String MeemDiagram_LabelText = "Meem Diagram";

	public String PropertyDescriptor_MeemDefinition_ExportScope = "Export Scope";

	public String PropertyDescriptor_MeemDefinition_Version = "Definition Version";

	public String MeemXYLayoutEditPolicy_AddCommandLabelText = "Reparenting MeemElement";
	public String MeemXYLayoutEditPolicy_CreateCommandLabelText = "Creating MeemElement";
	
	public String MeemFlowLayoutEditPolicy_AddCommandLabelText = "Reparenting MeemElement";
	public String MeemFlowLayoutEditPolicy_CreateCommandLabelText = "Creating MeemElement";

	public String AddCommand_Label = "Add command";
	public String SetLocationCommand_Label_Location = "Move Object";
	public String SetLocationCommand_Label_Resize = "Resize Object";
	public String CreateCommand_Label = "Create Object";
	
	public String MeemContainerEditPolicy_OrphanCommandLabelText = "Orphan children";
	public String MeemElementEditPolicy_OrphanCommandLabelText = "Reparenting MeemDiagram";
	
	public String OrphanChildCommand_Label = "Orphan MeemDiagram";
	
	public String ConfigurationPlugin_Category_ControlGroup_Label = "Control Group";
	public String ConfigurationPlugin_Tool_SelectionTool_SelectionTool_Label = "Selection";
	public String ConfigurationPlugin_Tool_SelectionTool_SelectionTool_Description = "Selection Tool";
	
	public String ConfigurationPlugin_Tool_MarqueeSelectionTool_MarqueeSelectionTool_Label = "Marquee Selection";
	public String ConfigurationPlugin_Tool_MarqueeSelectionTool_MarqueeSelectionTool_Description = "Marqee Selection Tool";

	public String ConfigurationPlugin_Category_Templates_Label = "Templates";
	public String ConfigurationPlugin_Tool_CreationTool_ConfigurationLabel = "Blank MeemDefinition";
	public String ConfigurationPlugin_Tool_CreationTool_ConfigurationDescription = "Blank MeemDefinition";

	public String PropertyDescriptor_MeemWedge_Impl = "Implementation Class";
	public String PropertyDescriptor_MeemWedge_PersistentFields = "Persistent Fields";

	
	public String PropertyDescriptor_MeemFacet_Name = "Name";
	public String PropertyDescriptor_MeemFacet_IsInbound = "Is Inbound";
	public String PropertyDescriptor_MeemFacet_Type = "Type";
	
	public String DeleteCommand_Label = "Delete";
	
	//=== Properties ===
	// Meem Properties
	public String Property_Meem_Name_Label = Resource.getString("Name");
	public String Property_Meem_Name_Description = Resource.getString("The name of this meem in this worksheet.");
	public String Property_Meem_Size_Label = Resource.getString("Size");
	public String Property_Meem_Size_Description = Resource.getString("Change the size of the meem.");
	public String Property_Meem_Location_Label = Resource.getString("Location");
	public String Property_Meem_Location_Description = Resource.getString("Change the location of the meem.");
	public String Property_Meem_ViewMode_Label = Resource.getString("View Mode");
	public String Property_Meem_ViewMode_Description = Resource.getString("Change the view mode of the meem.");
	public String Property_Meem_MeemPath_Label = Resource.getString("Meem Path");
	public String Property_Meem_MeemPath_Description = Resource.getString("The meem path of the meem.");
	public String Property_Meem_MeemAttributes_Label = Resource.getString("Meem Attributes");
	public String Property_Meem_MeemAttributes_Description = Resource.getString("Other Meem Attributes");
	public String Property_Meem_Configuration_Label = Resource.getString("Configuration");
	public String Property_Meem_Configuration_Description = Resource.getString("Maji Configurable properties");
	public String Property_Meem_LifeCycleState_Label = Resource.getString("Life Cycle State");
	public String Property_Meem_LifeCycleState_Description = Resource.getString("The life cycle state of the meem.");
	public String Property_Meem_LifeCycleStateLimit_Label = Resource.getString("Life Cycle State Limit");
	public String Property_Meem_LifeCycleStateLimit_Description = Resource.getString("The life cycle state limit of the meem.");
	public String Property_Meem_LastMessage_Label = Resource.getString("Last Error Message");
	public String Property_Meem_LastMessage_Description = Resource.getString("The last error message associates with this meem.");
	public String Property_Meem_LastRejectedReason_Label = Resource.getString("Last Rejected Reason");
	public String Property_Meem_LastRejectedReason_Description = Resource.getString("The last rejected reason from configuring this meem.");

	// Wedge Properties
	public String Property_Wedge_PersistentFields_Label = Resource.getString("Persistent Fields");
	public String Property_Wedge_PersistentFields_Description = Resource.getString("The persistents field of this wedge.");
	public String Property_Wedge_ImplementationClassName_Label = Resource.getString("Implementation Class Name");
	public String Property_Wedge_ImplementationClassName_Description = Resource.getString("The Implementation class name of this wedge.");
	public String Property_Wedge_Identifier_Label = Resource.getString("Wedge identifier");
  public String Property_Wedge_Identifier_Description = Resource.getString("The identifer for this wedge");
  
	// Facet Properties
	public String Property_Facet_Identifier_Label = Resource.getString("Identifier");
	public String Property_Facet_Identifier_Description = Resource.getString("The Identifier of this facet.");
	public String Property_Facet_InterfaceName_Label = Resource.getString("Interface Name");
	public String Property_Facet_InterfaceName_Description = Resource.getString("The interface name of this facet.");
	
	// Outbound-Facet Properties
	public String Property_FacetOutbound_WedgePublicFieldName_Label = Resource.getString("Wedge Public Field Name");
	public String Property_FacetOutbound_WedgePublicFieldName_Description = Resource.getString("This Wedge Public Field Name of this outbound facet.");

	// Inbound-Facet Properties
	public String Property_FacetInbound_ContentRequired_Label = Resource.getString("Content Required");
	public String Property_FacetInbound_ContentRequired_Description = Resource.getString("Whether this inbound facet requires content.");
	
	// Dependency Properties
	public String Property_Dependency_Multiplicity_Label = Resource.getString("Multiplicity");
	public String Property_Dependency_Multiplicity_Description = Resource.getString("The Multiplicity of the dependency (SINGLE or MANY).");
	public String Property_Dependency_Strong_Label = Resource.getString("Strong");
	public String Property_Dependency_Strong_Description = Resource.getString("Whether the dependency is strong or weak.");
	public String Property_Dependency_Scope_Label = Resource.getString("Scope");
	public String Property_Dependency_Scope_Description = Resource.getString("The scope of the dependency.");
	public String Property_Dependency_TargetMeemPath_Label = Resource.getString("Target meem path");
	public String Property_Dependency_TargetMeemPath_Description = Resource.getString("The meem path of the depending meem.");
	public String Property_Dependency_TargetFacetIdentifier_Label = Resource.getString("Target Facet Identifier");
	public String Property_Dependency_TargetFacetIdentifier_Description = Resource.getString("The facet identifier of the target.");
	
	// Entry-Of Properties
	public String Property_EntryOf_EntryName_Label = Resource.getString("Entry Name");
	public String Property_EntryOf_EntryName_Description = Resource.getString("The name of the entry in the category.");

	public String Property_EntryOf_MeemPath_Label = Resource.getString("Meem Path");
	public String Property_EntryOf_MeemPath_Description = Resource.getString("The meem path of the meem in the category.");
	
	// Dependency Properties
	
	//=== Palette ===
	public String Palette_ToolsGroup_Label = Resource.getString("Tools");

	//=== Control Group ===
	public String Palette_ToolsGroup_Selection_Label = Resource.getString("Selection");
	public String Palette_ToolsGroup_Selection_Description = Resource.getString("This tool can be used to create Dependency/Entry-Of connection.");
	
	public String Palette_ToolsGroup_Marquee_Label = Resource.getString("Marquee");
	public String Palette_ToolsGroup_Marquee_Description = Resource.getString("This tool can be used to select multiple objects by drawing box around them.");

	public String Palette_ToolsGroup_ConnectionCreation_Label = Resource.getString("Connection Creation");
	public String Palette_ToolsGroup_ConnectionCreation_Description = Resource.getString("This tool can be used to create Dependency/Entry-Of connection.");

	//=== Parts Group ===
	public String Palette_Parts_Label = Resource.getString("Parts");

	public String Palette_Parts_Wedge_Label = Resource.getString("Wedge");
	public String Palette_Parts_Wedge_Description = Resource.getString("Creates a Wedge.");

	public String Palette_Parts_Inbound_Facet_Label = Resource.getString("Inbound Facet");
	public String Palette_Parts_Inbound_Facet_Description = Resource.getString("Creates an Inbound Facet.");

	public String Palette_Parts_Outbound_Facet_Label = Resource.getString("Outbound Facet");
	public String Palette_Parts_Outbound_Facet_Description = Resource.getString("Creates an Outbound Facet.");

	//=== Component Group ===
	public String Palette_Components_Label = Resource.getString("Components");

	public String Palette_Components_Meem_Label = Resource.getString("Meem");
	public String Palette_Components_Meem_Description = Resource.getString("Creates a Meem.");
	
	public String Palette_Components_MeemPlex_Label = Resource.getString("MeemPlex");
	public String Palette_Components_MeemPlex_Description = Resource.getString("Creates a MeemPlex.");
	
	public String Palette_Components_Category_Label = Resource.getString("Category");
	public String Palette_Components_Category_Description = Resource.getString("Creates a Category.");

	public String Palette_Components_LCM_Label = Resource.getString("LCM");
	public String Palette_Components_LCM_Description = Resource.getString("Creates a Live-Cycle-Manager(LCM).");

	//Commands
	public String AddMeemCommand_Label = Resource.getString("Add Meem");
	public String RemoveMeemCommand_Label = Resource.getString("Remove Meem");
	public String AddDependencyCommand_Label = Resource.getString("Add Dependency");
	public String RemoveDependencyCommand_Label = Resource.getString("Remove Dependency");
	
	// Actions
	public String AddMeemAction_ActionLabelText = Resource.getString("Add Meem");
	public String AddMeemAction_ActionToolTipText = Resource.getString("Add new meem to configuration.");
	public String WedgeAction_Add_ActionLabelText = Resource.getString("Add Wedge");
	public String WedgeAction_Delete_actionLabelText = Resource.getString("Delete Wedge");

	public String AddDependencyAction_ActionLabelText = Resource.getString("Add Dependency");
	public String AddDependencyAction_ActionToolTipText = Resource.getString("Add new dependency to facets.");
	public String RemoveDependencyAction_ActionLabelText = Resource.getString("Remove Dependency");
	public String RemoveDependencyAction_ActionToolTipText = Resource.getString("Remove dependency from Facets.");
	
	// Sort Actions
	public static String SortAction_Wedge_Ascending_Label = Resource.getString("Sort Wedge Name Ascending");
	public static String SortAction_Wedge_Ascending_Tooltip = Resource.getString("Sort Wedge Name Ascending");
	public static String SortAction_Wedge_Descending_Label = Resource.getString("Sort Wedge Name Descending");
	public static String SortAction_Wedge_Descending_Tooltip = Resource.getString("Sort Wedge Name Descending");
	public static String SortAction_Facet_Ascending_Label = Resource.getString("Sort Facet Name Ascending");
	public static String SortAction_Facet_Ascending_Tooltip = Resource.getString("Sort Facet Name Ascending");
	public static String SortAction_Facet_Descending_Label = Resource.getString("Sort Facet Name Descending");
	public static String SortAction_Facet_Descending_Tooltip = Resource.getString("Sort Facet Name Descending");
	
	// Router Actions	
	public String RouterAction_Standard_Label = "Standard Router";
	public String RouterAction_Standard_ToolTip = "Standard Connection Router";
	public String RouterAction_Manhattan_Label = "Manhattan Router";
	public String RouterAction_Manhattan_ToolTip = "Manhattan Connection router";
	
	// Dependency Actions
	public String DependencyAction_Weak_Label = "Weak Dependency";
	public String DependencyAction_Weak_ToolTip = "Make the selected dependencies weak dependencies.";
	public String DependencyAction_Weak_CommandLabel = "making dependency weak";
	public String DependencyAction_Weak_CommandsLabel = "making dependencies weak";

	public String DependencyAction_Reverse_Label = "Reverse Source and Target Facet";
	public String DependencyAction_Reverse_ToolTip = "Reverse the source and the target facet of the selected dependencies.";
	public String DependencyAction_Reverse_CommandLabel = "reversing dependency source and target facet";
	public String DependencyAction_Reverse_CommandsLabel = "reversing dependencies source and target facet";
	
	//Meem Actions
	public String MeemAction_Remove_Label = "Remove Reference";
	public String MeemAction_Remove_Description = "Remove the selected Meem from this worksheet.";

	public String MeemAction_Destroy_Label = "Destroy Original";
	public String MeemAction_Destroy_Description = "Destroy the selected Meem from the Meem-store permanently.";
		
	public String MeemAction_ShowSystemWedges_Label = "Show System Wedges";
	public String MeemAction_ShowSystemWedges_Description = "Shows and hides system defined Wedges of selected Meem.";
	
	// LifeCycle Actions
	public String LifeCycleAction_Dormant_Label = "Make DORMANT";
	public String LifeCycleAction_Loaded_Label = "Make LOADED";
	public String LifeCycleAction_Ready_Label = "Make READY";
	public String LifeCycleAction_AllDormant_Label = "Make All DORMANT";
	public String LifeCycleAction_AllLoaded_Label = "Make All LOADED";
	public String LifeCycleAction_AllReady_Label = "Make All READY";
	
	// View Mode Actions
	public String ViewModeDetailed_Label = Resource.getString("Detailed View");
	public String ViewModeDetailed_Tooltip = Resource.getString("Detailed View");
	
	public String ViewModeIconic_Label = Resource.getString("Iconic View");
	public String ViewModeIconic_Tooltip = Resource.getString("Iconic View");
	
	public String ViewModeDevice_Label = Resource.getString("Device View");
	public String ViewModeDevice_Tooltip = Resource.getString("Device View");
	
	// Layout Actions
	public String LayoutAction_LayoutStandard_Label = Resource.getString("Standard Layout");
	public String LayoutAction_LayoutStandard_Description = Resource.getString("Apply the Standard Layout");
	
	// Filter PropertySheet Page
	public String FilterPropertySheet_Configuration_Label = Resource.getString("Configuration");
	public String FilterPropertySheet_Configuration_Tooltip = Resource.getString("Show Configuration Properties");
	public String FilterPropertySheet_Configuration_Description = Resource.getString("Shows configuration related properties.");

	public String FilterPropertySheet_Definition_Label = Resource.getString("Definition");
	public String FilterPropertySheet_Definition_Tooltip = Resource.getString("Show Definition Properties");
	public String FilterPropertySheet_Definition_Description = Resource.getString("Shows definition related properties.");

	public String FilterPropertySheet_Presentation_Label = Resource.getString("Presentation");
	public String FilterPropertySheet_Presentation_Tooltip = Resource.getString("Show Presentation Properties");
	public String FilterPropertySheet_Presentation_Description = Resource.getString("Shows Presentation related properties.");

	public String FilterPropertySheet_ShowAll_Label = Resource.getString("Show All");
	public String FilterPropertySheet_ShowAll_Tooltip = Resource.getString("Show All Properties");
	public String FilterPropertySheet_ShowAll_Description = Resource.getString("Shows all properties.");

}
