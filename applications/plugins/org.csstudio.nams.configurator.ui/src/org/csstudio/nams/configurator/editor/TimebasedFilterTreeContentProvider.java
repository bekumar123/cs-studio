
package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.TimebasedFilterBean;
import org.eclipse.jface.viewers.Viewer;


public class TimebasedFilterTreeContentProvider extends FilterTreeContentProvider {
	
	public enum TimebasedFilterTreeContentType {
		START, STOP;
	}
	
	private final TimebasedFilterTreeContentType contentType;
	
	public TimebasedFilterTreeContentProvider(TimebasedFilterTreeContentType contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		if(newInput instanceof TimebasedFilterBean) {
			TimebasedFilterBean filterBean = ((TimebasedFilterBean) newInput);
			if (this.contentType == TimebasedFilterTreeContentType.START) {
				setRootCondition(filterBean.getStartRootCondition());
			} else {
				setRootCondition(filterBean.getStopRootCondition());
			}
		} else {
			setRootCondition(null);
		}
	}
}
