package org.csstudio.alarm.treeview.views.actions;

import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class SelectAlarmAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String pvName = "";

        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        for (ProcessVariable pv : pvs) {
        	if (pv != null) {
        		pvName = pv.getName();
				break;
        	}
		}

		if (!pvName.isEmpty()) {
			try {
					AlarmTreeView alarmTreeView = (AlarmTreeView) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(AlarmTreeView.getID());
					alarmTreeView.select(pvName);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
