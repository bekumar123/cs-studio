
package org.csstudio.nams.configurator.views;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.nams.configurator.actions.BeanToEditorId;
import org.csstudio.nams.configurator.beans.DefaultFilterBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.TimebasedFilterBean;
import org.csstudio.nams.configurator.editor.ConfigurationEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class FilterView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.filter"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return DefaultFilterBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		return AbstractNamsView.getConfigurationBeanService().getFilterBeans();
	}
	
	@Override
	protected List<IAction> getMenuActions() {
		List<IAction> result = new ArrayList<IAction>(2);
		
		result.add(new Action() {
			@Override
			public void run() {
				ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
						new TimebasedFilterBean());

				final IWorkbenchPage activePage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				final String editorId = BeanToEditorId.getEnumForClass(
						TimebasedFilterBean.class).getEditorId();

				try {
					activePage.openEditor(editorInput, editorId);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public String getText() {
				return "New Timebased Filter";
			}
		});

		result.add(new Action() {
			@Override
			public void run() {
				ConfigurationEditorInput editorInput = new ConfigurationEditorInput(
						new DefaultFilterBean());
				
				final IWorkbenchPage activePage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				final String editorId = BeanToEditorId.getEnumForClass(
						DefaultFilterBean.class).getEditorId();
				
				try {
					activePage.openEditor(editorInput, editorId);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public String getText() {
				return "New Filter";
			}
		});
		
		return result;
	}
}
