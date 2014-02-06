package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class StringFilterCondition implements FilterCondition {

	private final String compareString;
	private final MessageKeyEnum messageKey;

	private StringFilterConditionComparator regelComparator; 
	private final ILogger errorLogger;
	
	public StringFilterCondition(final StringFilterConditionOperator operator,
			final MessageKeyEnum messageKey, final String compareString, ILogger errorLogger) {
		this.messageKey = messageKey;
		this.compareString = compareString;
		this.errorLogger = errorLogger;
		this.regelComparator = new StringFilterConditionComparator(operator, true);
		this.regelComparator.setComparedString(compareString);
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht) {
		boolean result = false;
		
		try {
			result = regelComparator.compare(nachricht.getValueFor(messageKey));
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
	public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
		return this.pruefeNachricht(nachricht);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compareString == null) ? 0 : compareString.hashCode());
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
		StringFilterCondition other = (StringFilterCondition) obj;
		if (compareString == null) {
			if (other.compareString != null)
				return false;
		} else if (!compareString.equals(other.compareString))
			return false;
		if (messageKey != other.messageKey)
			return false;
		if (regelComparator == null) {
			if (other.regelComparator != null)
				return false;
		} else if (!regelComparator.equals(other.regelComparator))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "string(value("  + messageKey + ") " + regelComparator + ")";
	}
	
}
