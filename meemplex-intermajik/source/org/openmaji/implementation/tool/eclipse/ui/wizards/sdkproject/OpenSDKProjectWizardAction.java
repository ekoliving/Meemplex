package org.openmaji.implementation.tool.eclipse.ui.wizards.sdkproject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
//import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;

public class OpenSDKProjectWizardAction extends Action implements IWorkbenchWindowActionDelegate
{
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
		return "org.openmajik.intermajik.actions.SDKProjectWizard";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.createFromFile(Images.class, "icons/deployment16.gif");
	}

	public String getText()
	{
		return "Create a new Meemkit Project...";
	}

	public void run()
	{
		Shell shell = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		WizardDialog dialog = new WizardDialog(shell, new SDKProjectWizard()); 
  /*
   *   TODO: Replace the project path text entry with a DirectoryDialog like this one:
   
          DirectoryDialog dialog = 
                new DirectoryDialog(shell);
            dialog.setMessage("Where would you like to create the new project?");
           String projectPath;
           projectPath = dialog.open();
           System.out.println("Project Path = ["+ projectPath + "]");
     */
 
		dialog.create();
		dialog.open();
        
	}

}
