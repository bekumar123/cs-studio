package org.csstudio.nams.configurator.beans;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultFilterBean extends FilterBean<DefaultFilterBean> {

	private List<FilterbedingungBean> conditions = new LinkedList<FilterbedingungBean>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
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
		DefaultFilterBean other = (DefaultFilterBean) obj;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		} else if (!conditions.equals(other.conditions))
			return false;
		return true;
	}

	/**
	 * returns a list of an and combined {@link FilterbedingungBean} list. this
	 * is done for backwards compatibility
	 * 
	 * @return
	 */
	public List<FilterbedingungBean> getConditions() {
		return new LinkedList<FilterbedingungBean>(this.conditions);
	}

	public void setConditions(final List<FilterbedingungBean> conditions) {
		final List<FilterbedingungBean> oldValue = this.conditions;
		this.conditions = conditions;
		Collections.sort(this.conditions, new FilterbedingungBeanComparator());
		this.pcs.firePropertyChange(PropertyNames.conditions.name(), oldValue,
				conditions);
	}

	@Override
	protected void doUpdateState(DefaultFilterBean bean) {
		super.doUpdateState(bean);
		
		if(bean instanceof DefaultFilterBean) {
			final List<FilterbedingungBean> cloneList = new LinkedList<FilterbedingungBean>();
			
			final List<FilterbedingungBean> list = ((DefaultFilterBean) bean).getConditions();
			for (final FilterbedingungBean filterbedingungBean : list) {
				cloneList.add(filterbedingungBean.getClone());
			}
		
			this.setConditions(cloneList);
		}
	}
}
