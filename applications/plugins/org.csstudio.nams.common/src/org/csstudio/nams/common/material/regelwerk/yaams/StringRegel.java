package org.csstudio.nams.common.material.regelwerk.yaams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.WildcardStringCompare;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class StringRegel implements Regel {

	private final StringRegelOperator operator;
	private final String compareString;

	private final MessageKeyEnum messageKey;

	private final SimpleDateFormat amsDateFormat;
	private final ILogger errorLogger; 
	
	public StringRegel(final StringRegelOperator operator,
			final MessageKeyEnum messageKey, final String compareString, ILogger errorLogger) {
		this.operator = operator;
		this.messageKey = messageKey;
		this.compareString = compareString;
		this.errorLogger = errorLogger;
		this.amsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	private boolean wildcardStringCompare(final String value,
			final String wildcardString2) {
		try {
			return WildcardStringCompare.compare(value, wildcardString2);
		} catch (final Exception e) {
			// TODO handle Exception
			return true;
		}
	}
	
	private int numericCompare(final String value, final String compare)
			throws NumberFormatException {
		final double dVal = Double.parseDouble(value);
		final double dCompVal = Double.parseDouble(compare);

		return Double.compare(dVal, dCompVal);
	}

	private int timeCompare(final String value, final String compare)
			throws ParseException {

	    final Date dateValue = amsDateFormat.parse(value);
	    final Date dateCompValue = amsDateFormat.parse(compare);

		return dateValue.compareTo(dateCompValue);
	}


	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht) {
		boolean result = false;

		final String value = nachricht.getValueFor(this.messageKey);

		try {
			switch (this.operator) {
			
			    // text compare
			    case OPERATOR_TEXT_EQUAL:
			        result = this.wildcardStringCompare(value,
					this.compareString);
			        break;
			    case OPERATOR_TEXT_NOT_EQUAL:
			        result = !this.wildcardStringCompare(value,
					this.compareString);
			        break;

	            // numeric compare
	            case OPERATOR_NUMERIC_LT:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) < 0;
	                } else {
	                    result = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_LT_EQUAL:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) <= 0;
	                } else {
	                    result = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_EQUAL:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) == 0;
	                } else {
	                    result = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_GT_EQUAL:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) >= 0;
	                } else {
	                    result = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_GT:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) > 0;
	                } else {
	                    result = false;
	                }
	                break;
	            case OPERATOR_NUMERIC_NOT_EQUAL:
	                if (!value.isEmpty()) {
	                    result = this.numericCompare(value, this.compareString) != 0;
	                } else {
	                    result = false;
	                }
	                break;

    			// time compare
    			case OPERATOR_TIME_BEFORE:
    				result = this.timeCompare(value, this.compareString) < 0;
    				break;
    			case OPERATOR_TIME_BEFORE_EQUAL:
    				result = this.timeCompare(value, this.compareString) <= 0;
    				break;
    			case OPERATOR_TIME_EQUAL:
    				result = this.timeCompare(value, this.compareString) == 0;
    				break;
    			case OPERATOR_TIME_AFTER_EQUAL:
    				result = this.timeCompare(value, this.compareString) >= 0;
    				break;
    			case OPERATOR_TIME_AFTER:
    				result = this.timeCompare(value, this.compareString) > 0;
    				break;
    			case OPERATOR_TIME_NOT_EQUAL:
    				result = this.timeCompare(value, this.compareString) != 0;
    				break;
			}
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
}
