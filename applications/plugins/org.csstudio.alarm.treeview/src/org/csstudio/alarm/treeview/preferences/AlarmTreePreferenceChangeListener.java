package org.csstudio.alarm.treeview.preferences;

import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class AlarmTreePreferenceChangeListener implements
		IPropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(AlarmTreeView.getID());
		if (view != null) {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().hideView(view);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(AlarmTreeView.getID());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
