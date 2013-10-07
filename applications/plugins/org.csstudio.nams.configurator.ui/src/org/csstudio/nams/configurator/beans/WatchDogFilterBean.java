package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;


public class WatchDogFilterBean extends FilterBean<WatchDogFilterBean> {

	private int timeOut;
	private JunctorConditionForFilterTreeBean rootCondition;
	
	public WatchDogFilterBean() {
		rootCondition = new JunctorConditionForFilterTreeBean();
		rootCondition.setJunctorConditionType(JunctorConditionType.AND);
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public int getTimeOut() {
		return timeOut;
	}

	public JunctorConditionForFilterTreeBean getRootCondition() {
		return rootCondition;
	}
	
	public void setRootCondition(
			JunctorConditionForFilterTreeBean rootCondition) {
		this.rootCondition = rootCondition;
	}

	
	@Override
	public void doUpdateState(WatchDogFilterBean bean) {
		super.doUpdateState(bean);
			
		this.rootCondition = (JunctorConditionForFilterTreeBean) bean.rootCondition.getClone();
		this.timeOut = bean.timeOut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((rootCondition == null) ? 0 : rootCondition
						.hashCode());
		result = prime * result + timeOut;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatchDogFilterBean other = (WatchDogFilterBean) obj;
		if (rootCondition == null) {
			if (other.rootCondition != null)
				return false;
		} else if (!rootCondition.equals(other.rootCondition))
			return false;
		if (timeOut != other.timeOut)
			return false;
		return true;
	}
	
}
