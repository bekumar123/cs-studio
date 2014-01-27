package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class StringRegel implements Regel {

	private final String compareString;
	private final MessageKeyEnum messageKey;

	private StringRegelComparator regelComparator; 
	private final ILogger errorLogger;
	
	public StringRegel(final StringRegelOperator operator,
			final MessageKeyEnum messageKey, final String compareString, ILogger errorLogger) {
		this.messageKey = messageKey;
		this.compareString = compareString;
		this.errorLogger = errorLogger;
		this.regelComparator = new StringRegelComparator(operator, true);
	}

	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht) {
		boolean result = false;
		
		try {
			result = regelComparator.compare(nachricht.getValueFor(messageKey), compareString);
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
	public boolean pruefeNachricht(AlarmNachricht nachricht, AlarmNachricht vergleichsNachricht) {
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
		StringRegel other = (StringRegel) obj;
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


	
}
