package org.openmaji.implementation.tool.eclipse.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.openmaji.system.meemkit.core.MeemkitWizard;
import org.openmaji.system.meemkit.core.MeemkitWizardDescriptor;

public class ActionStarter extends Action implements IWorkbenchWindowActionDelegate
{
  private static final Logger logger = Logger.getAnonymousLogger();

  private final String id;
  private final MeemkitWizardDescriptor meemkitWizardDescriptor;

  public ActionStarter(String id, MeemkitWizardDescriptor meemkitWizardDescriptor)
  {
    this.id = id;
    this.meemkitWizardDescriptor = meemkitWizardDescriptor;
  }

  public void run(IAction action)
	{
		run();
	}

	public void dispose()
	{
	}

	public void init(IWorkbenchWindow window)
	{
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	public String getId()
	{
		return id;
	}

	public ImageDescriptor getImageDescriptor()
	{
    if ( ( meemkitWizardDescriptor.getResourceClass() == null ) || ( meemkitWizardDescriptor.getImageFilename() == null ) )
    {
      return null;
    }

    Class resourceClass = null;
    try
    {
      resourceClass = Class.forName(meemkitWizardDescriptor.getResourceClass());
    }
    catch ( ClassNotFoundException ex )
    {
      logger.log(Level.WARNING, "Unable to load class '"+resourceClass+"'");
      return null;
    }

		return ImageDescriptor.createFromFile(resourceClass,meemkitWizardDescriptor.getImageFilename());
	}

	public String getText()
	{
		return meemkitWizardDescriptor.getText();
	}

	public void run()
	{
    MeemkitWizard wizard = null;
    try
    {
      Class theClass = Class.forName(meemkitWizardDescriptor.getWizardClass());
      wizard = (MeemkitWizard) theClass.newInstance();
    }
    catch ( ClassNotFoundException ex )
    {
    	logger.log(Level.WARNING, "Class not found '"+meemkitWizardDescriptor.getWizardClass()+"'");
    }
    catch ( IllegalAccessException ex )
    {
    	logger.log(Level.WARNING, "IllegalAccessException for '"+meemkitWizardDescriptor.getWizardClass()+"': "+ex.getMessage());
    }
    catch ( InstantiationException ex )
    {
    	logger.log(Level.WARNING, "Unable to instantiate '"+meemkitWizardDescriptor.getWizardClass()+"': "+ex.getMessage());
    }
    catch ( ClassCastException ex )
    {
    	logger.log(Level.WARNING, "The class '"+meemkitWizardDescriptor.getWizardClass()+"' does not implement MeemkitWizard interface");
    }

    if ( wizard == null )
    {
      return;
    }

    JFrame jframe = new JFrame();
    jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    wizard.initialize(jframe.getContentPane());
    jframe.pack();
    jframe.setVisible(true);
	}
}
