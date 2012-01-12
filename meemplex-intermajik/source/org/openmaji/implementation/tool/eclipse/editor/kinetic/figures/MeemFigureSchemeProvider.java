/*
 * Created on 1/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import java.io.Serializable;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.common.util.ColorTransformer;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.CursorDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.util.PlatformHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.FacetEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.WedgeEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MeemFigureSchemeProvider 
	implements Cloneable, Serializable, IFigureSchemeProvider {
		
	private static final long serialVersionUID = 6424227717462161145L;

	static private IFigureSchemeProvider instance;
	
	static protected final String PRIMARY_FONT_NAME = "Tahoma";
	static protected final String SECONDARY_FONT_NAME = "Tahoma";
	
	private MeemScheme absentScheme;	// For LifeCycleState.ABSENT
	private MeemScheme dormantScheme;	// For LifeCycleState.DORMANT
	private MeemScheme loadedScheme;	// For LifeCycleState.LOADED
	//private MeemScheme pendingScheme;	// For LifeCycleState.PENDING
	private MeemScheme readyScheme;		// For LifeCycleState.READY

	static public IFigureSchemeProvider getInstance() {
		if(instance == null) instance = new MeemFigureSchemeProvider();
		return instance;
	}
	/**
	 * Constructs an instance of DefaultFigureSchemeProvider.
	 */
	protected MeemFigureSchemeProvider() {
		setDefault();
	}
	/**
	 * Sets all the figure scheme to default.
	 */
	public void setDefault() {
		readyScheme = createDefaultReadyScheme();
		loadedScheme = createDefaultLoadedScheme();
		dormantScheme = createDefaultDormantScheme();
		//pendingScheme = createDefaultPendingScheme();
		absentScheme = createDefaultAbsentScheme();
	}
	
	protected MeemScheme createDefaultReadyScheme() {
		MeemScheme ready = new MeemScheme();
		// Meem 
		Color foreground = new Color(Display.getDefault(), ColorConstants.white.getRGB());
		Color background = new Color(Display.getDefault(), 20, 141, 198);
		Color description = new Color(Display.getDefault(), ColorConstants.yellow.getRGB());

		FigureScheme scheme = ready.getMeemScheme();
		scheme.setBorder(ColorConstants.black, 3);
		scheme.setColors(ColorConstants.white, background);
		scheme.setCaptionFont(SECONDARY_FONT_NAME, 10, SWT.BOLD, foreground, background);
		scheme.setDescriptionFont(PRIMARY_FONT_NAME, 8, SWT.NORMAL, description, background);
		
		// Wedge
		scheme = ready.getWedgeScheme();
		scheme.setBorder(background, 0);
		background = new Color(Display.getDefault(), 65, 177, 245);
		Color darkerCaption = new Color(Display.getDefault(), 0, 0, 51);
		scheme.setColors(ColorConstants.black, background);
		scheme.setCaptionFont(PRIMARY_FONT_NAME, PlatformHelper.getMinFontHeight(), SWT.BOLD|SWT.ITALIC, darkerCaption, background);
		scheme.setDescriptionFont(PRIMARY_FONT_NAME, 8, SWT.NORMAL, description, background);

		// Facet
		scheme = ready.getFacetScheme();
		scheme.setBorder(background, 0);
		background = new Color(Display.getDefault(), 95, 214, 255);
//		Color darkerCaption = new Color(Display.getDefault(), 0, 0, 51);
		scheme.setColors(ColorConstants.white, background);
		scheme.setCaptionFont(PRIMARY_FONT_NAME, PlatformHelper.getMinFontHeight(), SWT.NORMAL, darkerCaption, background);
		scheme.setDescriptionFont(PRIMARY_FONT_NAME, 8, SWT.NORMAL, description, background);
		return ready;
	}
	
	protected MeemScheme createDefaultLoadedScheme() {
		MeemScheme loaded = readyScheme.createBrighten();
		return loaded;
	}
	
	protected MeemScheme createDefaultDormantScheme() {
		MeemScheme dormant = createDefaultReadyScheme();
		Color foreground = ColorConstants.yellow;
		Color background = ColorConstants.orange;
		Color border = ColorTransformer.darken(background);
		dormant.getMeemScheme().setColors(foreground, background);
		dormant.getMeemScheme().setCursor(CursorDescriptor.APPSTARTING);
		dormant.getWedgeScheme().setColors(foreground, background);
		dormant.getWedgeScheme().setBorderColor(border);
		dormant.getWedgeScheme().setCursor(CursorDescriptor.APPSTARTING);
		dormant.getFacetScheme().setColors(foreground,background);
		dormant.getFacetScheme().setBorderColor(border);
		dormant.getFacetScheme().setCursor(CursorDescriptor.APPSTARTING);
		return dormant;
	}
	
	protected MeemScheme createDefaultPendingScheme() {
		MeemScheme pending = createDefaultReadyScheme();
		Color foreground = ColorConstants.yellow;
		Color background = ColorConstants.orange;
		Color border = ColorTransformer.darken(background);
		pending.getMeemScheme().setColors(foreground, background);
		pending.getMeemScheme().setCursor(CursorDescriptor.APPSTARTING);
		
		pending.getWedgeScheme().setColors(foreground, background);
		pending.getWedgeScheme().setBorderColor(border);
		pending.getWedgeScheme().setCursor(CursorDescriptor.APPSTARTING);
		
		pending.getFacetScheme().setColors(foreground,background);
		pending.getFacetScheme().setBorderColor(border);
		pending.getFacetScheme().setCursor(CursorDescriptor.APPSTARTING);

		return pending;
	}
	
	protected MeemScheme createDefaultAbsentScheme() {
		MeemScheme absent = createDefaultReadyScheme();
		Color background = ColorConstants.red;
		Color foreground = ColorTransformer.darken(background);
		absent.getMeemScheme().setColors(foreground,background);
		absent.getMeemScheme().setCursor(CursorDescriptor.APPSTARTING);
		absent.getWedgeScheme().setColors(foreground,background);
		absent.getWedgeScheme().setBorderColor(foreground);
		absent.getWedgeScheme().setCursor(CursorDescriptor.APPSTARTING);
		absent.getFacetScheme().setColors(foreground,background);
		absent.getFacetScheme().setBorderColor(foreground);
		absent.getFacetScheme().setCursor(CursorDescriptor.APPSTARTING);
		return absent;
	}

	/**
	 * Overridden to 
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider#getScheme(org.eclipse.gef.EditPart)
	 */
	public FigureScheme getScheme(EditPart editPart) {
		MeemClientProxy meem = null;
		FigureScheme figureScheme = null;
		if(editPart instanceof MeemEditPart) {
			meem = ((Meem)editPart.getModel()).getProxy();
			figureScheme = getMeemScheme(meem);
		}
		else
		if(editPart instanceof WedgeEditPart) {
			Wedge wedge = (Wedge)editPart.getModel();
			meem = wedge.getMeem().getProxy();
			figureScheme = getWedgeScheme(meem, wedge);
		}
		else
		if(editPart instanceof FacetEditPart) {
			meem = ((Facet)editPart.getModel()).getMeem().getProxy();
			figureScheme = getFacetScheme(meem);
		}
		if(figureScheme != null) return figureScheme;
		return absentScheme.getMeemScheme();
	}
	
	protected FigureScheme getMeemScheme(MeemClientProxy meem) {
		LifeCycleState state = getCurrentState(meem);
		if(LifeCycleState.READY.equals(state)) {
			return readyScheme.getMeemScheme();
		}
		else
		if(LifeCycleState.LOADED.equals(state)){
			return loadedScheme.getMeemScheme();
		}
		else
		return dormantScheme.getMeemScheme();
	}
	
	protected FigureScheme getWedgeScheme(MeemClientProxy meem, Wedge wedge) {
		LifeCycleState state = getCurrentState(meem);
		
		if(LifeCycleState.LOADED.equals(state)) {
			WedgeAttribute wedgeAttribute = 
				meem.getMetaMeem().getStructure().getWedgeAttribute(wedge.getAttributeIdentifier());
			if(wedgeAttribute == null) return absentScheme.getWedgeScheme();
			return loadedScheme.getWedgeScheme();
		}
		else
		if(LifeCycleState.READY.equals(state)) {
			return readyScheme.getWedgeScheme();
		}
		return dormantScheme.getWedgeScheme();
	}
	
	protected FigureScheme getFacetScheme(MeemClientProxy meem) {
		LifeCycleState state = getCurrentState(meem);
		if(LifeCycleState.READY.equals(state)) {
			return readyScheme.getFacetScheme();
		}
		else
		if(LifeCycleState.LOADED.equals(state)){
			return loadedScheme.getFacetScheme();
		}
		return dormantScheme.getFacetScheme();
	}
	
	private LifeCycleState getCurrentState(MeemClientProxy meem) {
		LifeCycleState state = null;
		if(meem == null)	state = LifeCycleState.ABSENT;
		else							state = meem.getLifeCycle().getState();
		return state;
	}
}
