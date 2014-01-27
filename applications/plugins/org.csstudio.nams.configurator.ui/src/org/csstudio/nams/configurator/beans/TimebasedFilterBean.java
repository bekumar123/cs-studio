package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;


public class TimebasedFilterBean extends FilterBean<TimebasedFilterBean> {
	
	public static enum TimebasedPropertyNames {timeout, sendOnTimeout}

	private int timeout = 10;
	private boolean sendOnTimeout = true;
	private JunctorConditionForFilterTreeBean startRootCondition;
	private JunctorConditionForFilterTreeBean stopRootCondition;
	
	public TimebasedFilterBean() {
		startRootCondition = new JunctorConditionForFilterTreeBean();
		startRootCondition.setJunctorConditionType(JunctorConditionType.AND);
		stopRootCondition = new JunctorConditionForFilterTreeBean();
		stopRootCondition.setJunctorConditionType(JunctorConditionType.AND);
	}
	
	public void setTimeout(int timeout) {
		int oldValue = this.timeout;
		this.timeout = timeout;
		this.pcs.firePropertyChange(TimebasedPropertyNames.timeout.name(), oldValue,
				this.getTimeout());
	}
	
	public int getTimeout() {
		return timeout;
	}

	public boolean isSendOnTimeout() {
		return sendOnTimeout;
	}
	
	public void setSendOnTimeout(boolean sendOnTimeout) {
		boolean oldValue = this.sendOnTimeout;
		this.sendOnTimeout = sendOnTimeout;
		this.pcs.firePropertyChange(
				TimebasedPropertyNames.sendOnTimeout.name(), oldValue, this.isSendOnTimeout());
	}
	
	public JunctorConditionForFilterTreeBean getStartRootCondition() {
		return startRootCondition;
	}
	
	public void setStartRootCondition(
			JunctorConditionForFilterTreeBean startRootCondition) {
		this.startRootCondition = startRootCondition;
	}

	public JunctorConditionForFilterTreeBean getStopRootCondition() {
		return stopRootCondition;
	}
	
	public void setStopRootCondition(
			JunctorConditionForFilterTreeBean stopRootCondition) {
		this.stopRootCondition = stopRootCondition;
	}
	
	@Override
	public void doUpdateState(TimebasedFilterBean bean) {
		super.doUpdateState(bean);
			
		this.startRootCondition = (JunctorConditionForFilterTreeBean) bean.startRootCondition.getClone();
		this.stopRootCondition = (JunctorConditionForFilterTreeBean) bean.stopRootCondition.getClone();
		this.timeout = bean.timeout;
		this.sendOnTimeout = bean.isSendOnTimeout();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((startRootCondition == null) ? 0 : startRootCondition
						.hashCode());
		result = prime
				* result
				+ ((stopRootCondition == null) ? 0 : stopRootCondition
						.hashCode());
		result = prime * result + timeout;
		result = prime * result + (sendOnTimeout ? 1 : 2);
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
		TimebasedFilterBean other = (TimebasedFilterBean) obj;
		if (startRootCondition == null) {
			if (other.startRootCondition != null)
				return false;
		} else if (!startRootCondition.equals(other.startRootCondition))
			return false;
		if (stopRootCondition == null) {
			if (other.stopRootCondition != null)
				return false;
		} else if (!stopRootCondition.equals(other.stopRootCondition))
			return false;
		if (timeout != other.timeout)
			return false;
		if(sendOnTimeout != other.sendOnTimeout)
			return false;
		return true;
	}
	
}
