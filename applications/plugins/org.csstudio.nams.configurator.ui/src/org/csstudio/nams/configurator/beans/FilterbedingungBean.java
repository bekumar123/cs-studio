
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;

public class FilterbedingungBean extends
		AbstractConfigurationBean<FilterbedingungBean> {

	public static enum PropertyNames {
		filterbedingungID, description, name, filterSpecificBean
	}

	private int filterbedingungID;
	private String description = ""; //$NON-NLS-1$

	private String name = ""; //$NON-NLS-1$

	private FilterConditionAddOnBean filterSpecificBean;

	public FilterbedingungBean() {
		this.filterbedingungID = -1;
		this.filterSpecificBean = new StringFilterConditionBean();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final FilterbedingungBean other = (FilterbedingungBean) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.filterSpecificBean == null) {
			if (other.filterSpecificBean != null) {
				return false;
			}
		} else if (!this.filterSpecificBean.equals(other.filterSpecificBean)) {
			return false;
		}
		if (this.filterbedingungID != other.filterbedingungID) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
    public String getDisplayName() {
		return this.name == null ? Messages.FilterbedingungBean_without_name : this.name;
	}

	public int getFilterbedingungID() {
		return this.filterbedingungID;
	}

	public AbstractConfigurationBean<?> getFilterSpecificBean() {
		return (AbstractConfigurationBean<?>) this.filterSpecificBean;
	}

	@Override
    public int getID() {
		return this.getFilterbedingungID();
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((this.description == null) ? 0 : this.description.hashCode());
		result = prime
				* result
				+ ((this.filterSpecificBean == null) ? 0
						: this.filterSpecificBean.hashCode());
		result = prime * result + this.filterbedingungID;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public void setDescription(final String description) {
		final String oldValue = this.description;
		this.description = (description != null) ? description : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.description.name(), oldValue,
				description);
	}

	public void setFilterbedingungID(final int filterbedingungID) {
		final int oldValue = this.filterbedingungID;
		this.filterbedingungID = filterbedingungID;
		this.pcs.firePropertyChange(PropertyNames.filterbedingungID.name(),
				oldValue, filterbedingungID);
	}

	public void setFilterSpecificBean(
			final FilterConditionAddOnBean filterSpecificBean) {
		final FilterConditionAddOnBean oldValue = this.filterSpecificBean;
		this.filterSpecificBean = filterSpecificBean;
		this.pcs.firePropertyChange(PropertyNames.filterSpecificBean.name(),
				oldValue, filterSpecificBean);
	}

	@Override
    public void setID(final int id) {
		this.setFilterbedingungID(id);
	}

	public void setName(final String name) {
		final String oldValue = this.name;
		this.name = (name != null) ? name : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.name.name(), oldValue, name);
	}

	@Override
	public String toString() {
		return this.getDisplayName();
	}

	@Override
	protected void doUpdateState(final FilterbedingungBean bean) {
		this.setDescription(bean.getDescription());
		this.setName(bean.getName());
		this.setFilterbedingungID(bean.getFilterbedingungID());
		this.setFilterSpecificBean((FilterConditionAddOnBean) bean
				.getFilterSpecificBean());

		if (this.filterSpecificBean != null) {
			bean
					.setFilterSpecificBean((FilterConditionAddOnBean) this.filterSpecificBean
							.getClone());
		} else {
			// TODO mw: default is always ODER, i'm not sure about this here
			// (gs) no default is null
//			final StringFilterConditionBean junctorConditionBean = new StringFilterConditionBean();
//			this.filterSpecificBean = junctorConditionBean;
//			bean.setFilterSpecificBean(junctorConditionBean);
		}
	}

	@Override
    public void setDisplayName(String name) {
		this.setName(name);
	}
}
