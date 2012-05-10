/*
 * @(#)BugReportWizard.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.wizards.bugreport;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class BugReportWizard extends Wizard {

	public static final String BUG_REPORT_EMAIL_ADDRESS = "bugs@majitek.com";
	//public static final String BUG_REPORT_EMAIL_ADDRESS = "bugs@mg.myretsu.com";

	BugDetailsPage bugDetailsPage = new BugDetailsPage();
	UserDetailsPage userDetailsPage = new UserDetailsPage();
	ReportPreviewPage reportPreviewPage;
	
	public BugReportWizard() {
		reportPreviewPage = new ReportPreviewPage(bugDetailsPage, userDetailsPage);

		userDetailsPage.setName(MajiPlugin.getDefault().getPreferenceStore().getString("BugReport_Name"));
		userDetailsPage.setEmail(MajiPlugin.getDefault().getPreferenceStore().getString("BugReport_Email"));
		userDetailsPage.setEmailHost(MajiPlugin.getDefault().getPreferenceStore().getString("BugReport_Email_Host"));
		userDetailsPage.setEmailUser(MajiPlugin.getDefault().getPreferenceStore().getString("BugReport_Email_User"));
		userDetailsPage.setEmailRequiresLogin(MajiPlugin.getDefault().getPreferenceStore().getBoolean("BugReport_Email_Requires_Login"));
		userDetailsPage.setSaveEmailPassword(MajiPlugin.getDefault().getPreferenceStore().getBoolean("BugReport_Save_Email_Password"));
		userDetailsPage.setEmailPassword(MajiPlugin.getDefault().getPreferenceStore().getString("BugReport_Email_Password"));
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {

		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Name", userDetailsPage.getName());
		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Email", userDetailsPage.getEmail());
		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Email_Host", userDetailsPage.getEmailHost());
		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Email_User", userDetailsPage.getEmailUser());

		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Email_Requires_Login", userDetailsPage.getEmailRequiresLogin());

		boolean saveEmailPassword = userDetailsPage.getSaveEmailPassword();
		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Save_Email_Password", saveEmailPassword);
		String password = "";
		if (saveEmailPassword) {
			password = userDetailsPage.getEmailPassword();
		}
		MajiPlugin.getDefault().getPreferenceStore().setValue("BugReport_Email_Password", password);
		return sendMail();
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();

		addPage(bugDetailsPage);
		addPage(userDetailsPage);
		addPage(reportPreviewPage);

		setWindowTitle("Bug Report Wizard");
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	public boolean canFinish() {
		if (reportPreviewPage.isCurrentPage()) {
			return true;
		}
		return false;

	}

	private boolean sendMail() {
		String host = userDetailsPage.getEmailHost();
		String from = userDetailsPage.getEmail();

		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", Boolean.toString(userDetailsPage.getEmailRequiresLogin()));

		String user = userDetailsPage.getEmailUser();
		String password = userDetailsPage.getEmailPassword();

		MailAuthenticator mailAuthenticator = new MailAuthenticator(user, password);

		// Get session
		Session session = Session.getDefaultInstance(props, mailAuthenticator);

		// Define message
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(BUG_REPORT_EMAIL_ADDRESS));
			message.setSubject(bugDetailsPage.getSummary());
			message.setText(reportPreviewPage.getReportText());
			message.setSentDate(new Date());
			message.saveChanges();

			// Send message			
			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			handleSendException(e);
			return false;
		}

		return true;
	}

	private void handleSendException(MessagingException e) {
		if (e instanceof SendFailedException) {
			if (e.getNextException() instanceof AuthenticationFailedException) {
				handleAuthenticationException();
				return;
			}

			if (e.getNextException() instanceof MessagingException) {
				handleMessagingException((MessagingException) e.getNextException());
			}
		}
	}

	private void handleMessagingException(MessagingException e) {
		Shell shell = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		SendErrorDialog dialog = new SendErrorDialog(shell, e.getMessage());
		int buttonIndex = dialog.open();
		if (buttonIndex == 0) {
			copyReportToClipboard();
		}
	}

	private void handleAuthenticationException() {
		Shell shell = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		SendErrorDialog dialog = new SendErrorDialog(shell, "Username/password failed");
		int buttonIndex = dialog.open();
		if (buttonIndex == 0) {
			copyReportToClipboard();
		}
	}

	private void copyReportToClipboard() {
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		String textData = reportPreviewPage.getReportText();
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
		clipboard.dispose();

	}

}
