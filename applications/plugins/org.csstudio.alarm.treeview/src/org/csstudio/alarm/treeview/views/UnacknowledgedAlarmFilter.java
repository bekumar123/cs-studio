package org.csstudio.alarm.treeview.views;

import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class UnacknowledgedAlarmFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IAlarmTreeNode) {
			IAlarmTreeNode alarmTreeNode = (IAlarmTreeNode) element;
			return alarmTreeNode.getUnacknowledgedAlarmSeverity() != EpicsAlarmSeverity.UNKNOWN 
				&& alarmTreeNode.getUnacknowledgedAlarmSeverity() != EpicsAlarmSeverity.NO_ALARM;
		}

		// If the element is not an IAlarmTreeNode, we don't know what it is,
		// so as a safe default, it passes through the filter.
		return true;
	}

}
