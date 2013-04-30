package org.csstudio.nams.configurator.service;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.FilterAction;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.IReceiverBean;
import org.csstudio.nams.configurator.beans.User2GroupBean;

public class FilterForAlarmbearbeiterService {

	private final AlarmbearbeiterBean alarmBearbeiter;
	private final ConfigurationBeanService configurationBeanService;
	private ConfigurationBeanServiceListener configurationBeanServiceListener;

	private List<FilterForAlarmbearbeiterListener> listeners;

	public FilterForAlarmbearbeiterService(AlarmbearbeiterBean alarmBearbeiter,
			ConfigurationBeanService configurationBeanService) {
		this.alarmBearbeiter = alarmBearbeiter;
		this.configurationBeanService = configurationBeanService;
		configurationBeanServiceListener = new ConfigurationBeanServiceListenerImpl();
		configurationBeanService
				.addConfigurationBeanServiceListener(configurationBeanServiceListener);

		listeners = new ArrayList<FilterForAlarmbearbeiterService.FilterForAlarmbearbeiterListener>();
	}

	public List<FilterForAlarmbearbeiter> getActiveFilterBeans() {
		ArrayList<FilterForAlarmbearbeiter> result = new ArrayList<FilterForAlarmbearbeiter>();

		FilterBean[] filterBeans = this.configurationBeanService
				.getFilterBeans();
		for (FilterBean filterBean : filterBeans) {
			List<FilterAction> filterActions = filterBean.getActions();
			for (FilterAction filterAction : filterActions) {
				IReceiverBean receiver = filterAction.getReceiver();
				if (receiver instanceof AlarmbearbeiterBean) {
					if (receiver.equals(this.alarmBearbeiter)) {
						result.add(new FilterForAlarmbearbeiter(filterBean));
					}
				} else if (receiver instanceof AlarmbearbeiterGruppenBean) {
					AlarmbearbeiterGruppenBean alarmbearbeiterGruppenBean = (AlarmbearbeiterGruppenBean) receiver;
					if (alarmbearbeiterGruppenBean.isActive()) {
						List<User2GroupBean> groupUsers = alarmbearbeiterGruppenBean.getUsers();
						for (User2GroupBean user2GroupBean : groupUsers) {
							if (user2GroupBean.isActive()
									&& user2GroupBean.getUserBean().equals(
											alarmBearbeiter)) {
								result.add(new FilterForAlarmbearbeiter(filterBean, alarmbearbeiterGruppenBean));
							}
						}
					}
				}
			}
		}

		return result;
	}

	public void addListener(FilterForAlarmbearbeiterListener listener) {
		if (listener != null && !this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeListener(FilterForAlarmbearbeiterListener listener) {
		this.listeners.remove(listener);
	}

	public void dispose() {
		this.configurationBeanService
				.removeConfigurationBeanServiceListener(configurationBeanServiceListener);
	}

	private void handleBeanDeleted(IConfigurationBean bean) {
		if(this.isRelevantBean(bean)) {
			this.fireChange();
		}
	}

	private void handleBeanInsert(IConfigurationBean bean) {
		if(this.isRelevantBean(bean)) {
			this.fireChange();
		}
	}

	private void handleBeanUpdate(IConfigurationBean bean) {
		if(this.isRelevantBean(bean)) {
			this.fireChange();
		}
	}

	private void handleConfigurationReload() {
		this.fireChange();
	}
	
	private boolean isRelevantBean(IConfigurationBean bean) {
		return bean instanceof FilterBean || bean instanceof AlarmbearbeiterGruppenBean; 
	}

	private void fireChange() {
		for (FilterForAlarmbearbeiterListener listener : this.listeners) {
			listener.onChanged();
		}
	}
	
	public class FilterForAlarmbearbeiter {
		private FilterBean filterBean;
		private AlarmbearbeiterGruppenBean groupBean;
		
		public FilterForAlarmbearbeiter(FilterBean filterBean) {
			this(filterBean, null);
		}
		public FilterForAlarmbearbeiter(FilterBean filterBean, AlarmbearbeiterGruppenBean groupBean) {
			this.filterBean = filterBean;
			this.groupBean = groupBean;
		}
		
		public FilterBean getFilterBean() {
			return filterBean;
		}
		
		public AlarmbearbeiterGruppenBean getGroupBean() {
			return groupBean;
		}
		
		public boolean hasGroup() {
			return this.groupBean != null;
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder(this.filterBean.toString());
			if(this.hasGroup()) {
				result.append(" (Group: ").append(this.getGroupBean().toString()).append(")");
			}
			
			return result.toString();
		}
	}

	public interface FilterForAlarmbearbeiterListener {
		void onChanged();
	}

	private class ConfigurationBeanServiceListenerImpl implements
			ConfigurationBeanServiceListener {

		@Override
		public void onBeanDeleted(IConfigurationBean bean) {
			handleBeanDeleted(bean);
		}

		@Override
		public void onBeanInsert(IConfigurationBean bean) {
			handleBeanInsert(bean);
		}

		@Override
		public void onBeanUpdate(IConfigurationBean bean) {
			handleBeanUpdate(bean);
		}

		@Override
		public void onConfigurationReload() {
			handleConfigurationReload();
		}

	}

}
