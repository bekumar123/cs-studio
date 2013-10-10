package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;


public class WatchDogFilterBean extends FilterBean<WatchDogFilterBean> {

	public static enum WatchDogPropertyNames {timeout}
	private int timeout = 10;
	private JunctorConditionForFilterTreeBean rootCondition;
	
	public WatchDogFilterBean() {
		rootCondition = new JunctorConditionForFilterTreeBean();
		rootCondition.setJunctorConditionType(JunctorConditionType.AND);
	}
	
	public void setTimeout(int timeout) {
		int oldValue = this.timeout;
		this.timeout = timeout;
		pcs.firePropertyChange(WatchDogPropertyNames.timeout.name(), oldValue, timeout);
	}
	
	public int getTimeout() {
		return timeout;
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
		this.timeout = bean.timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((rootCondition == null) ? 0 : rootCondition
						.hashCode());
		result = prime * result + timeout;
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
		if (timeout != other.timeout)
			return false;
		return true;
	}
	
}
