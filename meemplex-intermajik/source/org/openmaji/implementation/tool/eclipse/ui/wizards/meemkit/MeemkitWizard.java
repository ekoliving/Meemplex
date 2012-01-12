package org.openmaji.implementation.tool.eclipse.ui.wizards.meemkit;

import org.eclipse.jface.wizard.Wizard;

/**
 * This eclipse wizard is used to manage the meemkits that are installed
 * in a MeemSpace.
 * 
 * @author Chris Kakris
 */

public class MeemkitWizard extends Wizard
{
  private MeemkitPage meemkitPage = new MeemkitPage();

  /**
   * Called when the wizard has been canceled, this method will ensure that the
   * wizard page is correctly cleaned up.
   * 
   * @return Always returns true
   */

  public boolean performCancel()
  {
    return performFinish();
  }

	/**
   * Called when the wizard has completed, this method will ensure that the
   * wizard page is correctly cleaned up.
   * 
   * @return Always returns true
   */

  public boolean performFinish()
	{
    meemkitPage.cleanUp();
    return true;
	}

	/**
   * Add the single meemkit page to the wizard.
   */

  public void addPages()
	{
		super.addPages();

		addPage(meemkitPage);
		setWindowTitle("Meemkit Wizard");
	}

	/**
   * Indicates that the wizard is able to be finished.
   * 
   * @return Always returns true
   */

  public boolean canFinish()
	{
		return true;
	}
}
