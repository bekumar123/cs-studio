package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class PropertyCompareFilterCondition implements FilterCondition {

	private final MessageKeyEnum messageKey;
	private final StringFilterConditionComparator regelComparator; 

	private final ILogger errorLogger;
	
	public PropertyCompareFilterCondition(final StringFilterConditionOperator operator,
			final MessageKeyEnum messageKey, ILogger errorLogger) {
		this.messageKey = messageKey;
		this.errorLogger = errorLogger;
		regelComparator = new StringFilterConditionComparator(operator, false);
	}


	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht) {
		// TODO fz: Exception?
		return false;
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
		boolean result = false;
		
		final String value = nachricht.getValueFor(this.messageKey);
		final String vergleichsNachrichtValue = vergleichsNachricht.getValueFor(this.messageKey);

		try {
			regelComparator.setComparedString(vergleichsNachrichtValue);
			result = regelComparator.compare(value);
		} catch (final Exception e) {
            if(this.errorLogger != null) {
                this.errorLogger.logErrorMessage(this,
                        "An error occured during parsing of : " + nachricht);
            }
			result = true;
		}

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messageKey == null) ? 0 : messageKey.hashCode());
		result = prime * result
				+ ((regelComparator == null) ? 0 : regelComparator.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyCompareFilterCondition other = (PropertyCompareFilterCondition) obj;
		if (messageKey != other.messageKey)
			return false;
		if (regelComparator == null) {
			if (other.regelComparator != null)
				return false;
		} else if (!regelComparator.equals(other.regelComparator))
			return false;
		return true;
	}
}
