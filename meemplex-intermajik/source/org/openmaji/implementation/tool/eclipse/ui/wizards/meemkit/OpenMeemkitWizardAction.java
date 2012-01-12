package org.openmaji.implementation.tool.eclipse.ui.wizards.meemkit;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;

public class OpenMeemkitWizardAction extends Action implements IWorkbenchWindowActionDelegate
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
		return "org.openmajik.intermajik.actions.MeemkitWizard";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return ImageDescriptor.createFromFile(Images.class, "icons/meemkit16.gif");
	}

	public String getText()
	{
		return "Manage Meemkits...";
	}

	public void run()
	{
		Shell shell = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		WizardDialog dialog = new WizardDialog(shell, new MeemkitWizard());
		dialog.create();
		dialog.open();
	}

}
