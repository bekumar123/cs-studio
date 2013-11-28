package org.csstudio.nams.configurator.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageExtensionBean extends AbstractConfigurationBean<MessageExtensionBean> {

	public static enum PropertyNames {
		pvName, messageExtensions
	}

	private int id;
	private String pvName;
	private Map<String, String> messageExtensions = new HashMap<String, String>();
	
	public String getPvName() {
		return pvName;
	}
	
	public void setPvName(final String pvName) {
		final String oldValue = getPvName();
		this.pvName = (pvName != null) ? pvName : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.pvName.name(), oldValue, getPvName());
	}
	
	public Map<String, String> getMessageExtensions() {
		return Collections.unmodifiableMap(messageExtensions);
	}
	
	public void setMessageExtensions(Map<String, String> messageExtensions) {
		if(!messageExtensions.equals(this.messageExtensions)) {
			final Map<String, String> oldValue = getMessageExtensions();
			this.messageExtensions = (messageExtensions != null) ? new HashMap<String, String>(messageExtensions) : new HashMap<String, String>();
			this.pcs.firePropertyChange(PropertyNames.messageExtensions.name(), oldValue, getMessageExtensions());
		}
	}
	
	public void setMessageExtension(String messageKey, String messageValue) {
		if (!messageExtensions.containsKey(messageKey) || !messageExtensions.get(messageKey).equals(messageValue)) {
			final Map<String, String> oldValue = new HashMap<String, String>(messageExtensions);
			messageExtensions.put(messageKey, messageValue);
			this.pcs.firePropertyChange(PropertyNames.messageExtensions.name(), oldValue, getMessageExtensions());
		}
	}
	
	public void removeMessageExtension(String messageKey) {
		if(messageExtensions.containsKey(messageKey)) {
			final Map<String, String> oldValue = new HashMap<String, String>(messageExtensions);
			messageExtensions.remove(messageKey);
			this.pcs.firePropertyChange(PropertyNames.messageExtensions.name(), oldValue, getMessageExtensions());
		}
	}

	@Override
	public String getDisplayName() {
		String pvName = getPvName();
		return pvName != null ? pvName : Messages.MessageExtensionBean_no_name; 
	}

	@Override
	public void setDisplayName(String name) {
		setPvName(name);
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}


	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		result = prime * result + ((messageExtensions == null) ? 0 : messageExtensions.hashCode());
		result = prime * result + ((pvName == null) ? 0 : pvName.hashCode());
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
		MessageExtensionBean other = (MessageExtensionBean) obj;
		if (id != other.id)
			return false;
		if (messageExtensions == null) {
			if (other.messageExtensions != null)
				return false;
		} else if (!messageExtensions.equals(other.messageExtensions))
			return false;
		if (pvName == null) {
			if (other.pvName != null)
				return false;
		} else if (!pvName.equals(other.pvName))
			return false;
		return true;
	}

	@Override
	protected void doUpdateState(MessageExtensionBean bean) {
		this.id = bean.id;
		this.pvName = bean.pvName;
		this.messageExtensions = new HashMap<String, String>(bean.messageExtensions);
	}

	
}
