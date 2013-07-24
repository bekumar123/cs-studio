package org.csstudio.nams.configurator.beans;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;


public class TimebasedFilterBean extends FilterBean<TimebasedFilterBean> {

	private int timeOut;
	private JunctorConditionForFilterTreeBean startRootCondition;
	private JunctorConditionForFilterTreeBean stopRootCondition;
	
	public TimebasedFilterBean() {
		startRootCondition = new JunctorConditionForFilterTreeBean();
		startRootCondition.setJunctorConditionType(JunctorConditionType.AND);
		stopRootCondition = new JunctorConditionForFilterTreeBean();
		stopRootCondition.setJunctorConditionType(JunctorConditionType.AND);
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public int getTimeOut() {
		return timeOut;
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
		this.timeOut = bean.timeOut;
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
		if (timeOut != other.timeOut)
			return false;
		return true;
	}
	
}
