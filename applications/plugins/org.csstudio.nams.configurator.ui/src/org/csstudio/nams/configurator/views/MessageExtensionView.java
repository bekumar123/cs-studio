package org.csstudio.nams.configurator.views;

import java.io.File;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.MessageExtensionBean;
import org.csstudio.nams.configurator.service.MessageExtensionsFileImporter;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;

public class MessageExtensionView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.messageextension"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return MessageExtensionBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		return AbstractNamsView.getConfigurationBeanService().getMessageExtensionBeans();
	}

	@Override
	public void createPartControl(Composite rootComposite) {
		super.createPartControl(rootComposite);
		final IActionBars actionBar = this.getViewSite().getActionBars();
		actionBar.getToolBarManager().add(new Action() {
			@Override
			public int getStyle() {
				return SWT.BORDER | SWT.ICON_WORKING;
			}

			@Override
			public String getText() {
				return "Import";
			}

			@Override
			public String getToolTipText() {
				return "Nachrichten-Erweiterungen aus CSV importieren";
			}

			@Override
			public void run() {
				FileDialog fileDialog = new FileDialog(getViewSite().getShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.extcsv" });
				fileDialog.setText("Choose Message Extensions File");
				String csvFilePath = fileDialog.open();
				if (csvFilePath != null) {
					File csvFile = new File(csvFilePath);
					new MessageExtensionsFileImporter(_configurationBeanService, _logger).importMessageExtensions(csvFile);
				}
			}
		});
	}
}
