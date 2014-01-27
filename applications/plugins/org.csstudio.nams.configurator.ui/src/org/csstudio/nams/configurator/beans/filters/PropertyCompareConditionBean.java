package org.csstudio.nams.configurator.beans.filters;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;

public class PropertyCompareConditionBean extends AbstractConfigurationBean<PropertyCompareConditionBean> implements
FilterConditionAddOnBean {
	
	public static enum PropertyNames {
		messageKeyValue, operator;
	}
	
	private MessageKeyEnum messageKeyValue = MessageKeyEnum.NAME;
	private StringRegelOperator operator = StringRegelOperator.OPERATOR_TEXT_EQUAL;

	public void setMessageKeyValue(MessageKeyEnum messageKeyValue) {
		MessageKeyEnum oldValue = this.messageKeyValue;
		this.messageKeyValue = messageKeyValue;
		this.pcs.firePropertyChange(PropertyNames.messageKeyValue.name(), oldValue,
				this.messageKeyValue);
	}
	
	public MessageKeyEnum getMessageKeyValue() {
		return messageKeyValue;
	}
	
	public void setOperator(StringRegelOperator operator) {
		StringRegelOperator oldValue = this.operator;
		this.operator = operator;
		this.pcs.firePropertyChange(PropertyNames.operator.name(), oldValue,
				this.operator);
	}
	
	public StringRegelOperator getOperator() {
		return operator;
	}
	
	@Override
	public String getDisplayName() {
		return "newMessage." + this.messageKeyValue + " " + this.operator.toString() + " original." 
				+ this.messageKeyValue;
	}

	@Override
	public void setDisplayName(String name) {
		// nothing to do here!
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void setID(int id) {
		// nothing to do?
	}

	@Override
	protected void doUpdateState(PropertyCompareConditionBean bean) {
		this.setMessageKeyValue(bean.getMessageKeyValue());
		this.setOperator(bean.getOperator());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((messageKeyValue == null) ? 0 : messageKeyValue.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
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
		PropertyCompareConditionBean other = (PropertyCompareConditionBean) obj;
		if (messageKeyValue != other.messageKeyValue)
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
	
	

}
