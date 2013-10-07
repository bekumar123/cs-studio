package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.WatchDogFilterBean;
import org.eclipse.jface.viewers.Viewer;

public class WatchDogFilterTreeContentProvider extends
		FilterTreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof WatchDogFilterBean) {
			WatchDogFilterBean filterBean = ((WatchDogFilterBean) newInput);
			setRootCondition(filterBean.getRootCondition());
		} else {
			setRootCondition(null);
		}
	}

}
