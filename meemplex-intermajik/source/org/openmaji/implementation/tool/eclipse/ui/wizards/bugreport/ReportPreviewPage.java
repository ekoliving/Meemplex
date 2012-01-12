/*
 * @(#)ReportPreviewPage.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.wizards.bugreport;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openmaji.implementation.tool.eclipse.Common;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class ReportPreviewPage extends WizardPage {

	private String reportText = "";
	private BugDetailsPage bugDetailsPage;
	private UserDetailsPage userDetailsPage;
	private Text txtReport;

	public ReportPreviewPage(BugDetailsPage bugDetailsPage, UserDetailsPage userDetailsPage) {
		super("Bug Report Preview Page");
		setTitle("Bug Report Preview Page");
		setMessage("Please review the contents of the bug report and click Finish to submit");
		this.bugDetailsPage = bugDetailsPage;		
		this.userDetailsPage = userDetailsPage;
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.layout(true);
		
		Label lblSummary = new Label(composite, SWT.NONE);
		lblSummary.setText("Report contents");		
		
		txtReport = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
		txtReport.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		setControl(composite);

		generateReport();		
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isCurrentPage()
	 */
	public boolean isCurrentPage() {
		boolean currentPage = super.isCurrentPage(); 
		if (currentPage) {
			generateReport();
		}
		return currentPage;
	}

	private void generateReport() {
		StringBuffer report = new StringBuffer();
		
		report.append("reporter.name=");
		report.append(userDetailsPage.getName());
		report.append("\n");
		report.append("reporter.email=");
		report.append(userDetailsPage.getEmail());
		report.append("\n");
		report.append(getOS());
		report.append(getVersion());
		report.append(getJavaVersion());
		report.append("report.summary=");
		report.append(bugDetailsPage.getSummary());
		report.append("\n");
		report.append("report.details=");
		
		String details = bugDetailsPage.getDetails();
		String lineSep = System.getProperty("line.separator");
		details = details.replaceAll(lineSep, "\\\\n");
		
		report.append(details);
		report.append("\n");
		
		reportText = report.toString();
		
		txtReport.setText(reportText);
	}
	
	private String getOS() {
		StringBuffer s = new StringBuffer();
		s.append("os.name=");
		s.append(System.getProperty("os.name"));
		s.append("\n");
		s.append("os.arch=");
		s.append(System.getProperty("os.arch"));
		s.append("\n");
		s.append("os.version=");
		s.append(System.getProperty("os.version"));
		s.append("\n");
		return s.toString();
	}
	
	// this should really be the intermajik release version, but there is no easy way of getting it at the moment
	private String getVersion() {
		return "maji.version=" + Common.getIdentification() + "\n";
	}
	
	private String getJavaVersion() {
		StringBuffer s = new StringBuffer();
		s.append("java.version=");
		s.append(System.getProperty("java.version"));
		s.append("\n");
		s.append("java.vendor=");
		s.append(System.getProperty("java.vendor"));
		s.append("\n");
		return s.toString();
	}
	
	public String getReportText() {
		return reportText;
	}

}
