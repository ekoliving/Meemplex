package org.openmaji.implementation.tool.eclipse.ui.wizards.sdkproject;


import org.eclipse.jface.wizard.Wizard;

public class SDKProjectWizard extends Wizard
{
  public SDKProjectPage sdkPage = new SDKProjectPage();

	public SDKProjectWizard()
    {
 
	}

	public boolean performFinish()
	{
       //String projectName, packageName, projectPath;
       //projectName = sdkPage.getProjectName();
       //packageName = sdkPage.getPackageName();
       //projectPath = sdkPage.getProjectPath();

System.err.println("COMMENTED OUT BY CHRISTOS 20041014");
       //       ProjectBuilder.create( projectName ,packageName, projectPath );
       return true;
    };


	public void addPages()
	{
		super.addPages();

		addPage(sdkPage);
		setWindowTitle("Meemkit Project Creator Wizard");
	}

	public boolean canFinish()
	{
		return true;
	}

}
